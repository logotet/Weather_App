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
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
) {

    suspend fun getCurrentWeatherResponse(
        city: String,
        measure: String,
    ): NetworkResult<CurrentWeatherModel> {
        return withContext(Dispatchers.IO) {
            getResultData{ apiService.getCurrentCityWeather(city, measure) }
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
        if (this is Success) {
            Success(mapper(data))
        } else {
            Error<R>((this as Error).message)
        }

    private suspend fun <T> getResultData(
        getData: suspend () -> Response<T>,
    ): NetworkResult<T> {
        return try {
            val apiResult = getData.invoke()
            Success(apiResult.body()!!)
        } catch (e: Exception) {
            if (e is HttpException) {
                mapError(e)
            } else {
                Error(e.message)
            }
        }
    }

    private fun <T> mapError(exception: HttpException): NetworkResult.Error<T> {
        val gson = Gson()
        val fromJson =
            gson.fromJson(exception.response()?.errorBody()?.string(),
                WeatherErrorResponse::class.java)
        return Error(fromJson.message)
    }
}

