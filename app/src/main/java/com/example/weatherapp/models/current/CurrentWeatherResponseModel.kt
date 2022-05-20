package com.example.weatherapp.models.current

data class CurrentWeather(
    val clouds: Clouds,
    val main: Main,
    val name: String,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind,
    val coord: Coord
)

data class Clouds(
    val all: Int
)

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class Wind(
    val deg: Int,
    val speed: Double
)

data class Coord(
    val lon: Double,
    val lat: Double
)