package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.data.local.daos.*
import com.example.weatherapp.data.local.utils.RoomConverters
import com.example.weatherapp.models.local.*

@Database(entities = [
    LocalWeatherModel::class,
    City::class,
    LocalHour::class,
    SavedLocation::class,
    CurrentLocation::class], version = 1)
@TypeConverters(RoomConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun hourDao(): HourDao
    abstract fun cityDao(): RecentCityDao
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun currentLocationDao(): CurrentLocationDao
}