package com.kharchabook.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.kharchabook.KharchaApp
import com.kharchabook.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as KharchaApp
        if (!app.settings.reminderEnabled) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "kharcha_reminder"
        if (nm.getNotificationChannel(channelId) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Daily expense reminder",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Kharcha Book")
            .setContentText("Aaj ka kharcha likhna yaad hai?")
            .setAutoCancel(true)
            .build()

        nm.notify(1001, notif)

        // Reschedule for tomorrow
        Reminders.schedule(context, app.settings.reminderHour, app.settings.reminderMinute)
    }
}
