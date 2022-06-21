package com.example.weatherapp.models.local

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "location_weather",
    primaryKeys = ["name", "saved"],
    indices = [Index(value = ["name"], unique = true)])
data class LocalWeatherModel(
//    @PrimaryKey(autoGenerate = false)
    val name: String,
    val description: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val icon: String,
    val lat: Double,
    val lon: Double,
    val windDirection: Int,
    var hours: List<LocalHour>? = null,
    val addedAt: Long = System.currentTimeMillis(),
    var saved: Boolean = false,
    var currentLocation: Boolean = false,
)

@Entity(tableName = "location_hours",
    foreignKeys = [ForeignKey(entity = LocalWeatherModel::class,
        parentColumns = ["name", "saved"],
        childColumns = ["cityName", "saved"],
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
    val windDirection: Int,
    val timeZoneOffset: Int = 10800,
    val saved: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
