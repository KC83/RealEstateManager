package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.mockito.Mockito
import com.openclassrooms.realestatemanager.utils.InternetManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    private var mockInternetManager: InternetManager = Mockito.mock(InternetManager::class.java)

    /**
     * Check if internet is connected
     */
    @Test
    fun testInternetConnection_returnTrue() {
        setInternetManagerMock(true)
        assertTrue(mockInternetManager.isConnected())
    }

    /**
     * Check if internet is not connected
     */
    @Test
    fun testInternetConnection_returnFalse() {
        setInternetManagerMock(false)
        assertFalse(mockInternetManager.isConnected())
    }

    /**
     * Set value of the internet manager mock
     */
    private fun setInternetManagerMock(isConnected: Boolean) {
        Mockito.`when`(mockInternetManager.isConnected()).thenReturn(isConnected)
    }
}