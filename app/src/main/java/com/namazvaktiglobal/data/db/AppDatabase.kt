package com.namazvaktiglobal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PrayerTimesEntity::class, CalendarEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun calendarDao(): CalendarDao
}
