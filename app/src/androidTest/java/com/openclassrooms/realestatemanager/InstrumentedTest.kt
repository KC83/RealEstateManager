package com.openclassrooms.realestatemanager

import android.content.Context
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.given
import com.openclassrooms.realestatemanager.di.module
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
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

    // TEST FOR OPEN THE MAPS IF WE HAVE A INTERNET CONNECTION
    @Test
    fun openMapsActivity_returnTrue() {
        declareMock<InternetManager> {
            given(isConnected()).willReturn(true)
        }
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))
    }

    // TEST FOR NOT OPEN THE MAPS IF WE DON'T HAVE A INTERNET CONNECTION
    @Test
    fun openMapsActivity_returnFalse() {
        declareMock<InternetManager> {
            given(isConnected()).willReturn(false)
        }
        onView(withId(R.id.btn_maps)).perform(click())
        onView(withId(R.id.estate_list)).check(matches(isDisplayed()))
    }

    /*
    @Test
    fun checkToastMessage_returnTrue() {
        declareMock<InternetManager> {
            given(isConnected()).willReturn(true)
        }

        onView(withId(R.id.bottom_app_bar))
            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.btn_internet));


        /*
        openActionBarOverflowOrOptionsMenu(context);
        onView(withId(R.id.btn_internet)).perform(click())
        */

        //onView(withId(R.id.bottom_app_bar)).perform(NavigationViewActions.navigateTo(R.id.btn_internet));
        //onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.btn_internet));
        //onView(withText(R.string.internet)).perform(click())
        //onView(withText("Vérification de la connexion Internet")).perform(click())

    }*/
}