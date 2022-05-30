package com.example.weatherapp.data

import com.example.weatherapp.data.remote.NetworkResult

fun <T> NetworkResult<T>.checkResult(
    onSuccess: (T) -> Unit,
    onError: (NetworkResult.Error<T>) -> Unit,
) {
    when(this){
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onError(this)
    }
}