package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.PlaceDao
import com.openclassrooms.realestatemanager.data.model.Place
import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val placeDao: PlaceDao) {
    val allPlaces: Flow<List<Place>> = placeDao.getPlaces()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(place: Place) {
        placeDao.insert(place)
    }
}