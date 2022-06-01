package com.example.weatherapp.interactors.localcalls

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository

class GetLocationByName(private val repository: Repository) {
    suspend fun getCity(city: String): LocalWeatherModel?{
        return repository.loadCity(city)
    }
}