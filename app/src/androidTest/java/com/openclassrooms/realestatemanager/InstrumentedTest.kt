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
import com.nhaarman.mockito_kotlin.given
import com.openclassrooms.realestatemanager.di.module
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class InstrumentedTest: KoinTest {
    private lateinit var context: Context

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<EstateListActivity> = ActivityTestRule(EstateListActivity::class.java)

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        context = getInstrumentation().context
        if(GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(context)
                modules(module)
            }
        }
    }

    @Test
    fun openMapsActivity_returnTrue() {
        declareMock<InternetManager> {
            given(isConnected()).willReturn(true)
        }
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun openMapsActivity_returnFalse() {
        declareMock<InternetManager> {
            given(isConnected()).willReturn(false)
        }
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.estate_list)).check(matches(isDisplayed()))
    }
}