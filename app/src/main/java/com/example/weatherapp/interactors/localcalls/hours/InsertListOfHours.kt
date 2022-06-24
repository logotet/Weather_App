package com.example.weatherapp.interactors.localcalls.hours

import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.repository.Repository

class InsertListOfHours(
    private val repository: Repository
) {
    suspend fun insertHours(localHours: List<LocalHour>){
        repository.insertLocalHours(localHours)
    }
}