package com.example.weatherapp.data.local

import com.example.weatherapp.models.local.LocalWeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherLocalDataSource(private val weatherDatabase: WeatherDatabase) {

    suspend fun insert(dataModel: LocalWeatherModel) {
        withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().insert(dataModel)
        }
    }

    suspend fun loadCity(city: String): LocalWeatherModel?{
       return withContext(Dispatchers.IO){
            weatherDatabase.weatherDao().getCity(city)
        }
    }
}