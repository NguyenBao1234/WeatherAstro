package com.example.weatherastro.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitObj
{
    private const val serviceURL ="https://api.weatherapi.com"
    private fun GetRetrofitInstance() : Retrofit
    {
        return Retrofit.Builder().baseUrl(serviceURL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    val WeatherApiInstance : WeatherAPI = GetRetrofitInstance().create<WeatherAPI>()
}