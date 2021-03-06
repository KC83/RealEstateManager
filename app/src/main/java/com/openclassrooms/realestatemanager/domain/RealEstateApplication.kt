package com.openclassrooms.realestatemanager.domain

import android.app.Application
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.di.module
import com.openclassrooms.realestatemanager.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RealEstateApplication : Application() {
    private val database: RealEstateRoomDatabase by inject()

    val agentRepository by lazy {
        AgentRepository(database.agentDao())
    }
    val estatePlaceRepository by lazy {
        EstatePlaceRepository(database.estatePlaceDao())
    }
    val estateRepository by lazy {
        EstateRepository(database.estateDao(), database.typeDao(), database.statusDao(), database.agentDao(), database.estateImageDao(), database.estatePlaceDao())
    }
    val placeRepository by lazy {
        PlaceRepository(database.placeDao())
    }
    val statusRepository by lazy {
        StatusRepository(database.statusDao())
    }
    val typeRepository by lazy {
        TypeRepository(database.typeDao())
    }
    val estateImageRepository by lazy {
        EstateImageRepository(database.estateImageDao())
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RealEstateApplication)
            modules(module)
        }
    }
}