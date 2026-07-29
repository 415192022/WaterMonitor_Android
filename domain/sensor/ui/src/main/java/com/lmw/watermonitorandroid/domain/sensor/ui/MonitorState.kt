package com.lmw.watermonitorandroid.domain.sensor.ui

import com.lmw.watermonitorandroid.domain.sensor.api.ConnectionState
import com.lmw.watermonitorandroid.domain.sensor.api.SensorData

data class MonitorState(
    val sensorData: SensorData = SensorData(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isRefreshing: Boolean = false
)