package com.lmw.watermonitorandroid.domain.sensor.api

object ServerConfig {
    const val DEFAULT_IP = "192.168.4.1"
    const val DEFAULT_PORT = 80
    const val SSE_ENDPOINT = "/api/stream"
    const val WIFI_SSID = "WaterMonitor"

    fun sseUrl(ip: String = DEFAULT_IP, port: Int = DEFAULT_PORT): String {
        return "http://$ip:$port$SSE_ENDPOINT"
    }
}