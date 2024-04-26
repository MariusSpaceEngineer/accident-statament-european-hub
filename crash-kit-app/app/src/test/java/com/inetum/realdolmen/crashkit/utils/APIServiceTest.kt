package com.inetum.realdolmen.crashkit.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.inetum.realdolmen.crashkit.dto.LocationCoordinatesData
import com.inetum.realdolmen.crashkit.dto.LoginData
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.dto.RegisterData
import com.inetum.realdolmen.crashkit.dto.RegisterResponse
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import com.inetum.realdolmen.crashkit.dto.Vehicle
import com.inetum.realdolmen.crashkit.services.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val okHttpClient = OkHttpClient.Builder().build()
        val gson = GsonBuilder()
            .registerTypeAdapter(Vehicle::class.java, VehicleAdapter())
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLogin() = runBlocking {
        // Arrange
        val loginData = LoginData("test@email.com", "password")
        val expectedResponse = LoginResponse(token = "token", errorMessage = null)
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.login(loginData)

        val responseBody = response.body()

        // Assert
        assertEquals(response.code(), 200)
        assertEquals(expectedResponse.token, responseBody?.token)
        assertEquals(expectedResponse.errorMessage, responseBody?.errorMessage)
    }

    @Test
    fun testRegister() = runBlocking {
        // Arrange
        val registerData = RegisterData(
            "John",
            "Doe",
            "john.doe@email.com",
            "password"
        )
        val expectedResponse = RegisterResponse(token = "token", errorMessage = null)
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.register(registerData)

        val responseBody = response.body()

        // Assert
        assertEquals(response.code(), 200)
        assertEquals(expectedResponse.token, responseBody?.token)
    }

    @Test
    fun testLoginWithInvalidCredentials() = runBlocking {
        // Arrange
        val loginData = LoginData("invalid@email.com", "invalid_password")
        val expectedResponse = LoginResponse(token = null, errorMessage = "Invalid email or password")
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400) // Bad Request
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.login(loginData)

        // Assert
        assertEquals(response.code(), 400)
//        val responseBody = response.body()
//        assertEquals(expectedResponse.token, responseBody?.token)
//        assertEquals(expectedResponse.errorMessage, responseBody?.errorMessage)
    }



    @Test
    fun testRegisterWithExistingEmail() = runBlocking {
        // Arrange
        val registerData = RegisterData(
            "John",
            "Doe",
            "existing.email@email.com",
            "password"
        )
        val expectedResponse = RegisterResponse(token = null, errorMessage = "User already exists")
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(409) // Conflict
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.register(registerData)

        // Assert
        assertEquals(response.code(), 409)
        //val responseBody = response.body()
    }

    @Test
    fun testGetLocationAddressWithValidCoordinates() = runBlocking {
        // Arrange
        val locationCoordinatesData = LocationCoordinatesData(37.7749, 122.4194) // Coordinates for San Francisco, for example
        val expectedResponse = RequestResponse(successMessage = "San Francisco, CA, USA", errorMessage = null) // Replace with your expected address
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200) // OK
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.getLocationAddress(locationCoordinatesData)

        // Assert
        assertEquals(response.code(), 200)
        val responseBody = response.body()
        assertEquals(expectedResponse.successMessage, responseBody?.successMessage)
        assertEquals(expectedResponse.errorMessage, responseBody?.errorMessage)
    }

    @Test
    fun testGetLocationAddressWithInvalidCoordinates() = runBlocking {
        // Arrange
        val locationCoordinatesData = LocationCoordinatesData(0.0, 0.0) // Coordinates unlikely to have an address
        val expectedResponse = RequestResponse(successMessage = null, errorMessage = "An error occurred when fetching location, please try again.")
        val expectedResponseBody = Gson().toJson(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500) // Internal Server Error
                .setBody(expectedResponseBody)
        )

        // Act
        val response = apiService.getLocationAddress(locationCoordinatesData)

        // Assert
        assertEquals(response.code(), 500)
//        val responseBody = response.body()
//        assertEquals(expectedResponse.successMessage, responseBody?.successMessage)
//        assertEquals(expectedResponse.errorMessage, responseBody?.errorMessage)
    }

}
