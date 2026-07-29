package com.lmw.watermonitorandroid.domain.system.impl.api

import com.lmw.watermonitorandroid.domain.system.api.DeviceInfo
import retrofit2.http.GET

interface SystemApi {

    @GET("api/files")
    suspend fun testConnection(): Boolean

    @GET("api/info")
    suspend fun getDeviceInfo(): DeviceInfo
}