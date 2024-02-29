package com.inetum.realdolmen.crashkit

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient

class CrashKitApp: Application() {

    companion object {
        lateinit var httpClient: OkHttpClient
            private set
        lateinit var securePreferences: SecurePreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        httpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, 10 * 1024 * 1024)) // 10 MiB
            .build()
        securePreferences= SecurePreferences(this)
    }
}