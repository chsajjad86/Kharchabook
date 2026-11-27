package com.kharchabook.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

object CycleUtils {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun startOfDay(millis: Long): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    fun endOfDayExclusive(millis: Long): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = startOfDay(millis)
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return c.timeInMillis
    }

    fun cycleStart(millis: Long, startDay: Int): Long {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        val today = c.get(Calendar.DAY_OF_MONTH)
        val anchor = startDay.coerceIn(1, c.getActualMaximum(Calendar.DAY_OF_MONTH))

        c.set(Calendar.DAY_OF_MONTH, 1)
        if (today < anchor) c.add(Calendar.MONTH, -1)
        val max = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        c.set(Calendar.DAY_OF_MONTH, startDay.coerceIn(1, max))
        zeroTime(c)
        return c.timeInMillis
    }

    fun nextCycleStart(currentStart: Long, startDay: Int): Long {
        val c = Calendar.getInstance().apply { timeInMillis = currentStart }
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.add(Calendar.MONTH, 1)
        val max = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        c.set(Calendar.DAY_OF_MONTH, startDay.coerceIn(1, max))
        zeroTime(c)
        return c.timeInMillis
    }

    fun prevCycleStart(currentStart: Long, startDay: Int): Long {
        val c = Calendar.getInstance().apply { timeInMillis = currentStart }
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.add(Calendar.MONTH, -1)
        val max = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        c.set(Calendar.DAY_OF_MONTH, startDay.coerceIn(1, max))
        zeroTime(c)
        return c.timeInMillis
    }

    fun cycleRange(millis: Long, startDay: Int): Pair<Long, Long> {
        val start = cycleStart(millis, startDay)
        return start to nextCycleStart(start, startDay)
    }

    fun shiftCycle(start: Long, startDay: Int, delta: Int): Long {
        var s = start
        repeat(abs(delta)) {
            s = if (delta > 0) nextCycleStart(s, startDay) else prevCycleStart(s, startDay)
        }
        return s
    }

    fun dayLabel(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", Locale.US).format(millis)

    fun shortDayLabel(millis: Long): String =
        SimpleDateFormat("EEE, d MMM", Locale.US).format(millis)

    fun cycleLabel(start: Long, endExclusive: Long): String {
        val fmt = SimpleDateFormat("d MMM yyyy", Locale.US)
        return "${fmt.format(start)} – ${fmt.format(endExclusive - DAY_MS / 24)}"
    }

    fun timeLabel(millis: Long): String =
        SimpleDateFormat("h:mm a", Locale.US).format(millis)

    private fun zeroTime(c: Calendar) {
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
    }
}
