package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetSavedLocationByCoords(private val repository: Repository) {
    fun getCityByCoords(lat:Double, lon: Double): Flow<Result<LocalWeatherModel?>> {
        return repository.getCityByCoords(lat, lon)
    }
}