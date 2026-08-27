package com.volumelock.ui

import android.app.Application
import android.media.AudioManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.volumelock.data.VolumeRepository
import com.volumelock.data.VolumeStream
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VolumeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = VolumeRepository(app)
    private val audioManager = app.getSystemService(AudioManager::class.java)

    val lockState: StateFlow<Boolean> =
        repository.lockState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val targets: StateFlow<Map<VolumeStream, Int>> =
        repository.targetVolumes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun maxVolume(stream: VolumeStream): Int =
        audioManager.getStreamMaxVolume(stream.androidStreamType)

    fun currentVolume(stream: VolumeStream): Int =
        audioManager.getStreamVolume(stream.androidStreamType)

    fun toggleLock() {
        viewModelScope.launch { repository.setLockState(!lockState.value) }
    }

    fun setTargetVolume(stream: VolumeStream, value: Int) {
        viewModelScope.launch {
            repository.setTargetVolume(stream, value)
            // Si el candado está activo, aplica el nuevo objetivo de inmediato.
            if (lockState.value) audioManager.setStreamVolume(stream.androidStreamType, value, 0)
        }
    }
}
