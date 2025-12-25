package com.namazvaktiglobal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.namazvaktiglobal.R

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra(NotificationHelper.EXTRA_PRAYER_NAME) ?: return
        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.notification_channel_prayer))
            .setContentText("$prayerName time")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(prayerName.hashCode(), notification)
    }
}
