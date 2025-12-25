package com.namazvaktiglobal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerTimesDao {
    @Query("SELECT * FROM prayer_times WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getPrayerTimes(cacheKey: String): PrayerTimesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(entity: PrayerTimesEntity)
}
