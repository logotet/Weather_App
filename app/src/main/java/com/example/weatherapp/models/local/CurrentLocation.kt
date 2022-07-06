package com.example.weatherapp.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_location")
class CurrentLocation(
    val name: String,
    var lat: Double,
    var lon: Double,
    @PrimaryKey(autoGenerate = false)
    val current: Boolean = true,
) {
}