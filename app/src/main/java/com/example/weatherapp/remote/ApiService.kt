package com.example.weatherapp.data.remote

import com.example.weatherapp.models.current.CurrentWeather
import com.example.weatherapp.models.hourly.HourApiResponseModel
import com.example.weatherapp.utils.AppConstants.API_KEY
import com.example.weatherapp.utils.AppConstants.EXCLUDE_HOUR_DATA
import com.example.weatherapp.utils.AppConstants.SOFIA_LAT
import com.example.weatherapp.utils.AppConstants.SOFIA_LON
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /*Get current weather for the city of Sofia*/
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String?,
        @Query("units") units: String,
        @Query("appid") apiKey: String = API_KEY
    ): Response<CurrentWeather>

    /*Get the weather with the coordinates of Sofia for every hour
     in the next 48h*/
    @GET("onecall")
    suspend fun getHourlyWeather(
        @Query("units") units: String,
        @Query("lat") lat: String = SOFIA_LAT,
        @Query("lon") lon: String = SOFIA_LON,
        @Query("exclude") exclude: String = EXCLUDE_HOUR_DATA,
        @Query("appid") apiKey: String = API_KEY
    ): Response<HourApiResponseModel>

}