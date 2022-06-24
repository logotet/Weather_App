package com.example.weatherapp.interactors.apicalls

import com.example.weatherapp.data.Result
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetLocationNameByCoords(
    private val repository: Repository,
) {
    suspend fun getLocationName(
        lat: Double,
        lon: Double
    ):Result<String> {
        return repository.getCityNameByCoords(lat, lon)
    }
}