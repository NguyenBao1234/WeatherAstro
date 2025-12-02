package com.example.weatherastro.View


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.weatherastro.Model.Forecast.ForecastDay

import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.Model.Forecast.Hour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun WeatherForecastScreen(inWeatherData: ForecastModel, OnWeeklyCardClick: (Int) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dự báo theo giờ", "Dự báo theo tuần")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFF1A1A2E),
//                        Color(0xFF16213E),
//                        Color(0xFF0F3460)
//                    )
//                )
                shape = RoundedCornerShape(15.dp),
                color = Color(0x4D0F3460)
            )
            .padding(16.dp),
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            divider = {
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp
                )
            },
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .padding(horizontal = 24.dp)
                        .background(
                            Color(0xFF00335D),
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
            }

        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content
        when (selectedTab) {
            0 -> HourlyForecastPage(inWeatherData.forecast.forecastday[0].hour,inWeatherData.location.localtime )
            1 -> WeeklyForecastContent(inWeatherData, {dayIndexParam->OnWeeklyCardClick(dayIndexParam)})
        }
    }
}
@Composable
fun HourlyForecastPage(inHourData: List<Hour>, inLocaltime: String, isForecastDay : Boolean = false) {
    var selectedHour by remember { mutableStateOf<Hour?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val localTime = LocalDateTime.parse(inLocaltime, formatter)

    val futureHours = inHourData.filter {
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
            HourlyForecastCard(hour, isNow, isForecastDay, { hourParam->
                selectedHour = hourParam})
        }
    }
    selectedHour?.let { hour ->
        HourDetailDialog(
            hour = hour,
            onDismiss = { selectedHour = null }
        )
    }
}

@Composable
fun HourlyForecastCard(hour: Hour, isNow: Boolean = false, isForecastDay : Boolean, OnClick:(Hour) -> Unit) {
    val time = hour.time.split(" ")[1] // Lấy phần giờ từ "2024-01-01 14:00"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .background(
                if (isNow) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = {OnClick(hour)})
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        // Time
        Text(
            text = if (isNow && !isForecastDay) "Bây giờ" else time,
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
                1003, 1006 -> if(isDay) "⛅" else "☁️"     // Partly cloudy, cloudy
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
fun WeeklyForecastContent(inWeatherData: ForecastModel, OnWeeklyCardClick:(Int)-> Unit ) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val isDay = (inWeatherData.current.is_day==1)
            items(inWeatherData.forecast.forecastday.count()){ index ->
                val forcastDay = inWeatherData.forecast.forecastday[index]
                WeeklyForecastCard(forcastDay, index, isDay, {OnWeeklyCardClick(index)} )
            }
        }
    }
}

@Composable
fun WeeklyForecastCard(forecastDay: ForecastDay, indexDay : Int, isDay : Boolean, OnCardClick: () -> Unit) {

//
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
            .clickable(onClick = {OnCardClick()})
    ) {
        // Time
        Text(
            text = if (isToday) "Hôm nay" else dayOfWeek,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        val isDay = !isToday || isDay
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


@Composable
fun HourDetailDialog(hour: Hour, onDismiss: () -> Unit) {
    val time = hour.time.split(" ")[1]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213E)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chi tiết lúc $time",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = Color.White, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Temperature & Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherIcon(
                        conditionCode = hour.condition.code,
                        isDay = hour.is_day == "1",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${hour.temp_c.toDouble().toInt()}°C",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = hour.condition.text,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Weather Details Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WeatherDetailRow("Cảm giác như", "${hour.feelslike_c.toDouble().toInt()}°C")
                    WeatherDetailRow("Độ ẩm", "${hour.humidity}%")
                    WeatherDetailRow("Tốc độ gió", "${hour.wind_kph} km/h")
                    WeatherDetailRow("Hướng gió", hour.wind_dir)
                    WeatherDetailRow("Tầm nhìn", "${hour.vis_km} km")
                    WeatherDetailRow("Áp suất", "${hour.pressure_mb} mb")
                    WeatherDetailRow("Lượng mưa", "${hour.precip_mm} mm")
                    WeatherDetailRow("Xác suất mưa", "${hour.chance_of_rain}%")
                    WeatherDetailRow("Chỉ số UV", hour.uv)
                    WeatherDetailRow("Mây", "${hour.cloud}%")
                }
            }
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}