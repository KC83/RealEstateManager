package com.openclassrooms.realestatemanager.di

import android.content.Context
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import com.openclassrooms.realestatemanager.utils.tools.InternetManagerImpl

object Injection {
    fun provideInternetManager(context: Context): InternetManager = InternetManagerImpl(context)
}