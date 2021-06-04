package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.openclassrooms.realestatemanager.di.Injection
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    private lateinit var context: Context

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<EstateListActivity> = ActivityTestRule(EstateListActivity::class.java)

    @Before
    fun setup() {
        // Set context
        context = getInstrumentation().context
    }

    @Test
    fun openMapsActivity_returnTrue() {
        setInternetManagerMock(true)
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun openMapsActivity_returnFalse() {
        setInternetManagerMock(false)
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.map_fragment)).check(matches(not(isDisplayed())))
    }

    /**
     * Set value of the internet manager mock
     */
    private fun setInternetManagerMock(isConnected: Boolean) {
        Mockito.`when`(Injection.provideInternetManager(context).isConnected()).thenReturn(isConnected)
    }
}