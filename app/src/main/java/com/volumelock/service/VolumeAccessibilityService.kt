package com.volumelock.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

/**
 * T1.1 — Servicio de accesibilidad mínimo: detecta y loguea las teclas físicas de
 * volumen SIN consumirlas todavía. El bloqueo real (consumir el evento) llega en T1.2.
 */
class VolumeAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AccessibilityService conectado")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val tecla = if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) "VOLUME_UP" else "VOLUME_DOWN"
                val accion = if (event.action == KeyEvent.ACTION_DOWN) "DOWN" else "UP"
                Log.d(TAG, "Tecla $tecla ($accion) detectada — NO consumida (T1.1)")
            }
        }
        // T1.1: no consumimos nada, el volumen debe seguir cambiando normal.
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No usado en esta fase.
    }

    override fun onInterrupt() {
        // No usado en esta fase.
    }

    companion object {
        private const val TAG = "VolumeLock"
    }
}
