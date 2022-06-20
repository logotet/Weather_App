package com.example.weatherapp.interactors.apicalls

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.repository.Repository

class GetHourlyWeather(
    private val repository: Repository,
) {
    suspend fun getHours(
        measure: String,
        lat: Double,
        lon: Double,
    ): Result<List<HourWeatherModel>> {
        return repository.getHourlyWeather(measure, lat, lon)
    }
}