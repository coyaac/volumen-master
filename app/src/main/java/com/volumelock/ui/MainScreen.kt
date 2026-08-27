package com.volumelock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volumelock.data.VolumeStream
import com.volumelock.ui.components.LockHero
import com.volumelock.ui.components.StatusBadge
import com.volumelock.ui.components.VolumeSliderRow
import com.volumelock.ui.theme.LocalVlColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(viewModel: VolumeViewModel, modifier: Modifier = Modifier) {
    val locked by viewModel.lockState.collectAsStateWithLifecycle()
    val since by viewModel.lockSince.collectAsStateWithLifecycle()
    val targets by viewModel.targets.collectAsStateWithLifecycle()
    val vl = LocalVlColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LockHero(
            locked = locked,
            since = since?.let { SimpleDateFormat("HH:mm", Locale("es", "ES")).format(Date(it)) },
            onToggle = viewModel::toggleLock,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (locked) {
                StatusBadge(vl.lockedContainer, vl.onLockedContainer, Icons.Rounded.Lock, "${targets.size} canales fijos")
            } else {
                StatusBadge(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Icons.Rounded.LockOpen,
                    "Sin vigilancia",
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Volumen objetivo", style = MaterialTheme.typography.titleMedium)

                val dragging = remember { mutableStateMapOf<VolumeStream, Float>() }
                VolumeStream.entries.forEach { stream ->
                    val ui = STREAM_UI.getValue(stream)
                    val max = viewModel.maxVolume(stream)
                    val saved = targets[stream] ?: viewModel.currentVolume(stream)
                    val value = dragging[stream] ?: saved.toFloat()
                    VolumeSliderRow(
                        icon = ui.icon,
                        label = ui.label,
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

        OutlinedButton(onClick = viewModel::fixToCurrent, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Tune, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Fijar con el volumen actual", textAlign = TextAlign.Center)
        }
    }
}
