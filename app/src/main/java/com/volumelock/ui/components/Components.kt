package com.volumelock.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.volumelock.ui.theme.LocalVlColors
import com.volumelock.ui.theme.MonoTextStyle

/** Bloque de estado que encabeza la pantalla principal. Único elemento que cambia de color. */
@Composable
fun LockHero(locked: Boolean, since: String?, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val vl = LocalVlColors.current
    val bg by animateColorAsState(
        if (locked) vl.lockedContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(300), label = "heroBg"
    )
    val fg by animateColorAsState(
        if (locked) vl.onLockedContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300), label = "heroFg"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(28.dp))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = if (locked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(40.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (locked) "Bloqueado" else "Desbloqueado",
                style = MaterialTheme.typography.headlineSmall,
                color = fg,
            )
            Text(
                text = when {
                    locked && since != null -> "Activo desde las $since"
                    locked -> "El volumen está fijo"
                    else -> "El volumen puede cambiar"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = fg.copy(alpha = 0.85f),
            )
        }
        Switch(
            checked = locked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = vl.onLocked,
                checkedTrackColor = vl.locked,
            ),
        )
    }
}

/** Píldora de estado / conteo. */
@Composable
fun StatusBadge(bg: Color, fg: Color, icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}

/** Fila slider de volumen objetivo por stream. */
@Composable
fun VolumeSliderRow(
    icon: ImageVector,
    label: String,
    value: Float,
    max: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = 0f..max.toFloat(),
                steps = (max - 1).coerceAtLeast(0),
                colors = SliderDefaults.colors(),
                modifier = Modifier.height(48.dp),
            )
        }
        Text(
            text = "${value.toInt()} / $max",
            style = MaterialTheme.typography.bodyLarge.merge(MonoTextStyle),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 56.dp),
        )
    }
}

/** Fila del historial de cambios. */
@Composable
fun LogEntryRow(
    time: String,
    icon: ImageVector,
    streamLabel: String,
    from: Int,
    to: Int,
    reverted: Boolean,
) {
    val vl = LocalVlColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            time,
            style = MaterialTheme.typography.bodySmall.merge(MonoTextStyle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(44.dp),
        )
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(streamLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("$from → $to", style = MaterialTheme.typography.bodyMedium.merge(MonoTextStyle), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                if (reverted) Icons.Rounded.Undo else Icons.Rounded.Remove,
                contentDescription = null,
                tint = if (reverted) vl.locked else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Text(
                if (reverted) "Revertido" else "Permitido",
                style = MaterialTheme.typography.labelMedium,
                color = if (reverted) vl.locked else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Aviso de permiso (warning / resuelto). */
@Composable
fun Banner(
    title: String,
    body: String,
    resolved: Boolean,
    icon: ImageVector,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val vl = LocalVlColors.current
    val bg = if (resolved) vl.lockedContainer else MaterialTheme.colorScheme.errorContainer
    val fg = if (resolved) vl.onLockedContainer else MaterialTheme.colorScheme.onErrorContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(if (resolved) Icons.Rounded.CheckCircle else icon, contentDescription = null, tint = fg, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = fg)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = fg, modifier = Modifier.padding(top = 4.dp))
            if (!resolved && actionLabel != null && onAction != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}
