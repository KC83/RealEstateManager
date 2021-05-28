package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.ClockImpl
import com.openclassrooms.realestatemanager.utils.LoanImpl
import com.openclassrooms.realestatemanager.utils.tools.ConverterImpl
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UnitTest {
    // TESTS FOR CONVERT EURO TO DOLLAR
    @Test
    fun convertEuroToDollar_returnTrue() {
        val euros = 5.0
        val expected = 6.10

        val converter = ConverterImpl()
        assertEquals(expected,converter.convertEuroToDollar(euros))
    }
    @Test
    fun convertEuroToDollar_returnFalse() {
        val euros = 5.0
        val expected = 15.5

        val converter = ConverterImpl()
        assertNotEquals(expected,converter.convertEuroToDollar(euros))
    }

    // TESTS FOR CONVERT DOLLAR TO EURO
    @Test
    fun convertDollarToEuro_returnTrue() {
        val dollars = 5.0
        val expected = 4.10

        val converter = ConverterImpl()
        assertEquals(expected,converter.convertDollarToEuro(dollars))
    }
    @Test
    fun convertDollarToEuro_returnFalse() {
        val dollars = 5.0
        val expected = 15.5

        val converter = ConverterImpl()
        assertNotEquals(expected,converter.convertDollarToEuro(dollars))
    }

    // TESTS FOR DATE
    @Test
    fun todayDate_returnTrue() {
        val expected = "01/01/2021"
        val clock = ClockImpl()
        val date = clock.getCurrentDate(Date(1609455600000))

        assertEquals(expected,date)
    }
    @Test
    fun todayDate_returnFalse() {
        val expected = "01/05/2021"
        val clock = ClockImpl()
        val date = clock.getCurrentDate(Date(1609455600000))

        assertNotEquals(expected,date)
    }
    @Test
    fun todayDate_returnFalseFormat() {
        val expected = "2021/01/01"
        val clock = ClockImpl()
        val date = clock.getCurrentDate(Date(1609455600000))

        assertNotEquals(expected,date)
    }

    // TESTS FOR MORTGAGE LOAN
    @Test
    fun loanGetMonthlyPayment_returnTrue() {
        val expected = 747.76

        val loan = LoanImpl()
        loan.setLoanAmount(156000.0)
        loan.setInterestRate(1.43)
        loan.setLoanDuration(20)

        assertEquals(expected,loan.getMonthlyPayment())
    }
    @Test
    fun loanGetMonthlyPayment_returnFalse() {
        val expected = 500.0

        val loan = LoanImpl()
        loan.setLoanAmount(240000.0)
        loan.setInterestRate(0.85)
        loan.setLoanDuration(15)

        assertNotEquals(expected,loan.getMonthlyPayment())
    }
}