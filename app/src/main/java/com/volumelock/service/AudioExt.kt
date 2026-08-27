package com.volumelock.service

import android.media.AudioManager
import android.util.Log

/**
 * Fija el volumen de un stream sin crashear. `setStreamVolume` lanza SecurityException
 * en STREAM_RING/STREAM_NOTIFICATION cuando el cambio afectaría el modo silencio/No
 * molestar y la app no tiene acceso a la política de notificaciones (DND). Devuelve
 * true si se aplicó, false si se ignoró por falta de permiso.
 */
fun AudioManager.setStreamVolumeSafe(streamType: Int, index: Int, flags: Int): Boolean =
    try {
        setStreamVolume(streamType, index, flags)
        true
    } catch (e: SecurityException) {
        Log.w("VolumeLock", "setStreamVolume(stream=$streamType) sin acceso a No molestar: ${e.message}")
        false
    }
