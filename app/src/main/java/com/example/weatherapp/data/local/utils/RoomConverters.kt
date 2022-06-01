package com.example.weatherapp.data.local.utils

import androidx.room.TypeConverter
import com.example.weatherapp.models.local.LocalHour
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class RoomConverters {
    @TypeConverter
    fun fromHoursToJson(hours: List<LocalHour>?): String?{
        return Gson().toJson(hours)
    }

    @TypeConverter
    fun fromJsonToHours(hours: String?): List<LocalHour>?{
        val listType: Type? = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(hours, listType)
    }
}