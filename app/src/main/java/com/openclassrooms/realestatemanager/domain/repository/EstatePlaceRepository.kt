package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.EstatePlaceDao
import com.openclassrooms.realestatemanager.data.model.EstatePlace

class EstatePlaceRepository(private val estatePlaceDao: EstatePlaceDao, private val estateId: Int?) {
    val allEstatePlaceForAEstate: LiveData<List<EstatePlace>> = estatePlaceDao.getPlacesForAEstate(estateId)

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