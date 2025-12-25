package com.namazvaktiglobal.data.api

import com.namazvaktiglobal.data.model.ApiCalendarResponse
import com.namazvaktiglobal.data.model.ApiTimingsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimesApi {
    @GET("v1/timings")
    suspend fun getTimingsByCoordinates(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("adjustment") hijriAdjustment: Int
    ): ApiTimingsResponse

    @GET("v1/timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("adjustment") hijriAdjustment: Int
    ): ApiTimingsResponse

    @GET("v1/calendar")
    suspend fun getCalendarByCoordinates(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("adjustment") hijriAdjustment: Int
    ): ApiCalendarResponse

    @GET("v1/calendarByCity")
    suspend fun getCalendarByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("adjustment") hijriAdjustment: Int
    ): ApiCalendarResponse
}
