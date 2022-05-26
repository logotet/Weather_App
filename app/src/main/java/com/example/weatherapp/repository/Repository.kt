package com.example.weatherapp.repository

import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.data.remote.NetworkResult.*
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.error.WeatherErrorResponse
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel
import com.example.weatherapp.utils.mapToHourWeatherModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
) {

    suspend fun getCurrentWeatherResponse(
        city: String,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getCurrentCityWeather(city, measure)
            try {
                val currentCityModel = response.body()!!.mapApiToCurrentModel()
                Success(currentCityModel)
            } catch (e: Exception) {
                val gson = Gson()
                val fromJson =
                    gson.fromJson(response.errorBody()?.string(), WeatherErrorResponse::class.java)
                Error(fromJson.message)
            }
        }
    }

    suspend fun getCurrentCoordWeatherResponse(
        lat: Double,
        lon: Double,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            val response =
                apiService.getCurrentCoordWeather(lat.toString(), lon.toString(), measure)
            try {
                val currentCityModel = response.body()!!.mapApiToCurrentModel()
                Success(currentCityModel)
            } catch (e: Exception) {
                val gson = Gson()
                val fromJson =
                    gson.fromJson(response.errorBody()?.string(), WeatherErrorResponse::class.java)
                Error(fromJson.message)
            }
        }

    }

    suspend fun getHourlyWeather(
        measure: String,
        lat: Double,
        lon: Double,
    ): NetworkResult<List<HourWeatherModel>> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getHourlyWeather(measure, lat.toString(), lon.toString())
            try {
                val hours = response.body().mapToHourWeatherModel()
                Success(hours)
            } catch (e: Exception) {
                val gson = Gson()
                val fromJson =
                    gson.fromJson(response.errorBody()?.string(), WeatherErrorResponse::class.java)
                Error(fromJson.message)
            }
        }
    }
}