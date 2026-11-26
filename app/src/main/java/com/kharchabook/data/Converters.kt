package com.kharchabook.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromKeywords(list: List<String>): String = list.joinToString("|")

    @TypeConverter
    fun toKeywords(raw: String): List<String> =
        if (raw.isBlank()) emptyList() else raw.split("|")
}
