package com.example.weatherapp.models.utils

import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel

fun CurrentWeatherModel.mapCurrentToLocal(): LocalWeatherModel {
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

fun LocalWeatherModel.mapLocalToCurrentModel(): CurrentWeatherModel {
    return CurrentWeatherModel(
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

fun List<HourWeatherModel>.mapToLocalHours(cityName: String): List<LocalHour> {
    val hours = mutableListOf<LocalHour>()
    this.forEach { h ->
        hours.add(
            LocalHour(
                cityName,
                h.hourTemperature,
                h.hourWindSpeed,
                h.hourIcon,
                h.hour,
                h.windDirection,
                h.timeZoneOffset,
            )
        )
    }
    return hours
}

fun List<LocalHour>.mapToCurrentHours(): List<HourWeatherModel> {
    val hours = mutableListOf<HourWeatherModel>()
    this.forEach { h ->
        hours.add(
            HourWeatherModel(
                h.hourTemperature,
                h.hourWindSpeed,
                h.hourIcon,
                h.hour,
                h.hourWindDirection,
                h.timeZoneOffset
            )
        )
    }
    return hours
}