package com.example.globalnamazvakti.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prayers: List<PrayerEntity>)

    @Query("SELECT * FROM prayers WHERE dateGregorian = :today")
    suspend fun getPrayerForDate(today: String): PrayerEntity?

    @Query("SELECT * FROM prayers")
    suspend fun getAll(): List<PrayerEntity>
}
