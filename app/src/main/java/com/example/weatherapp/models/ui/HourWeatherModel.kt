package com.example.weatherapp.models.ui

class HourWeatherModel(
    val hourTemperature: Double,
    val hourWindSpeed: Double,
    val hourIcon: String,
    val hour: Long,
    val windDirection: Int,
    val timeZoneOffset: Int = 10800
)