package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Status

@Dao
interface StatusDao {
    @Query("SELECT * FROM status")
    fun getStatusWithCursor(): Cursor

    @Query("SELECT * FROM status ORDER BY name")
    fun getStatus(): LiveData<List<Status>>

    @Query("SELECT * FROM status WHERE status.id = :id")
    suspend fun getStatusById(id: Long): Status

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(status: Status)
}