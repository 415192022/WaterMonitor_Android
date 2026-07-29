package com.lmw.watermonitorandroid.domain.system.api

data class ServerAddress(
    val ip: String = "192.168.4.1",
    val port: Int = 80
) {
    val baseUrl: String get() = "http://$ip:$port/"
    val sseUrl: String get() = "http://$ip:$port/api/stream"
}