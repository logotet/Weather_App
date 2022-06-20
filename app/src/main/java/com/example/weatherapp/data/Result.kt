package com.example.weatherapp.data

sealed class Result<out T>() {
    class Success<out T>(val data: T) : Result<T>()

    class Error<out T>(val message: String? = "ERROR") : Result<T>()
}