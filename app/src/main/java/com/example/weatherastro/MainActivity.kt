package com.example.weatherastro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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


