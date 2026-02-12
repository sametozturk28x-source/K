package com.example.globalnamazvakti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PrayerEntity::class], version = 1, exportSchema = false)
abstract class PrayerDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
}
