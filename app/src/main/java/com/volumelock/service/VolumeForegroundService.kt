package com.volumelock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.volumelock.R
import com.volumelock.data.VolumeLogDatabase
import com.volumelock.data.VolumeLogEntity
import com.volumelock.data.VolumeRepository
import com.volumelock.data.VolumeStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * T3.1 + T3.2 — Servicio en primer plano que monitorea los cambios de volumen del
 * sistema (BroadcastReceiver de VOLUME_CHANGED_ACTION), los registra en el log (RF06)
 * y, si el candado está activo, restaura el volumen objetivo (RF03).
 *
 * El receiver se registra en runtime porque VOLUME_CHANGED_ACTION ya no se entrega a
 * receivers declarados en el manifest en versiones recientes de Android.
 */
class VolumeForegroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var audioManager: AudioManager
    private lateinit var repository: VolumeRepository
    private val dao by lazy { VolumeLogDatabase.get(this).volumeLogDao() }

    @Volatile
    private var lockActive: Boolean = false

    /** Valor que fijó el propio servicio al restaurar, por stream. Sirve para
     * ignorar el broadcast que genera nuestra propia restauración (evita bucle). */
    private val selfSet = mutableMapOf<Int, Int>()

    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != VOLUME_CHANGED_ACTION) return
            val stream = intent.getIntExtra(EXTRA_STREAM_TYPE, -1)
            val newValue = intent.getIntExtra(EXTRA_STREAM_VALUE, -1)
            val prevValue = intent.getIntExtra(EXTRA_PREV_STREAM_VALUE, -1)
            if (stream < 0 || newValue < 0) return
            handleVolumeChange(stream, prevValue, newValue)
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        repository = VolumeRepository(this)
        createChannel()

        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(volumeReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(volumeReceiver, filter)
        }

        // Fuente de verdad del candado: DataStore. Refleja el estado al AccessibilityService
        // (bloqueo de teclas) y a la notificación.
        scope.launch {
            repository.lockState.collect { active ->
                lockActive = active
                VolumeAccessibilityService.lockActive = active
                updateNotification()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            foregroundServiceType()
        )
        return START_STICKY
    }

    private fun handleVolumeChange(stream: Int, prevValue: Int, newValue: Int) {
        // Ignora el broadcast provocado por nuestra propia restauración.
        if (selfSet[stream] == newValue) {
            selfSet.remove(stream)
            return
        }

        scope.launch {
            val target = targetFor(stream)
            val debeRevertir = shouldRevert(lockActive, target, newValue)

            if (debeRevertir) {
                selfSet[stream] = target!!
                audioManager.setStreamVolume(stream, target, 0)
            }

            dao.insertAndTrim(
                VolumeLogEntity(
                    timestamp = System.currentTimeMillis(),
                    stream = streamName(stream),
                    oldValue = prevValue,
                    newValue = newValue,
                    reverted = debeRevertir,
                )
            )
            Log.d(TAG, "log: ${streamName(stream)} $prevValue→$newValue revertido=$debeRevertir")
        }
    }

    private suspend fun targetFor(stream: Int): Int? {
        val vs = VolumeStream.entries.find { it.androidStreamType == stream } ?: return null
        return repository.targetVolume(vs).first()
    }

    private fun streamName(stream: Int): String =
        VolumeStream.entries.find { it.androidStreamType == stream }?.name ?: "STREAM_$stream"

    // --- Notificación ---

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.fgs_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val estado = if (lockActive) {
            getString(R.string.fgs_estado_bloqueado)
        } else {
            getString(R.string.fgs_estado_desbloqueado)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(estado)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification() {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification())
    }

    private fun foregroundServiceType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }

    override fun onDestroy() {
        unregisterReceiver(volumeReceiver)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {

        /** Decide si un cambio de volumen debe revertirse: solo con candado activo,
         * volumen objetivo configurado, y valor distinto al objetivo (RF03). */
        fun shouldRevert(lockActive: Boolean, target: Int?, newValue: Int): Boolean =
            lockActive && target != null && newValue != target

        private const val TAG = "VolumeLock"
        private const val CHANNEL_ID = "volumelock_status"
        private const val NOTIFICATION_ID = 1

        // Constantes no públicas de AudioManager, pero estables desde hace años.
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        private const val EXTRA_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
        private const val EXTRA_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE"
        private const val EXTRA_PREV_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE"

        fun start(context: Context) {
            val intent = Intent(context, VolumeForegroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, VolumeForegroundService::class.java))
        }
    }
}
