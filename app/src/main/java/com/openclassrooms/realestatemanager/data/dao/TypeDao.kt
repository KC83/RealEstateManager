package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Type
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeDao {
    @Query("SELECT * FROM type")
    fun getTypesWithCursor(): Cursor

    @Query("SELECT * FROM type ORDER BY name")
    fun getTypes(): Flow<List<Type>>

    @Query("SELECT * FROM type WHERE type.id = :id")
    suspend fun getTypeById(id: Long): Type

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(type: Type)
}