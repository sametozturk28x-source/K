package com.namazvaktiglobal.ui.viewmodel

import android.Manifest
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.namazvaktiglobal.data.model.TimingsData
import com.namazvaktiglobal.data.preferences.SettingsDataStore
import com.namazvaktiglobal.location.LocationProvider
import com.namazvaktiglobal.location.LocationResult
import com.namazvaktiglobal.repository.LocationChoice
import com.namazvaktiglobal.repository.PrayerTimesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: PrayerTimesRepository,
    private val settingsDataStore: SettingsDataStore,
    private val locationProvider: LocationProvider,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state

    fun loadMonth(month: Int = LocalDate.now().monthValue, year: Int = LocalDate.now().year) {
        viewModelScope.launch {
            val settings = settingsDataStore.settingsFlow.first()
            val locationChoice = if (settings.useManualLocation && settings.manualCity.isNotBlank()) {
                LocationChoice.Manual(settings.manualCity, settings.manualCountry)
            } else {
                val hasPermission = ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                if (!hasPermission) {
                    _state.value = _state.value.copy(error = "Location permission required.")
                    return@launch
                }
                when (val result = locationProvider.getCurrentLocation()) {
                    is LocationResult.Success -> LocationChoice.Coordinates(result.latitude, result.longitude)
                    is LocationResult.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                        return@launch
                    }
                }
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.getMonthlyCalendar(locationChoice, settings, month, year)
            if (result.isSuccess) {
                _state.value = _state.value.copy(isLoading = false, days = result.getOrThrow().data)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
            }
        }
    }
}

data class CalendarUiState(
    val isLoading: Boolean = false,
    val days: List<TimingsData> = emptyList(),
    val error: String? = null
)
