package com.lmw.watermonitorandroid.domain.device.ui

import com.lmw.watermonitorandroid.domain.device.api.FileInfo

data class AdminState(
    val files: List<FileInfo> = emptyList(),
    val currentPath: String = "/",
    val totalBytes: Long = 0,
    val usedBytes: Long = 0,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val previewContent: String? = null,
    val previewPath: String? = null,
    val deleteTarget: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)