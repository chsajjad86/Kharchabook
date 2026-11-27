package com.kharchabook

import android.app.Application
import com.kharchabook.data.AppDatabase
import com.kharchabook.data.Repository
import com.kharchabook.reminder.Reminders
import com.kharchabook.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KharchaApp : Application() {

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: Repository
        private set
    lateinit var settings: Settings
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        repository = Repository(db.categoryDao(), db.expenseDao())
        settings = Settings(this)

        appScope.launch { repository.seedIfEmpty() }

        if (settings.reminderEnabled) {
            Reminders.schedule(this, settings.reminderHour, settings.reminderMinute)
        }
    }
}
