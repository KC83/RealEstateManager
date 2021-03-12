package com.openclassrooms.realestatemanager.data.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import kotlinx.coroutines.flow.Flow

@Dao
interface EstatePlaceDao {
    @Query("SELECT * FROM estate_place WHERE estate_id = :estateId ORDER BY id")
    fun getPlacesForAEstate(estateId: Int?): Flow<List<EstatePlace>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estatePlace: EstatePlace)

    @Delete
    suspend fun delete(estatePlace: EstatePlace)
}