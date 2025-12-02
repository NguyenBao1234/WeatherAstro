package com.example.weatherastro.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
        else -> R.drawable.home_page
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

        WeatherInfoBox1( inWeatherData)

        Spacer(modifier = Modifier.height(16.dp))

        // KHỐI THÔNG TIN 2 (info_box_2)
        WeatherInfoBox2(inWeatherData)
        HourlyForecastPage(inWeatherData.forecast.forecastday[0].hour, inWeatherData.location.localtime)
    }
}

@Composable
fun WeatherInfoBox1(inWheatherdata: ForecastModel)
{
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Giới hạn chiều rộng (tương đương 400dp)
            .padding(16.dp)
            // Tương đương android:background="@drawable/weather_info"
            .background(Color(0x4D0F3460), shape = RoundedCornerShape(12.dp))
    ) {
        DetailCard(
            value = "${inWheatherdata.current.humidity}%",
            label = "Độ ẩm",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .height(70.dp)
        )
        // GRID 1: 3 hàng, 2 cột (sử dụng Row và weight)
        Column {
            // Hàng 1
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(value = "${inWheatherdata.current.wind_kph} km/h", label = "Tốc độ gió", modifier = Modifier.weight(1f))
                GridItem(value = inWheatherdata.current.wind_dir, label = "Hướng gió", modifier = Modifier.weight(1f))
            }
            // Hàng 2
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(value = "${inWheatherdata.current.pressure_in} inHg", label = "Áp suất", modifier = Modifier.weight(1f))
                GridItem(value = "${inWheatherdata.current.cloud}%", label = "Mây che phủ", modifier = Modifier.weight(1f))
            }
            // Hàng 3
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(value = "${inWheatherdata.current.precip_mm}mm", label = "Lượng mưa", modifier = Modifier.weight(1f))
                GridItem(value = "${inWheatherdata.current.gust_kph} km/h", label = "Gió giật", modifier = Modifier.weight(1f))
            }
        }
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

@Composable
fun WeatherInfoBox2(data: ForecastModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Giới hạn chiều rộng
            .padding(16.dp)
            .background(Color(0x4DE0E0E0), shape = RoundedCornerShape(12.dp))
    ) {
        // GRID 2: 2 hàng, 2 cột
        Column {
            // Hàng 1
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(value = "${data.current.feelslike_c}°C", label = "Cảm thấy như", modifier = Modifier.weight(1f))
                GridItem(value = "${data.current.windchill_c}°C", label = "Nhiệt độ gió", modifier = Modifier.weight(1f))
            }
            // Hàng 2
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(value = "${data.current.uv}", label = "Chỉ số UV", modifier = Modifier.weight(1f))
                GridItem(value = "${data.current.vis_km} km", label = "Tầm nhìn xa", modifier = Modifier.weight(1f))
            }
        }
    }
}