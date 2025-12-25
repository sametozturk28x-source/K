package com.namazvaktiglobal.di

import android.content.Context
import androidx.room.Room
import com.namazvaktiglobal.data.db.AppDatabase
import com.namazvaktiglobal.data.db.CalendarDao
import com.namazvaktiglobal.data.db.PrayerTimesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "namaz_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePrayerTimesDao(database: AppDatabase): PrayerTimesDao = database.prayerTimesDao()

    @Provides
    fun provideCalendarDao(database: AppDatabase): CalendarDao = database.calendarDao()
}
