package com.example.weatherastro.Model.Forecast

data class ForecastDay(
    val date : String,
    val date_epoch : Double,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)