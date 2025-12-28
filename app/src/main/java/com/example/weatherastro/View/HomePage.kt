package com.example.weatherastro.View


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.weatherastro.R
import com.example.weatherastro.SaveLocation

@Composable
fun HomePage(inViewModel : WeatherVM, onDetailClick : () -> Unit, OnWeeklyForcastClick:(Int) -> Unit )
{
    val WeatherResponseState = inViewModel.forecastResponse.observeAsState()

    var mCity by remember { mutableStateOf("") }
    val LocalController = LocalSoftwareKeyboardController.current

    val bgImage = when (val result = WeatherResponseState.value) {
        is ApiState.Success<ForecastModel> -> {
            val currentWeather = result.dataInstance.current
            val conditionCode = currentWeather.condition.code
            val isDay = currentWeather.is_day == 1
            getBackgroundByCode(conditionCode, isDay)
        }
        else -> R.drawable.home_page
    }

    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
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
                            focusManager.clearFocus()
                        }
                    )
                )

                IconButton({
                    inViewModel.GetData(mCity)
                    LocalController?.hide()
                    focusManager.clearFocus()
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
                is ApiState.Success<ForecastModel> -> DrawWeatherOverview(result.dataInstance, onDetailClick, OnWeeklyForcastClick)
                null ->{}
            }
        }
    }

}
@Composable
fun DrawWeatherOverview(inWeatherData: ForecastModel, onDetailClick : () -> Unit, OnWeeklyCardClick: (Int) -> Unit)
{
    SaveLocation(LocalContext.current,inWeatherData.location.lat, inWeatherData.location.lon)
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
        Text(inWeatherData.current.condition.text)
        //___________________________________________
        Spacer(Modifier.height(15.dp))
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onDetailClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0x4DFFFFFF)),
            border = BorderStroke(1.dp, Color.White)
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
        WeatherForecastScreen(inWeatherData,{dayIndexParam->OnWeeklyCardClick(dayIndexParam)})
        Spacer(Modifier.height(80.dp))
    }
}
@Composable
fun DrawColumnProperty(inProperty:String, inValue:String)
{
    Column( Modifier
        .padding(16.dp)
        , horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(inValue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(inProperty)
    }
}


fun getBackgroundByCode(inConditionCode: Int, isDay: Boolean): Int {
    return when (inConditionCode) {
        1000 -> if (isDay) R.drawable.sunny_background else R.drawable.home_page
        1003, 1006, 1009 -> R.drawable.cloud_background
        1150, 1153, 1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246, 1249, 1273, 1276 -> R.drawable.rain_background
        1066, 1210 -> R.drawable.snow_background
        else -> R.drawable.cloud_background
    }
}