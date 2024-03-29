package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityInstrumentedUITest {
    @get: Rule
    var activityScenarioRule = activityScenarioRule<HomeActivity>()

    @Test
    fun testHomeActivityUIElements() {
        // Check if all UI elements are displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragmentContainerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}