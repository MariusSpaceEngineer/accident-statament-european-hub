package com.inetum.realdolmen.crashkit.fragments.statement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.fragments.ProfileFragment
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentInstrumentedUITest {
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
    fun testProfileCardUIElements() {
        // Launch the fragment in a test container
        launchFragmentInContainer<ProfileFragment>(themeResId = R.style.Theme_CrashKit)

        // Check if the cards are displayed
        onView(withId(R.id.tv_profile_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.mc_profile_personal_information))
            .check(matches(isDisplayed()))

        //Click the expand button on the personal card
        onView(withId(R.id.ib_profile_personal_card_button))
            .perform(click())

        //Check if the personal card fields are displayed
        onView(withId(R.id.et_profile_personal_first_name_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_personal_last_name_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_personal_email_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_personal_phone_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_personal_address_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_personal_postal_code_value))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ib_profile_personal_card_button))
            .check(matches(isClickable()))

        //Check if the fields are not editable
        onView(withId(R.id.et_profile_personal_first_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_last_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_email_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_phone_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_address_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_postal_code_value))
            .check(matches(not(isEnabled())))

        //Click the personal card edit button in order to make the personal card fields editable
        onView(withId(R.id.tv_profile_personal_card_edit))
            .perform(click())
        //Update button should also appear in the personal information card
        onView(withId(R.id.btn_profile_personal_card_update))
            .check(matches(isDisplayed()))
        expectedText =
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.cancel_button)
        onView(withId(R.id.tv_profile_personal_card_edit)).check(
            matches(
                withText(
                    expectedText
                )
            )
        )

        // Add tests to check if the user can add text to every field in the personal card
        onView(withId(R.id.et_profile_personal_first_name_value)).perform(
            typeText("John"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_personal_last_name_value)).perform(
            typeText("Doe"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_personal_email_value)).perform(
            typeText("johndoe@gmail.com"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_personal_phone_value)).perform(
            typeText("0123456789"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_personal_address_value)).perform(
            typeText("John Str."),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_personal_postal_code_value)).perform(
            typeText("2658"),
            ViewActions.closeSoftKeyboard()
        )

        // Check the entered text
        onView(withId(R.id.et_profile_personal_first_name_value)).check(matches(withText("John")))
        onView(withId(R.id.et_profile_personal_last_name_value)).check(matches(withText("Doe")))
        onView(withId(R.id.et_profile_personal_email_value)).check(matches(withText("johndoe@gmail.com")))
        onView(withId(R.id.et_profile_personal_phone_value)).check(matches(withText("0123456789")))
        onView(withId(R.id.et_profile_personal_address_value)).check(matches(withText("John Str.")))
        onView(withId(R.id.et_profile_personal_postal_code_value)).check(matches(withText("2658")))

        //Click the personal card edit button in order to make the personal card fields uneditable again
        onView(withId(R.id.tv_profile_personal_card_edit))
            .perform(click())

        expectedText =
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.edit_button)
        onView(withId(R.id.tv_profile_personal_card_edit)).check(
            matches(
                withText(
                    expectedText
                )
            )
        )

        //Check if the fields are not editable
        onView(withId(R.id.et_profile_personal_first_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_last_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_email_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_phone_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_address_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_personal_postal_code_value))
            .check(matches(not(isEnabled())))

        //Click the expand button again in the personal card to hide the fields
        onView(withId(R.id.ib_profile_personal_card_button))
            .perform(click())

        //Check if the personal card fields are hidden
        onView(withId(R.id.et_profile_personal_first_name_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_personal_last_name_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_personal_email_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_personal_phone_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_personal_address_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_personal_postal_code_value))
            .check(matches(not(isDisplayed())))

    }

    @Test
    fun testInsuranceCardUIElements() {
        // Launch the fragment in a test container
        launchFragmentInContainer<ProfileFragment>(themeResId = R.style.Theme_CrashKit)

        // Check if the card and the title is displayed
        onView(withId(R.id.tv_profile_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.mc_profile_insurance_information))
            .check(matches(isDisplayed()))

        //Click the expand button on the insurance card
        onView(withId(R.id.ib_profile_insurance_card_button))
            .perform(click())

        //Check if the insurance card fields are displayed
        onView(withId(R.id.tv_profile_insurance_card_change_insurance))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_company_name_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_company_policy_number_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_company_insurance_availability_date_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_company_insurance_expiration_date_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_agency_country_value))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_profile_date_time_picker_insurance_certificate_dates))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_agency_name_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_agency_email_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_profile_insurance_agency_phone_number_value))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ib_profile_insurance_card_button))
            .perform(scrollTo())
            .check(matches(isClickable()))

        //Check if the fields are not editable
        onView(withId(R.id.et_profile_insurance_company_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_policy_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_insurance_availability_date_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_insurance_expiration_date_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_country_value))
            .perform(scrollTo())
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_email_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_phone_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_address_value))
            .check(matches(not(isEnabled())))

        //Click the insurance card edit button in order to make the insurance card fields editable
        onView(withId(R.id.tv_profile_insurance_card_edit))
            .perform(scrollTo())
            .perform(click())
        //Update button should also appear in the personal information card
        onView(withId(R.id.btn_profile_insurance_card_update))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        expectedText =
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.cancel_button)
        onView(withId(R.id.tv_profile_insurance_card_edit)).check(
            matches(
                withText(
                    expectedText
                )
            )
        )

        // Add tests to check if the user can add text to every field in the insurance card
        onView(withId(R.id.et_profile_insurance_company_name_value)).perform(
            scrollTo(),
            typeText("Company Name"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_insurance_company_policy_number_value)).perform(
            typeText("PN454949"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value)).perform(
            typeText("GC4894946"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_profile_insurance_agency_country_value))
            .perform(
                scrollTo(),
                typeText("Country"),
                ViewActions.closeSoftKeyboard()
            )
        onView(withId(R.id.et_profile_insurance_agency_name_value))
            .perform(
                typeText("Agency Name"),
                ViewActions.closeSoftKeyboard()
            )

        //TODO: find out why those fields crash the emulator
//        onView(withId(R.id.et_profile_insurance_agency_email_value))
//            .perform(
//                ViewActions.typeText("agency@example.com"),
//                ViewActions.closeSoftKeyboard()
//            )
//        onView(withId(R.id.et_profile_insurance_agency_phone_number_value))
//            .perform(
//                ViewActions.typeText("0123456789"),
//                ViewActions.closeSoftKeyboard()
//            )
//        onView(withId(R.id.et_profile_insurance_agency_address_value))
//            .perform(
//                ViewActions.typeText("Example str."),
//                ViewActions.closeSoftKeyboard()
//            )

        // Check the entered text
        onView(withId(R.id.et_profile_insurance_company_name_value)).perform(scrollTo())
            .check(matches(withText("Company Name")))
        onView(withId(R.id.et_profile_insurance_company_policy_number_value)).check(
            matches(
                withText(
                    "PN454949"
                )
            )
        )
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value)).check(
            matches(
                withText("GC4894946")
            )
        )
        onView(withId(R.id.et_profile_insurance_agency_country_value)).perform(scrollTo())
            .check(matches(withText("Country")))
        onView(withId(R.id.et_profile_insurance_agency_name_value)).check(matches(withText("Agency Name")))

        //Click the personal card edit button in order to make the personal card fields uneditable again
        onView(withId(R.id.tv_profile_insurance_card_edit))
            .perform(
                scrollTo(),
                click()
            )

        expectedText =
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.edit_button)
        onView(withId(R.id.tv_profile_insurance_card_edit)).check(
            matches(
                withText(
                    expectedText
                )
            )
        )

        //Check if the fields are not editable
        onView(withId(R.id.et_profile_insurance_company_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_policy_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_insurance_availability_date_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_company_insurance_expiration_date_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_country_value))
            .perform(scrollTo())
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_name_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_email_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_phone_number_value))
            .check(matches(not(isEnabled())))
        onView(withId(R.id.et_profile_insurance_agency_address_value))
            .check(matches(not(isEnabled())))

        //Click the expand button again in the personal card to hide the fields
        onView(withId(R.id.ib_profile_insurance_card_button))
            .perform(
                scrollTo(),
                click()
            )

        //Check if the insurance card fields are hidden
        onView(withId(R.id.tv_profile_insurance_card_change_insurance))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_company_name_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_company_policy_number_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_company_green_card_number_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_company_insurance_availability_date_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_company_insurance_expiration_date_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_agency_country_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_profile_date_time_picker_insurance_certificate_dates))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_agency_name_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_agency_email_value))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.et_profile_insurance_agency_phone_number_value))
            .check(matches(not(isDisplayed())))

    }

}