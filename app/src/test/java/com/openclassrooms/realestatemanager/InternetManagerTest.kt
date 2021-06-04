package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.openclassrooms.realestatemanager.utils.InternetManagerImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNetworkInfo
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(RobolectricTestRunner::class)
class InternetManagerTest {
    private lateinit var context: Context;
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var shadowNetworkInfo: ShadowNetworkInfo
    private lateinit var internetManager: InternetManagerImpl

    @Before
    fun setUp() {
        context = application.applicationContext
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        internetManager = InternetManagerImpl(context)
        shadowNetworkInfo = shadowOf(connectivityManager.activeNetworkInfo)
    }

    @Test
    fun getActiveNetworkInfo_returnTrue() {
        assertNotNull(connectivityManager.activeNetworkInfo)
    }

    @Test
    fun openMapsActivity_returnTrue() {
        setConnection(true)
        assertTrue(internetManager.isConnected())
    }

    @Test
    fun openMapsActivity_returnFalse() {
        setConnection(false)
        assertFalse(internetManager.isConnected())
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
}