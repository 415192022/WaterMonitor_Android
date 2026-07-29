package com.lmw.watermonitorandroid.domain.device.impl

import com.lmw.watermonitorandroid.domain.device.api.DeviceService
import com.lmw.watermonitorandroid.domain.device.api.FileListResponse
import com.lmw.watermonitorandroid.domain.device.impl.api.DeviceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(
    private val deviceApi: DeviceApi
) : DeviceService {

    override fun browseFiles(path: String): Flow<FileListResponse> = flow {
        emit(deviceApi.browseFiles(path))
    }

    override fun readFile(path: String): Flow<String> = flow {
        emit(deviceApi.readFile(path))
    }

    override fun uploadFile(name: String, bytes: ByteArray): Flow<Boolean> = flow {
        val requestBody = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", name, requestBody)
        emit(deviceApi.uploadFile(part))
    }

    override fun deleteFile(path: String): Flow<Boolean> = flow {
        emit(deviceApi.deleteFile(path))
    }
}