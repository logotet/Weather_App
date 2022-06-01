package com.example.weatherapp.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.data.remote.NetworkResult.*
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.error.WeatherErrorResponse
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel
import com.example.weatherapp.utils.mapToHourWeatherModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception

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
    suspend fun insertData(dataModel: LocalWeatherModel) {
        weatherLocalDataSource.insert(dataModel)
    }

    suspend fun loadCity(city: String): LocalWeatherModel? {
        return weatherLocalDataSource.loadCity(city)
    }
}

