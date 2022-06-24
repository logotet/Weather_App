package com.example.weatherapp.models.api

data class HourApiResponseModel(
    val hourly: List<Hourly>,
    val timezone: String,
    val timezone_offset: Int
)

data class Hourly(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Rain,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double,
    val weather: List<WeatherX>
)


data class Rain(
    val `1h`: Double
)

data class WeatherX(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)
