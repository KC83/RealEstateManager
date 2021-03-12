package com.openclassrooms.realestatemanager.domain.repository

import android.app.Application
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy {
        RealEstateRoomDatabase.getDatabase(this, applicationScope)
    }

    val agentRepository by lazy {
        AgentRepository(database.agentDao())
    }
    val estatePlaceRepository by lazy {
        EstatePlaceRepository(database.estatePlaceDao(), null)
    }
    val estateRepository by lazy {
        EstateRepository(database.estateDao())
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
        EstateImageRepository(database.estateImageDao(), null)
    }
}