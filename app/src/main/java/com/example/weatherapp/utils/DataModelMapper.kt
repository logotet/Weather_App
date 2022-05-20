package com.example.weatherapp.utils

import com.example.weatherapp.models.current.CurrentWeather
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourApiResponseModel
import com.example.weatherapp.models.hourly.HourWeatherModel

fun CurrentWeather.mapApiToCurrentModel(): CurrentWeatherModel {
    return CurrentWeatherModel(
        this!!.name,
        this.weather[0].description,
        this.main.temp,
        this.wind.speed,
        this.main.humidity,
        this.weather[0].icon,
        this.coord.lat,
        this.coord.lon,
        this.wind.deg
    )
}

fun HourApiResponseModel?.mapToHourWeatherModel(): List<HourWeatherModel> {
    val hoursApiResponse = this?.hourly
    val hours = mutableListOf<HourWeatherModel>()
    hoursApiResponse?.forEach { h ->
        hours.add(
            HourWeatherModel(
                h.temp,
                h.wind_speed,
                h.weather[0].icon,
                h.dt.toLong(),
                h.wind_deg
            )
        )
    }
    return hours
}

