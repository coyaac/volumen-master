package com.volumelock.service

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.volumelock.R
import com.volumelock.data.VolumeRepository
import com.volumelock.data.VolumeStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Globo flotante (estilo chat-head) para ver/ajustar volúmenes y el candado sobre
 * cualquier app. Semitransparente en reposo, se imanta al borde al soltarlo y se
 * borra arrastrándolo a la zona inferior central. Requiere permiso de overlay.
 */
class BubbleService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var windowManager: WindowManager
    private lateinit var audioManager: AudioManager
    private lateinit var repository: VolumeRepository

    private var bubbleView: ImageView? = null
    private var panelView: View? = null
    private var deleteView: View? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        repository = VolumeRepository(this)
        createChannel()
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(), fgsType())
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        showBubble()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun overlayType() = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

    private fun showBubble() {
        val bubble = ImageView(this).apply {
            setImageResource(R.drawable.ic_lock_tile)
            setBackgroundResource(R.drawable.bubble_bg)
            val p = dp(12)
            setPadding(p, p, p, p)
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            alpha = IDLE_ALPHA
        }
        bubbleParams = WindowManager.LayoutParams(
            dp(56), dp(56),
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = dp(200)
        }
        bubble.setOnTouchListener(dragListener())
        windowManager.addView(bubble, bubbleParams)
        bubbleView = bubble
    }

    private fun dragListener(): View.OnTouchListener {
        var initX = 0
        var initY = 0
        var touchX = 0f
        var touchY = 0f
        var moved = false
        return View.OnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    initX = bubbleParams.x; initY = bubbleParams.y
                    touchX = e.rawX; touchY = e.rawY; moved = false
                    wake()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - touchX).toInt()
                    val dy = (e.rawY - touchY).toInt()
                    if (abs(dx) + abs(dy) > dp(8)) {
                        if (!moved) showDeleteZone()
                        moved = true
                    }
                    if (moved) {
                        bubbleParams.x = initX + dx
                        bubbleParams.y = initY + dy
                        bubbleView?.let { windowManager.updateViewLayout(it, bubbleParams) }
                        bubbleView?.scaleX = if (overDelete(e.rawX, e.rawY)) 0.7f else 1f
                        bubbleView?.scaleY = bubbleView?.scaleX ?: 1f
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    hideDeleteZone()
                    bubbleView?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(120)?.start()
                    when {
                        !moved -> { togglePanel(); fadeToIdle() }
                        overDelete(e.rawX, e.rawY) -> deleteBubble()
                        else -> snapToEdge()
                    }
                    true
                }
                else -> false
            }
        }
    }

    // --- Animaciones de reposo / imán ---

    private fun wake() {
        bubbleView?.animate()?.alpha(1f)?.setDuration(120)?.start()
    }

    private fun fadeToIdle() {
        bubbleView?.animate()?.alpha(IDLE_ALPHA)?.setStartDelay(1200)?.setDuration(400)?.start()
    }

    private fun snapToEdge() {
        val screenW = resources.displayMetrics.widthPixels
        val targetX = if (bubbleParams.x + dp(28) < screenW / 2) 0 else screenW - dp(56)
        ValueAnimator.ofInt(bubbleParams.x, targetX).apply {
            duration = 260
            interpolator = DecelerateInterpolator()
            addUpdateListener { a ->
                bubbleParams.x = a.animatedValue as Int
                bubbleView?.let { runCatching { windowManager.updateViewLayout(it, bubbleParams) } }
            }
            start()
        }
        fadeToIdle()
    }

    // --- Zona de borrado ---

    private fun showDeleteZone() {
        if (deleteView != null) return
        val zone = TextView(this).apply {
            text = "✕"
            setTextColor(Color.WHITE)
            textSize = 22f
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.delete_bg)
        }
        val params = WindowManager.LayoutParams(
            dp(64), dp(64),
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = dp(64)
        }
        windowManager.addView(zone, params)
        zone.alpha = 0f
        zone.animate().alpha(1f).setDuration(150).start()
        deleteView = zone
    }

    private fun hideDeleteZone() {
        deleteView?.let { runCatching { windowManager.removeView(it) } }
        deleteView = null
    }

    /** True si el dedo está sobre la zona de borrado (parte inferior central). */
    private fun overDelete(rawX: Float, rawY: Float): Boolean {
        val screenW = resources.displayMetrics.widthPixels
        val screenH = resources.displayMetrics.heightPixels
        val cx = screenW / 2f
        val cy = screenH - dp(64) - dp(32)
        return hypot(rawX - cx, rawY - cy) < dp(90)
    }

    private fun deleteBubble() {
        scope.launch { repository.setBubbleEnabled(false) }
        stopSelf()
    }

    // --- Panel de volúmenes + candado ---

    private fun togglePanel() {
        if (panelView != null) { collapsePanel(); return }
        val panel = LayoutInflater.from(this).inflate(R.layout.bubble_panel, null)
        bindSlider(panel, R.id.sb_music, AudioManager.STREAM_MUSIC)
        bindSlider(panel, R.id.sb_ring, AudioManager.STREAM_RING)
        bindSlider(panel, R.id.sb_notif, AudioManager.STREAM_NOTIFICATION)
        bindSlider(panel, R.id.sb_alarm, AudioManager.STREAM_ALARM)
        bindLockSwitch(panel.findViewById(R.id.sw_lock))
        panel.findViewById<View>(R.id.panel_close).setOnClickListener { collapsePanel() }

        val screenW = resources.displayMetrics.widthPixels
        val params = WindowManager.LayoutParams(
            dp(300),
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = bubbleParams.x.coerceIn(0, (screenW - dp(300)).coerceAtLeast(0))
            y = bubbleParams.y + dp(64)
        }
        windowManager.addView(panel, params)
        panelView = panel
    }

    private fun collapsePanel() {
        panelView?.let { runCatching { windowManager.removeView(it) } }
        panelView = null
    }

    private fun bindLockSwitch(sw: Switch) {
        scope.launch {
            val locked = repository.lockState.first()
            sw.isChecked = locked
            sw.setOnCheckedChangeListener { _, checked ->
                scope.launch {
                    repository.setLockState(checked)
                    if (checked) VolumeForegroundService.start(this@BubbleService)
                }
            }
        }
    }

    private fun bindSlider(panel: View, seekBarId: Int, stream: Int) {
        val sb = panel.findViewById<SeekBar>(seekBarId)
        val vs = VolumeStream.entries.find { it.androidStreamType == stream }
        sb.max = audioManager.getStreamMaxVolume(stream)
        sb.progress = audioManager.getStreamVolume(stream)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                // Actualiza el objetivo ANTES de aplicar el volumen: con el candado activo,
                // el servicio de restauración ve el nuevo objetivo y no revierte el cambio
                // (si no, se produce el "amague" de subir y volver al valor anterior).
                if (vs != null) scope.launch {
                    repository.setTargetVolume(vs, progress)
                    audioManager.setStreamVolumeSafe(stream, progress, 0)
                } else {
                    audioManager.setStreamVolumeSafe(stream, progress, 0)
                }
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    // --- Notificación / lifecycle ---

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Globo de volumen", NotificationManager.IMPORTANCE_MIN)
            )
        }
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Globo de volumen activo")
            .setSmallIcon(R.drawable.ic_lock_tile)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

    private fun fgsType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        else 0

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        collapsePanel()
        hideDeleteZone()
        bubbleView?.let { runCatching { windowManager.removeView(it) } }
        bubbleView = null
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "volumelock_bubble"
        private const val NOTIFICATION_ID = 2
        private const val IDLE_ALPHA = 0.45f

        fun start(context: Context) =
            context.startForegroundService(Intent(context, BubbleService::class.java))

        fun stop(context: Context) =
            context.stopService(Intent(context, BubbleService::class.java))
    }
}
