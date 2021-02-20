package com.openclassrooms.realestatemanager.domain.repository

import android.app.Application
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {
        RealEstateRoomDatabase.getDatabase(this, applicationScope)
    }

    val agentRepository by lazy {
        AgentRepository(database.agentDao())
    }
    val EstatePlaceRepository by lazy {
        EstatePlaceRepository(database.estatePlaceDao(), null)
    }
    val EstateRepository by lazy {
        EstateRepository(database.estateDao())
    }
    val PlaceRepository by lazy {
        PlaceRepository(database.placeDao())
    }
    val StatusRepository by lazy {
        StatusRepository(database.statusDao())
    }
    val TypeRepository by lazy {
        TypeRepository(database.typeDao())
    }
}