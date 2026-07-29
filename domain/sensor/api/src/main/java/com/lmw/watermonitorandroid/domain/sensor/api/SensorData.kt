package com.lmw.watermonitorandroid.domain.sensor.api

data class SensorData(
    val tds: Float = 0f,
    val level: Float = 0f,
    val press: Float = 0f,
    val temp: Float = 0f,
    val humi: Float = 0f,
    val status: Int = 0
)