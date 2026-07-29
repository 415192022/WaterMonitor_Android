package com.lmw.watermonitorandroid.domain.sensor.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmw.watermonitorandroid.domain.sensor.api.ConnectionState
import com.lmw.watermonitorandroid.domain.sensor.api.SensorData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(
    viewModel: MonitorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("水质监控") }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectionStatusCard(connectionState = state.connectionState)
                SensorDataCard(data = state.sensorData)
                RainbowGauge(
                    rings = listOf(
                        RingConfig(label = "TDS", value = state.sensorData.tds, maxValue = 1000f, color = Color(0xFFE53935), unit = "ppm"),
                        RingConfig(label = "水位", value = state.sensorData.level, maxValue = 100f, color = Color(0xFFFF9800), unit = "cm"),
                        RingConfig(label = "压力", value = state.sensorData.press, maxValue = 100f, color = Color(0xFF1E88E5), unit = "kPa"),
                        RingConfig(label = "湿度", value = state.sensorData.humi, maxValue = 100f, color = Color(0xFF43A047), unit = "%"),
                        RingConfig(label = "温度", value = state.sensorData.temp, maxValue = 50f, color = Color(0xFF8E24AA), unit = "°C")
                    ),
                    centerContent = {
                        val (text, bgColor, textColor) = when (state.sensorData.status) {
                            3 -> Triple("危险", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                            2 -> Triple("警告", Color(0xFFFFF8E1), Color(0xFFFF9800))
                            1 -> Triple("正常", Color(0xFFE8F5E9), Color(0xFF4CAF50))
                            else -> Triple("离线", Color(0xFFFAFAFA), Color(0xFF9E9E9E))
                        }
                        val animatedBgColor by animateColorAsState(
                            targetValue = bgColor,
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 500),
                            label = "statusBg"
                        )
                        val animatedTextColor by animateColorAsState(
                            targetValue = textColor,
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 500),
                            label = "statusText"
                        )
                        val scale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = keyframes {
                                durationMillis = 400
                                0.7f at 0
                                1.15f at 150
                                0.95f at 280
                                1f at 400
                            },
                            label = "statusScale"
                        )
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = scale; scaleY = scale }
                                .background(color = animatedBgColor, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = text, color = animatedTextColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(connectionState: ConnectionState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (connectionState) {
                ConnectionState.Connected -> Color(0xFFE8F5E9)
                ConnectionState.Connecting -> Color(0xFFFFF8E1)
                ConnectionState.Disconnected -> Color(0xFFFFEBEE)
                ConnectionState.WifiNotConnected -> Color(0xFFFFEBEE)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (connectionState) {
                ConnectionState.Connected -> {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Color(0xFF4CAF50)
                    }
                }
                ConnectionState.Connecting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
                else -> {
                    Box(modifier = Modifier.size(12.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = when (connectionState) {
                    ConnectionState.Connected -> "已连接"
                    ConnectionState.Connecting -> "连接中..."
                    ConnectionState.Disconnected -> "已断开"
                    ConnectionState.WifiNotConnected -> "WiFi未连接"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SensorDataCard(data: SensorData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "传感器数据",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SensorRow(label = "TDS", value = "${data.tds}", unit = "ppm")
            SensorRow(label = "水位", value = "${data.level}", unit = "cm")
            SensorRow(label = "压力", value = "${data.press}", unit = "kPa")
            SensorRow(label = "温度", value = "${data.temp}", unit = "°C")
            SensorRow(label = "湿度", value = "${data.humi}", unit = "%")
            SystemStatusRow(status = data.status)
        }
    }
}

@Composable
private fun SensorRow(label: String, value: String, unit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SystemStatusRow(status: Int) {
    val (text, color) = when (status) {
        3 -> "危险" to Color(0xFFD32F2F)
        2 -> "警告" to Color(0xFFFF9800)
        1 -> "正常" to Color(0xFF4CAF50)
        else -> "离线" to Color(0xFF9E9E9E)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "系统状态",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}