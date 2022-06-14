package com.example.weatherapp.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "location_weather")
data class LocalWeatherModel(
    @PrimaryKey(autoGenerate = false)
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
)

@Entity(tableName = "location_hours",
        foreignKeys = [ForeignKey(entity = LocalWeatherModel::class,
    parentColumns = ["name"],
    childColumns = ["cityName"],
    onDelete = CASCADE,
    onUpdate = CASCADE
)])
data class LocalHour(
    val cityName: String,
    val hourTemperature: Double,
    val hourWindSpeed: Double,
    val hourIcon: String,
    val hour: Long,
    val windDirection: Int,
    val timeZoneOffset: Int = 10800,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
