package com.tagtrack.app.core.data.local.db

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromList(value: List<String>?): String? = value?.joinToString(separator = ";")

    @TypeConverter
    fun toList(value: String?): List<String> = value?.split(";")?.filter { it.isNotBlank() } ?: emptyList()
}
