package com.lmw.watermonitorandroid.domain.system.impl

import com.lmw.watermonitorandroid.domain.system.api.DeviceInfo
import com.lmw.watermonitorandroid.domain.system.api.ServerAddress
import com.lmw.watermonitorandroid.domain.system.api.SystemService
import com.lmw.watermonitorandroid.domain.system.impl.api.SystemApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    private val systemApi: SystemApi,
    private val serverAddressStore: ServerAddressStore,
    private val okHttpClient: OkHttpClient
) : SystemService {

    override fun getDeviceInfo(): Flow<DeviceInfo> = flow {
        emit(systemApi.getDeviceInfo())
    }.flowOn(Dispatchers.IO)

    override fun testConnection(): Flow<Boolean> = flow {
        val address = serverAddressStore.getAddress()
        val result = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(address.baseUrl + "api/files")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
        emit(result)
    }

    override fun getServerAddress(): Flow<ServerAddress> = flow {
        emit(serverAddressStore.getAddress())
    }

    override fun saveServerAddress(address: ServerAddress): Flow<Boolean> = flow {
        emit(serverAddressStore.saveAddress(address))
    }
}