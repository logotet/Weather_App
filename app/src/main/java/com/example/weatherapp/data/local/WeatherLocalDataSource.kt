package com.example.weatherapp.data.local

import com.example.weatherapp.models.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

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

    fun getFavoritesLocationsByName(names: List<String>): Flow<List<LocalWeatherModel>> {
        return weatherDatabase.weatherDao().getFavoritesByNames(names)
    }

    suspend fun deleteOldCityData() {
        val favoriteCities = weatherDatabase.savedLocationDao().getAll()
        weatherDatabase.weatherDao().deleteNotIn(favoriteCities)
    }

    //LocalHour
    suspend fun insertLocalHours(localHours: List<LocalHour>) {
        withContext(Dispatchers.IO) {
            weatherDatabase.hourDao().insertLocationHours(localHours)
        }
    }

    fun getLocationHours(cityName: String): Flow<List<LocalHour>> {
        return weatherDatabase.hourDao().getLocationHours(cityName)
    }

    //Recent locations
    suspend fun insertCityName(city: City) {
        weatherDatabase.cityDao().insert(city)
    }

    fun getRecentCityNames(): Flow<List<City>> {
        return weatherDatabase.cityDao().getRecent()
    }

    suspend fun getRecentlyUpdatedCityNames(): List<String> {
        return weatherDatabase.cityDao().getMostRecent()
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

    suspend fun deleteLocation(cityName: String) {
        weatherDatabase.savedLocationDao().deleteCity(cityName)
    }

    //Current location
    suspend fun insertCurrentLocationCoords(city: CurrentLocation) {
        weatherDatabase.currentLocationDao().insertCurrent(city)
    }

    fun getCurrentLocationCoords(): Flow<CurrentLocation?> {
        return weatherDatabase.currentLocationDao().getCurrent()
    }

    suspend fun deleteCurrentLocationCoords() {
        weatherDatabase.currentLocationDao().deleteCurrent()
    }
}