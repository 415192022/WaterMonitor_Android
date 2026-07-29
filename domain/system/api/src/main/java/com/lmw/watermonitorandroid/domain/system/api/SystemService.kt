package com.lmw.watermonitorandroid.domain.system.api

import kotlinx.coroutines.flow.Flow

interface SystemService {
    fun getDeviceInfo(): Flow<DeviceInfo>
    fun testConnection(): Flow<Boolean>
    fun getServerAddress(): Flow<ServerAddress>
    fun saveServerAddress(address: ServerAddress): Flow<Boolean>
}