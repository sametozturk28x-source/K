package com.example.globalnamazvakti.domain

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

enum class CalculationMethod(val methodId: Int, val displayName: String) {
    MUSLIM_WORLD_LEAGUE(3, "Muslim World League"),
    ISNA(2, "ISNA"),
    EGYPT(5, "Egyptian"),
    UMM_AL_QURA(4, "Umm al-Qura"),
    DIYANET(13, "Diyanet"),
    TEHRAN(7, "University of Tehran")
}

enum class AsrMethod {
    STANDARD,
    HANAFI
}

enum class HighLatitudeRule {
    ANGLE_BASED,
    MIDNIGHT,
    ONE_SEVENTH
}

data class PrayerTimes(
    val dateGregorian: String,
    val dateHijri: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val sunset: String,
    val maghrib: String,
    val isha: String
)
