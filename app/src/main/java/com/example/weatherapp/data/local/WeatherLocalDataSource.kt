package com.example.weatherapp.data.local

import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalWeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSource(private val weatherDatabase: WeatherDatabase) {

    //LocalWeatherModel
    suspend fun insert(dataModel: LocalWeatherModel) {
        withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().insert(dataModel)
        }
    }

    suspend fun loadCity(city: String): LocalWeatherModel? {
        return withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().getCity(city)
        }
    }

    suspend fun getAllLocations(): List<LocalWeatherModel>? {
        return withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().getAll()
        }
    }

    fun getRecentLocations(): Flow<List<LocalWeatherModel>> {
        return weatherDatabase.weatherDao().getRecent()
    }

    suspend fun getFavoriteLocations(): List<LocalWeatherModel>? {
        return withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().getFavorites()
        }
    }

    //City
    suspend fun insertCityName(city: City) {
        weatherDatabase.cityDao().insert(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherDatabase.cityDao().getRecent()
    }
}