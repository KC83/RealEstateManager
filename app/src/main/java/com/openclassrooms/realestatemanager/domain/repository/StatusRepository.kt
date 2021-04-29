package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.StatusDao
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.coroutines.flow.Flow

class StatusRepository(private val statusDao: StatusDao) {
    val allStatus: LiveData<List<Status>> = statusDao.getStatus()

    suspend fun getStatusById(id: Long) = statusDao.getStatusById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(status: Status) {
        statusDao.insert(status)
    }
}