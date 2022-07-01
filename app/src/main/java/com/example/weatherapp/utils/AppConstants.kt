package com.example.weatherapp.utils

object AppConstants {
    const val API_KEY = "658767b0ae936b022f59a69f44868419"
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val BASE_URL_GEOCODING = "https://api.openweathermap.org/geo/1.0/"
    const val IMG_URL = "https://openweathermap.org/img/wn/"
    const val IMG_URL_SUFFIX = "@2x.png"

    const val EXCLUDE_HOUR_DATA = "daily, minutely, alerts"

    const val CAMERA_STANDARD_ZOOM = 10F

    const val DATABASE = "weather_db"

    const val CACHE_TIMEOUT: Long = 300_000 //5m
    const val OLD_DATA_TIMEOUT: Long = 172_800_000 //48h
}