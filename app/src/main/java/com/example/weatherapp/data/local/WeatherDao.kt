package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.utils.AppConstants
import com.example.weatherapp.utils.AppConstants.DATABASE

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(localWeatherModel: LocalWeatherModel)

    @Query("SELECT * FROM localweathermodel")
    suspend fun getAll(): List<LocalWeatherModel>

    @Query("SELECT * FROM localweathermodel WHERE name LIKE :cityName")
    suspend fun getCity(cityName: String): LocalWeatherModel?

    @Query("DELETE FROM localweathermodel WHERE name LIKE :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("DELETE FROM localweathermodel")
    suspend fun deleteAll()

}