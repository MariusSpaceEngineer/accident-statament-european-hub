package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleAMotorInsuranceFragment
import io.mockk.clearMocks
import io.mockk.mockk
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleInsuranceFragmentInstrumentedUITest {
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

        launchFragmentInContainer<VehicleAMotorInsuranceFragment>(themeResId = R.style.Theme_CrashKit) {
            VehicleAMotorInsuranceFragment().also { fragment ->
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
    fun testVehicleInsuranceUIElements() {
        // Check to see if the fields are displayed on the screen
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_name))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_policy_number))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_certificate_expiration_date))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_certificate_availability_date))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_certificate_expiration_date))
            .check(
                matches(
                    isDisplayed()
                )
            )
        //Check if insurance certificate availability and expiration edit fields are clickable
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_certificate_availability_date)).check(
            matches(not(isEnabled()))
        )
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_certificate_expiration_date)).check(
            matches(not(isEnabled()))
        )
        onView(withId(R.id.cb_statement_damaged_covered))
            .perform(scrollTo())
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_email))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_name))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_address))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_country))
            .check(
                matches(
                    isDisplayed()
                )
            )

        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_phone_number))
            .check(
                matches(
                    isDisplayed()
                )
            )


        //Add text to the fields
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_name))
            .perform(scrollTo(), typeText("Test Insurance Company Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_policy_number))
            .perform(typeText("Test Policy Number"), closeSoftKeyboard())
        onView(withId(R.id.cb_statement_damaged_covered))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_email))
            .perform(typeText("Test Agency Email"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_name))
            .perform(typeText("Test Agency Name"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_address))
            .perform(typeText("Test Agency Address"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_country))
            .perform(typeText("Test Agency Country"), closeSoftKeyboard())
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_phone_number))
            .perform(typeText("Test Agency Phone Number"), closeSoftKeyboard())


        // Check if the text has been filled correctly
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_name))
            .check(matches(withText("Test Insurance Company Name")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_company_policy_number))
            .check(matches(withText("Test Policy Number")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_email))
            .check(matches(withText("Test Agency Email")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_name))
            .check(matches(withText("Test Agency Name")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_address))
            .check(matches(withText("Test Agency Address")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_country))
            .check(matches(withText("Test Agency Country")))
        onView(withId(R.id.et_statement_vehicle_a_insurance_agency_phone_number))
            .check(matches(withText("Test Agency Phone Number")))

    }
}