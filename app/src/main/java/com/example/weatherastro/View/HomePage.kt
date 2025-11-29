package com.example.weatherastro.View


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherastro.ViewModel.WeatherVM
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.Model.WeatherModel
import com.example.weatherastro.R

@Composable
fun HomePage(inViewModel : WeatherVM, onDetailClick : () -> Unit)
{
    val WeatherResponseState = inViewModel.forecastResponse.observeAsState()

    var mCity by remember { mutableStateOf("") }
    val LocalController = LocalSoftwareKeyboardController.current
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .paint(painter = painterResource(id = R.drawable.home_page))
            .verticalScroll(rememberScrollState(0)),

        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly)
        {
            OutlinedTextField(
                modifier = Modifier.weight(1F),
                value = mCity,
                singleLine = true,
                onValueChange = { mCity = it },
                label = {Text("search address")},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search  // Hiển thị nút Search trên bàn phím
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        inViewModel.GetData(mCity)
                        LocalController?.hide()
                    }
                )
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
            is ApiState.Loading -> CircularProgressIndicator()
            is ApiState.Success<ForecastModel> -> DrawWeatherOverview(result.dataInstance, onDetailClick)
            null ->{}
        }
    }
}
@Composable
fun DrawWeatherOverview(inWeatherData: ForecastModel, onDetailClick : () -> Unit)
{
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally)
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
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onDetailClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0x4DFFFFFF)),
            border = BorderStroke(2.dp, Color.White)
        ){
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
    Column( Modifier
        .padding(16.dp)
        , horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(inValue, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(inProperty, color = Color.White)
    }
}