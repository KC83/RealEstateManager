package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.TypeDao
import com.openclassrooms.realestatemanager.data.model.Type

class TypeRepository(private val typeDao: TypeDao) {
    val allTypes: LiveData<List<Type>> = typeDao.getTypes()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(type: Type) {
        typeDao.insert(type)
    }
}