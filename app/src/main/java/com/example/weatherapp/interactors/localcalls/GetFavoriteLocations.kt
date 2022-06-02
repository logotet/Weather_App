package com.example.weatherapp.interactors.localcalls

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository

class GetFavoriteLocations(private val repository: Repository) {
    suspend fun getFavoriteLocations(): List<LocalWeatherModel>? {
        return repository.getFavoriteLocations()
    }
}