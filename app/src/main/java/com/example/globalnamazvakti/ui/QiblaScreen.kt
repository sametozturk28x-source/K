package com.example.globalnamazvakti.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.example.globalnamazvakti.util.CompassUtils

@Composable
fun QiblaScreen(latitude: Double, longitude: Double) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    val qiblaBearing = CompassUtils.calculateQiblaBearing(latitude, longitude).toFloat()

    DisposableEffect(context) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        CompassUtils.lowPass(event.values.clone(), gravity)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        CompassUtils.lowPass(event.values.clone(), geomagnetic)
                    }
                }
                val rotationMatrix = FloatArray(9)
                val orientation = FloatArray(3)
                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthRadians = orientation[0]
                    azimuth = CompassUtils.normalizeDegree(Math.toDegrees(azimuthRadians.toDouble()).toFloat())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        manager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            manager.unregisterListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Kaaba",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.graphicsLayer {
                rotationZ = qiblaBearing - azimuth
            }
        )
    }
}
