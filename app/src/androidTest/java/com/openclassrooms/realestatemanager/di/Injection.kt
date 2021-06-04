package com.openclassrooms.realestatemanager.di

import android.content.Context
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import org.mockito.Mockito

object Injection {
    fun provideInternetManager(context: Context): InternetManager {
        return Mockito.mock(InternetManager::class.java)
    }
}