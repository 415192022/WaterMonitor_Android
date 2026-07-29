package com.lmw.watermonitorandroid.domain.device.api

data class FileListResponse(
    val path: String = "/",
    val totalBytes: Long = 0,
    val usedBytes: Long = 0,
    val files: List<FileInfo> = emptyList()
)