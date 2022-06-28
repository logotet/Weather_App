package com.example.weatherapp.interactors.apicalls

import com.example.weatherapp.data.Result
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetCurrentCoordWeather(
    private val repository: Repository,
) {
    suspend fun getCurrentCoordWeather(
        lat: Double,
        lon: Double,
        measure: String,
    ): Flow<Result<Unit>> {
        return repository.getNetworkWeatherFromCoordinates(lat, lon, measure)
    }
}