package com.volumelock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volumelock.data.VolumeLogEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class Range(val label: String) { TODAY("Hoy"), WEEK("7 días"), ALL("Todo") }

private val ES = Locale("es", "ES")
private val TIME_FMT = SimpleDateFormat("HH:mm", ES)
private val DAY_FMT = SimpleDateFormat("EEEE, d 'de' MMMM", ES)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: VolumeViewModel, modifier: Modifier = Modifier) {
    val all by viewModel.history.collectAsStateWithLifecycle(initialValue = emptyList())
    var range by remember { mutableStateOf(Range.TODAY) }

    val cutoff = when (range) {
        Range.TODAY -> startOfToday()
        Range.WEEK -> System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        Range.ALL -> Long.MIN_VALUE
    }
    val entries = all.filter { it.timestamp >= cutoff }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Range.entries.forEachIndexed { i, r ->
                SegmentedButton(
                    selected = range == r,
                    onClick = { range = r },
                    shape = SegmentedButtonDefaults.itemShape(i, Range.entries.size),
                ) { Text(r.label) }
            }
        }

        if (entries.isEmpty()) {
            Text(
                "Sin cambios registrados en este periodo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            return@Column
        }

        // Agrupa por día (las entradas ya vienen ordenadas por timestamp desc).
        val groups = entries.groupBy { dayKey(it.timestamp) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            groups.forEach { (_, dayEntries) ->
                item {
                    Text(
                        text = "${DAY_FMT.format(Date(dayEntries.first().timestamp)).replaceFirstChar { it.uppercase() }} · ${dayEntries.size} cambios",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            dayEntries.forEach { LogRow(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogRow(entry: VolumeLogEntity) {
    val ui = streamUiForName(entry.stream)
    com.volumelock.ui.components.LogEntryRow(
        time = TIME_FMT.format(Date(entry.timestamp)),
        icon = ui.icon,
        streamLabel = ui.label,
        from = entry.oldValue,
        to = entry.newValue,
        reverted = entry.reverted,
    )
}

private fun startOfToday(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun dayKey(ts: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ts
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis
