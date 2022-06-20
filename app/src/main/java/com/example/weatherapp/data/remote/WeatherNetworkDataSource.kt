package com.example.weatherapp.data.remote

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.api.CityNameModel
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel
import com.example.weatherapp.utils.mapToHourWeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNetworkDataSource(
    private val apiService: ApiService,
    private val geocodeApiService: GeocodeApiService
    ) {
    suspend fun getCurrentWeatherResponse(
        city: String,
        measure: String,
    ): Result<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResult { apiService.getCurrentCityWeather(city, measure) }
                .mapSuccess {
                    it.mapApiToCurrentModel()
                }
        }
    }

    suspend fun getCurrentCoordWeatherResponse(
        lat: Double,
        lon: Double,
        measure: String,
    ): Result<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResult {
                apiService.getCurrentCoordWeather(measure, lat.toString(), lon.toString())
            }
                .mapSuccess {
                    it.mapApiToCurrentModel()
                }
        }
    }

    suspend fun getHourlyWeather(
        measure: String,
        lat: Double,
        lon: Double,
    ): Result<List<HourWeatherModel>> {
        return withContext(Dispatchers.IO) {
            getResult {
              apiService.getHourlyWeather(measure, lat.toString(), lon.toString())
            }
                .mapSuccess {
                    it.mapToHourWeatherModel()
                }
        }
    }

    suspend fun getCityNameByCoords(
        lat: Double,
        lon: Double,
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            getResult {
                geocodeApiService.getLocationName(lat.toString(), lon.toString())
            }.mapSuccess {
                it[0].name
            }
        }
    }
}