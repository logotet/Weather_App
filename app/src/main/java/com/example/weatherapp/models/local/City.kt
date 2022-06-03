package com.example.weatherapp.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_names")
data class City(
    @PrimaryKey
    val cityName: String,
    val addedAt: Long = System.currentTimeMillis()
)