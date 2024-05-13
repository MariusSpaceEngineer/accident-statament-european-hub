package com.inetum.realdolmen.crashkit.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inetum.realdolmen.crashkit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityInstrumentedUITest {
    @get: Rule
    var activityScenarioRule = activityScenarioRule<RegisterActivity>()

    @Test
    fun testRegisterActivityUIElements() {
        // Perform UI interactions and assertions
        onView(withId(R.id.et_register_first_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_register_last_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_register_email)).check(matches(isDisplayed()))
        onView(withId(R.id.et_register_password)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.et_register_confirm_password)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.btn_register_submit))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.et_register_phone_number)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.et_register_address)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.et_register_postal_code)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.btn_register_submit)).check(
            matches(
                isClickable()
            )
        )

        // Add text to the EditText fields
        onView(withId(R.id.et_register_first_name)).perform(scrollTo(), typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.et_register_last_name)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.et_register_email)).perform(
            typeText("john.doe@example.com"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.et_register_phone_number)).perform(
            typeText("0123456789"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.et_register_address)).perform(
            typeText("Example street 68"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.et_register_postal_code)).perform(
            typeText("2658"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.et_register_password)).perform(
            typeText("password123"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.et_register_confirm_password)).perform(
            scrollTo(),
            typeText("password123"),
            closeSoftKeyboard()
        );

        // Assert that the text in the EditText fields is the same as what was typed
        onView(withId(R.id.et_register_first_name)).check(matches(withText("John")));
        onView(withId(R.id.et_register_last_name)).check(matches(withText("Doe")));
        onView(withId(R.id.et_register_email)).check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.et_register_phone_number)).check(matches(withText("0123456789")));
        onView(withId(R.id.et_register_address)).check(matches(withText("Example street 68")));
        onView(withId(R.id.et_register_postal_code)).check(matches(withText("2658")));
        onView(withId(R.id.et_register_password)).check(matches(withText("password123")));
        onView(withId(R.id.et_register_confirm_password)).check(matches(withText("password123")));

    }
}