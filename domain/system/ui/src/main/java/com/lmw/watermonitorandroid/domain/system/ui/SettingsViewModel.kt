package com.lmw.watermonitorandroid.domain.system.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmw.watermonitorandroid.domain.system.api.ServerAddress
import com.lmw.watermonitorandroid.domain.system.api.SystemService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val systemService: SystemService
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadServerAddress()
    }

    private fun loadServerAddress() {
        viewModelScope.launch {
            systemService.getServerAddress().collect { address ->
                _state.value = _state.value.copy(
                    serverAddress = address,
                    editIp = address.ip,
                    editPort = address.port.toString()
                )
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isTesting = true, testResult = null)
            systemService.testConnection()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isTesting = false,
                        testResult = false,
                        errorMessage = e.message ?: "连接失败"
                    )
                }
                .collect { result ->
                    _state.value = _state.value.copy(
                        isTesting = false,
                        testResult = result
                    )
                }
        }
    }

    fun loadDeviceInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingInfo = true, errorMessage = null)
            systemService.getDeviceInfo()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoadingInfo = false,
                        errorMessage = e.message ?: "获取设备信息失败"
                    )
                }
                .collect { info ->
                    _state.value = _state.value.copy(
                        deviceInfo = info,
                        isLoadingInfo = false
                    )
                }
        }
    }

    fun startEditing() {
        _state.value = _state.value.copy(
            isEditing = true,
            editIp = _state.value.serverAddress.ip,
            editPort = _state.value.serverAddress.port.toString()
        )
    }

    fun cancelEditing() {
        _state.value = _state.value.copy(isEditing = false)
    }

    fun updateEditIp(ip: String) {
        _state.value = _state.value.copy(editIp = ip)
    }

    fun updateEditPort(port: String) {
        _state.value = _state.value.copy(editPort = port)
    }

    fun saveServerAddress() {
        val ip = _state.value.editIp.trim()
        val port = _state.value.editPort.trim().toIntOrNull() ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            systemService.saveServerAddress(ServerAddress(ip = ip, port = port))
                .catch { e ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "保存失败"
                    )
                }
                .collect { success ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        isEditing = false
                    )
                    if (success) {
                        _state.value = _state.value.copy(
                            serverAddress = ServerAddress(ip = ip, port = port),
                            successMessage = "保存成功，重启应用后生效"
                        )
                    } else {
                        _state.value = _state.value.copy(
                            errorMessage = "保存失败"
                        )
                    }
                }
        }
    }

    fun dismissMessage() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}