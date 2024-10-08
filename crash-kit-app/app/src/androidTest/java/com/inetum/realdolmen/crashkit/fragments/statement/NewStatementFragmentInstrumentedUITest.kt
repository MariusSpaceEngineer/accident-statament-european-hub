package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import io.mockk.clearMocks
import io.mockk.mockk
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewStatementFragmentInstrumentedUITest {

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

        launchFragmentInContainer<NewStatementFragment>(themeResId = R.style.Theme_CrashKit) {
            NewStatementFragment().also { fragment ->
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
    fun testNewStatementUIElements() {
        // Perform UI interactions and assertions
        onView(withId(R.id.et_statement_accident_date)).check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_accident_location)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_accident_injured)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_accident_material_damage_other_vehicles)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.cb_statement_accident_material_damage_other_objects)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.btn_statement_accident_previous))
            .perform(ViewActions.scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.cb_statement_witness_present)).check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_witness_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_witness_address)).check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_witness_phone)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_statement_accident_next)).check(matches(isDisplayed()))


        // Add tests to check if the user can add text to every EditText
        onView(withId(R.id.et_statement_accident_location)).perform(
            typeText("Test Location"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_statement_witness_name)).perform(
            typeText("Test Name"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_statement_witness_address)).perform(
            typeText("Test Address"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_statement_witness_phone)).perform(
            typeText("Test Phone"),
            ViewActions.closeSoftKeyboard()
        )

        // Check the entered text
        onView(withId(R.id.et_statement_accident_location)).check(matches(withText("Test Location")))
        onView(withId(R.id.et_statement_witness_name)).check(matches(withText("Test Name")))
        onView(withId(R.id.et_statement_witness_address)).check(matches(withText("Test Address")))
        onView(withId(R.id.et_statement_witness_phone)).check(matches(withText("Test Phone")))

        //Tests to check if the checkboxes work
        onView(withId(R.id.cb_statement_accident_injured)).perform(ViewActions.click())
        onView(withId(R.id.cb_statement_accident_material_damage_other_vehicles)).perform(
            ViewActions.click()
        )
        onView(withId(R.id.cb_statement_accident_material_damage_other_objects)).perform(
            ViewActions.click()
        )
        //Uncheck checkbox
        onView(withId(R.id.cb_statement_accident_material_damage_other_objects)).perform(
            ViewActions.click()
        )
        onView(withId(R.id.cb_statement_witness_present)).perform(
            ViewActions.click()
        )

        //Check to see if the witness fields are still visible
        onView(withId(R.id.et_statement_witness_name)).check(matches(not(isDisplayed())))
        onView(withId(R.id.et_statement_witness_address)).check(matches(not(isDisplayed())))
        onView(withId(R.id.et_statement_witness_phone)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_statement_accident_next)).check(matches(not(isDisplayed())))


        //Check to see if the buttons are clickable
        onView(withId(R.id.btn_date_time_picker)).check(matches(isClickable()))
        onView(withId(R.id.btn_statement_accident_previous)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_statement_accident_next)).check(matches(isEnabled()))


        //Check if the accidentDate edit text is clickable
        onView(withId(R.id.et_statement_accident_date)).check(matches(not(isEnabled())))

        //Check the checkboxes
        onView(withId(R.id.cb_statement_accident_injured)).check(matches(isChecked()))
        onView(withId(R.id.cb_statement_accident_material_damage_other_vehicles)).check(
            matches(
                isChecked()
            )
        )
        onView(withId(R.id.cb_statement_accident_material_damage_other_objects)).check(
            matches(
                isNotChecked()
            )
        )
        onView(withId(R.id.cb_statement_witness_present)).check(
            matches(
                isChecked()
            )
        )

    }

}
