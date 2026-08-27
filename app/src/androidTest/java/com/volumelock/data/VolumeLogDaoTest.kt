package com.volumelock.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VolumeLogDaoTest {

    private lateinit var db: VolumeLogDatabase
    private lateinit var dao: VolumeLogDao

    private fun entry(timestamp: Long, reverted: Boolean = false) = VolumeLogEntity(
        timestamp = timestamp,
        stream = "STREAM_MUSIC",
        oldValue = 7,
        newValue = 5,
        reverted = reverted,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VolumeLogDatabase::class.java
        ).build()
        dao = db.volumeLogDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insert_y_lectura_por_flow() = runTest {
        dao.insert(entry(timestamp = 100, reverted = true))
        dao.getAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(100L, items[0].timestamp)
            assertEquals(true, items[0].reverted)
            assertEquals("STREAM_MUSIC", items[0].stream)
        }
    }

    @Test
    fun getAll_ordena_por_timestamp_descendente() = runTest {
        dao.insert(entry(timestamp = 100))
        dao.insert(entry(timestamp = 300))
        dao.insert(entry(timestamp = 200))
        val items = dao.getAll().first()
        assertEquals(listOf(300L, 200L, 100L), items.map { it.timestamp })
    }

    @Test
    fun deleteOlderThan_borra_solo_los_antiguos() = runTest {
        dao.insert(entry(timestamp = 100))
        dao.insert(entry(timestamp = 500))
        dao.insert(entry(timestamp = 1000))
        val borrados = dao.deleteOlderThan(cutoff = 500)
        assertEquals(1, borrados) // solo el de 100
        assertEquals(listOf(1000L, 500L), dao.getAll().first().map { it.timestamp })
    }

    @Test
    fun insertAndTrim_respeta_el_limite_de_registros() = runTest {
        repeat(5) { i -> dao.insertAndTrim(entry(timestamp = i.toLong()), maxRecords = 3) }
        val items = dao.getAll().first()
        assertEquals(3, items.size)
        // se quedan los 3 más recientes (timestamps 4,3,2)
        assertEquals(listOf(4L, 3L, 2L), items.map { it.timestamp })
    }
}
