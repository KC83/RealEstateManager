package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.utils.InternetManagerImpl
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf
import org.robolectric.internal.bytecode.ShadowedObject
import org.robolectric.shadows.ShadowSettings

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var context: Context;
    private lateinit var internetManager: InternetManagerImpl;

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        internetManager = InternetManagerImpl(context)
    }

    @Test
    fun getActiveNetworkInfo_returnTrue() {
        assertNotNull(connectivityManager.activeNetworkInfo)
    }

    @Test
    fun openMapsActivity_returnTrue() {
        //setConnection(true)
        //onView(withId(R.id.btn_maps)).perform(click())

        //val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        //val shadowNetworkInfo: ShadowNetworkInfo? = shadowOf(networkInfo)

        //println(shadowNetworkInfo)
        //val shadowNetworkInfo = connectivityManager?.activeNetworkInfo

        //shadowNetworkInfo?.setConnectionStatus(NetworkInfo.State.CONNECTED)

        //assertTrue(internetManager?.isConnected() == true)


        //onView(withId(R.id.btn_maps)).perform(click())
    }




    private fun setConnection(isConnected: Boolean) {
        var wifiState: Int = WifiManager.WIFI_STATE_ENABLED
        if (!isConnected) {
            wifiState = WifiManager.WIFI_STATE_DISABLED
        }

        var wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        shadowOf(wifiManager).setWifiState(wifiState)
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        shadowOf(wifiManager).setWifiState(wifiState)

        var connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowOf(connectivityManager).setBackgroundDataSetting(isConnected)
        connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowOf(connectivityManager).setBackgroundDataSetting(isConnected)

        if (isConnected) {
            shadowOf(connectivityManager.activeNetworkInfo).setConnectionStatus(NetworkInfo.State.CONNECTING)
        } else {
            shadowOf(connectivityManager.activeNetworkInfo).setConnectionStatus(NetworkInfo.State.DISCONNECTED)
        }

        ShadowSettings.setWifiOn(isConnected)
        ShadowSettings.setAirplaneMode(isConnected)
    }


    /*private var internetManager: InternetManagerImpl? = null

    private var connectivityManager: ConnectivityManager? = null
    private var shadowNetworkInfo: ShadowNetworkInfo? = null

    @Rule @JvmField
    var activityRule: ActivityTestRule<EstateListActivity> = ActivityTestRule(EstateListActivity::class.java)

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        internetManager = InternetManagerImpl(context)

        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //shadowNetworkInfo = shadowOf(connectivityManager?.activeNetworkInfo)
        //     shadowOfActiveNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());
        shadowNetworkInfo = shadowOf(connectivityManager!!.activeNetworkInfo)
    }

    @Test
    fun getActiveNetworkInfo_returnTrue() {
        assertNotNull(connectivityManager?.activeNetworkInfo)
    }

    @Test
    fun openMapsActivity_returnTrue() {
        shadowNetworkInfo?.setConnectionStatus(NetworkInfo.State.CONNECTED)

        assertTrue(internetManager?.isConnected() == true)


        //onView(withId(R.id.btn_maps)).perform(click())
    }

    @Test
    fun openMapsActivity_returnFalse() {
        shadowNetworkInfo?.setConnectionStatus(NetworkInfo.State.DISCONNECTED)

        assertFalse(internetManager?.isConnected() == true)

        //onView(withId(R.id.btn_maps)).perform(click())
    }

    */


    /*
    private var mockInternetManager: InternetManager = Mockito.mock(InternetManager::class.java)

    @Rule @JvmField
    var activityRule: ActivityTestRule<EstateListActivity> = ActivityTestRule(EstateListActivity::class.java)



    @Test
    fun checkIfCanOpenMapsActivity() {
        val wifi = activityRule.activity.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifi!!.isWifiEnabled = false

        onView(withId(R.id.btn_maps))
            .perform(click())


    }


    /**
     * Set value of the internet manager mock
     */
    private fun setInternetManagerMock(isConnected: Boolean) {
        Mockito.`when`(mockInternetManager.isConnected()).thenReturn(isConnected)
    }

     */
}