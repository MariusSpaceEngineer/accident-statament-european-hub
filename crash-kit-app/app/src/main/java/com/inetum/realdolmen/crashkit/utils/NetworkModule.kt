package com.inetum.realdolmen.crashkit.utils

import com.google.gson.Gson
import com.inetum.realdolmen.crashkit.BuildConfig
import com.inetum.realdolmen.crashkit.services.ApiService
import com.inetum.realdolmen.crashkit.utils.NetworkModule.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * `NetworkModule` is a singleton object that provides methods for creating and configuring network-related instances.
 * It provides methods to create an OkHttpClient, a Retrofit instance, and an ApiService.
 *
 * @property BASE_URL The base URL for the API endpoints.
 *
 * @function provideOkHttpClient This function creates and returns an OkHttpClient. It adds an interceptor to the OkHttpClient
 * that modifies the request to include an Authorization header if a JWT token is present in the secured preferences.
 *
 * @function provideRetrofit This function creates and returns a Retrofit instance. It sets the base URL, adds the OkHttpClient
 * and a Gson converter factory to the Retrofit Builder.
 *
 * @function provideApiService This function creates and returns an ApiService. It uses the Retrofit instance to create the ApiService.
 */
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

    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
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

