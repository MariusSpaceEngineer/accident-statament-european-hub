package com.inetum.realdolmen.crashkit.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentInstrumentedUITest {
    private lateinit var device: UiDevice
    private var originalWindowAnimationScale: String = ""
    private var originalTransitionAnimationScale: String = ""
    private var originalAnimatorDurationScale: String = ""

    private val mockNavController = mockk<NavController>(relaxed = true)

    //Disable animations on device and set up navController as required
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

        launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_CrashKit) {
            HomeFragment().also { fragment ->
                // In addition to returning a new instance of our Fragment,
                // get a callback whenever the fragment’s view is created
                // or destroyed so that we can set the mock NavController
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        Navigation.setViewNavController(fragment.requireView(), mockNavController)
                    }
                }
            }
        }
    }

    //Restore animations
    @After
    fun teardown() {
        device.executeShellCommand("settings put global window_animation_scale $originalWindowAnimationScale")
        device.executeShellCommand("settings put global transition_animation_scale $originalTransitionAnimationScale")
        device.executeShellCommand("settings put global animator_duration_scale $originalAnimatorDurationScale")
        clearMocks(mockNavController)
    }

    @Test
    fun testHomeFragmentUIElements() {
        // Check if the UI elements are displayed
        onView(withId(R.id.tv_home_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.img_main_background))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cv_home_statement))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_home_statement_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_home_statement_description))
            .check(matches(isDisplayed()))
        onView(withId(R.id.iv_home_statement_picture))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cv_home_qr))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_home_qr_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_home_qr_description))
            .check(matches(isDisplayed()))
        onView(withId(R.id.iv_home_qr_picture))
            .check(matches(isDisplayed()))

        //Check if the cards are clickable
        onView(withId(R.id.cv_home_statement))
            .check(matches(isClickable()))
        onView(withId(R.id.cv_home_qr))
            .check(matches(isClickable()))
    }
}