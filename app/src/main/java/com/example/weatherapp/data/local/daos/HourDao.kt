package com.example.weatherapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.local.LocalHour
import kotlinx.coroutines.flow.Flow

@Dao
interface HourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationHour(localHour: LocalHour)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationHours(localHours: List<LocalHour>)

    @Query("SELECT * FROM location_hours WHERE cityName LIKE :locationName")
    fun getLocationHours2(locationName: String): Flow<List<LocalHour>>

    @Query("SELECT * FROM location_hours WHERE cityName LIKE :cityName")
    fun getLocationHours(cityName: String): Flow<List<LocalHour>>
}