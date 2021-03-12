package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.StatusDao
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.coroutines.flow.Flow

class StatusRepository(private val statusDao: StatusDao) {
    val allStatus: Flow<List<Status>> = statusDao.getStatus()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(status: Status) {
        statusDao.insert(status)
    }
}