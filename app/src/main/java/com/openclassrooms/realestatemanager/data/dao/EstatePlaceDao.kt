package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.EstatePlace

@Dao
interface EstatePlaceDao {
    @Query("SELECT * FROM estate_place WHERE estate_id = :estateId ORDER BY id")
    fun getPlacesForAEstate(estateId: Int?): LiveData<List<EstatePlace>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estatePlace: EstatePlace)

    @Delete
    suspend fun delete(estatePlace: EstatePlace)
}