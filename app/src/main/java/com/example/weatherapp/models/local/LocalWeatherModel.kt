package com.example.weatherapp.models.local

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
    var hours: List<LocalHour>? = null
)

data class LocalHour(
    val hourTemperature: Double,
    val hourWindSpeed: Double,
    val hourIcon: String,
    val hour: Long,
    val windDirection: Int,
    val timeZoneOffset: Int = 10800,
)
