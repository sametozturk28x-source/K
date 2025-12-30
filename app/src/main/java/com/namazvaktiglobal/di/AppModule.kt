package com.namazvaktiglobal.di

import android.content.Context
import com.namazvaktiglobal.data.api.PrayerTimesApi
import com.namazvaktiglobal.data.db.CalendarDao
import com.namazvaktiglobal.data.db.PrayerTimesDao
import com.namazvaktiglobal.ads.AdManager
import com.namazvaktiglobal.ads.ConsentManager
import com.namazvaktiglobal.data.preferences.SettingsDataStore
import com.namazvaktiglobal.location.LocationProvider
import com.namazvaktiglobal.qibla.CompassSensor
import com.namazvaktiglobal.repository.PrayerTimesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return LocationProvider(context)
    }

    @Provides
    @Singleton
    fun provideCompassSensor(@ApplicationContext context: Context): CompassSensor {
        return CompassSensor(context)
    }

    @Provides
    @Singleton
    fun provideAdManager(@ApplicationContext context: Context): AdManager {
        return AdManager(context)
    }

    @Provides
    @Singleton
    fun provideConsentManager(@ApplicationContext context: Context): ConsentManager {
        return ConsentManager(context)
    }

    @Provides
    @Singleton
    fun providePrayerTimesRepository(
        api: PrayerTimesApi,
        prayerTimesDao: PrayerTimesDao,
        calendarDao: CalendarDao,
        json: Json
    ): PrayerTimesRepository {
        return PrayerTimesRepository(api, prayerTimesDao, calendarDao, json)
    }
}
