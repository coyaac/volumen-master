package com.volumelock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File

class VolumeRepositoryTest {

    @TempDir
    lateinit var tmpDir: File

    private fun newDataStore(scope: CoroutineScope): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = scope) {
            File(tmpDir, "test.preferences_pb")
        }

    @Test
    fun `lockState por defecto es false`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        assertEquals(false, repo.lockState.first())
    }

    @Test
    fun `setLockState persiste el estado`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        repo.setLockState(true)
        assertEquals(true, repo.lockState.first())
        repo.setLockState(false)
        assertEquals(false, repo.lockState.first())
    }

    @Test
    fun `targetVolume es null si nunca se configuro`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        assertNull(repo.targetVolume(VolumeStream.MUSIC).first())
    }

    @Test
    fun `setTargetVolume guarda por stream de forma independiente`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        repo.setTargetVolume(VolumeStream.MUSIC, 7)
        repo.setTargetVolume(VolumeStream.RING, 3)
        assertEquals(7, repo.targetVolume(VolumeStream.MUSIC).first())
        assertEquals(3, repo.targetVolume(VolumeStream.RING).first())
        assertNull(repo.targetVolume(VolumeStream.ALARM).first())
    }

    @Test
    fun `targetVolumes devuelve solo los configurados`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        repo.setTargetVolume(VolumeStream.MUSIC, 5)
        repo.setTargetVolume(VolumeStream.NOTIFICATION, 2)
        assertEquals(
            mapOf(VolumeStream.MUSIC to 5, VolumeStream.NOTIFICATION to 2),
            repo.targetVolumes().first()
        )
    }

    @Test
    fun `setTargetVolume rechaza valores negativos`() = runTest {
        val repo = VolumeRepository(newDataStore(backgroundScope))
        assertThrows<IllegalArgumentException> {
            repo.setTargetVolume(VolumeStream.MUSIC, -1)
        }
    }

    @Test
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `los valores sobreviven a reabrir el DataStore (persistencia real)`() = runTest {
        // Un scope propio para el primer DataStore; el mismo archivo se reabre después.
        val scope1 = CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher(testScheduler))
        VolumeRepository(newDataStore(scope1)).apply {
            setLockState(true)
            setTargetVolume(VolumeStream.ALARM, 6)
        }
        scope1.coroutineContext[kotlinx.coroutines.Job]!!.cancel()

        // Nueva instancia sobre el mismo archivo = simula reinicio de la app.
        val repo2 = VolumeRepository(newDataStore(backgroundScope))
        assertEquals(true, repo2.lockState.first())
        assertEquals(6, repo2.targetVolume(VolumeStream.ALARM).first())
    }
}
