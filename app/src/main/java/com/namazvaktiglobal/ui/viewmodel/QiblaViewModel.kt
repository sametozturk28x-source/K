package com.namazvaktiglobal.ui.viewmodel

import android.Manifest
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.namazvaktiglobal.location.LocationProvider
import com.namazvaktiglobal.location.LocationResult
import com.namazvaktiglobal.qibla.CompassSensor
import com.namazvaktiglobal.qibla.QiblaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val compassSensor: CompassSensor,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(QiblaUiState())
    val state: StateFlow<QiblaUiState> = _state

    init {
        _state.value = _state.value.copy(sensorAvailable = compassSensor.hasSensor)
        viewModelScope.launch {
            compassSensor.azimuthFlow().collect { azimuth ->
                _state.value = _state.value.copy(deviceAzimuth = azimuth)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val hasPermission = ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                _state.value = _state.value.copy(error = "Location permission required for Qibla direction.")
                return@launch
            }
            when (val result = locationProvider.getCurrentLocation()) {
                is LocationResult.Success -> {
                    val bearing = QiblaCalculator.calculateBearing(result.latitude, result.longitude)
                    _state.value = QiblaUiState(
                        bearing = bearing,
                        locationLabel = "${result.latitude}, ${result.longitude}"
                    )
                }
                is LocationResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
            }
        }

    }
}

data class QiblaUiState(
    val bearing: Double = 0.0,
    val deviceAzimuth: Float = 0f,
    val sensorAvailable: Boolean = false,
    val locationLabel: String = "",
    val error: String? = null
)
