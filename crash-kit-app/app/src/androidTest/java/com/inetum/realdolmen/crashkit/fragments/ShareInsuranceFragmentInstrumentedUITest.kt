package com.inetum.realdolmen.crashkit.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareInsuranceFragmentInstrumentedUITest {
    private lateinit var device: UiDevice
    private var originalWindowAnimationScale: String = ""
    private var originalTransitionAnimationScale: String = ""
    private var originalAnimatorDurationScale: String = ""

    private var expectedText: String = ""

    //Disable animations on device as required
    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        originalWindowAnimationScale =
            device.executeShellCommand("settings get global window_animation_scale")
        originalTransitionAnimationScale =
            device.executeShellCommand("settings get global transition_animation_scale")
        originalAnimatorDurationScale =
            device.executeShellCommand("settings get global animator_duration_scale")

        device.executeShellCommand("settings put global window_animation_scale 0")
        device.executeShellCommand("settings put global transition_animation_scale 0")
        device.executeShellCommand("settings put global animator_duration_scale 0")
    }

    //Restore animations
    @After
    fun teardown() {
        device.executeShellCommand("settings put global window_animation_scale $originalWindowAnimationScale")
        device.executeShellCommand("settings put global transition_animation_scale $originalTransitionAnimationScale")
        device.executeShellCommand("settings put global animator_duration_scale $originalAnimatorDurationScale")
    }

    @Test
    fun testShareInsuranceUIElements() {
        // Launch the fragment in a test container
        launchFragmentInContainer<ShareInsuranceInformationFragment>(themeResId = R.style.Theme_CrashKit)

        // Check if the title and the generate qr-button are displayed
        onView(withId(R.id.tv_share_insurance_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_share_insurance_generate_qr_code))
            .check(matches(isDisplayed()))

        //Check if the generate qr-button is clickable
        onView(withId(R.id.btn_share_insurance_generate_qr_code))
            .check(matches(isClickable()))
        //Check if the rest of the elements are invisible
        onView(withId(R.id.iv_share_insurance_qr_code))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_share_insurance_qr_code_description))
            .check(matches(not(isDisplayed())))
    }
}