package com.volumelock.ui

import android.app.Application
import android.content.Intent
import android.media.AudioManager
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.volumelock.data.VolumeLogDatabase
import com.volumelock.data.VolumeLogEntity
import com.volumelock.data.VolumeRepository
import com.volumelock.data.VolumeStream
import com.volumelock.service.BubbleService
import com.volumelock.service.VolumeAccessibilityService
import com.volumelock.service.setStreamVolumeSafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VolumeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = VolumeRepository(app)
    private val audioManager = app.getSystemService(AudioManager::class.java)
    private val logDao = VolumeLogDatabase.get(app).volumeLogDao()

    val lockState: StateFlow<Boolean> =
        repository.lockState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val lockSince: StateFlow<Long?> =
        repository.lockSince.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val targets: StateFlow<Map<VolumeStream, Int>> =
        repository.targetVolumes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val history: Flow<List<VolumeLogEntity>> = logDao.getAll()

    val reactivateOnBoot: StateFlow<Boolean> =
        repository.reactivateOnBoot.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setReactivateOnBoot(enabled: Boolean) {
        viewModelScope.launch { repository.setReactivateOnBoot(enabled) }
    }

    val bubbleEnabled: StateFlow<Boolean> =
        repository.bubbleEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun canDrawOverlays(): Boolean =
        Settings.canDrawOverlays(getApplication())

    fun requestOverlayPermission() {
        launchSettings(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${getApplication<Application>().packageName}")
            )
        )
    }

    /** Activa/desactiva el globo flotante. Requiere permiso de overlay para activarlo. */
    fun setBubbleEnabled(enabled: Boolean) {
        val app = getApplication<Application>()
        if (enabled && !canDrawOverlays()) {
            requestOverlayPermission()
            return
        }
        viewModelScope.launch {
            repository.setBubbleEnabled(enabled)
            if (enabled) BubbleService.start(app) else BubbleService.stop(app)
        }
    }

    fun maxVolume(stream: VolumeStream): Int = audioManager.getStreamMaxVolume(stream.androidStreamType)
    fun currentVolume(stream: VolumeStream): Int = audioManager.getStreamVolume(stream.androidStreamType)

    fun toggleLock() {
        viewModelScope.launch { repository.setLockState(!lockState.value) }
    }

    fun setTargetVolume(stream: VolumeStream, value: Int) {
        viewModelScope.launch {
            repository.setTargetVolume(stream, value)
            if (lockState.value) audioManager.setStreamVolumeSafe(stream.androidStreamType, value, 0)
        }
    }

    /** Fija todos los objetivos al volumen actual del sistema. */
    fun fixToCurrent() {
        viewModelScope.launch {
            VolumeStream.entries.forEach { repository.setTargetVolume(it, currentVolume(it)) }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { logDao.clear() }
    }

    // --- Permisos ---

    fun isAccessibilityEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val name = "${getApplication<Application>().packageName}/${VolumeAccessibilityService::class.java.name}"
        return enabled.split(':').any { it.equals(name, ignoreCase = true) }
    }

    /** Acceso a la política de No molestar: necesario para fijar llamada/notificación. */
    fun isDndAccessGranted(): Boolean {
        val nm = getApplication<Application>().getSystemService(android.app.NotificationManager::class.java)
        return nm.isNotificationPolicyAccessGranted
    }

    /**
     * Abre la lista de "Acceso a No molestar" (mismo lugar para conceder o quitar).
     * Android no permite otorgar este acceso por código ni saltar directo al interruptor
     * de la app con una API pública, así que solo podemos abrir la lista; la guía en la
     * tarjeta le dice al usuario que busque VolumeLock y lo active.
     */
    fun openDndAccessSettings() {
        launchSettings(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    fun isBatteryUnrestricted(): Boolean {
        val pm = getApplication<Application>().getSystemService(PowerManager::class.java)
        return pm.isIgnoringBatteryOptimizations(getApplication<Application>().packageName)
    }

    fun openAccessibilitySettings() {
        launchSettings(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun requestIgnoreBatteryOptimizations() {
        // Si ya está sin restricciones, abre la lista para poder volver a restringir.
        if (isBatteryUnrestricted()) {
            launchSettings(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            return
        }
        launchSettings(
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                android.net.Uri.parse("package:${getApplication<Application>().packageName}")
            )
        )
    }

    private fun launchSettings(intent: Intent) {
        tryLaunch(intent)
    }

    /** Abre unos ajustes; devuelve false si el dispositivo no tiene esa pantalla. */
    private fun tryLaunch(intent: Intent): Boolean {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching { getApplication<Application>().startActivity(intent) }.isSuccess
    }
}
