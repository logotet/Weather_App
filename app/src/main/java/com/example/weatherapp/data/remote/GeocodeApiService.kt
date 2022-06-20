package com.example.weatherapp.data.remote

import com.example.weatherapp.models.api.CityNameModel
import com.example.weatherapp.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApiService {

    /*Get list of names of the location base on
    the coordinates*/
    @GET("reverse")
    suspend fun getLocationName(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = AppConstants.API_KEY,
    ): Response<CityNameModel>
}