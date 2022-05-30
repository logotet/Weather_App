package com.example.weatherapp.data.remote

sealed class NetworkResult<out T>() {
    class Success<out T>(val data: T) : NetworkResult<T>()

    class Error<out T>(val message: String? = "ERROR") : NetworkResult<T>()
}