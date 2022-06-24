package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.data.remote.GeocodeApiService
import com.example.weatherapp.data.remote.WeatherNetworkDataSource
import com.example.weatherapp.interactors.apicalls.GetCurrentCityWeather
import com.example.weatherapp.interactors.apicalls.GetCurrentCoordWeather
import com.example.weatherapp.interactors.apicalls.GetHourlyWeather
import com.example.weatherapp.interactors.apicalls.GetLocationNameByCoords
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentCityNames
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentLocations
import com.example.weatherapp.interactors.localcalls.citynames.InsertCityName
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.hours.InsertListOfHours
import com.example.weatherapp.interactors.localcalls.locations.*
import com.example.weatherapp.repository.Repository
import com.example.weatherapp.ui.hours.HourViewModel
import com.example.weatherapp.utils.AppConstants
import com.example.weatherapp.models.Measure
import com.example.weatherapp.utils.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @WeatherRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @GeocodeRetrofit
    @Provides
    @Singleton
    fun provideGeocodeRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL_GEOCODING)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GeocodeRetrofit

    @Provides
    @Singleton
    fun provideApiService(@WeatherRetrofit retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideGeocodeService(@GeocodeRetrofit retrofit: Retrofit): GeocodeApiService =
        retrofit.create(GeocodeApiService::class.java)

    @Provides
    @Singleton
    fun providesRepository(
        weatherLocalDataSource: WeatherLocalDataSource,
        weatherNetworkDataSource: WeatherNetworkDataSource,
    ): Repository =
        Repository(weatherLocalDataSource, weatherNetworkDataSource)

    @Provides
    @Singleton
    fun providesGetCurrentWeather(repository: Repository): GetCurrentCityWeather =
        GetCurrentCityWeather(repository)

    @Provides
    @Singleton
    fun providesGetCoordWeather(repository: Repository): GetCurrentCoordWeather =
        GetCurrentCoordWeather(repository)

    @Provides
    @Singleton
    fun providesGetHourlyWeather(repository: Repository): GetHourlyWeather =
        GetHourlyWeather(repository)

    @Provides
    @Singleton
    fun providesHourViewModel(measure: Measure, resourceProvider: ResourceProvider): HourViewModel =
        HourViewModel(measure, resourceProvider)

    @Provides
    @Singleton
    fun providesStringProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProvider(context)

    @Provides
    @Singleton
    fun providesWeatherNetworkDataSource(
        apiService: ApiService,
        geocodeApiService: GeocodeApiService,
    ): WeatherNetworkDataSource =
        WeatherNetworkDataSource(apiService, geocodeApiService)

    @Provides
    @Singleton
    fun providesWeatherRoomDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).build()

    @Provides
    @Singleton
    fun providesWeatherLocalDataSource(weatherDatabase: WeatherDatabase): WeatherLocalDataSource =
        WeatherLocalDataSource(weatherDatabase)

    @Provides
    @Singleton
    fun providesInsertIntoDatabase(repository: Repository): InsertIntoDatabase =
        InsertIntoDatabase(repository)

    @Provides
    @Singleton
    fun providesGetLocationFromDatabase(repository: Repository): GetLocationByName =
        GetLocationByName(repository)

    @Provides
    @Singleton
    fun providesGetAllLocations(repository: Repository): GetAllLocations =
        GetAllLocations(repository)

    @Provides
    @Singleton
    fun providesGetRecentLocations(repository: Repository): GetRecentLocations =
        GetRecentLocations(repository)

    @Provides
    @Singleton
    fun providesGetFavoriteLocations(repository: Repository): GetFavoriteLocations =
        GetFavoriteLocations(repository)

    @Provides
    @Singleton
    fun providesInsertCityName(repository: Repository): InsertCityName =
        InsertCityName(repository)

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
    fun providesInsertListOfHours(repository: Repository): InsertListOfHours =
        InsertListOfHours(repository)

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
    fun providesGGetCityByCoords(repository: Repository): GetSavedLocationByCoords =
        GetSavedLocationByCoords(repository)

    @Provides
    @Singleton
    fun providesGetCityByCoordsFromAPI(repository: Repository): GetLocationNameByCoords =
        GetLocationNameByCoords(repository)

    @Provides
    @Singleton
    fun providesInsertSavedLocation(repository: Repository): InsertSavedLocation =
        InsertSavedLocation(repository)
}