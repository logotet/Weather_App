package com.example.weatherapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.CurrentLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(currentLocation: CurrentLocation)

    @Query("SELECT * FROM current_location")
    fun getCurrent(): Flow<CurrentLocation>
}