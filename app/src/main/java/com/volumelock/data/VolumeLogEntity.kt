package com.volumelock.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Un evento de cambio de volumen detectado (RF06). */
@Entity(tableName = "volume_log")
data class VolumeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val stream: String,
    val oldValue: Int,
    val newValue: Int,
    val reverted: Boolean,
)
