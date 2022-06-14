package com.example.weatherapp.interactors.localcalls.citynames

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetRecentLocations(private val repository: Repository) {
    suspend fun getRecentLocations(): Flow<List<LocalWeatherModel>> {
        return repository.getRecentLocations()
    }
}