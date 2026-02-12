package com.example.globalnamazvakti.data.remote

import com.squareup.moshi.Json

data class AladhanCalendarResponse(
    @Json(name = "data") val data: List<AladhanDay>
)

data class AladhanDay(
    @Json(name = "timings") val timings: AladhanTimings,
    @Json(name = "date") val date: AladhanDate
)

data class AladhanTimings(
    @Json(name = "Fajr") val fajr: String,
    @Json(name = "Sunrise") val sunrise: String,
    @Json(name = "Dhuhr") val dhuhr: String,
    @Json(name = "Asr") val asr: String,
    @Json(name = "Sunset") val sunset: String,
    @Json(name = "Maghrib") val maghrib: String,
    @Json(name = "Isha") val isha: String
)

data class AladhanDate(
    @Json(name = "gregorian") val gregorian: AladhanGregorian,
    @Json(name = "hijri") val hijri: AladhanHijri
)

data class AladhanGregorian(
    @Json(name = "date") val date: String
)

data class AladhanHijri(
    @Json(name = "date") val date: String
)
