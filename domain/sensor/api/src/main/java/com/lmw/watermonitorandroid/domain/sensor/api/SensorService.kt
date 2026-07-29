package com.lmw.watermonitorandroid.domain.sensor.api

import kotlinx.coroutines.flow.Flow

interface SensorService {
    fun observeSensorData(): Flow<SensorData>
    fun getConnectionState(): Flow<ConnectionState>
    fun reconnect()
}