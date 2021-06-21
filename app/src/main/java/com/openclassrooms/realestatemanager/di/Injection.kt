package com.openclassrooms.realestatemanager.di

import android.util.Log
import androidx.room.Room
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import com.openclassrooms.realestatemanager.utils.tools.InternetManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val module = module {
    single<InternetManager> { InternetManagerImpl(get()) }
    single { CoroutineScope(SupervisorJob()) }
    single {
        Room.databaseBuilder(androidContext(), RealEstateRoomDatabase::class.java, "realestate_database")
            .addCallback(RealEstateRoomDatabase.RealEstateRoomDatabaseCallback(get()))
            .build()
    }
}