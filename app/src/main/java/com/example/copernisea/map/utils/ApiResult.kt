package com.example.copernisea.map.utils

sealed class ApiResult<T> {
    data class Success<T>(
        val result: T
    ) : ApiResult<T>()

    data class Error<T>(
        val throwable: Throwable
    ) : ApiResult<T>()
}

suspend fun <T> getResult(block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    }catch (ex: Exception) {
        ApiResult.Error(ex)
    }
}