package com.example.globalnamazvakti.di

import android.content.Context
import androidx.room.Room
import com.example.globalnamazvakti.data.local.PrayerDao
import com.example.globalnamazvakti.data.local.PrayerDatabase
import com.example.globalnamazvakti.data.remote.AladhanApiService
import com.example.globalnamazvakti.data.repository.PrayerRepositoryImpl
import com.example.globalnamazvakti.domain.PrayerRepository
import com.google.android.gms.ads.MobileAds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAladhanApi(retrofit: Retrofit): AladhanApiService {
        return retrofit.create(AladhanApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PrayerDatabase {
        return Room.databaseBuilder(context, PrayerDatabase::class.java, "prayer.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePrayerDao(database: PrayerDatabase): PrayerDao = database.prayerDao()

    @Provides
    @Singleton
    fun providePrayerRepository(
        apiService: AladhanApiService,
        prayerDao: PrayerDao
    ): PrayerRepository = PrayerRepositoryImpl(apiService, prayerDao)

    @Provides
    @Singleton
    fun provideMobileAds(@ApplicationContext context: Context): MobileAds {
        return MobileAds.apply {
            initialize(context)
        }
    }
}
