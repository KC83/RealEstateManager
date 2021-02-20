package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Status

@Dao
interface StatusDao {
    @Query("SELECT * FROM status ORDER BY name")
    fun getStatus(): LiveData<List<Status>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(status: Status)
}