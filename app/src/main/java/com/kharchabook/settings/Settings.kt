package com.kharchabook.settings

import android.content.Context
import androidx.core.content.edit

class Settings(context: Context) {

    private val prefs = context.getSharedPreferences("kharcha_settings", Context.MODE_PRIVATE)

    var cycleStartDay: Int
        get() = prefs.getInt("cycleStartDay", 25)
        set(v) = prefs.edit { putInt("cycleStartDay", v.coerceIn(1, 28)) }

    var reminderEnabled: Boolean
        get() = prefs.getBoolean("reminderEnabled", true)
        set(v) = prefs.edit { putBoolean("reminderEnabled", v) }

    var reminderHour: Int
        get() = prefs.getInt("reminderHour", 21)
        set(v) = prefs.edit { putInt("reminderHour", v) }

    var reminderMinute: Int
        get() = prefs.getInt("reminderMinute", 0)
        set(v) = prefs.edit { putInt("reminderMinute", v) }
}
