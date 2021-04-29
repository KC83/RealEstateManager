package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.TypeDao
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.data.model.Type
import kotlinx.coroutines.flow.Flow

class TypeRepository(private val typeDao: TypeDao) {
    val allTypes: Flow<List<Type>> = typeDao.getTypes()

    suspend fun getTypeById(id: Long) = typeDao.getTypeById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(type: Type) {
        typeDao.insert(type)
    }
}