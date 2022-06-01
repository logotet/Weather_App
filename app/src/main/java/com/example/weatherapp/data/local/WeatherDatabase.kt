package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.data.local.utils.RoomConverters
import com.example.weatherapp.models.local.LocalWeatherModel

@Database(entities = [LocalWeatherModel::class], version = 1)
@TypeConverters(RoomConverters::class)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}