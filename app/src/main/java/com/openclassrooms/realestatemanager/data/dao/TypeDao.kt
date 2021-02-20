package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Type

@Dao
interface TypeDao {
    @Query("SELECT * FROM type ORDER BY name")
    fun getTypes(): LiveData<List<Type>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(type: Type)
}