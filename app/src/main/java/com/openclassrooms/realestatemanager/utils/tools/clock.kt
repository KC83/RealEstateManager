package com.openclassrooms.realestatemanager.utils.tools

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

interface Clock {
    fun getCurrentDate(date: Date = Date()): String
}

class ClockImpl : Clock {
    @SuppressLint("SimpleDateFormat")
    override fun getCurrentDate(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return simpleDateFormat.format(date)
    }
}