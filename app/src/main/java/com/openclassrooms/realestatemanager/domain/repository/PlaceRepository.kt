package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.PlaceDao
import com.openclassrooms.realestatemanager.data.model.Place

class PlaceRepository(private val placeDao: PlaceDao) {
    val allPlaces: LiveData<List<Place>> = placeDao.getPlaces()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(place: Place) {
        placeDao.insert(place)
    }
}