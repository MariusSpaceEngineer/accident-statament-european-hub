package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedUITest {
    @get: Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun testMainActivityUIElements() {
        // Check if all UI elements are displayed
        onView(withId(R.id.img_main_background))
            .check(matches(isDisplayed()))
        onView(withId(R.id.img_main_logo))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_description))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_main_login_redirect))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_main_register_redirect))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_main_guest_redirect))
            .check(matches(isDisplayed()))

        //Check if the buttons are clickable
        onView(withId(R.id.btn_main_login_redirect))
            .check(matches(isClickable()))
        onView(withId(R.id.btn_main_register_redirect))
            .check(matches(isClickable()))
        onView(withId(R.id.btn_main_guest_redirect))
            .check(matches(isClickable()))
    }
}