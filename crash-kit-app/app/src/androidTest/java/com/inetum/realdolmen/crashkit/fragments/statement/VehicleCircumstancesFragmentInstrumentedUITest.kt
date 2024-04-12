package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleACircumstancesFragment
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleCircumstancesFragmentInstrumentedUITest {
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

        launchFragmentInContainer<VehicleACircumstancesFragment>(themeResId = R.style.Theme_CrashKit) {
            VehicleACircumstancesFragment().also { fragment ->
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
    fun testVehicleCircumstancesUIElements() {
        // Check to see if the fields are displayed on the screen
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_parked_stopped))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_leaving_parking_opening_door))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_parking))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_emerging_from_car_park_private_ground_track))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_car_park_private_ground_track))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_roundabout))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_going_same_direction_different_lane))
            .perform(scrollTo())
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_circulating_roundabout))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_striking_rear_same_direction_same_lane))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_reversing))
            .perform(scrollTo())
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_changing_lanes))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_overtaking))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_right))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_left))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.tv_statement_circumstances_vehicle_a_total_crosses))
            .perform(scrollTo())
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_encroaching_reserved_lane_for_opposite_direction))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_coming_right_junction))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_not_observed_sign_red_light))
            .check(
                matches(
                    isDisplayed()
                )
            )

        // Click all checkboxes
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_parked_stopped))
            .perform(scrollTo(), click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_leaving_parking_opening_door))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_parking))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_emerging_from_car_park_private_ground_track))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_car_park_private_ground_track))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_roundabout))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_going_same_direction_different_lane))
            .perform(scrollTo(), click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_circulating_roundabout))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_striking_rear_same_direction_same_lane))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_reversing))
            .perform(scrollTo(), click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_changing_lanes))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_overtaking))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_right))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_left))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_encroaching_reserved_lane_for_opposite_direction))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_coming_right_junction))
            .perform(click())
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_not_observed_sign_red_light))
            .perform(click())

        // Check to see if all checkboxes have been clicked
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_parked_stopped))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_leaving_parking_opening_door))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_parking))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_emerging_from_car_park_private_ground_track))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_car_park_private_ground_track))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_entering_roundabout))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_going_same_direction_different_lane))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_circulating_roundabout))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_striking_rear_same_direction_same_lane))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_reversing))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_changing_lanes))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_overtaking))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_right))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_turning_left))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_encroaching_reserved_lane_for_opposite_direction))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_coming_right_junction))
            .check(matches(isChecked()))
        onView(withId(R.id.cb_statement_circumstances_vehicle_a_not_observed_sign_red_light))
            .check(matches(isChecked()))


    }
}