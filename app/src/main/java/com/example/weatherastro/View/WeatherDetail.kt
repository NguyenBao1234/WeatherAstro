package com.example.weatherastro.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.Model.WeatherModel
import com.example.weatherastro.ViewModel.WeatherVM

@Composable()
fun WeatherDetail (inWeatherVM : WeatherVM, onBackPress : ()-> Unit)
{
    val WeatherResponseState = inWeatherVM.mWeatherResponse.observeAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = {onBackPress()}) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
        }
        Text("Detail")
        when(val result = WeatherResponseState.value)
        {
            is ApiState.Error -> Text(result.message)
            is ApiState.Loading -> CircularProgressIndicator()
            is ApiState.Success<WeatherModel> -> Text("Hello")
            null ->{}
        }
    }
}