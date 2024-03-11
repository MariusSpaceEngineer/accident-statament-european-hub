package com.inetum.realdolmen.crashkit.utils

import com.inetum.realdolmen.crashkit.services.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://10.0.2.2:8080/api/v1/"

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
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
