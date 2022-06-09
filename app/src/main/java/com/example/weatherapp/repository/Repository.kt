package com.example.weatherapp.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalWeatherModel
import kotlinx.coroutines.flow.Flow

class Repository(
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
) {
    suspend fun getCityNetworkWeather(
        city: String,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return weatherNetworkDataSource.getCurrentWeatherResponse(city, measure)
    }

    suspend fun getCoordWeatherNetwork(
        lat: Double,
        lon: Double,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return weatherNetworkDataSource.getCurrentCoordWeatherResponse(lat, lon, measure)
    }

    suspend fun getHourlyWeather(
        measure: String,
        lat: Double,
        lon: Double,
    ): NetworkResult<List<HourWeatherModel>> {
        return weatherNetworkDataSource.getHourlyWeather(measure, lat, lon)
    }


    //Local
    //LocalWeatherModel
    suspend fun insertData(dataModel: LocalWeatherModel) {
        weatherLocalDataSource.insert(dataModel)
    }

    fun loadCity(city: String): Flow<LocalWeatherModel?> {
        return weatherLocalDataSource.loadCity(city)
    }

    suspend fun getAllLocations(): List<LocalWeatherModel>? {
        return weatherLocalDataSource.getAllLocations()
    }

    fun getRecentLocations(): Flow<List<LocalWeatherModel>> {
        return weatherLocalDataSource.getRecentLocations()
    }

    suspend fun getFavoriteLocations(): List<LocalWeatherModel>? {
        return weatherLocalDataSource.getFavoriteLocations()
    }

    suspend fun deleteLocation(cityName: String) {
        weatherLocalDataSource.deleteLocation(cityName)
    }


    //City
    suspend fun insertCityName(city: City) {
        weatherLocalDataSource.insertCityName(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherLocalDataSource.getRecentCityNames()
    }
}

