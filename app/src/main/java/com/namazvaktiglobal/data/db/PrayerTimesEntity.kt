package com.namazvaktiglobal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimesEntity(
    @PrimaryKey val cacheKey: String,
    val date: String,
    val timezone: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val hijriDate: String,
    val methodName: String,
    val updatedAt: Long
)
