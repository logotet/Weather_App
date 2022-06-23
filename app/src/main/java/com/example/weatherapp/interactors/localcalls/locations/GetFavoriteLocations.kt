package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetFavoriteLocations(private val repository: Repository) {
    suspend fun getFavoriteLocations(): Flow<List<LocalWeatherModel>> {
        return repository.getFavoriteLocations()
    }
}