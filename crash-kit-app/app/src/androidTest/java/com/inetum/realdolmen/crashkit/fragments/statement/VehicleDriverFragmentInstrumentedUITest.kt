package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleADriverFragment
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleDriverFragmentInstrumentedUITest {
    private lateinit var device: UiDevice
    private var originalWindowAnimationScale: String = ""
    private var originalTransitionAnimationScale: String = ""
    private var originalAnimatorDurationScale: String = ""

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
    fun testVehicleDriverUIElements() {
        // Launch the fragment in a test container
        launchFragmentInContainer<VehicleADriverFragment>(themeResId = R.style.Theme_CrashKit)

        // Check to see if the fields are displayed on the screen
        onView(withId(R.id.et_statement_vehicle_a_driver_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_driver_first_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_driver_date_of_birth))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_driver_address))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_driver_country))
            .check(
                matches(
                    isDisplayed()
                )
            )
        //Check if driverDateOfBirth edit fields is clickable
        onView(withId(R.id.et_statement_vehicle_a_driver_date_of_birth)).check(
            matches(not(isEnabled()))
        )

        onView(withId(R.id.et_statement_vehicle_a_driver_driving_license_expiration_date))
            .perform(scrollTo())
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_driver_phone_number))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_driver_email))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_driver_driving_license_number))
            .check(
                matches(
                    isDisplayed()
                )
            )

        //Check if driverDrivingLicenseExpirationDate edit fields is clickable
        onView(withId(R.id.et_statement_vehicle_a_driver_driving_license_expiration_date)).check(
            matches(not(isEnabled()))
        )

        // Perform UI interactions
        onView(withId(R.id.et_statement_vehicle_a_driver_name))
            .perform(scrollTo(), typeText("Test Driver Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_first_name))
            .perform(typeText("Test Driver First Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_address))
            .perform(typeText("Test Driver Address"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_country))
            .perform(typeText("Test Driver Country"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_driving_license_number))
            .perform(scrollTo(), typeText("Test License Number"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_phone_number))
            .perform(typeText("Test Driver Phone Number"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_driver_email))
            .perform(typeText("Test Driver Email"), closeSoftKeyboard())

        // Check if the fields have been filled correctly
        onView(withId(R.id.et_statement_vehicle_a_driver_name))
            .check(matches(withText("Test Driver Name")))
        onView(withId(R.id.et_statement_vehicle_a_driver_first_name))
            .check(matches(withText("Test Driver First Name")))
        onView(withId(R.id.et_statement_vehicle_a_driver_address))
            .check(matches(withText("Test Driver Address")))
        onView(withId(R.id.et_statement_vehicle_a_driver_country))
            .check(matches(withText("Test Driver Country")))
        onView(withId(R.id.et_statement_vehicle_a_driver_phone_number))
            .check(matches(withText("Test Driver Phone Number")))
        onView(withId(R.id.et_statement_vehicle_a_driver_email))
            .check(matches(withText("Test Driver Email")))
        onView(withId(R.id.et_statement_vehicle_a_driver_driving_license_number))
            .check(matches(withText("Test License Number")))

    }
}