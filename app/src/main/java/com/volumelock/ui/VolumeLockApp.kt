package com.volumelock.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class Tab(val label: String) { LOCK("Candado"), LOG("Historial"), SETTINGS("Ajustes") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeLockApp(viewModel: VolumeViewModel = viewModel()) {
    var tab by remember { mutableStateOf(Tab.LOCK) }

    // Reactiva el globo al abrir la app si quedó activado y hay permiso de overlay.
    val bubbleOn by viewModel.bubbleEnabled.collectAsStateWithLifecycle()
    LaunchedEffect(bubbleOn) {
        if (bubbleOn && viewModel.canDrawOverlays()) viewModel.setBubbleEnabled(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (tab) {
                            Tab.LOCK -> "VolumeLock"
                            Tab.LOG -> "Historial"
                            Tab.SETTINGS -> "Permisos"
                        }
                    )
                },
                actions = {
                    if (tab == Tab.LOG) {
                        IconButton(onClick = viewModel::clearHistory) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Borrar historial")
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == Tab.LOCK,
                    onClick = { tab = Tab.LOCK },
                    icon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    label = { Text(Tab.LOCK.label) },
                )
                NavigationBarItem(
                    selected = tab == Tab.LOG,
                    onClick = { tab = Tab.LOG },
                    icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                    label = { Text(Tab.LOG.label) },
                )
                NavigationBarItem(
                    selected = tab == Tab.SETTINGS,
                    onClick = { tab = Tab.SETTINGS },
                    icon = { Icon(Icons.Rounded.Tune, contentDescription = null) },
                    label = { Text(Tab.SETTINGS.label) },
                )
            }
        },
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (tab) {
            Tab.LOCK -> MainScreen(viewModel, contentModifier)
            Tab.LOG -> HistoryScreen(viewModel, contentModifier)
            Tab.SETTINGS -> PermissionsScreen(viewModel, contentModifier)
        }
    }
}
