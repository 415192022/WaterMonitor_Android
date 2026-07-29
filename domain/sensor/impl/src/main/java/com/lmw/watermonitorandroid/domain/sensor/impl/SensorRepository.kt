package com.lmw.watermonitorandroid.domain.sensor.impl

import android.util.Log
import com.lmw.watermonitorandroid.domain.sensor.api.ConnectionState
import com.lmw.watermonitorandroid.domain.sensor.api.SensorData
import com.lmw.watermonitorandroid.domain.sensor.api.SensorService
import com.lmw.watermonitorandroid.domain.sensor.api.ServerConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepository @Inject constructor(
    private val sseClient: SseClient,
    private val gson: Gson
) : SensorService {

    companion object {
        private const val TAG = "SensorRepo"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    private val _sensorData = MutableStateFlow(SensorData())
    private var isConnected = false
    private var connectionJob: kotlinx.coroutines.Job? = null

    override fun observeSensorData(): Flow<SensorData> {
        ensureConnection()
        return _sensorData.asStateFlow()
    }

    override fun getConnectionState(): Flow<ConnectionState> {
        return _connectionState.asStateFlow()
    }

    override fun reconnect() {
        if (isConnected) return
        Log.d(TAG, "reconnect triggered")
        ensureConnection()
    }

    private fun ensureConnection() {
        if (isConnected) return
        isConnected = true
        _connectionState.value = ConnectionState.Connecting

        connectionJob = scope.launch {
            sseClient.connect(ServerConfig.sseUrl())
                .map { data ->
                    Log.d(TAG, "received: $data")
                    _connectionState.value = ConnectionState.Connected
                    parseSensorData(data)
                }
                .catch { e ->
                    Log.e(TAG, "error: ${e.message}", e)
                    _connectionState.value = ConnectionState.Disconnected
                    isConnected = false
                }
                .collect { data ->
                    _sensorData.value = data
                }
        }
    }

    private fun parseSensorData(json: String): SensorData {
        return try {
            gson.fromJson(json, SensorData::class.java) ?: SensorData()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "parse error", e)
            SensorData()
        }
    }
}