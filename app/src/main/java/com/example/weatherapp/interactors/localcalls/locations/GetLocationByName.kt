package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetLocationByName(private val repository: Repository) {
   fun getCity(city: String): Flow<Result<LocalWeatherModel?>> {
        return repository.getLocationFromDatabase(city)
    }
}