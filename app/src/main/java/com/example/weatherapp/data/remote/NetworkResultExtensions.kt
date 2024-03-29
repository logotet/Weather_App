package com.example.weatherapp.data.remote

import com.example.weatherapp.data.Result
import com.example.weatherapp.models.error.WeatherErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import retrofit2.HttpException
import retrofit2.Response

fun <T> Result<T>.checkStatus(
    onSuccess: (T) -> Unit,
    onError: (Result.Error<T>) -> Unit,
) {
    when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(this)
    }
}

fun <T, R> Result<T>.mapSuccess(mapper: (T) -> R): Result<R> =
    if (this is Result.Success) {
        Result.Success(mapper(data))
    } else {
        Result.Error((this as Result.Error).message)
    }

suspend fun <T> getResult(
    getData: suspend () -> Response<T>,
): Result<T> {
    return try {
        val apiResult = getData.invoke()
        Result.Success(apiResult.body()!!)
    } catch (e: Exception) {
        if (e is HttpException) {
            mapError(e)
        } else {
            Result.Error(e.message)
        }
    }
}

fun <T> mapError(exception: HttpException): Result.Error<T> {
    val gson = Gson()
    val fromJson =
        gson.fromJson(exception.response()?.errorBody()?.string(),
            WeatherErrorResponse::class.java)
    return Result.Error(fromJson.message)
}


fun <T> Flow<T>.mapToResult(): Flow<Result<T>> {
    return this.transform {
        it.toString()
        if (it != null) {
            emit(Result.Success(it))
        } else {
            emit(Result.Error())
        }
    }
}

suspend inline fun <T> Flow<Result<T>>.collectResult(
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Result.Error<T>) -> Unit,
) {
    this@collectResult.collect {
        when (it) {
            is Result.Success -> onSuccess(it.data)
            is Result.Error -> onError(it)
        }
    }
}

suspend inline fun <T> Result<T>.flowOfResult(
    onSuccess: (T) -> Result<Unit>,
    onError: () -> Result<Unit>,
): Flow<Result<Unit>> {
    return when (this) {
        is Result.Success -> flowOf(onSuccess(data))
        is Result.Error -> flowOf(Result.Error(this.message))
    }
}

