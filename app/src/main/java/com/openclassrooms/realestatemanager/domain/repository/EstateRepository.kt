package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.EstateDao
import com.openclassrooms.realestatemanager.data.model.Estate

class EstateRepository(private val estateDao: EstateDao) {
    val allEstates: LiveData<List<Estate>> = estateDao.getEstates()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(estate: Estate) {
        estateDao.delete(estate)
    }
}