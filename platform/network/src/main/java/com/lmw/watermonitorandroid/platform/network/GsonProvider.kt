package com.lmw.watermonitorandroid.platform.network

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GsonProvider {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}