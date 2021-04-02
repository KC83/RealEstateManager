package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.EstatePlaceDao
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import kotlinx.coroutines.flow.Flow

class EstatePlaceRepository(private val estatePlaceDao: EstatePlaceDao, private val estateId: Int?) {
    fun getPlacesForAEstate(estateId: Long): Flow<List<EstatePlace>> {
        return estatePlaceDao.getPlacesForAEstate(estateId)
    }

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