package com.example.weatherapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.LocalHour
import com.example.weatherapp.models.local.LocalWeatherModel
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationWeather(localWeatherModel: LocalWeatherModel)

    @Query("SELECT * FROM location_weather")
    suspend fun getAll(): List<LocalWeatherModel>

    @Query("SELECT * FROM location_weather ORDER BY addedAt DESC LIMIT 5 ")
    fun getRecent(): Flow<List<LocalWeatherModel>>

    @Query("SELECT * FROM location_weather WHERE name IN (:names)")
    fun getFavoritesByNames(names: List<String>): Flow<List<LocalWeatherModel>>

    @Query("SELECT * FROM location_weather WHERE name LIKE :cityName")
    fun getCity(cityName: String): Flow<LocalWeatherModel?>

    @Query("SELECT * FROM location_weather WHERE lat LIKE :lat AND lon LIKE :lon")
    fun getCityByCoords(lat: Double, lon: Double): Flow<LocalWeatherModel?>

    @Query("DELETE FROM location_weather WHERE name LIKE :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("DELETE FROM location_weather")
    suspend fun deleteAll()
}