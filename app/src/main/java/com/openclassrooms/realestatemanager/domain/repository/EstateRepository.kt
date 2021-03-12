package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.EstateDao
import com.openclassrooms.realestatemanager.data.model.Estate
import kotlinx.coroutines.flow.Flow

class EstateRepository(private val estateDao: EstateDao) {
    val allEstates: Flow<List<Estate>> = estateDao.getEstates()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(estate: Estate): Long {
        return estateDao.insert(estate)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(estate: Estate) {
        estateDao.delete(estate)
    }
}