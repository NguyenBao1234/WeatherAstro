package com.example.weatherastro.ui.navigation

object Route
{
    val Home = "home"
    val WeatherDetail = "weatherDetail"
    val ForcastDayDetail = "forecast_day_detail/{dayIndex}"

    fun forecastDayRoute(dayIndex: Int) = "forecast_day_detail/$dayIndex"
}