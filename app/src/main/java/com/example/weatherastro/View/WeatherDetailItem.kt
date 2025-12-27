package com.example.weatherastro.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun WeatherDetailItem(
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