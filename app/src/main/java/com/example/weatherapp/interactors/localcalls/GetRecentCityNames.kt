package com.example.weatherapp.interactors.localcalls

import com.example.weatherapp.models.local.City
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetRecentCityNames(
    private val repository: Repository
) {
    fun getRecentCityNames(): Flow<List<City>> {
        return repository.getRecentCityNames()
    }
}