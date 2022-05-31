package com.example.weatherapp.data.local

import androidx.room.Database

//@Database(entities = [User::class], version = 1)
abstract class WeatherDatabase {
    abstract fun weatherDao(): WeatherDao
}