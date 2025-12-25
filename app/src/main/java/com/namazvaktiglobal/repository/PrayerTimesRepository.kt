package com.namazvaktiglobal.repository

import com.namazvaktiglobal.data.api.PrayerTimesApi
import com.namazvaktiglobal.data.db.CalendarDao
import com.namazvaktiglobal.data.db.CalendarEntity
import com.namazvaktiglobal.data.db.PrayerTimesDao
import com.namazvaktiglobal.data.db.PrayerTimesEntity
import com.namazvaktiglobal.data.model.ApiCalendarResponse
import com.namazvaktiglobal.data.model.ApiTimingsResponse
import com.namazvaktiglobal.data.model.TimingsData
import com.namazvaktiglobal.data.preferences.AppSettings
import com.namazvaktiglobal.domain.PrayerTimes
import com.namazvaktiglobal.domain.parseTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class PrayerTimesRepository(
    private val api: PrayerTimesApi,
    private val prayerTimesDao: PrayerTimesDao,
    private val calendarDao: CalendarDao,
    private val json: Json
) {
    private val cacheTtlMs = 12 * 60 * 60 * 1000L

    suspend fun getTodayTimes(
        location: LocationChoice,
        settings: AppSettings
    ): Result<PrayerTimes> = withContext(Dispatchers.IO) {
        val cacheKey = buildCacheKey(location, settings)
        val cached = prayerTimesDao.getPrayerTimes(cacheKey)
        if (cached != null && !isExpired(cached.updatedAt)) {
            return@withContext Result.success(cached.toDomain())
        }
        runCatching {
            val response = when (location) {
                is LocationChoice.Coordinates -> api.getTimingsByCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    method = settings.calculationMethod,
                    school = settings.asrMethod,
                    hijriAdjustment = settings.hijriAdjustment
                )
                is LocationChoice.Manual -> api.getTimingsByCity(
                    city = location.city,
                    country = location.country,
                    method = settings.calculationMethod,
                    school = settings.asrMethod,
                    hijriAdjustment = settings.hijriAdjustment
                )
            }
            val entity = response.data.toEntity(cacheKey)
            prayerTimesDao.insertPrayerTimes(entity)
            entity.toDomain()
        }.recoverCatching { error ->
            if (cached != null) {
                cached.toDomain()
            } else {
                throw error
            }
        }
    }

    suspend fun getMonthlyCalendar(
        location: LocationChoice,
        settings: AppSettings,
        month: Int,
        year: Int
    ): Result<ApiCalendarResponse> = withContext(Dispatchers.IO) {
        val cacheKey = buildCalendarKey(location, settings, month, year)
        val cached = calendarDao.getCalendar(cacheKey)
        if (cached != null && !isExpired(cached.updatedAt)) {
            return@withContext Result.success(json.decodeFromString(ApiCalendarResponse.serializer(), cached.payloadJson))
        }
        runCatching {
            val response = when (location) {
                is LocationChoice.Coordinates -> api.getCalendarByCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    month = month,
                    year = year,
                    method = settings.calculationMethod,
                    school = settings.asrMethod,
                    hijriAdjustment = settings.hijriAdjustment
                )
                is LocationChoice.Manual -> api.getCalendarByCity(
                    city = location.city,
                    country = location.country,
                    month = month,
                    year = year,
                    method = settings.calculationMethod,
                    school = settings.asrMethod,
                    hijriAdjustment = settings.hijriAdjustment
                )
            }
            val payloadJson = json.encodeToString(ApiCalendarResponse.serializer(), response)
            calendarDao.insertCalendar(
                CalendarEntity(
                    cacheKey = cacheKey,
                    month = month,
                    year = year,
                    payloadJson = payloadJson,
                    updatedAt = System.currentTimeMillis()
                )
            )
            response
        }.recoverCatching { error ->
            if (cached != null) {
                json.decodeFromString(ApiCalendarResponse.serializer(), cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    private fun isExpired(updatedAt: Long): Boolean = System.currentTimeMillis() - updatedAt > cacheTtlMs

    private fun buildCacheKey(location: LocationChoice, settings: AppSettings): String {
        return "${location.cacheKey()}_${settings.calculationMethod}_${settings.asrMethod}_${settings.hijriAdjustment}"
    }

    private fun buildCalendarKey(location: LocationChoice, settings: AppSettings, month: Int, year: Int): String {
        return "calendar_${buildCacheKey(location, settings)}_${month}_${year}"
    }

    private fun TimingsData.toEntity(cacheKey: String): PrayerTimesEntity {
        return PrayerTimesEntity(
            cacheKey = cacheKey,
            date = date.readable,
            timezone = meta.timezone,
            fajr = timings.fajr,
            sunrise = timings.sunrise,
            dhuhr = timings.dhuhr,
            asr = timings.asr,
            maghrib = timings.maghrib,
            isha = timings.isha,
            hijriDate = date.hijri.date,
            methodName = meta.method.name,
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun PrayerTimesEntity.toDomain(): PrayerTimes {
        val localDate = LocalDate.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault())
        return PrayerTimes(
            date = localDate,
            timezone = timezone,
            hijriDate = hijriDate,
            methodName = methodName,
            times = mapOf(
                "Fajr" to parseTime(fajr),
                "Sunrise" to parseTime(sunrise),
                "Dhuhr" to parseTime(dhuhr),
                "Asr" to parseTime(asr),
                "Maghrib" to parseTime(maghrib),
                "Isha" to parseTime(isha)
            )
        )
    }
}

sealed class LocationChoice {
    data class Coordinates(val latitude: Double, val longitude: Double) : LocationChoice()
    data class Manual(val city: String, val country: String) : LocationChoice()

    fun cacheKey(): String = when (this) {
        is Coordinates -> "${latitude}_${longitude}"
        is Manual -> "${city}_${country}".lowercase()
    }
}
