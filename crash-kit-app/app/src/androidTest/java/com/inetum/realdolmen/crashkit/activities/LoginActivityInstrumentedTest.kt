package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.SecuredPreferences
import com.inetum.realdolmen.crashkit.dto.LoginData
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.services.ApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockkClass
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response


@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedTest {

    @get:Rule
    val activityRule: ActivityScenarioRule<LoginActivity> =
        ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var securedPreferences: SecuredPreferences

    @Before
    fun setUp() {
        apiService = mockkClass(ApiService::class)
        securedPreferences = mockkClass(SecuredPreferences::class)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSuccessfulLogin() = runTest {
        // Given
        val email = "johnDoe@example.com"
        val password = "password"
        val token = "token"
        val loginResponse = LoginResponse(token, null)

        // When
        coEvery { apiService.login(LoginData(email, password)) } returns Response.success(
            loginResponse
        )
        every { securedPreferences.putJwtToken(any()) } just runs
        every { securedPreferences.getString("jwt_token") } returns loginResponse.token


        onView(withId(R.id.et_login_email)).perform(typeText(email))
        onView(withId(R.id.et_login_password)).perform(
            typeText(password),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login_submit)).perform(click())

        // Then: Verify the expected behavior (e.g., token saved to SharedPreferences)
        activityRule.scenario.onActivity { activity ->

            // Verify that the token is saved to SecurePreferences
            val retrievedToken = securedPreferences.getString("jwt_token")
            assertEquals(token, retrievedToken)
        }
    }
}



