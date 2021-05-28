package com.openclassrooms.realestatemanager.utils.tools

import java.math.RoundingMode

interface Converter {
    fun convertEuroToDollar(euros: Double): Double
    fun convertDollarToEuro(dollars: Double): Double
}

class ConverterImpl : Converter {
    override fun convertEuroToDollar(euros: Double): Double {
        val number: Double = euros*1.22
        return number.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    override fun convertDollarToEuro(dollars: Double): Double {
        val number: Double = dollars*0.82
        return number.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}