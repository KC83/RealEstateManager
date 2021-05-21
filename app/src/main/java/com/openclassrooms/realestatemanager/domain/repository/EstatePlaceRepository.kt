package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.EstatePlaceDao
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.data.model.Place

class EstatePlaceRepository(private val estatePlaceDao: EstatePlaceDao) {
    suspend fun getPlacesForAEstate(estateId: Long): List<Place> = estatePlaceDao.getPlacesForAEstate(estateId)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(estatePlace: EstatePlace) {
        estatePlaceDao.insert(estatePlace)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(estatePlace: EstatePlace) {
        estatePlaceDao.delete(estatePlace)
    }
}