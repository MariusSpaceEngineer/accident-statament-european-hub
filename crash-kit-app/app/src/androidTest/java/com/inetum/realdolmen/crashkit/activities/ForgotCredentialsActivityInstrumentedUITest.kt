package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ForgotCredentialsActivityInstrumentedUITest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ForgotCredentialsActivity>()

    @Test
    fun testForgotCredentialsActivityUIElements() {
        // Perform UI interactions and assertions
        onView(withId(R.id.et_forgot_credentials_email)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.et_forgot_credentials_new_password)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.et_forgot_credentials_new_password_confirm)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btn_forgot_credentials_submit)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btn_forgot_credentials_submit)).check(ViewAssertions.matches(ViewMatchers.isClickable()))


        // Add text to the EditText fields
        onView(withId(R.id.et_forgot_credentials_email)).perform(typeText("john.doe@example.com"),
            ViewActions.closeSoftKeyboard()
        );
        onView(withId(R.id.et_forgot_credentials_new_password)).perform(typeText("password123"),
            ViewActions.closeSoftKeyboard()
        );
        onView(withId(R.id.et_forgot_credentials_new_password_confirm)).perform(
            typeText("password1234"),
            ViewActions.closeSoftKeyboard()
        );


        // Assert that the text in the EditText fields is the same as what was typed
        onView(withId(R.id.et_forgot_credentials_email)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "john.doe@example.com"
                )
            )
        );
        onView(withId(R.id.et_forgot_credentials_new_password)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "password123"
                )
            )
        );
        onView(withId(R.id.et_forgot_credentials_new_password_confirm)).check(
            ViewAssertions.matches(ViewMatchers.withText("password1234")));


    }

}



