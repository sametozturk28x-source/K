package com.example.globalnamazvakti.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CompassUtils {
    fun lowPass(input: FloatArray, output: FloatArray?, alpha: Float = 0.8f): FloatArray {
        if (output == null) return input
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
        return output
    }

    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val kaabaLat = Math.toRadians(21.4225)
        val kaabaLon = Math.toRadians(39.8262)
        val userLat = Math.toRadians(latitude)
        val userLon = Math.toRadians(longitude)

        val dLon = kaabaLon - userLon
        val y = sin(dLon)
        val x = cos(userLat) * tan(kaabaLat) - sin(userLat) * cos(dLon)
        return (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
    }

    fun normalizeDegree(value: Float): Float {
        var angle = value
        while (angle < 0) angle += 360
        while (angle >= 360) angle -= 360
        return angle
    }

    private fun tan(value: Double): Double {
        return sin(value) / cos(value)
    }

    fun magnitude(values: FloatArray): Float {
        return sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2])
    }
}
