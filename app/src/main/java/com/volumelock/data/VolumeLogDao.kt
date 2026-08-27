package com.volumelock.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VolumeLogDao {

    @Insert
    suspend fun insert(entry: VolumeLogEntity): Long

    /** Historial completo, más reciente primero, observable (RF07). */
    @Query("SELECT * FROM volume_log ORDER BY timestamp DESC")
    fun getAll(): Flow<List<VolumeLogEntity>>

    /** Rotación por antigüedad (RNF05). Devuelve cuántas filas borró. */
    @Query("DELETE FROM volume_log WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long): Int

    @Query("SELECT COUNT(*) FROM volume_log")
    suspend fun count(): Int

    /** Deja solo los `limit` registros más recientes. Devuelve cuántas filas borró. */
    @Query(
        "DELETE FROM volume_log WHERE id NOT IN " +
            "(SELECT id FROM volume_log ORDER BY timestamp DESC LIMIT :limit)"
    )
    suspend fun trimToLimit(limit: Int): Int

    /** Inserta y aplica el límite de registros en una sola transacción (RNF05). */
    @Transaction
    suspend fun insertAndTrim(entry: VolumeLogEntity, maxRecords: Int = MAX_RECORDS) {
        insert(entry)
        trimToLimit(maxRecords)
    }

    companion object {
        /** Límite de registros del historial (RNF05). */
        const val MAX_RECORDS = 5000
    }
}
