package com.example.weatherapp.interactors.localcalls.citynames

import com.example.weatherapp.models.local.City
import com.example.weatherapp.repository.Repository

class InsertCityName(
    private val repository: Repository
) {
    suspend fun insertCityName(city: City){
        repository.insertCityName(city)
    }
}