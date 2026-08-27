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
                // T1.2: si el candado está activo, consumimos el evento (return true)
                // y el cambio de volumen no llega al sistema.
                if (lockActive) {
                    Log.d(TAG, "Tecla $tecla ($accion) BLOQUEADA (lock activo)")
                    return true
                }
                Log.d(TAG, "Tecla $tecla ($accion) detectada — NO consumida (lock inactivo)")
            }
        }
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

        // Estado del candado. Por defecto false para no dejar el volumen bloqueado
        // tras instalar (aún no hay toggle de UI ni persistencia). En la Fase 2 se
        // conecta al estado real del candado (VolumeRepository / DataStore).
        // Bloqueo real ya verificado en Redmi Note 9 / MIUI con este flag en true.
        // ponytail: flag estático temporal, reemplazar por estado observable en T2.1.
        var lockActive: Boolean = false
    }
}
