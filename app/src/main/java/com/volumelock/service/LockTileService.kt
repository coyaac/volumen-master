package com.volumelock.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.volumelock.R
import com.volumelock.data.VolumeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * T5.1 — Quick Settings Tile: activa/desactiva el candado desde el panel rápido (HU03).
 * El estado se comparte con la app vía DataStore (misma fuente de verdad).
 */
class LockTileService : TileService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val repository by lazy { VolumeRepository(this) }

    override fun onStartListening() {
        scope.launch { render(repository.lockState.first()) }
    }

    override fun onClick() {
        scope.launch {
            val newState = !repository.lockState.first()
            repository.setLockState(newState)
            // Asegura el servicio de protección en marcha al bloquear desde el tile.
            if (newState) VolumeForegroundService.start(this@LockTileService)
            render(newState)
        }
    }

    private fun render(locked: Boolean) {
        qsTile?.apply {
            state = if (locked) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = getString(R.string.app_name)
            contentDescription = if (locked) "Bloqueado" else "Desbloqueado"
            icon = Icon.createWithResource(this@LockTileService, R.drawable.ic_lock_tile)
            updateTile()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
