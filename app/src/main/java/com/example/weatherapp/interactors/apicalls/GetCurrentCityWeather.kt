package com.example.weatherapp.interactors.apicalls

import com.example.weatherapp.data.Result
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetCurrentCityWeather(
    private val repository: Repository,
) {
    suspend fun getCurrentWeather(
        city: String,
        measure: String,
    ): Flow<Result<Unit>> {
        return repository.getNetworkWeatherByLocationName(city, measure)
    }
}