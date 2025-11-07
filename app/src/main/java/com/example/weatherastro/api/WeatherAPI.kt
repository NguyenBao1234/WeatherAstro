package com.example.weatherastro.api

import com.example.weatherastro.Model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI
{
    @GET("/v1/current.json")
    suspend fun GetWeatherData(@Query("key") inApiKey : String, @Query("q")inCityName:String): Response<WeatherModel>

}