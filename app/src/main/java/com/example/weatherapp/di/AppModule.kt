package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.interactors.GetCurrentCityWeather
import com.example.weatherapp.interactors.GetCurrentCoordWeather
import com.example.weatherapp.interactors.GetHourlyWeather
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesRepository(apiService: ApiService): Repository =
        Repository(apiService)

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
}