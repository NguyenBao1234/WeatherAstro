package com.example.weatherastro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.weatherastro.ui.theme.WeatherAstroTheme

class MainActivity : ComponentActivity()
{
    val mWeatherModel : WeatherVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAstroTheme {
                Surface (modifier = Modifier.fillMaxSize(),color = MaterialTheme.colorScheme.background)
                {
                    WeatherPage(mWeatherModel)
                }
            }
        }
    }
}


