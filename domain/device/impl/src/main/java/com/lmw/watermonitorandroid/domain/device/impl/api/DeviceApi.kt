package com.lmw.watermonitorandroid.domain.device.impl.api

import com.lmw.watermonitorandroid.domain.device.api.FileListResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface DeviceApi {

    @GET("api/files")
    suspend fun browseFiles(@Query("path") path: String): FileListResponse

    @GET("api/files/read")
    suspend fun readFile(@Query("path") path: String): String

    @Multipart
    @POST("api/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Boolean

    @DELETE("api/files")
    suspend fun deleteFile(@Query("path") path: String): Boolean
}