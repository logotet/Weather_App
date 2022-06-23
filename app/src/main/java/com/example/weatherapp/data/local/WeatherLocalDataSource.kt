package com.example.weatherapp.data.local

import com.example.weatherapp.models.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

class WeatherLocalDataSource(private val weatherDatabase: WeatherDatabase) {

    //LocalWeatherModel
    suspend fun insert(dataModel: LocalWeatherModel) {
        withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().insertLocationWeather(dataModel)
        }
    }

    fun getCity(city: String): Flow<LocalWeatherModel?> {
        return weatherDatabase.weatherDao().getCity(city)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getCityByCoords(): Flow<LocalWeatherModel?> {
        //TODO why can't it happen using flow???
        return getCurrentLocationCoords().flatMapLatest { location ->
            try {
                weatherDatabase.weatherDao().getCityByCoords(location.lat, location.lon)
            } catch (e: NullPointerException) {
                weatherDatabase.weatherDao().getCityByCoords(1.0, 1.0)
            }
        }
    }

    suspend fun getAllLocations(): List<LocalWeatherModel>? {
        return withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().getAll()
        }
    }

    fun getRecentLocations(): Flow<List<LocalWeatherModel>> {
        return weatherDatabase.weatherDao().getRecent()
    }

    fun getFavoritesLocationsByName(names: List<String>): Flow<List<LocalWeatherModel>> {
        return weatherDatabase.weatherDao().getFavoritesByNames(names)
    }

    suspend fun deleteLocation(cityName: String) {
        weatherDatabase.savedLocationDao().deleteCity(cityName)
    }

    //LocalHour
    suspend fun insertLocalHours(localHours: List<LocalHour>) {
        withContext(Dispatchers.IO) {
            weatherDatabase.weatherDao().insertLocationHours(localHours)
        }
    }

    fun getLocationHours(cityName: String): Flow<List<LocalHour>> {
        return weatherDatabase.weatherDao().getLocationHours(cityName)
    }

    //Recent locations
    suspend fun insertCityName(city: City) {
        weatherDatabase.cityDao().insert(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherDatabase.cityDao().getRecent()
    }

    // Saved locations
    suspend fun insertAsSavedOrNot(city: SavedLocation) {
        weatherDatabase.savedLocationDao().insert(city)
    }

    suspend fun getSavedLocations(): List<String> {
        return weatherDatabase.savedLocationDao().getAll()
    }

    fun getSavedLocation(name: String): Flow<String> {
        return weatherDatabase.savedLocationDao().getSavedLocation(name)
    }

    //Current location
    suspend fun insertCurrentLocationCoords(city: CurrentLocation) {
        weatherDatabase.currentLocationDao().insertCurrent(city)
    }

    fun getCurrentLocationCoords(): Flow<CurrentLocation> {
        return weatherDatabase.currentLocationDao().getCurrent()
    }
}