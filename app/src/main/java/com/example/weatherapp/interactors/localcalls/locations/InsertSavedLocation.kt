package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.models.local.SavedLocation
import com.example.weatherapp.repository.Repository

class InsertSavedLocation(private val repository: Repository) {
    suspend fun insertData(location: SavedLocation) {
        repository.insertAsSaved(location)
    }
}