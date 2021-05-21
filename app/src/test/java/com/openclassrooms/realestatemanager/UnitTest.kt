package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Clock
import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UnitTest {
    private var mockClock: Clock = Mockito.mock(Clock::class.java)

    @Before
    fun setup() {
        Mockito.`when`(mockClock.getCurrentDate()).thenReturn("01/01/2021")
    }

    // TESTS FOR CONVERT EURO TO DOLLAR FUNCTION
    @Test
    fun convertEuroToDollar_returnTrue() {
        val euro = 24.78
        val expected = 29.98
        assertEquals(expected, Utils.convertEuroToDollar(euro))
    }
    @Test
    fun convertEuroToDollar_returnFalse() {
        val euro = 5.3
        val expected : Double = 102.5
        assertNotEquals(expected, Utils.convertEuroToDollar(euro))
    }

    // TESTS FOR DATE FUNCTION
    @Test
    fun todayDate_returnTrue() {
        val expected = "01/01/2021"
        val date = mockClock.getCurrentDate()
        assertEquals(expected,date)
    }
    @Test
    fun todayDate_returnFalse() {
        val expected = "01/05/2021"
        val date = mockClock.getCurrentDate()
        assertNotEquals(expected,date)
    }
    @Test
    fun todayDate_returnFalseFormat() {
        val expected = "2021/01/01"
        val date = mockClock.getCurrentDate()
        assertNotEquals(expected,date)
    }
}