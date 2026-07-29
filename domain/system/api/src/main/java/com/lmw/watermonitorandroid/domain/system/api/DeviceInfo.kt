package com.lmw.watermonitorandroid.domain.system.api

data class DeviceInfo(
    val firmwareVersion: String = "",
    val uptime: Long = 0,
    val signalStrength: Int = 0,
    val ssid: String = "",
    val ip: String = "",
    val macAddress: String = ""
)