package com.example.globalnamazvakti.domain

import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun getPrayerTimes(
        coordinates: Coordinates,
        method: CalculationMethod,
        month: Int,
        year: Int
    ): Flow<Result<List<PrayerTimes>>>
}
