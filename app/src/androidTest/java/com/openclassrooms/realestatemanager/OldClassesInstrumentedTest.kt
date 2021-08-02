package com.openclassrooms.realestatemanager

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.utils.OldMainActivity
import com.openclassrooms.realestatemanager.utils.OldUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class OldClassesInstrumentedTest {


    @Rule
    @JvmField
    var activityRule: ActivityTestRule<OldMainActivity> = ActivityTestRule(OldMainActivity::class.java, true, false)

    @Before
    fun setup() {
        val intent = Intent()
        activityRule.launchActivity(intent)
    }

    @Test
    fun checkLayout() {
        onView(withId(R.id.activity_main_activity_text_view_main)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_main_activity_text_view_quantity)).check(matches(isDisplayed()))
    }

    @Test
    fun checkTextViewQuantity() {
        val quantity = OldUtils.convertDollarToEuro(100)

        onView(withId(R.id.activity_main_activity_text_view_quantity)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_main_activity_text_view_quantity)).check(matches(withText(quantity.toString())))
    }
}