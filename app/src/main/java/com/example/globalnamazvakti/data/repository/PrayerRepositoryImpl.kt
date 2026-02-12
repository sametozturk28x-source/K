package com.example.globalnamazvakti.data.repository

import com.example.globalnamazvakti.data.local.PrayerDao
import com.example.globalnamazvakti.data.local.PrayerEntity
import com.example.globalnamazvakti.data.remote.AladhanApiService
import com.example.globalnamazvakti.domain.CalculationMethod
import com.example.globalnamazvakti.domain.Coordinates
import com.example.globalnamazvakti.domain.PrayerRepository
import com.example.globalnamazvakti.domain.PrayerTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class PrayerRepositoryImpl(
    private val apiService: AladhanApiService,
    private val prayerDao: PrayerDao
) : PrayerRepository {
    override fun getPrayerTimes(
        coordinates: Coordinates,
        method: CalculationMethod,
        month: Int,
        year: Int
    ): Flow<Result<List<PrayerTimes>>> = flow {
        try {
            val cached = prayerDao.getAll()
            if (cached.isNotEmpty()) {
                emit(Result.success(cached.map { it.toDomain() }))
                return@flow
            }

            val response = apiService.getMonthlyPrayerTimes(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
                method = method.methodId,
                month = month,
                year = year
            )

            val entities = response.data.map { day ->
                PrayerEntity(
                    dateGregorian = day.date.gregorian.date,
                    dateHijri = day.date.hijri.date,
                    fajr = day.timings.fajr,
                    sunrise = day.timings.sunrise,
                    dhuhr = day.timings.dhuhr,
                    asr = day.timings.asr,
                    sunset = day.timings.sunset,
                    maghrib = day.timings.maghrib,
                    isha = day.timings.isha
                )
            }

            prayerDao.insertAll(entities)
            emit(Result.success(entities.map { it.toDomain() }))
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Failed to fetch prayer times")
            val fallback = prayerDao.getAll()
            if (fallback.isNotEmpty()) {
                emit(Result.success(fallback.map { it.toDomain() }))
            } else {
                emit(Result.failure(throwable))
            }
        }
    }
}

private fun PrayerEntity.toDomain(): PrayerTimes = PrayerTimes(
    dateGregorian = dateGregorian,
    dateHijri = dateHijri,
    fajr = fajr,
    sunrise = sunrise,
    dhuhr = dhuhr,
    asr = asr,
    sunset = sunset,
    maghrib = maghrib,
    isha = isha
)
