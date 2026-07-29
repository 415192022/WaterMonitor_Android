package com.lmw.watermonitorandroid.domain.sensor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmw.watermonitorandroid.domain.sensor.api.ConnectionState
import com.lmw.watermonitorandroid.domain.sensor.api.SensorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val sensorService: SensorService
) : ViewModel() {

    private val _state = MutableStateFlow(MonitorState())
    val state: StateFlow<MonitorState> = _state.asStateFlow()

    init {
        observeConnectionState()
        observeSensorData()
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            sensorService.getConnectionState().collect { connectionState ->
                _state.value = _state.value.copy(connectionState = connectionState)
            }
        }
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            sensorService.observeSensorData()
                .catch { e ->
                    _state.value = _state.value.copy(
                        connectionState = ConnectionState.Disconnected,
                        isRefreshing = false
                    )
                }
                .collect { sensorData ->
                    _state.value = _state.value.copy(
                        sensorData = sensorData,
                        isRefreshing = false
                    )
                }
        }
    }

    fun refresh() {
        _state.value = _state.value.copy(isRefreshing = true)
        if (_state.value.connectionState == ConnectionState.Disconnected) {
            sensorService.reconnect()
        }
    }
}