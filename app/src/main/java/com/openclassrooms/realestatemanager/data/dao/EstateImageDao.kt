package com.openclassrooms.realestatemanager.data.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.EstateImage

@Dao
interface EstateImageDao {
    @Query("SELECT * FROM estate_image WHERE estate_id = :estateId ORDER BY id")
    suspend fun getImagesForAEstate(estateId: Long?): List<EstateImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estateImage: EstateImage)

    @Delete
    suspend fun delete(estateImage: EstateImage)
}