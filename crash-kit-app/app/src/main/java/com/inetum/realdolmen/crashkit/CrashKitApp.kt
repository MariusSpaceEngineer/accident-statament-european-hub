package com.inetum.realdolmen.crashkit

import android.app.Application
import com.inetum.realdolmen.crashkit.services.ApiService
import com.inetum.realdolmen.crashkit.utils.NetworkModule
import com.inetum.realdolmen.crashkit.utils.SecuredPreferences

class CrashKitApp: Application() {

    companion object {
        lateinit var apiService: ApiService
            private set
        lateinit var securedPreferences: SecuredPreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        securedPreferences= SecuredPreferences(this)
        val okHttpClient = NetworkModule.provideOkHttpClient(securedPreferences)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        apiService = NetworkModule.provideApiService(retrofit)
    }
}