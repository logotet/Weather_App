package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetFavoriteLocationByName(private val repository: Repository) {
    fun getCity(city: String): Flow<LocalWeatherModel?> {
        return repository.getFavoriteCity(city)
    }
}