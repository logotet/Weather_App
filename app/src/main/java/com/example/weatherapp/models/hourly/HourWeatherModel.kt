package com.example.weatherapp.models.hourly

class HourWeatherModel(
    val hourTemperature: Double,
    val hourWindSpeed: Double,
    val hourIcon: String,
    val hour: Long,
    val windDirection: Int
)