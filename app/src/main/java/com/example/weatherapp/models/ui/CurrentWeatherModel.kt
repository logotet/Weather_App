package com.example.weatherapp.models.ui

class CurrentWeatherModel(
    val name: String,
    val description: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val icon: String,
    val lat: Double,
    val lon: Double,
    val windDirection: Int,
//    var hours: List<HourWeatherModel>? = null
    )