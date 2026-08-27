package com.volumelock.data

import android.content.Context
import android.media.AudioManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Streams de audio que el candado puede fijar (RF05). */
enum class VolumeStream(val androidStreamType: Int) {
    MUSIC(AudioManager.STREAM_MUSIC),
    RING(AudioManager.STREAM_RING),
    NOTIFICATION(AudioManager.STREAM_NOTIFICATION),
    ALARM(AudioManager.STREAM_ALARM),
}

/**
 * Persistencia de configuración vía Jetpack DataStore (RF: estado del candado y
 * volumen objetivo por stream). Recibe el DataStore por constructor para poder
 * testearlo en JVM sin Context (RNF07: DI simple).
 */
class VolumeRepository(private val dataStore: DataStore<Preferences>) {

    /** Estado del candado. Por defecto false (desbloqueado). */
    val lockState: Flow<Boolean> = dataStore.data.map { it[LOCK_KEY] ?: false }

    /** Instante (epoch millis) en que se activó el candado, o null si está inactivo. */
    val lockSince: Flow<Long?> = dataStore.data.map { it[LOCK_SINCE_KEY] }

    /** Si el candado debe reactivarse tras reiniciar el dispositivo (HU07). */
    val reactivateOnBoot: Flow<Boolean> = dataStore.data.map { it[REACTIVATE_ON_BOOT_KEY] ?: false }

    suspend fun setReactivateOnBoot(enabled: Boolean) {
        dataStore.edit { it[REACTIVATE_ON_BOOT_KEY] = enabled }
    }

    /** Si el globo flotante de volumen está activo. */
    val bubbleEnabled: Flow<Boolean> = dataStore.data.map { it[BUBBLE_ENABLED_KEY] ?: false }

    suspend fun setBubbleEnabled(enabled: Boolean) {
        dataStore.edit { it[BUBBLE_ENABLED_KEY] = enabled }
    }

    suspend fun setLockState(active: Boolean) {
        dataStore.edit {
            it[LOCK_KEY] = active
            if (active) it[LOCK_SINCE_KEY] = System.currentTimeMillis() else it.remove(LOCK_SINCE_KEY)
        }
    }

    /** Volumen objetivo de un stream, o null si nunca se configuró. */
    fun targetVolume(stream: VolumeStream): Flow<Int?> =
        dataStore.data.map { it[targetKey(stream)] }

    suspend fun setTargetVolume(stream: VolumeStream, value: Int) {
        require(value >= 0) { "El volumen objetivo no puede ser negativo: $value" }
        dataStore.edit { it[targetKey(stream)] = value }
    }

    /** Mapa de los volúmenes objetivo configurados (solo los que tienen valor). */
    fun targetVolumes(): Flow<Map<VolumeStream, Int>> =
        dataStore.data.map { prefs ->
            VolumeStream.entries.mapNotNull { s -> prefs[targetKey(s)]?.let { s to it } }.toMap()
        }

    companion object {
        private val LOCK_KEY = booleanPreferencesKey("lock_active")
        private val LOCK_SINCE_KEY = androidx.datastore.preferences.core.longPreferencesKey("lock_since")
        private val REACTIVATE_ON_BOOT_KEY = booleanPreferencesKey("reactivate_on_boot")
        private val BUBBLE_ENABLED_KEY = booleanPreferencesKey("bubble_enabled")
        private fun targetKey(stream: VolumeStream) = intPreferencesKey("target_${stream.name}")
    }
}

/** DataStore de producción, ligado al Context de la app. */
private val Context.volumeDataStore by preferencesDataStore(name = "volumelock_prefs")

/** Factory de producción: crea el repositorio con el DataStore real de la app. */
fun VolumeRepository(context: Context): VolumeRepository =
    VolumeRepository(context.applicationContext.volumeDataStore)
