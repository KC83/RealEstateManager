package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.dao.*
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateModel
import kotlinx.coroutines.flow.map

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

    fun getEstateById(id: Long): LiveData<EstateModel> {
        return estateDao.getEstateById(id).map { estate ->
            val type = typeDao.getTypeById(estate.typeId)
            val status = statusDao.getStatusById(estate.statusId)
            val agent = agentDao.getAgentById(estate.agentId)
            val images = estateImageDao.getImagesForAEstate(estate.id).toMutableList()
            val estatePlaces = estatePlaceDao.getEstatePlacesForAEstate(estate.id).toMutableList()
            val places = estatePlaceDao.getPlacesForAEstate(estate.id)

            EstateModel(estate, type, status, agent, images, estatePlaces, places)
        }.asLiveData()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(estate: Estate): Long {
        return estateDao.insert(estate)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun delete(estate: Estate) {
        estateDao.delete(estate)
    }
}