package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.Estate

@Dao
interface EstateDao {
    @Query("SELECT * FROM estate ORDER BY insert_date")
    fun getEstates(): LiveData<List<Estate>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estate: Estate)

    @Delete
    suspend fun delete(estate: Estate)
}