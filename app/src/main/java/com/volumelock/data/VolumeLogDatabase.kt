package com.volumelock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VolumeLogEntity::class], version = 1)
abstract class VolumeLogDatabase : RoomDatabase() {

    abstract fun volumeLogDao(): VolumeLogDao

    companion object {
        @Volatile
        private var instance: VolumeLogDatabase? = null

        fun get(context: Context): VolumeLogDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    VolumeLogDatabase::class.java,
                    "volumelock.db"
                ).build().also { instance = it }
            }
    }
}
