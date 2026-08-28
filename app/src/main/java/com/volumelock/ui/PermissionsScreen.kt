package com.volumelock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volumelock.ui.components.Banner

@Composable
fun PermissionsScreen(viewModel: VolumeViewModel, modifier: Modifier = Modifier) {
    var accessibility by remember { mutableStateOf(viewModel.isAccessibilityEnabled()) }
    var battery by remember { mutableStateOf(viewModel.isBatteryUnrestricted()) }
    var dnd by remember { mutableStateOf(viewModel.isDndAccessGranted()) }
    val reactivateOnBoot by viewModel.reactivateOnBoot.collectAsStateWithLifecycle()
    val bubbleEnabled by viewModel.bubbleEnabled.collectAsStateWithLifecycle()

    // Al volver de Ajustes, re-evalúa el estado para que los avisos se resuelvan solos.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        accessibility = viewModel.isAccessibilityEnabled()
        battery = viewModel.isBatteryUnrestricted()
        dnd = viewModel.isDndAccessGranted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Dos permisos y listo", style = MaterialTheme.typography.headlineSmall)
        Text(
            "VolumeLock necesita observar el volumen del sistema y seguir despierto en segundo plano. Los avisos desaparecen cuando los resuelves.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Banner(
            title = if (accessibility) "Permiso de Accesibilidad activo" else "Falta el permiso de Accesibilidad",
            body = if (accessibility) "VolumeLock puede detectar y bloquear los cambios de volumen." else "Sin este permiso, VolumeLock no puede detectar los cambios de volumen. El botón te lleva a la lista: busca VolumeLock y actívalo.",
            resolved = accessibility,
            icon = Icons.Rounded.AccessibilityNew,
            actionLabel = if (accessibility) "Quitar acceso" else "Activar accesibilidad",
            onAction = viewModel::openAccessibilitySettings,
        )

        Banner(
            title = if (battery) "Optimización de batería desactivada" else "Optimización de batería activa",
            body = if (battery) "VolumeLock seguirá vigilando con la pantalla apagada." else "Márcala como sin restricciones para que el sistema no cierre el servicio.",
            resolved = battery,
            icon = Icons.Rounded.BatterySaver,
            actionLabel = if (battery) "Volver a restringir" else "Quitar restricción",
            onAction = viewModel::requestIgnoreBatteryOptimizations,
        )

        Banner(
            title = if (dnd) "Acceso a No molestar concedido" else "Falta acceso a No molestar",
            body = if (dnd) "VolumeLock puede fijar el volumen de llamada y notificación. El botón abre la lista por si quieres quitarlo." else "Sin este acceso no se puede fijar llamada ni notificación (multimedia y alarma sí). El botón abre la lista: busca VolumeLock y actívalo.",
            resolved = dnd,
            icon = Icons.Rounded.DoNotDisturbOn,
            actionLabel = if (dnd) "Quitar acceso" else "Activar No molestar",
            onAction = viewModel::openDndAccessSettings,
        )

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    Text("Reactivar al reiniciar", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "El candado vuelve a su estado tras encender el móvil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = reactivateOnBoot, onCheckedChange = viewModel::setReactivateOnBoot)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Adjust, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    Text("Globo flotante de volumen", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Un globo sobre las demás apps para ajustar el volumen sin abrir VolumeLock.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = bubbleEnabled, onCheckedChange = viewModel::setBubbleEnabled)
            }
        }
    }
}
