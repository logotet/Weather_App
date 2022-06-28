package com.example.weatherapp.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.Result.*
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.data.remote.mapToResult
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.local.*
import com.example.weatherapp.models.utils.mapApiToCurrentModel
import com.example.weatherapp.models.utils.mapToLocalHours
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository(
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
) {
    //Network
    suspend fun getLocationNetworkWeather(
        city: String,
        measure: String,
    ): Flow<Result<Unit>> {
        val locationResult = weatherNetworkDataSource.getCurrentWeatherResponse(city, measure)
        return saveSuccess(locationResult, false)
    }

    suspend fun getNetworkWeatherFromCoordinates(
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
        city: String,
    ): Flow<Result<Unit>> {
        val hourlyWeather = weatherNetworkDataSource.getHourlyWeather(measure, lat, lon)
        delay(3000)
        return saveSuccessHours(hourlyWeather, city)
    }

    private suspend fun saveSuccess(
        cityNetworkWeather: Result<CurrentWeatherModel>,
        currentLocation: Boolean
    ): Flow<Result<Unit>> {
        return if (cityNetworkWeather is Success) {
            val dataModel = cityNetworkWeather.data.mapApiToCurrentModel()
            if(currentLocation) {
                weatherLocalDataSource.insertCurrentLocationCoords(CurrentLocation(dataModel.lat,
                    dataModel.lon))
            }
            insertData(dataModel)
            flowOf(Success(Unit))
        } else {
            flowOf(Error((cityNetworkWeather as Error).message))
        }
    }

    //TODO refactor
    private suspend fun saveSuccessHours(
        cityNetworkWeather: Result<List<HourWeatherModel>>,
        city: String,
    ): Flow<Result<Unit>> {
        return if (cityNetworkWeather is Success) {
            val dataModel = cityNetworkWeather.data.mapToLocalHours(city)
            insertLocalHours(dataModel)
            flowOf(Success(Unit))
        } else {
            flowOf(Error((cityNetworkWeather as Error).message))
        }
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

    suspend fun getCityByCoords(): Flow<Result<LocalWeatherModel?>> {
        val cityByCoords = weatherLocalDataSource.getCityByCoords()
        return cityByCoords.mapToResult()
    }

    suspend fun getAllLocations(): List<LocalWeatherModel>? {
        return weatherLocalDataSource.getAllLocations()
    }

    fun getRecentLocations(): Flow<List<LocalWeatherModel>> {
        return weatherLocalDataSource.getRecentLocations()
    }

    suspend fun deleteLocation(cityName: String) {
        weatherLocalDataSource.deleteLocation(cityName)
    }

    //LocalHours
    suspend fun insertLocalHours(localHours: List<LocalHour>) {
        weatherLocalDataSource.insertLocalHours(localHours)
    }

    fun getLocationHours(city: String): Flow<List<LocalHour>> {
        return weatherLocalDataSource.getLocationHours(city)
    }

    //City
    suspend fun insertCityName(city: City) {
        weatherLocalDataSource.insertCityName(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherLocalDataSource.getRecentCityNames()
    }

    //Saved Location
    fun getSavedLocation(name: String): Flow<String> {
        return weatherLocalDataSource.getSavedLocation(name)
    }

    suspend fun getFavoriteLocations(): Flow<List<LocalWeatherModel>> {
        val savedLocationsNames = weatherLocalDataSource.getSavedLocations()
        return weatherLocalDataSource.getFavoritesLocationsByName(savedLocationsNames)
    }

    suspend fun insertAsSaved(savedLocation: SavedLocation) {
        weatherLocalDataSource.insertAsSavedOrNot(savedLocation)
    }
}

