package com.example.weatherapp.models.local

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "location_weather",
    primaryKeys = ["name"],
)
data class LocalWeatherModel(
    val name: String,
    val description: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val icon: String,
    val lat: Double,
    val lon: Double,
    val windDirection: Int,
    val addedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "location_hours",
    primaryKeys = ["cityName", "hour"],
    foreignKeys = [ForeignKey(entity = LocalWeatherModel::class,
        parentColumns = ["name"],
        childColumns = ["cityName"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalHour(
    val cityName: String,
    val hourTemperature: Double,
    val hourWindSpeed: Double,
    val hourIcon: String,
    val hour: Long,
    val hourWindDirection: Int,
    val timeZoneOffset: Int = 10800,
)
