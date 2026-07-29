package com.lmw.watermonitorandroid.domain.device.api

data class FileInfo(
    val name: String = "",
    val path: String = "",
    val size: Long = 0,
    val type: String = "file"
) {
    val isDirectory: Boolean get() = type == "dir"
}