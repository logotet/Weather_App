package com.example.weatherapp.utils

import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Double?.formatTemperature(
    resourceProvider: ResourceProvider,
    unitSystem: UnitSystem,
): String {
    val tempFormat = when (unitSystem) {
        UnitSystem.METRIC -> resourceProvider.getString(R.string.celsius_temperature_format)
        UnitSystem.STANDARD -> resourceProvider.getString(R.string.standard_temperature_format)
        UnitSystem.IMPERIAL -> resourceProvider.getString(R.string.imperial_temperature_format)
    }
    return resourceProvider.getString(
        R.string.temperature_format,
        this ?: 0.0,
        tempFormat
    )
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

fun Long.formatHour(timeOffset: Int): String {
    val simpleDateFormat = SimpleDateFormat("HH:mm")
    TimeUnit.SECONDS.toMillis(timeOffset.toLong())
    simpleDateFormat.timeZone =
        SimpleTimeZone(TimeUnit.SECONDS.toMillis(timeOffset.toLong()).toInt(), "")
    return simpleDateFormat.format(TimeUnit.SECONDS.toMillis(this))
}

fun String.capitalizeFirstChar(): String {
    return this.replaceFirstChar {
        it.uppercase()
    }
}