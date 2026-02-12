package com.example.globalnamazvakti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey val dateGregorian: String,
    val dateHijri: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val sunset: String,
    val maghrib: String,
    val isha: String
)
