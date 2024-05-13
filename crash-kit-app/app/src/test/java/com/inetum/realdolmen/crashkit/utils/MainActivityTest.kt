package com.inetum.realdolmen.crashkit.utils

import com.auth0.android.jwt.JWT
import com.inetum.realdolmen.crashkit.activities.MainActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private lateinit var securedPreferences: SecuredPreferences
    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        securedPreferences = mockk(relaxed = true)
        activity = MainActivity()
        val field = MainActivity::class.java.getDeclaredField("securedPreferences")
        field.isAccessible = true
        field.set(activity, securedPreferences)
    }

    @Test
    fun checkLoginStatus_userNotRemembered() {
        every { securedPreferences.isLoginRemembered() } returns false

        activity.checkLoginStatus()

        verify { securedPreferences.deleteJwtToken() }
    }

    @Test
    fun checkLoginStatus_tokenNull() {
        every { securedPreferences.isLoginRemembered() } returns true
        every { securedPreferences.getJwtToken() } returns null

        activity.checkLoginStatus()

        verify { securedPreferences.deleteJwtToken() }
    }

    @Test
    fun checkLoginStatus_tokenExpired() {
        val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3MTU1OTE4MDUsImV4cCI6MTcxNTU5MTgxMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.I5wOp2ker-cTAoLLRAnwL1-wWP05uuhCXt7rkXGg_qo"
        val expiredToken = spyk(JWT(token))
        every { securedPreferences.isLoginRemembered() } returns true
        every { securedPreferences.getJwtToken() } returns token
        every { expiredToken.isExpired(10) } returns true

        activity.checkLoginStatus()

        verify { securedPreferences.deleteJwtToken() }
    }

}
