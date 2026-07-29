package com.lmw.watermonitorandroid.domain.system.impl.di

import android.content.Context
import android.content.SharedPreferences
import com.lmw.watermonitorandroid.domain.system.api.SystemService
import com.lmw.watermonitorandroid.domain.system.impl.SystemRepository
import com.lmw.watermonitorandroid.domain.system.impl.api.SystemApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemModule {

    @Binds
    abstract fun bindSystemService(impl: SystemRepository): SystemService

    companion object {
        @Provides
        @Singleton
        fun provideSystemApi(retrofit: Retrofit): SystemApi {
            return retrofit.create(SystemApi::class.java)
        }

        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences {
            return context.getSharedPreferences("system_prefs", Context.MODE_PRIVATE)
        }
    }
}