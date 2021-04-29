package com.openclassrooms.realestatemanager.data.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface EstatePlaceDao {
    @Query("SELECT * FROM estate_place WHERE estate_id = :estateId ORDER BY id")
    suspend fun getEstatePlacesForAEstate(estateId: Long?): List<EstatePlace>

    @Query("SELECT place.* FROM estate_place INNER JOIN place ON place.id = estate_place.place_id WHERE estate_place.estate_id = :estateId ORDER BY id")
    suspend fun getPlacesForAEstate(estateId: Long?): List<Place>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estatePlace: EstatePlace)

    @Delete
    suspend fun delete(estatePlace: EstatePlace)
}