package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.StatusDao
import com.openclassrooms.realestatemanager.data.model.Status

class StatusRepository(private val statusDao: StatusDao) {
    val allStatus: LiveData<List<Status>> = statusDao.getStatus()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(status: Status) {
        statusDao.insert(status)
    }
}