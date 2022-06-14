package com.example.weatherapp.models.utils

import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel

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

fun LocalWeatherModel.mapLocalToCurrentModel(): CurrentWeatherModel{
    return CurrentWeatherModel(
        this.name,
        this.description,
        this.temperature,
        this.windSpeed,
        this.humidity,
        this.icon,
        this.lat,
        this.lon,
        this.windDirection
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
                h.timeZoneOffset
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
                h.windDirection,
                h.timeZoneOffset
            )
        )
    }
    return hours
}