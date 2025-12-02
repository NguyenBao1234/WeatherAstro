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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import com.example.weatherastro.Model.Forecast.Day
import com.example.weatherastro.Model.Forecast.ForecastDay
import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.R
import com.example.weatherastro.ViewModel.WeatherVM
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable()
fun ForecastDayDetail (inWeatherVM : WeatherVM, dayIndex : Int, onBackPress : ()-> Unit)
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
            is ApiState.Success<ForecastModel> -> ForecastDetailPage(result.dataInstance.forecast.forecastday[dayIndex])
            null ->{}
        }
        IconButton(onClick = {onBackPress()},
            modifier = Modifier.padding(top = 25.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
        }
    }
}
@Composable
fun ForecastDetailPage(forecastDay: ForecastDay)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${forecastDay.day.condition.icon}".replace("64x64","128x128"),
            contentDescription = ""
        )
        Text(
            text = "${forecastDay.day.avgtemp_c}°C",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val dateStr = forecastDay.date  // "yyyy-MM-dd"
        val today = LocalDate.now()
        val forecastDate = LocalDate.parse(dateStr)
        val isToday = forecastDate.isEqual(today)
        val time = if (isToday) {
            val now = LocalDateTime.now()
            dateStr + " " + now.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            "$dateStr 00:00"
        }

        DetailForecast(forecastDay.day)
        HourlyForecastPage(forecastDay.hour, time, !isToday )
    }
}
@Composable
fun DetailForecast(data: Day) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
            .background(Color(0x4DFFFFFF), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Chi tiết dự báo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Nhiệt độ
        WeatherDetailItem(
            label = "Nhiệt độ trung bình",
            value = "${data.avgtemp_c}°C / ${data.avgtemp_f}°F"
        )

        WeatherDetailItem(
            label = "Nhiệt độ cao nhất",
            value = "${data.maxtemp_c}°C / ${data.maxtemp_f}°F"
        )

        WeatherDetailItem(
            label = "Nhiệt độ thấp nhất",
            value = "${data.mintemp_c}°C / ${data.mintemp_f}°F"
        )

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Độ ẩm và gió
        WeatherDetailItem(
            label = "Độ ẩm trung bình",
            value = "${data.avghumidity}%"
        )

        WeatherDetailItem(
            label = "Tốc độ gió tối đa",
            value = "${data.maxwind_kph} km/h (${data.maxwind_mph} mph)"
        )

        WeatherDetailItem(
            label = "Tầm nhìn trung bình",
            value = "${data.avgvis_km} km (${data.avgvis_miles} miles)"
        )

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Mưa và tuyết
        WeatherDetailItem(
            label = "Khả năng có mưa",
            value = "${data.daily_chance_of_rain}%"
        )

        if (data.totalprecip_mm.toDoubleOrNull() ?: 0.0 > 0) {
            WeatherDetailItem(
                label = "Lượng mưa",
                value = "${data.totalprecip_mm} mm (${data.totalprecip_in} in)"
            )
        }

        if (data.daily_chance_of_snow.toIntOrNull() ?: 0 > 0) {
            WeatherDetailItem(
                label = "Khả năng có tuyết",
                value = "${data.daily_chance_of_snow}%"
            )
        }

        if (data.totalsnow_cm.toDoubleOrNull() ?: 0.0 > 0) {
            WeatherDetailItem(
                label = "Lượng tuyết",
                value = "${data.totalsnow_cm} cm"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // UV
        WeatherDetailItem(
            label = "Chỉ số UV",
            value = data.uv.toString(),
            uvIndex = data.uv
        )
    }
}

@Composable
private fun WeatherDetailItem(
    label: String,
    value: String,
    uvIndex: Float? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            // Hiển thị mức độ UV bằng màu
            uvIndex?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when {
                                it <= 2 -> Color(0xFF4CAF50) // Thấp - Xanh lá
                                it <= 5 -> Color(0xFFFFEB3B) // Trung bình - Vàng
                                it <= 7 -> Color(0xFFFF9800) // Cao - Cam
                                it <= 10 -> Color(0xFFF44336) // Rất cao - Đỏ
                                else -> Color(0xFF9C27B0) // Cực cao - Tím
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}