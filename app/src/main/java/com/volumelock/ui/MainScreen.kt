package com.volumelock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Slider
import com.volumelock.data.VolumeStream

private val STREAM_LABELS = mapOf(
    VolumeStream.MUSIC to "Multimedia",
    VolumeStream.RING to "Llamada / Tono",
    VolumeStream.NOTIFICATION to "Notificaciones",
    VolumeStream.ALARM to "Alarma",
)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: VolumeViewModel = viewModel(),
) {
    val locked by viewModel.lockState.collectAsStateWithLifecycle()
    val targets by viewModel.targets.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LockCard(locked = locked, onToggle = viewModel::toggleLock)

        Text(
            text = "Volumen objetivo por canal",
            style = MaterialTheme.typography.titleMedium,
        )

        // Posición local de cada slider mientras se arrastra (se persiste al soltar).
        val dragging = remember { mutableStateMapOf<VolumeStream, Float>() }

        VolumeStream.entries.forEach { stream ->
            val max = viewModel.maxVolume(stream)
            val saved = targets[stream] ?: viewModel.currentVolume(stream)
            val value = dragging[stream] ?: saved.toFloat()
            StreamSlider(
                label = STREAM_LABELS[stream] ?: stream.name,
                value = value,
                max = max,
                onValueChange = { dragging[stream] = it },
                onValueChangeFinished = {
                    viewModel.setTargetVolume(stream, value.toInt())
                    dragging.remove(stream)
                },
            )
        }
    }
}

@Composable
private fun LockCard(locked: Boolean, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = if (locked) "🔒 Candado activo" else "🔓 Candado inactivo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (locked) "El volumen está protegido" else "Toca para proteger el volumen",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(checked = locked, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun StreamSlider(
    label: String,
    value: Float,
    max: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${value.toInt()} / $max", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..max.toFloat(),
            steps = (max - 1).coerceAtLeast(0),
        )
        Spacer(Modifier.height(4.dp))
    }
}
