package com.namazvaktiglobal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiTimingsResponse(
    val code: Int,
    val status: String,
    val data: TimingsData
)

@Serializable
data class ApiCalendarResponse(
    val code: Int,
    val status: String,
    val data: List<TimingsData>
)

@Serializable
data class TimingsData(
    val timings: Timings,
    val date: DateInfo,
    val meta: MetaInfo
)

@Serializable
data class Timings(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Sunrise") val sunrise: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)

@Serializable
data class DateInfo(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDate
)

@Serializable
data class HijriDate(
    val date: String,
    val day: String,
    val month: HijriMonth,
    val year: String
)

@Serializable
data class HijriMonth(
    val number: Int,
    val en: String
)

@Serializable
data class MetaInfo(
    val timezone: String,
    val method: MethodInfo
)

@Serializable
data class MethodInfo(
    val id: Int,
    val name: String
)
