package com.namazvaktiglobal.ui.viewmodel

import android.Manifest
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.namazvaktiglobal.data.preferences.SettingsDataStore
import com.namazvaktiglobal.domain.PrayerTimes
import com.namazvaktiglobal.location.LocationProvider
import com.namazvaktiglobal.location.LocationResult
import com.namazvaktiglobal.notifications.NotificationHelper
import com.namazvaktiglobal.repository.LocationChoice
import com.namazvaktiglobal.repository.PrayerTimesRepository
import com.namazvaktiglobal.workers.PrayerUpdateWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PrayerTimesRepository,
    private val settingsDataStore: SettingsDataStore,
    private val locationProvider: LocationProvider,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state

    private var countdownJob: Job? = null

    init {
        refresh()
        scheduleBackgroundWork()
        viewModelScope.launch {
            settingsDataStore.settingsFlow.collectLatest {
                refresh()
            }
        }
    }

    fun refresh() {
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
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Location permission required. Use manual location in settings."
                    )
                    return@launch
                }
                when (val result = locationProvider.getCurrentLocation()) {
                    is LocationResult.Success -> LocationChoice.Coordinates(result.latitude, result.longitude)
                    is LocationResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                        return@launch
                    }
                }
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.getTodayTimes(locationChoice, settings)
            if (result.isSuccess) {
                val prayerTimes = result.getOrThrow()
                val adjustedTimes = applyAdjustments(prayerTimes, settings.adjustments)
                _state.value = _state.value.copy(
                    isLoading = false,
                    prayerTimes = adjustedTimes,
                    locationLabel = locationChoice.label()
                )
                startCountdown(adjustedTimes)
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Unable to load prayer times"
                )
            }
        }
    }

    fun scheduleBackgroundWork() {
        val request = PeriodicWorkRequestBuilder<PrayerUpdateWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(getApplication())
            .enqueueUniquePeriodicWork("prayer_update", ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun sendTestNotification() {
        NotificationHelper(getApplication()).showTestNotification()
    }

    private fun applyAdjustments(prayerTimes: PrayerTimes, adjustments: Map<String, Int>): PrayerTimes {
        val updatedTimes = prayerTimes.times.mapValues { (name, time) ->
            val adjustmentMinutes = adjustments[name] ?: 0
            time.plusMinutes(adjustmentMinutes.toLong())
        }
        return prayerTimes.copy(times = updatedTimes)
    }

    private fun startCountdown(prayerTimes: PrayerTimes) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val zone = java.time.ZoneId.of(prayerTimes.timezone)
                val now = LocalTime.now(zone)
                val next = nextPrayer(prayerTimes, now)
                val nextTime = next?.second
                val countdown = if (nextTime != null) {
                    val today = LocalDate.now(zone)
                    val nextDateTime = LocalDateTime.of(today, nextTime)
                    val nowDateTime = LocalDateTime.now(zone)
                    val adjustedDateTime = if (nextDateTime.isBefore(nowDateTime)) {
                        nextDateTime.plusDays(1)
                    } else {
                        nextDateTime
                    }
                    java.time.Duration.between(nowDateTime, adjustedDateTime)
                        .toMinutes()
                        .coerceAtLeast(0)
                } else {
                    0
                }
                _state.value = _state.value.copy(
                    nextPrayerName = next?.first ?: "",
                    nextPrayerTime = nextTime?.toString() ?: "",
                    countdownMinutes = countdown
                )
                delay(1000L)
            }
        }
    }

    private fun nextPrayer(prayerTimes: PrayerTimes, now: LocalTime): Pair<String, LocalTime>? {
        return prayerTimes.times
            .toList()
            .sortedBy { it.second }
            .firstOrNull { it.second.isAfter(now) }
            ?: prayerTimes.times.toList().sortedBy { it.second }.firstOrNull()
    }
}

fun LocationChoice.label(): String = when (this) {
    is LocationChoice.Coordinates -> "Current location"
    is LocationChoice.Manual -> "$city, $country"
}

data class MainUiState(
    val isLoading: Boolean = false,
    val prayerTimes: PrayerTimes? = null,
    val locationLabel: String = "",
    val nextPrayerName: String = "",
    val nextPrayerTime: String = "",
    val countdownMinutes: Long = 0,
    val error: String? = null
)
