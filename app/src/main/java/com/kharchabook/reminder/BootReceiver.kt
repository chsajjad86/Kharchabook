package com.kharchabook.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kharchabook.KharchaApp

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as KharchaApp
            if (app.settings.reminderEnabled) {
                Reminders.schedule(context, app.settings.reminderHour, app.settings.reminderMinute)
            }
        }
    }
}
