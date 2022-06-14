package com.example.weatherapp.interactors.localcalls.hours

import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetLocationHours(
    private val repository: Repository
) {
    fun getLocationHours(city: String): Flow<Map<LocalWeatherModel?, List<LocalHour>>> {
        return repository.getLocationHours(city)
    }
}