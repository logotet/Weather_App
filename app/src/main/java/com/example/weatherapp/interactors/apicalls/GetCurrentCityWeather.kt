package com.example.weatherapp.interactors.apicalls

import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.repository.Repository

class GetCurrentCityWeather(
    private val repository: Repository,
) {
    suspend fun getCurrentWeather(
        city: String,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return repository.getCityNetworkWeather(city, measure)
    }
}