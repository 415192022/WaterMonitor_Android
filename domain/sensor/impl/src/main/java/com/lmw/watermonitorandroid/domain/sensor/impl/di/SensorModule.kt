package com.lmw.watermonitorandroid.domain.sensor.impl.di

import com.lmw.watermonitorandroid.domain.sensor.api.SensorService
import com.lmw.watermonitorandroid.domain.sensor.impl.SensorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorModule {
    @Binds
    abstract fun bindSensorService(impl: SensorRepository): SensorService
}