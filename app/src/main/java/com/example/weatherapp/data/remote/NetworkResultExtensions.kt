package com.example.weatherapp.data.remote

import com.example.weatherapp.models.error.WeatherErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception

fun <T> NetworkResult<T>.checkResult(
    onSuccess: (T) -> Unit,
    onError: (NetworkResult.Error<T>) -> Unit,
) {
    when (this) {
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onError(this)
    }
}

fun <T, R> NetworkResult<T>.mapSuccess(mapper: (T) -> R): NetworkResult<R> =
    if (this is NetworkResult.Success) {
        NetworkResult.Success(mapper(data))
    } else {
        NetworkResult.Error((this as NetworkResult.Error).message)
    }

suspend fun <T> getResultData(
    getData: suspend () -> Response<T>,
): NetworkResult<T> {
    return try {
        val apiResult = getData.invoke()
        NetworkResult.Success(apiResult.body()!!)
    } catch (e: Exception) {
        if (e is HttpException) {
            mapError(e)
        } else {
            NetworkResult.Error(e.message)
        }
    }
}

fun <T> mapError(exception: HttpException): NetworkResult.Error<T> {
    val gson = Gson()
    val fromJson =
        gson.fromJson(exception.response()?.errorBody()?.string(),
            WeatherErrorResponse::class.java)
    return NetworkResult.Error(fromJson.message)
}

//fun <R, T> Flow<Result<R>>.mapResultData( mapToLocal: (R) -> T): Flow<Result<T>> {
//    this.transform {
//        if (it is Result.Success) {
//            emit(Result.Success(mapToLocal.invoke(it.data)))
//        } else {
//            emit(Result.Error<T>("D"))
//        }
//    }
//}