package com.example.weatherapp.interactors

import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.repository.Repository

class GetCurrentCoordWeather(
    private val repository: Repository,
) {
    suspend fun getCurrentCoordWeather(
        lat: Double,
        lon: Double,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return repository.getCoordWeatherNetwork(lat, lon, measure)
    }
}