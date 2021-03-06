package com.example.weatherapp.data.remote

import com.example.weatherapp.models.api.CityNameModel
import com.example.weatherapp.models.api.CurrentWeather
import com.example.weatherapp.models.api.HourApiResponseModel
import com.example.weatherapp.utils.AppConstants.API_KEY
import com.example.weatherapp.utils.AppConstants.EXCLUDE_HOUR_DATA
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /*Get current weather with the name of a city*/
    @GET("weather")
    suspend fun getCurrentCityWeather(
        @Query("q") cityName: String?,
        @Query("units") units: String = METRIC_SYSTEM,
        @Query("appid") apiKey: String = API_KEY,
    ): Response<CurrentWeather>

    /*Get current weather on chosen coordinates*/
    @GET("weather")
    suspend fun getCurrentCoordWeather(
        @Query("units") units: String = METRIC_SYSTEM,
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") apiKey: String = API_KEY,
    ): Response<CurrentWeather>

    /*Get the weather with the coordinates of Sofia for every hour
     in the next 48h*/
    @GET("onecall")
    suspend fun getHourlyWeather(
        @Query("units") units: String = METRIC_SYSTEM,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = EXCLUDE_HOUR_DATA,
        @Query("appid") apiKey: String = API_KEY,
    ): Response<HourApiResponseModel>

    /*Get list of names of the location base on
    the coordinates*/
    @GET("reverse")
    suspend fun getLocationName(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = API_KEY,
    ): Response<CityNameModel>

    companion object {
        private const val METRIC_SYSTEM = "standard"
    }
}