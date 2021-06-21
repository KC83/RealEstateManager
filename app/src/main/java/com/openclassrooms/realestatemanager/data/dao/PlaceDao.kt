package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place")
    fun getPlacesWithCursor(): Cursor

    @Query("SELECT * FROM place ORDER BY name")
    fun getPlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: Place)
}