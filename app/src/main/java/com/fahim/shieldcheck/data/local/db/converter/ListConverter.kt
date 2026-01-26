package com.fahim.shieldcheck.data.local.db.converter

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(SEPARATOR)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(SEPARATOR)?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.joinToString(SEPARATOR)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.split(SEPARATOR)?.filter { it.isNotEmpty() }?.map { it.toInt() }
    }

    companion object {
        private const val SEPARATOR = "|||"
    }
}
