package com.example.weatherapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Double?.formatTemperature(
    resourceProvider: ResourceProvider,
    unitSystem: UnitSystem,
): String {
    val tempFormat = when (unitSystem) {
        UnitSystem.METRIC -> resourceProvider.getString(R.string.celsius_temperature_format)
        UnitSystem.STANDARD -> resourceProvider.getString(R.string.standard_temperature_format)
        UnitSystem.IMPERIAL -> resourceProvider.getString(R.string.imperial_temperature_format)
    }
    return resourceProvider.getString(R.string.temperature_format,
        this ?: 0.0,
        tempFormat)
}

fun Int?.formatHumidity(resourceProvider: ResourceProvider): String =
    resourceProvider.getString(R.string.humidity_format, this ?: 0)

fun Double?.formatSpeed(resourceProvider: ResourceProvider, unitSystem: UnitSystem): String {
    val speedFormat = when (unitSystem) {
        UnitSystem.METRIC -> resourceProvider.getString(R.string.metric_standard_speed_format)
        UnitSystem.STANDARD -> resourceProvider.getString(R.string.metric_standard_speed_format)
        UnitSystem.IMPERIAL -> resourceProvider.getString(R.string.imperial_speed_format)
    }
    return resourceProvider.getString(R.string.speed_format, this ?: 0.0, speedFormat)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.formatHour(resourceProvider: ResourceProvider, timeOffset: Int?): String {
    var format = ""
    timeOffset?.let {
        val zoneOffset = ZoneOffset.ofTotalSeconds(timeOffset)
        val dateTime = LocalDateTime.ofEpochSecond(this, 0, zoneOffset)
        format = resourceProvider.getString(R.string.hour_format, dateTime.hour.toString())
    }
    return format
}