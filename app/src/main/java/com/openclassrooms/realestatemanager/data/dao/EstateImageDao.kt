package com.openclassrooms.realestatemanager.data.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.EstateImage
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateImageDao {
    @Query("SELECT * FROM estate_image WHERE estate_id = :estateId ORDER BY id")
    fun getImagesForAEstate(estateId: Int?): Flow<List<EstateImage>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estateImage: EstateImage)

    @Delete
    suspend fun delete(estateImage: EstateImage)
}