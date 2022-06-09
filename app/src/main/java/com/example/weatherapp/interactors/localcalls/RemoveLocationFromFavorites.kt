package com.example.weatherapp.interactors.localcalls

import com.example.weatherapp.repository.Repository

class RemoveLocationFromFavorites(
    private val repository: Repository
) {
    suspend fun removeFromFavorites(cityName: String){
        repository.deleteLocation(cityName)
    }
}