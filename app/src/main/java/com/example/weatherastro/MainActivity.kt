package com.example.weatherastro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.View.ForecastDayDetail
import com.example.weatherastro.View.WeatherDetail
import com.example.weatherastro.View.HomePage
import com.example.weatherastro.ViewModel.WeatherVM
import com.example.weatherastro.ui.navigation.Route
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : ComponentActivity()
{
    val mWeatherModel : WeatherVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        mWeatherModel.forecastApiResponse.value = ApiState.Loading
        val savedLocation = LoadSavedLocation(this)

        if (savedLocation != null) {
            val (lat, lon) = savedLocation
            val locationString = "$lat,$lon"
            mWeatherModel.GetData(locationString)
            Log.d("Location", "Exist Location, Loaded location -> lat=$lat , lon=$lon")
        } else {
            GetCurrentLocation(this) { lat, lon ->
                val locationString = "$lat,$lon"
                SaveLocation(this, lat, lon)
                mWeatherModel.GetData(locationString)
                Log.d("Location", "No Location, Get via GPS location -> lat=$lat , lon=$lon")
            }
        }
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
                        onBackPress ={ mNavController.popBackStack()})
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            GetCurrentLocation(this) { lat, lon ->
                mWeatherModel.GetData("$lat,$lon")
                Log.d("Location", "First time Get location via GPS  -> lat=$lat , lon=$lon")
            }
        }
    }
}
@SuppressLint("MissingPermission")
fun GetCurrentLocation(context: Context, onResult: (lat: Double, lon: Double) -> Unit)
{
    val fused = LocationServices.getFusedLocationProviderClient(context) //Google Play Services(GPS + Wifi + Cell)

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
        Log.d("Location", "Request Permission Finish");
        return
    }

    fused.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            Log.d("Location", "GPS: Get Location Cache Success");
            onResult(location.latitude, location.longitude)
        }
        else{
            Log.d("Location", "GPS: Get Location Failed, New Request Location Start");
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                0L
            )
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()

            fused.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location = result.lastLocation
                        if (location != null) {
                            Log.d("Location", "GPS: Get Location Success")
                            onResult(location.latitude, location.longitude)
                        }
                        fused.removeLocationUpdates(this)
                    }
                },
                Looper.getMainLooper()
            )
        }
    }
}

fun SaveLocation(context: Context, lat: Double, lon: Double) {
    val pref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    pref.edit()
        .putFloat("lat", lat.toFloat())
        .putFloat("lon", lon.toFloat())
        .apply()
}

fun LoadSavedLocation(context: Context): Pair<Double, Double>? {
    val pref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    if (!pref.contains("lat")) return null
    val lat = pref.getFloat("lat", 0f).toDouble()
    val lon = pref.getFloat("lon", 0f).toDouble()
    return lat to lon
}