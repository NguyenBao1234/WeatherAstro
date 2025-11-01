package com.example.weatherastro.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObj
{
    private const val serviceURL ="http://api.weatherapi.com"
    private fun GetRetrofitInstance() : Retrofit
    {
        return Retrofit.Builder().baseUrl(serviceURL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}