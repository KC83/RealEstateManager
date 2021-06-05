package com.openclassrooms.realestatemanager.di

import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import com.openclassrooms.realestatemanager.utils.tools.InternetManagerImpl
import org.koin.dsl.module

val module = module {
    single<InternetManager> { InternetManagerImpl(get()) }
}