package com.namazvaktiglobal.domain

import java.time.LocalDate
import java.time.LocalTime


data class PrayerTimes(
    val date: LocalDate,
    val timezone: String,
    val hijriDate: String,
    val methodName: String,
    val times: Map<String, LocalTime>
)

fun parseTime(raw: String): LocalTime {
    val cleaned = raw.split(" ").firstOrNull() ?: raw
    return LocalTime.parse(cleaned)
}
