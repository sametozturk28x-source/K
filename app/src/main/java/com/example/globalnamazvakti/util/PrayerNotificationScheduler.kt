package com.example.globalnamazvakti.util

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PrayerNotificationScheduler {
    fun schedulePrayerNotification(
        context: Context,
        prayerName: String,
        prayerTimeMillis: Long
    ) {
        val delay = prayerTimeMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putString(PrayerNotificationWorker.KEY_PRAYER_NAME, prayerName)
            .build()

        val request = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
