package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.Estate
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateDao {
    @Query("SELECT * FROM estate")
    fun getEstatesWithCursor(): Cursor

    @Query("SELECT * FROM estate ORDER BY insert_date")
    fun getEstates(): Flow<List<Estate>>

    @Query("SELECT * FROM estate WHERE estate.id = :id")
    fun getEstateById(id: Long): Flow<Estate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estate: Estate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForTest(estate: Estate)

    @Delete
    suspend fun delete(estate: Estate)
}