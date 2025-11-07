package com.example.weatherastro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherastro.View.WeatherDetail
import com.example.weatherastro.View.HomePage
import com.example.weatherastro.ViewModel.WeatherVM
import com.example.weatherastro.ui.navigation.Route


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
                composable(Route.Home) { HomePage(mWeatherModel, onDetailClick = {
                    mNavController.navigate(Route.WeatherDetail)} )
                }
                composable(Route.WeatherDetail) {  WeatherDetail(mWeatherModel, onBackPress ={
                    mNavController.navigate(Route.Home)})
                }
            })
        }
    }
}


