package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.repository.Repository

class DeleteCurrentLocation(
    private val repository: Repository,
) {
    suspend fun deleteCurrentLocation() {
        repository.deleteCurrentLocationCoords()
    }
}