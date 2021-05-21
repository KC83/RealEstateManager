package com.openclassrooms.realestatemanager.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

interface Clock {
    fun getCurrentDate(): String
}

class ClockImpl : Clock {
    @SuppressLint("SimpleDateFormat")
    override fun getCurrentDate(): String {
        val date = SimpleDateFormat("dd/MM/yyyy")
        return date.format(Date())
    }
}