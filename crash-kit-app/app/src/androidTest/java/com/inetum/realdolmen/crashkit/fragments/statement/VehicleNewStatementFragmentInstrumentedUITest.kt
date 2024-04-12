package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleANewStatementFragment
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class VehicleNewStatementFragmentInstrumentedUITest {
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

        launchFragmentInContainer<VehicleANewStatementFragment>(themeResId = R.style.Theme_CrashKit) {
            VehicleANewStatementFragment().also { fragment ->
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
    fun testVehicleNewStatementUIElements() {
        // Check to see if the fields are displayed on the screen
        onView(withId(R.id.et_statement_policy_holder_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_policy_holder_first_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_policy_holder_address))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_policy_holder_postal_code))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_policy_holder_phone_number))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_policy_holder_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_country))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_mark_type))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_registration_number))
            .check(matches(isDisplayed()))


        //Add text to the fields
        onView(withId(R.id.et_statement_policy_holder_name))
            .perform(typeText("Test Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_policy_holder_first_name))
            .perform(typeText("Test First Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_policy_holder_address))
            .perform(typeText("Test Address"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_policy_holder_postal_code))
            .perform(typeText("Test Postal Code"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_policy_holder_phone_number))
            .perform(typeText("Test Phone Number"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_policy_holder_email))
            .perform(typeText("Test Email"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_country))
            .perform(scrollTo(), typeText("Test Country"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_mark_type))
            .perform(typeText("Test Mark Type"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_registration_number))
            .perform(typeText("Test Registration Number"), closeSoftKeyboard())

        // Check to see if fields have been filled correctly
        onView(withId(R.id.et_statement_policy_holder_name))
            .check(matches(withText("Test Name")))
        onView(withId(R.id.et_statement_policy_holder_first_name))
            .check(matches(withText("Test First Name")))
        onView(withId(R.id.et_statement_policy_holder_address))
            .check(matches(withText("Test Address")))
        onView(withId(R.id.et_statement_policy_holder_postal_code))
            .check(matches(withText("Test Postal Code")))
        onView(withId(R.id.et_statement_policy_holder_phone_number))
            .check(matches(withText("Test Phone Number")))
        onView(withId(R.id.et_statement_policy_holder_email))
            .check(matches(withText("Test Email")))
        onView(withId(R.id.et_statement_vehicle_a_country))
            .check(matches(withText("Test Country")))
        onView(withId(R.id.et_statement_vehicle_a_mark_type))
            .check(matches(withText("Test Mark Type")))
        onView(withId(R.id.et_statement_vehicle_a_registration_number))
            .check(matches(withText("Test Registration Number")))
    }

}