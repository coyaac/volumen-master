package com.volumelock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.volumelock.data.VolumeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * T5.2 — Reactiva la protección tras reiniciar el dispositivo si el usuario lo configuró (HU07).
 * Si la opción está desactivada, deja el candado apagado por defecto.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        val appContext = context.applicationContext
        val repository = VolumeRepository(appContext)
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                if (repository.reactivateOnBoot.first()) {
                    VolumeForegroundService.start(appContext)
                } else {
                    repository.setLockState(false)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
