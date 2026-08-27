package com.volumelock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
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
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.volumelock.R
import kotlin.math.abs

/**
 * Globo flotante (estilo chat-head) para ver y ajustar los volúmenes sobre cualquier
 * app. Requiere permiso "mostrar sobre otras apps" (SYSTEM_ALERT_WINDOW).
 */
class BubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var audioManager: AudioManager
    private var bubbleView: View? = null
    private var panelView: View? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createChannel()
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(), fgsType())
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        showBubble()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun overlayType() =
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

    private fun showBubble() {
        val bubble = ImageView(this).apply {
            setImageResource(R.drawable.ic_lock_tile)
            setBackgroundResource(R.drawable.bubble_bg)
            val p = dp(12)
            setPadding(p, p, p, p)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
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
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - touchX).toInt()
                    val dy = (e.rawY - touchY).toInt()
                    if (abs(dx) + abs(dy) > dp(8)) moved = true
                    bubbleParams.x = initX + dx
                    bubbleParams.y = initY + dy
                    bubbleView?.let { windowManager.updateViewLayout(it, bubbleParams) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) togglePanel()
                    true
                }
                else -> false
            }
        }
    }

    private fun togglePanel() {
        if (panelView != null) { collapsePanel(); return }
        val panel = LayoutInflater.from(this).inflate(R.layout.bubble_panel, null)
        bindSlider(panel, R.id.sb_music, AudioManager.STREAM_MUSIC)
        bindSlider(panel, R.id.sb_ring, AudioManager.STREAM_RING)
        bindSlider(panel, R.id.sb_notif, AudioManager.STREAM_NOTIFICATION)
        bindSlider(panel, R.id.sb_alarm, AudioManager.STREAM_ALARM)
        panel.findViewById<View>(R.id.panel_close).setOnClickListener { collapsePanel() }

        val params = WindowManager.LayoutParams(
            dp(300),
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // Mantiene el panel dentro de la pantalla aunque el globo esté en el borde.
            x = bubbleParams.x.coerceIn(0, (resources.displayMetrics.widthPixels - dp(300)).coerceAtLeast(0))
            y = bubbleParams.y + dp(64)
        }
        windowManager.addView(panel, params)
        panelView = panel
    }

    private fun collapsePanel() {
        panelView?.let { windowManager.removeView(it) }
        panelView = null
    }

    private fun bindSlider(panel: View, seekBarId: Int, stream: Int) {
        val sb = panel.findViewById<SeekBar>(seekBarId)
        sb.max = audioManager.getStreamMaxVolume(stream)
        sb.progress = audioManager.getStreamVolume(stream)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) audioManager.setStreamVolumeSafe(stream, progress, 0)
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

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
        bubbleView?.let { runCatching { windowManager.removeView(it) } }
        bubbleView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "volumelock_bubble"
        private const val NOTIFICATION_ID = 2

        fun start(context: Context) =
            context.startForegroundService(Intent(context, BubbleService::class.java))

        fun stop(context: Context) =
            context.stopService(Intent(context, BubbleService::class.java))
    }
}
