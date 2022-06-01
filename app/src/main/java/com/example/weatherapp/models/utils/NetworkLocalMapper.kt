package com.example.weatherapp.models.utils

import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel

fun CurrentWeatherModel.mapApiToCurrentModel(): LocalWeatherModel {
    return LocalWeatherModel(
        name,
        description,
        temperature,
        windSpeed,
        humidity,
        icon,
        lat,
        lon,
        windDirection
    )
}

fun List<HourWeatherModel>.mapToLocalHours(): List<LocalHour> {
    val hours = mutableListOf<LocalHour>()
    this.forEach { h ->
        hours.add(
            LocalHour(
                h.hourTemperature,
                h.hourWindSpeed,
                h.hourIcon,
                h.hour,
                h.windDirection,
                h.timeZoneOffset
            )
        )
    }
    return hours
}