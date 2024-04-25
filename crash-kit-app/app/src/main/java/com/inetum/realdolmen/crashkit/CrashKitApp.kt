package com.inetum.realdolmen.crashkit

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.inetum.realdolmen.crashkit.dto.Vehicle
import com.inetum.realdolmen.crashkit.services.ApiService
import com.inetum.realdolmen.crashkit.utils.NetworkModule
import com.inetum.realdolmen.crashkit.utils.SecuredPreferences
import com.inetum.realdolmen.crashkit.utils.VehicleAdapter

class CrashKitApp: Application() {

    companion object {
        lateinit var apiService: ApiService
            private set
        lateinit var gson: Gson
            private set
        lateinit var securedPreferences: SecuredPreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        securedPreferences= SecuredPreferences(this)

        gson = GsonBuilder()
            .registerTypeAdapter(Vehicle::class.java, VehicleAdapter())
            .create()

        val okHttpClient = NetworkModule.provideOkHttpClient(securedPreferences)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, gson) // Pass the Gson instance
        apiService = NetworkModule.provideApiService(retrofit)
    }
}
