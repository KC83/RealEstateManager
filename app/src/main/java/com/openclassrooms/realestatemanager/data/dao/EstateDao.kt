package com.openclassrooms.realestatemanager.data.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.Estate
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateDao {
    @Query("SELECT * FROM estate ORDER BY insert_date")
    fun getEstates(): Flow<List<Estate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estate: Estate): Long

    @Delete
    suspend fun delete(estate: Estate)
}