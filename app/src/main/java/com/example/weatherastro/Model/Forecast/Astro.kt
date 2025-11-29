package com.example.weatherastro.Model.Forecast

data class Astro(
    val is_moon_up: Int,
    val is_sun_up: Int,
    // % be mat duoc chieu sang cua mat trang
    val moon_illumination: Int,
    val moon_phase: String,
    val moonrise: String,
    val moonset: String,
    val sunrise: String,
    //Mat troi lan
    val sunset: String
)