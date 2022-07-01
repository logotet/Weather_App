package com.example.weatherapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.City
import com.example.weatherapp.utils.AppConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: City)

    @Query("SELECT * FROM city_names")
    fun getAll(): Flow<List<City>>

    @Query("SELECT * FROM city_names ORDER BY addedAt DESC LIMIT 5 ")
    fun getRecent(): Flow<List<City>>

    @Query("SELECT cityName FROM city_names WHERE (addedAt + :timeOffset) > :currentTime")
    suspend fun getMostRecent(
        timeOffset: Long = AppConstants.CACHE_TIMEOUT,
        currentTime: Long = System.currentTimeMillis(),
    ): List<String>

    @Query("DELETE FROM city_names WHERE cityName LIKE :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("DELETE FROM city_names")
    suspend fun deleteAll()

}