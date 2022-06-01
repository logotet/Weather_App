package com.example.weatherapp.interactors.localcalls

import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.repository.Repository

class InsertIntoDatabase(private val repository: Repository) {
    suspend fun insertData(dataModel: LocalWeatherModel) {
        repository.insertData(dataModel)
    }
}