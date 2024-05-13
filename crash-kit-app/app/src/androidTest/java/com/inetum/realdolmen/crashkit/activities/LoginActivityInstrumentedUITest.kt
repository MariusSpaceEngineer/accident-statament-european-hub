package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedUITest {
    @get:Rule
    val activityRule: ActivityScenarioRule<LoginActivity> =
        ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginActivityUIElements() {
        // Check if all UI elements are displayed
        onView(withId(R.id.img_login_background))
            .check(matches(isDisplayed()))
        onView(withId(R.id.img_login_logo))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_login_app_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_login_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_login_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_login_password))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cb_login_remember))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_login_forgot_password))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_login_submit))
            .check(matches(isDisplayed()))

        //Check if the buttons are clickable
        onView(withId(R.id.tv_login_forgot_password))
            .check(matches(isClickable()))
        onView(withId(R.id.btn_login_submit))
            .check(matches(isClickable()))

        // Add text to the EditText fields
        onView(withId(R.id.et_login_email)).perform(
            ViewActions.typeText("johndoe@example.com"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_login_password)).perform(
            ViewActions.typeText("1234"),
            ViewActions.closeSoftKeyboard()
        )

        // Assert that the text in the EditText fields is the same as what was typed
        onView(withId(R.id.et_login_email)).check(matches(ViewMatchers.withText("johndoe@example.com")))
        onView(withId(R.id.et_login_password)).check(matches(ViewMatchers.withText("1234")))
    }
}



