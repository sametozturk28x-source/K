package com.example.globalnamazvakti.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.globalnamazvakti.R

class PrayerNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: return Result.failure()
        createChannel(prayerName)

        val notification = NotificationCompat.Builder(applicationContext, prayerName)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(prayerName)
            .setContentText(applicationContext.getString(R.string.next_prayer))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(prayerName.hashCode(), notification)
        return Result.success()
    }

    private fun createChannel(prayerName: String) {
        val channel = NotificationChannel(
            prayerName,
            prayerName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val KEY_PRAYER_NAME = "KEY_PRAYER_NAME"
    }
}
