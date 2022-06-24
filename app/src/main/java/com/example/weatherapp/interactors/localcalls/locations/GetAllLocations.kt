package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository

class GetAllLocations(private val repository: Repository) {
    suspend fun getAllLocations(): List<LocalWeatherModel>? {
        return repository.getAllLocations()
    }
}