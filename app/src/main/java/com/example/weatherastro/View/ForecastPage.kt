package com.example.weatherastro.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherastro.Model.Forecast.ForecastDay
import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.Model.Forecast.Hour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HourlyForecastPage(inWeatherData: ForecastModel) {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val localTime = LocalDateTime.parse(inWeatherData.location.localtime, formatter)
    val hourlyData = inWeatherData.forecast.forecastday[0].hour
    val futureHours = hourlyData.filter {
        val hourTime = LocalDateTime.parse(it.time, formatter)
        hourTime.hour >= localTime.hour
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(futureHours.size){ index ->
            val hour = futureHours[index]
            val hourTime = LocalDateTime.parse(hour.time, formatter)
            val isNow = hourTime.hour == localTime.hour
            HourlyForecastCard(hour, isNow)
        }
    }
}

@Composable
fun HourlyForecastCard(hour: Hour, isNow: Boolean = false) {
    val time = hour.time.split(" ")[1] // Lấy phần giờ từ "2024-01-01 14:00"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .background(
                if (isNow) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        // Time
        Text(
            text = if (isNow) "Bây giờ" else time,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Weather Icon
        WeatherIcon(
            conditionCode = hour.condition.code,
            isDay = hour.is_day == "1",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Temperature
        Text(
            text = "${hour.temp_c}°C",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherIcon(conditionCode: Int, isDay: Boolean, modifier: Modifier = Modifier) {
    // Icon for glow condition
    val iconGlowColor = if (isDay) Color(0xFFFDB813) else Color(0xFFE8E8E8)

    Box(
        modifier = modifier
            .background(iconGlowColor.copy(alpha = 0.2f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // can use condition.icon URL instead
        Text(
            text = when (conditionCode) {
                1000 -> if (isDay) "☀️" else "🌙"          // Sunny / Clear
                1003, 1006 -> if(isDay) "⛅" else "☁️"                         // Partly cloudy, cloudy
                1009 -> "☁️"                               // Overcast
                1150, 1153, 1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246, 1249, 1273, 1276 -> "🌧️"
                1066, 1210 -> "🌨️"                         // Snow
                else -> "☁️"
            },
            fontSize = 24.sp
        )
    }
}
@Composable
fun WeeklyForecastContent(inWeatherData: ForecastModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(inWeatherData.forecast.forecastday.count()){ index ->
                WeeklyForecastCard(inWeatherData, index)
            }
        }
    }
}

@Composable
fun WeeklyForecastCard(forecastModel: ForecastModel, indexDay : Int) {

//
    val forecastDay = forecastModel.forecast.forecastday[indexDay]
    val isToday = indexDay == 0

    val dayOfWeek = convertDateToDayOfWeek(forecastDay.date)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .background(
                if (isToday) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        // Time
        Text(
            text = if (isToday) "Hôm nay" else dayOfWeek,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        val isDay = !isToday || forecastModel.current.is_day == 1
        // Weather Icon
        WeatherIcon(
            conditionCode = forecastDay.day.condition.code,
            isDay = isDay,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Temperature
        Text(
            text = "${forecastDay.day.avgtemp_c}°C",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatTime = LocalDate.parse(forecastDay.date, formatter)
        Text(
            text = "${formatTime.dayOfMonth} Tháng ${formatTime.monthValue}",
            color = Color.White,
            fontSize = 14.sp,
        )
    }
}
fun convertDateToDayOfWeek(dateString: String): String {


    // 1. Tạo đối tượng Locale tiếng Việt bằng hàm khởi tạo (Hỗ trợ tốt trên các API cũ)
    val vietnameseLocale: Locale = Locale("vi", "VN")
    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Dùng Locale.US để parsing format chuẩn quốc tế

    // Phân tích chuỗi thành LocalDate
    val date = LocalDate.parse(dateString, parser)

    // Định nghĩa bộ định dạng để hiển thị ngày trong tuần đầy đủ (EEEE)// Sau đó chuyển đổi sang tiếng Việt bằng Locale đã tạo
    val vietnameseFormatter = DateTimeFormatter.ofPattern("EEEE", vietnameseLocale)

    return date.format(vietnameseFormatter)
}