package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.dao.*
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EstateRepository(
        private val estateDao: EstateDao,
        private val typeDao: TypeDao,
        private val statusDao: StatusDao,
        private val agentDao: AgentDao,
        private val estateImageDao: EstateImageDao,
        private val estatePlaceDao: EstatePlaceDao,
) {
    fun getAllEstates(): LiveData<List<EstateModel>> {
        return estateDao.getEstates().map { estates ->
            estates.map { estate ->
                val type = typeDao.getTypeById(estate.typeId)
                val status = statusDao.getStatusById(estate.statusId)
                val agent = agentDao.getAgentById(estate.agentId)
                val images = estateImageDao.getImagesForAEstate(estate.id).toMutableList()
                val estatePlaces = estatePlaceDao.getEstatePlacesForAEstate(estate.id).toMutableList()
                val places = estatePlaceDao.getPlacesForAEstate(estate.id)

                EstateModel(estate, type, status, agent, images, estatePlaces, places)
            }
        }.asLiveData()
    }

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