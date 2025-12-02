package com.example.weatherastro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.View.ForecastDayDetail
import com.example.weatherastro.View.WeatherDetail
import com.example.weatherastro.View.HomePage
import com.example.weatherastro.ViewModel.WeatherVM
import com.example.weatherastro.ui.navigation.Route
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity()
{
    val mWeatherModel : WeatherVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mNavController = rememberNavController()
            NavHost(navController = mNavController, startDestination = Route.Home, builder = {
                composable(Route.Home) {
                    HomePage(mWeatherModel,
                            onDetailClick = {mNavController.navigate(Route.WeatherDetail)},
                        OnWeeklyForcastClick = {dayIndexParam->
                            mNavController.navigate(Route.forecastDayRoute(dayIndexParam))
                        })
                }
                composable(Route.WeatherDetail) {
                    WeatherDetail(mWeatherModel,
                        onBackPress ={mNavController.navigate(Route.Home)})
                }
                composable(
                    route = Route.ForcastDayDetail,
                    arguments = listOf(navArgument("dayIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0

                    ForecastDayDetail(
                        inWeatherVM = mWeatherModel,
                        dayIndex = dayIndex,
                        onBackPress = { mNavController.popBackStack() }
                    )
                }
            })
        }
    }
}
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onResult: (lat: Double, lon: Double) -> Unit) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            100
        )
        return
    }

    fused.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onResult(location.latitude, location.longitude)
        }
    }
}

fun saveLocation(context: Context, lat: Double, lon: Double) {
    val pref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    pref.edit()
        .putFloat("lat", lat.toFloat())
        .putFloat("lon", lon.toFloat())
        .apply()
}

fun loadSavedLocation(context: Context): Pair<Double, Double>? {
    val pref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    if (!pref.contains("lat")) return null
    val lat = pref.getFloat("lat", 0f).toDouble()
    val lon = pref.getFloat("lon", 0f).toDouble()
    return lat to lon
}
