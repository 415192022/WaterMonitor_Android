package com.lmw.watermonitorandroid.domain.device.api

import kotlinx.coroutines.flow.Flow

interface DeviceService {
    fun browseFiles(path: String): Flow<FileListResponse>
    fun readFile(path: String): Flow<String>
    fun uploadFile(name: String, bytes: ByteArray): Flow<Boolean>
    fun deleteFile(path: String): Flow<Boolean>
}