package com.example.globalnamazvakti.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface AladhanApiService {
    @GET("v1/calendar")
    suspend fun getMonthlyPrayerTimes(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): AladhanCalendarResponse
}
