package com.example.weatherapp.data.remote

import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.error.WeatherErrorResponse
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.utils.mapApiToCurrentModel
import com.example.weatherapp.utils.mapToHourWeatherModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception

class WeatherNetworkDataSource(
    private val apiService: ApiService,
) {

    suspend fun getCurrentWeatherResponse(
        city: String,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResultData { apiService.getCurrentCityWeather(city, measure) }
                .mapSuccess {
                    it.mapApiToCurrentModel()
                }
        }
    }

    suspend fun getCurrentCoordWeatherResponse(
        lat: Double,
        lon: Double,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResultData {
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
    ): NetworkResult<List<HourWeatherModel>> {
        return withContext(Dispatchers.IO) {
            getResultData {
                apiService.getHourlyWeather(measure, lat.toString(), lon.toString())
            }
                .mapSuccess {
                    it.mapToHourWeatherModel()
                }
        }
    }

    private fun <T, R> NetworkResult<T>.mapSuccess(mapper: (T) -> R): NetworkResult<R> =
        if (this is NetworkResult.Success) {
            NetworkResult.Success(mapper(data))
        } else {
            NetworkResult.Error((this as NetworkResult.Error).message)
        }

    private suspend fun <T> getResultData(
        getData: suspend () -> Response<T>,
    ): NetworkResult<T> {
        return try {
            val apiResult = getData.invoke()
            NetworkResult.Success(apiResult.body()!!)
        } catch (e: Exception) {
            if (e is HttpException) {
                mapError(e)
            } else {
                NetworkResult.Error(e.message)
            }
        }
    }

    private fun <T> mapError(exception: HttpException): NetworkResult.Error<T> {
        val gson = Gson()
        val fromJson =
            gson.fromJson(exception.response()?.errorBody()?.string(),
                WeatherErrorResponse::class.java)
        return NetworkResult.Error(fromJson.message)
    }
}