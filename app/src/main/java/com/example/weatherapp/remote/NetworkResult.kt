package com.example.weatherapp.data.remote

sealed class NetworkResult<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<out T>(data: T) : NetworkResult<T>(data)

    class Error<out T>(message: String? = "ERROR", data: T? = null) : NetworkResult<T>(message = message)
}