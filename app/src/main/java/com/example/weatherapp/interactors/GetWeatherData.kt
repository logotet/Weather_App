package com.example.weatherapp.interactors

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetWeatherData(
    private val repository: Repository,
) {
    suspend fun getWeatherData(
        location: String,
        unitSystem: UnitSystem,
    ): Flow<Result<LocalWeatherModel?>> {
        repository.getLocationNetworkWeather(location, unitSystem.value)
        return repository.getLocationFromDatabase(location)
    }
}