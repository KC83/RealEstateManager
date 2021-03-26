package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.EstateImageDao
import com.openclassrooms.realestatemanager.data.model.EstateImage
import kotlinx.coroutines.flow.Flow

class EstateImageRepository(private val estateImageDao: EstateImageDao) {
    fun getImagesForAEstate(estateId: Long): Flow<List<EstateImage>> {
        return estateImageDao.getImagesForAEstate(estateId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(estateImage: EstateImage) {
        estateImageDao.insert(estateImage)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(estateImage: EstateImage) {
        estateImageDao.delete(estateImage)
    }
}