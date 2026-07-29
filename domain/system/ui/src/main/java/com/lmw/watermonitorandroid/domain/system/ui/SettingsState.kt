package com.lmw.watermonitorandroid.domain.system.ui

import com.lmw.watermonitorandroid.domain.system.api.DeviceInfo
import com.lmw.watermonitorandroid.domain.system.api.ServerAddress

data class SettingsState(
    val serverAddress: ServerAddress = ServerAddress(),
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val isTesting: Boolean = false,
    val testResult: Boolean? = null,
    val isSaving: Boolean = false,
    val editIp: String = "",
    val editPort: String = "",
    val isEditing: Boolean = false,
    val isLoadingInfo: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)