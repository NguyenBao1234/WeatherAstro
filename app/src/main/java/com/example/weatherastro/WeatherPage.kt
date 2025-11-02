package com.example.weatherastro


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherastro.api.ApiState
import com.example.weatherastro.api.WeatherModel

@Composable
fun WeatherPage(inViewModel : WeatherVM)
{
    val WeatherResponseState = inViewModel.mWeatherResponse.observeAsState()
    val LocalController = LocalSoftwareKeyboardController.current
    var mCity by remember { mutableStateOf("") }
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly)
        {
            OutlinedTextField(
                modifier = Modifier.weight(1F),
                value = mCity,
                onValueChange = { mCity = it },
                label = {Text("search address")},

            )

            IconButton({
                inViewModel.GetData(mCity)
                LocalController?.hide()
            })
            {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search address")
            }

        }
        //___________________________________________
        when(val result = WeatherResponseState.value)
        {
            is ApiState.Error -> Text(result.message)
            ApiState.Loading -> CircularProgressIndicator()
            is ApiState.Success<WeatherModel> -> DrawWeatherDetails(result.dataInstance)
            null ->{}
        }
    }
}
@Composable
fun DrawWeatherDetails(inWeatherData: WeatherModel)
{
    Column (modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom)
        {
            Icon(imageVector = Icons.Default.LocationOn, "Location")
            Text(inWeatherData.location.name, fontSize = 30.sp)
            Spacer( Modifier.width(30.dp))
            Text(inWeatherData.location.country, fontSize = 25.sp)
        }
        //___________________________________________
        Spacer( Modifier.height(48.dp))
        Text("${inWeatherData.current.temp_c} °C", fontSize = 48.sp)
        //___________________________________________
        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${inWeatherData.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = ""
        )
        Log.i("Log", inWeatherData.current.condition.icon)
        //___________________________________________
        Text(inWeatherData.current.condition.text, color = Color.Gray)
        //___________________________________________
        Spacer(Modifier.height(15.dp))
        Card {
            Column (Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceAround)
            {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
                {
                    DrawColumnProperty("Humidity", inWeatherData.current.humidity.toString())
                    DrawColumnProperty("Wind Speed", inWeatherData.current.wind_kph.toString())
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
                {
                    DrawColumnProperty("UV", inWeatherData.current.uv.toString())
                    DrawColumnProperty("Wind Direction", inWeatherData.current.wind_dir)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
                {
                    DrawColumnProperty("Date", inWeatherData.location.localtime.split(" ")[0])

                }
            }
        }
    }
}
@Composable
fun DrawColumnProperty(inProperty:String, inValue:String)
{
    Column( Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(inValue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(inProperty, color = Color.Gray)
    }
}