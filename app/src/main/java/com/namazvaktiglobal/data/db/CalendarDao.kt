package com.namazvaktiglobal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CalendarDao {
    @Query("SELECT * FROM prayer_calendar WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getCalendar(cacheKey: String): CalendarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(entity: CalendarEntity)
}
