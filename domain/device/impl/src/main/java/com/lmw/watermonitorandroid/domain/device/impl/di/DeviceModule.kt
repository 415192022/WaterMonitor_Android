package com.lmw.watermonitorandroid.domain.device.impl.di

import com.lmw.watermonitorandroid.domain.device.api.DeviceService
import com.lmw.watermonitorandroid.domain.device.impl.DeviceRepository
import com.lmw.watermonitorandroid.domain.device.impl.api.DeviceApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    @Binds
    abstract fun bindDeviceService(impl: DeviceRepository): DeviceService

    companion object {
        @Provides
        @Singleton
        fun provideDeviceApi(retrofit: Retrofit): DeviceApi {
            return retrofit.create(DeviceApi::class.java)
        }
    }
}