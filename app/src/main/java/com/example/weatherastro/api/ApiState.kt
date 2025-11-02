package com.example.weatherastro.api

sealed class ApiState<out T>
{
    data class Success<T>(val dataInstance: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
}