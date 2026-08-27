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
import com.volumelock.service.VolumeAccessibilityService
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

    fun maxVolume(stream: VolumeStream): Int = audioManager.getStreamMaxVolume(stream.androidStreamType)
    fun currentVolume(stream: VolumeStream): Int = audioManager.getStreamVolume(stream.androidStreamType)

    fun toggleLock() {
        viewModelScope.launch { repository.setLockState(!lockState.value) }
    }

    fun setTargetVolume(stream: VolumeStream, value: Int) {
        viewModelScope.launch {
            repository.setTargetVolume(stream, value)
            if (lockState.value) audioManager.setStreamVolume(stream.androidStreamType, value, 0)
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

    fun isBatteryUnrestricted(): Boolean {
        val pm = getApplication<Application>().getSystemService(PowerManager::class.java)
        return pm.isIgnoringBatteryOptimizations(getApplication<Application>().packageName)
    }

    fun openAccessibilitySettings() {
        launchSettings(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun requestIgnoreBatteryOptimizations() {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            android.net.Uri.parse("package:${getApplication<Application>().packageName}")
        )
        launchSettings(intent)
    }

    private fun launchSettings(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }
}
