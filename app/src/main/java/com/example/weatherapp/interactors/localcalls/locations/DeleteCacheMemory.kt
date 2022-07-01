package com.example.weatherapp.interactors.localcalls.locations

import com.example.weatherapp.repository.Repository

class DeleteCacheMemory(
    private val repository: Repository,
) {
    suspend fun deleteCache() {
        repository.deleteCache()
    }
}