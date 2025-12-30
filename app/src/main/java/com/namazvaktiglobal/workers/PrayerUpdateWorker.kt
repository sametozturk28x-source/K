package com.namazvaktiglobal.workers

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.namazvaktiglobal.data.preferences.SettingsDataStore
import com.namazvaktiglobal.location.LocationProvider
import com.namazvaktiglobal.location.LocationResult
import com.namazvaktiglobal.notifications.NotificationHelper
import com.namazvaktiglobal.repository.LocationChoice
import com.namazvaktiglobal.repository.PrayerTimesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

@HiltWorker
class PrayerUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PrayerTimesRepository,
    private val settingsDataStore: SettingsDataStore,
    private val locationProvider: LocationProvider
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val settings = settingsDataStore.settingsFlow.first()
        val location = if (settings.useManualLocation && settings.manualCity.isNotBlank()) {
            LocationChoice.Manual(settings.manualCity, settings.manualCountry)
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return Result.retry()
            }
            when (val result = locationProvider.getCurrentLocation()) {
                is LocationResult.Success -> LocationChoice.Coordinates(result.latitude, result.longitude)
                is LocationResult.Error -> return Result.retry()
            }
        }

        val timesResult = repository.getTodayTimes(location, settings)
        if (timesResult.isFailure) {
            return Result.retry()
        }
        val times = timesResult.getOrThrow()
        val helper = NotificationHelper(context)
        val today = LocalDate.now()
        times.times.forEach { (name, time) ->
            if (settings.notifications[name] == true) {
                val dateTime = LocalDateTime.of(today, time)
                helper.schedulePrayerNotification(name, dateTime)
            }
        }
        return Result.success()
    }
}
