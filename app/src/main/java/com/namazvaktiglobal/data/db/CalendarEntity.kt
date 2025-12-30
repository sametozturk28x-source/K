package com.namazvaktiglobal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_calendar")
data class CalendarEntity(
    @PrimaryKey val cacheKey: String,
    val month: Int,
    val year: Int,
    val payloadJson: String,
    val updatedAt: Long
)
