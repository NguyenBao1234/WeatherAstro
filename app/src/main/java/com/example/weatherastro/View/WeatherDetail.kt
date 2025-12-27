package com.example.weatherastro.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.Model.Forecast.ForecastModel

import com.example.weatherastro.ViewModel.WeatherVM
import com.example.weatherastro.R


@Composable()
fun WeatherDetail (inWeatherVM : WeatherVM, onBackPress : ()-> Unit)
{
    val WeatherResponseState = inWeatherVM.forecastResponse.observeAsState()
    val bgImage = when (val result = WeatherResponseState.value) {
        is ApiState.Success<ForecastModel> -> {
            val currentWeather = result.dataInstance.current
            val conditionCode = currentWeather.condition.code
            val isDay = currentWeather.is_day == 1
            getBackgroundByCode(conditionCode, isDay)
        }
        else -> R.drawable.home_page//<-this is a default image background
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    )
    {
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        when(val result = WeatherResponseState.value)
        {
            is ApiState.Error -> Text(result.message)
            is ApiState.Loading -> CircularProgressIndicator()
            is ApiState.Success<ForecastModel> -> DrawDetailPage(result.dataInstance)
            null ->{}
        }
        IconButton(onClick = {onBackPress()},
            modifier = Modifier.padding(top = 25.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
        }
    }
}
@Composable
fun DrawDetailPage(inWeatherData: ForecastModel)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${inWeatherData.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = ""
        )
        Text(
            text = "${inWeatherData.current.temp_c}°C",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        CurrentDetailsForecast( inWeatherData)

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(8.dp)
                .background(Color(0x00FFFFFF))
        ) {HourlyForecastPage(inWeatherData.forecast.forecastday[0].hour, inWeatherData.location.localtime)}
        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
fun CurrentDetailsForecast(inWheatherdata: ForecastModel)
{
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
            .background(Color(0x4DFFFFFF), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ){
        Text(
            text = "Chi tiết dự báo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        val current = inWheatherdata.current
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Nhiệt độ
        WeatherDetailItem(
            label = "Nhiệt độ hiện tại",
            value = "${current.temp_c}°C / ${current.temp_f}°F"
        )

        WeatherDetailItem(
            label = "Cảm giác như",
            value = "${current.feelslike_c}°C / ${current.feelslike_f}°F"
        )

        WeatherDetailItem(
            label = "Chỉ số nóng",
            value = "${current.heatindex_c}°C / ${current.heatindex_f}°F"
        )

        WeatherDetailItem(
            label = "Nhiệt độ gió lạnh",
            value = "${current.windchill_c}°C / ${current.windchill_f}°F"
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

// Độ ẩm & gió
        WeatherDetailItem(
            label = "Độ ẩm",
            value = "${current.humidity}%"
        )

        WeatherDetailItem(
            label = "Tốc độ gió",
            value = "${current.wind_kph} km/h (${current.wind_mph} mph)"
        )

        WeatherDetailItem(
            label = "Hướng gió",
            value = "${current.wind_dir} (${current.wind_degree}°)"
        )

        WeatherDetailItem(
            label = "Gió giật",
            value = "${current.gust_kph} km/h (${current.gust_mph} mph)"
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

// Tầm nhìn & áp suất
        WeatherDetailItem(
            label = "Tầm nhìn",
            value = "${current.vis_km} km (${current.vis_miles} miles)"
        )

        WeatherDetailItem(
            label = "Áp suất",
            value = "${current.pressure_mb} mb (${current.pressure_in} in)"
        )

        WeatherDetailItem(
            label = "Độ che phủ mây",
            value = "${current.cloud}%"
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

// Mưa
        if (current.precip_mm > 0) {
            WeatherDetailItem(
                label = "Lượng mưa",
                value = "${current.precip_mm} mm (${current.precip_in} in)"
            )
        }

// UV
        WeatherDetailItem(
            label = "Chỉ số UV",
            value = current.uv.toString(),
            uvIndex = current.uv
        )
    }
}

@Composable
fun DetailCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0x80E0E0E0), shape = RoundedCornerShape(8.dp)) // Màu trắng giả định cho mono_detail
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun GridItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(6.dp) // Tương đương margin 6dp
            .height(70.dp)
            .background(Color(0x4D0F3460), shape = RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}