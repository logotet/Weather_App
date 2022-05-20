package com.example.weatherapp.interactors

import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.repository.Repository

class GetHourlyWeather(
    private val repository: Repository
) {
    suspend fun getHours(measure: String): NetworkResult<List<HourWeatherModel>> {
        return repository.getHourlyWeather(measure)
    }
}