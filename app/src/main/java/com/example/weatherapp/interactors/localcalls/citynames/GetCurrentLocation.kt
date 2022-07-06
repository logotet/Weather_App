package com.example.weatherapp.interactors.localcalls.citynames

import com.example.weatherapp.models.local.CurrentLocation
import com.example.weatherapp.repository.Repository

class GetCurrentLocation(
    private val repository: Repository,
) {
    fun getCurrentLocation(): kotlinx.coroutines.flow.Flow<CurrentLocation?> {
        return repository.getCurrentLocationCoords()
    }
}