package com.example.weatherapp.utils

import com.example.weatherapp.models.api.CurrentWeather
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.api.HourApiResponseModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel

fun CurrentWeather.mapApiToCurrentModel(): CurrentWeatherModel {
    return CurrentWeatherModel(
        this.name,
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
    val hours = mutableListOf<HourWeatherModel>()
    this?.let {
        val hoursApiResponse = this?.hourly
        hoursApiResponse.forEach { h ->
            hours.add(
                HourWeatherModel(
                    h.temp,
                    h.wind_speed,
                    h.weather[0].icon,
                    h.dt.toLong(),
                    h.wind_deg,
                    this.timezone_offset
                )
            )
        }
    }
    return hours
}

fun Map<LocalWeatherModel?, List<LocalHour>>.mapToList(): List<LocalHour>{
    val listOfHours = ArrayList<LocalHour>()
    for(keyEntry in this){
       listOfHours.addAll(keyEntry.value)
    }
    return listOfHours
}

