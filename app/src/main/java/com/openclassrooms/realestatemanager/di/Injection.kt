package com.openclassrooms.realestatemanager.di

import android.content.Context
import com.openclassrooms.realestatemanager.utils.InternetManager
import com.openclassrooms.realestatemanager.utils.InternetManagerImpl

object Injection {
    fun provideInternetManager(context: Context): InternetManager = InternetManagerImpl(context)
}