package com.example.weatherapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.SavedLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: SavedLocation)

    @Query("SELECT name FROM saved_locations")
    suspend fun getAll(): List<String>

    @Query("SELECT name FROM saved_locations WHERE name LIKE :cityName")
    fun getSavedLocation(cityName: String): Flow<String?>

    @Query("DELETE FROM saved_locations WHERE name LIKE :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("DELETE FROM saved_locations")
    suspend fun deleteAll()

}