package com.example.weatherapp.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.Result.*
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.data.remote.mapToResult
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.utils.mapApiToCurrentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository(
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
) {
    //Network
    suspend fun getCityNetworkWeather(
        city: String,
        measure: String,
    ): Flow<Result<Unit>> {
        val cityNetworkWeather = weatherNetworkDataSource.getCurrentWeatherResponse(city, measure)
        return saveSuccess(cityNetworkWeather, false)
    }

    suspend fun getCoordWeatherNetwork(
        lat: Double,
        lon: Double,
        measure: String,
    ): Flow<Result<Unit>> {
        val currentCoordWeatherResponse =
            weatherNetworkDataSource.getCurrentCoordWeatherResponse(lat, lon, measure)
        return saveSuccess(currentCoordWeatherResponse, true)
    }

    suspend fun getHourlyWeather(
        measure: String,
        lat: Double,
        lon: Double,
    ): Result<List<HourWeatherModel>> {
        return weatherNetworkDataSource.getHourlyWeather(measure, lat, lon)
    }

    suspend fun getCityNameByCoords(
        lat: Double,
        lon: Double,
    ): Result<String> {
        return weatherNetworkDataSource.getCityNameByCoords(lat, lon)
    }

    //Local
    //LocalWeatherModel
    suspend fun insertData(dataModel: LocalWeatherModel) {
        weatherLocalDataSource.insert(dataModel)
    }

    fun getLocationFromDatabase(city: String): Flow<Result<LocalWeatherModel?>> {
        return weatherLocalDataSource.getCity(city).mapToResult()
    }

    fun getFavoriteCity(city: String): Flow<LocalWeatherModel?> {
        return weatherLocalDataSource.getFavoriteCity(city)
    }

    fun getCityByCoords(lat: Double, lon: Double): Flow<Result<LocalWeatherModel?>> {
        val cityByCoords = weatherLocalDataSource.getCurrentLocation()
        return cityByCoords.mapToResult()
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

    //LocalHours
    suspend fun insertLocalHours(localHours: List<LocalHour>) {
        weatherLocalDataSource.insertLocalHours(localHours)
    }

    fun getLocationHours(city: String): Flow<Map<LocalWeatherModel?, List<LocalHour>?>> {
        return weatherLocalDataSource.getLocationHours(city)
    }

    //City
    suspend fun insertCityName(city: City) {
        weatherLocalDataSource.insertCityName(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherLocalDataSource.getRecentCityNames()
    }

    //
    private suspend fun saveSuccess(
        cityNetworkWeather: Result<CurrentWeatherModel>,
        isCurrentLocation: Boolean,
    ): Flow<Result<Unit>> {
        return if (cityNetworkWeather is Success) {
            val dataModel = cityNetworkWeather.data.mapApiToCurrentModel()
            dataModel.currentLocation = isCurrentLocation
            insertData(dataModel)
            flowOf(Success(Unit))
        } else {
            flowOf(Error((cityNetworkWeather as Error).message))
        }
    }
}

