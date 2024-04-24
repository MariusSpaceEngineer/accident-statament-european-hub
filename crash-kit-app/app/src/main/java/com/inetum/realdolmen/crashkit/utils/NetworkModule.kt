package com.inetum.realdolmen.crashkit.utils

import com.google.gson.GsonBuilder
import com.inetum.realdolmen.crashkit.BuildConfig
import com.inetum.realdolmen.crashkit.dto.Vehicle
import com.inetum.realdolmen.crashkit.services.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL

    fun provideOkHttpClient(securedPreferences: SecuredPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val jwtToken = securedPreferences.getString("jwt_token")
                val newRequest = if (jwtToken != null) {
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $jwtToken")
                        .build()
                } else {
                    originalRequest
                }
                chain.proceed(newRequest)
            }
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(Vehicle::class.java, VehicleAdapter())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
