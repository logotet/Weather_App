package com.example.weatherapp.di

import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.interactors.apicalls.GetLocationNameByCoords
import com.example.weatherapp.interactors.localcalls.citynames.GetCurrentLocation
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentCityNames
import com.example.weatherapp.interactors.localcalls.citynames.InsertRecentCityName
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.locations.*
import com.example.weatherapp.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InteractorsModule {
    @Provides
    @Singleton
    fun providesWeatherLocalDataSource(weatherDatabase: WeatherDatabase): WeatherLocalDataSource =
        WeatherLocalDataSource(weatherDatabase)

    @Provides
    @Singleton
    fun providesGetLocationFromDatabase(repository: Repository): GetLocationByName =
        GetLocationByName(repository)

    @Provides
    @Singleton
    fun providesGetFavoriteLocations(repository: Repository): GetFavoriteLocations =
        GetFavoriteLocations(repository)

    @Provides
    @Singleton
    fun providesInsertCityName(repository: Repository): InsertRecentCityName =
        InsertRecentCityName(repository)

    @Provides
    @Singleton
    fun providesGetRecentCityNames(repository: Repository): GetRecentCityNames =
        GetRecentCityNames(repository)

    @Provides
    @Singleton
    fun providesRemoveLocationFromFavorites(repository: Repository): RemoveLocationFromFavorites =
        RemoveLocationFromFavorites(repository)

    @Provides
    @Singleton
    fun providesGetLocationHours(repository: Repository): GetLocationHours =
        GetLocationHours(repository)

    @Provides
    @Singleton
    fun providesGetFavoritesLocationByName(repository: Repository): GetFavoriteLocationByName =
        GetFavoriteLocationByName(repository)

    @Provides
    @Singleton
    fun providesGetCityByCoordsFromAPI(repository: Repository): GetLocationNameByCoords =
        GetLocationNameByCoords(repository)

    @Provides
    @Singleton
    fun providesInsertSavedLocation(repository: Repository): InsertSavedLocation =
        InsertSavedLocation(repository)

    @Provides
    @Singleton
    fun providesDeleteCacheMemory(repository: Repository): DeleteCacheMemory =
        DeleteCacheMemory(repository)

    @Provides
    @Singleton
    fun providesGetLatestCityName(repository: Repository): GetCurrentLocation =
        GetCurrentLocation(repository)
}