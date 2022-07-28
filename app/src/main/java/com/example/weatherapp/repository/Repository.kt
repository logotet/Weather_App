package com.example.weatherapp.repository

import com.example.weatherapp.data.Result
import com.example.weatherapp.data.Result.Error
import com.example.weatherapp.data.Result.Success
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.data.remote.flowOfResult
import com.example.weatherapp.data.remote.mapToResult
import com.example.weatherapp.models.local.*
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.utils.mapCurrentToLocal
import com.example.weatherapp.models.utils.mapLocalToCurrentModel
import com.example.weatherapp.models.utils.mapToLocalHours
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class Repository(
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
) {
    //Network
    suspend fun getNetworkWeatherByLocationName(
        city: String,
    ): Flow<Result<Unit>> {
        if (checkRecentCity(city)) return flowOf(Success(Unit))
        val locationResult = weatherNetworkDataSource.getCurrentWeatherResponse(city)
        return saveSuccessfulResponse(locationResult)
    }

    suspend fun getNetworkWeatherFromCoordinates(
        lat: Double,
        lon: Double,
    ): Flow<Result<Unit>> {
        val currentCoordWeatherResponse =
            weatherNetworkDataSource.getCurrentCoordWeatherResponse(lat, lon)
        return saveSuccessfulCoords(currentCoordWeatherResponse)
    }

    suspend fun getHourlyWeather(
        lat: Double,
        lon: Double,
        city: String,
    ): Flow<Result<Unit>> {
        if (checkRecentCity(city)) return flowOf(Success(Unit))
        val hourlyWeather = weatherNetworkDataSource.getHourlyWeather(lat, lon)
        return saveSuccessHours(hourlyWeather, city)
    }

    private suspend fun checkRecentCity(city: String): Boolean {
        val latestRecentCityNames = weatherLocalDataSource.getRecentlyUpdatedCityNames()
        if (latestRecentCityNames.contains(city)) {
            return true
        }
        return false
    }

    //Local
    //LocalWeatherModel
    private suspend fun insertData(dataModel: LocalWeatherModel) {
        weatherLocalDataSource.insert(dataModel)
    }

    fun getLocationFromDatabase(city: String): Flow<Result<CurrentWeatherModel?>> {
        return weatherLocalDataSource.getCity(city).transform {
            emit(it?.mapLocalToCurrentModel())
        }.mapToResult()
    }

    suspend fun deleteLocation(cityName: String) {
        weatherLocalDataSource.deleteLocation(cityName)
    }

    private suspend fun saveSuccessfulCoords(
        currentCoordWeatherResponse: Result<CurrentWeatherModel>,
    ): Flow<Result<Unit>> {
        return currentCoordWeatherResponse.flowOfResult(
            {
                weatherLocalDataSource.insertCurrentLocationCoords(
                    CurrentLocation(
                        it.name, it.lat,
                        it.lon
                    )
                )
                Success(Unit)
            },
            {
                Error((currentCoordWeatherResponse as Error).message)
            }
        )
    }

    private suspend fun saveSuccessfulResponse(
        cityNetworkWeather: Result<CurrentWeatherModel>,
    ): Flow<Result<Unit>> {
        return cityNetworkWeather.flowOfResult(
            {
                val dataModel = it.mapCurrentToLocal()
                insertData(dataModel)
                //TODO the fun below throws sqlite exception as the parent entry is not yet inserted
//            getHourlyWeather(dataModel.name, dataModel.lat, dataModel.lon, units)
                Success(Unit)
            },
            {
                Error((cityNetworkWeather as Error).message)
            }
        )
    }

    //TODO refactor
    private suspend fun saveSuccessHours(
        cityNetworkWeather: Result<List<HourWeatherModel>>,
        city: String,
    ): Flow<Result<Unit>> {
        return if (cityNetworkWeather is Success) {
            val hourModel = cityNetworkWeather.data.mapToLocalHours(city)
            insertLocalHours(hourModel)
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

    //LocalHours
    private suspend fun insertLocalHours(localHours: List<LocalHour>) {
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

    //Current Location
    fun getCurrentLocationCoords(): Flow<CurrentLocation?> {
        return weatherLocalDataSource.getCurrentLocationCoords()
    }

    suspend fun deleteCurrentLocationCoords() {
        return weatherLocalDataSource.deleteCurrentLocationCoords()
    }

    //Unsaved entries
    suspend fun deleteCache() {
        weatherLocalDataSource.deleteOldCityData()
    }
}

