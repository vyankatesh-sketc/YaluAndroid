package com.yalu.india.core

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val statusCode: Int? = null, val throwable: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
    data object Idle : NetworkResult<Nothing>
}
