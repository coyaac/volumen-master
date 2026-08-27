package com.volumelock.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RingVolume
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.volumelock.data.VolumeStream

data class StreamUi(val icon: ImageVector, val label: String)

val STREAM_UI: Map<VolumeStream, StreamUi> = mapOf(
    VolumeStream.MUSIC to StreamUi(Icons.Rounded.VolumeUp, "Música"),
    VolumeStream.RING to StreamUi(Icons.Rounded.RingVolume, "Llamada"),
    VolumeStream.NOTIFICATION to StreamUi(Icons.Rounded.Notifications, "Notificación"),
    VolumeStream.ALARM to StreamUi(Icons.Rounded.Alarm, "Alarma"),
)

/** Icono + etiqueta para una entrada del log (cuyo stream es un String). */
fun streamUiForName(name: String): StreamUi {
    val vs = VolumeStream.entries.find { it.name == name }
    return vs?.let { STREAM_UI[it] } ?: StreamUi(Icons.Rounded.VolumeUp, name)
}
