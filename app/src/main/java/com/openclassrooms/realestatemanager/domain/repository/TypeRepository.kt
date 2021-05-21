package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.TypeDao
import com.openclassrooms.realestatemanager.data.model.Type
import kotlinx.coroutines.flow.Flow

class TypeRepository(private val typeDao: TypeDao) {
    val allTypes: Flow<List<Type>> = typeDao.getTypes()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(type: Type) {
        typeDao.insert(type)
    }
}