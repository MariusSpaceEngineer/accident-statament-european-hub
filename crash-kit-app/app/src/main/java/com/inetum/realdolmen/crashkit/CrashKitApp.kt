package com.inetum.realdolmen.crashkit

import android.app.Application
import com.inetum.realdolmen.crashkit.services.ApiService

class CrashKitApp: Application() {

    companion object {
        lateinit var apiService: ApiService
            private set
        lateinit var securePreferences: SecurePreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        securePreferences= SecurePreferences(this)
        val okHttpClient = NetworkModule.provideOkHttpClient(securePreferences)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        apiService = NetworkModule.provideApiService(retrofit)
    }
}