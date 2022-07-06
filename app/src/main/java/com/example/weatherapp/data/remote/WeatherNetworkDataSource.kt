package com.example.weatherapp.data.remote

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel
import com.example.weatherapp.utils.mapToHourWeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNetworkDataSource(
    private val apiService: ApiService,
    private val geocodeApiService: GeocodeApiService,
) {
    suspend fun getCurrentWeatherResponse(
        city: String,
    ): Result<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResult { apiService.getCurrentCityWeather(city) }
                .mapSuccess {
                    it.mapApiToCurrentModel()
                }
        }
    }

    suspend fun getCurrentCoordWeatherResponse(
        lat: Double,
        lon: Double,
    ): Result<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResult {
                apiService.getCurrentCoordWeather(lat = lat.toString(), lon = lon.toString())
            }
                .mapSuccess {
                    it.mapApiToCurrentModel()
                }
        }
    }

    suspend fun getHourlyWeather(
        lat: Double,
        lon: Double,
    ): Result<List<HourWeatherModel>> {
        return withContext(Dispatchers.IO) {
            getResult {
                apiService.getHourlyWeather(lat = lat.toString(), lon = lon.toString())
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