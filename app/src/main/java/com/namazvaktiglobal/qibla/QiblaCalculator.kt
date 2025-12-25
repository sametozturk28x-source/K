package com.namazvaktiglobal.qibla

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object QiblaCalculator {
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LON = 39.8262

    fun calculateBearing(latitude: Double, longitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val lonRad = Math.toRadians(longitude)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLonRad = Math.toRadians(KAABA_LON)
        val dLon = kaabaLonRad - lonRad
        val y = sin(dLon)
        val x = cos(latRad) * tan(kaabaLatRad) - sin(latRad) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }
}
