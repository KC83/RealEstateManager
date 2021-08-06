package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.data.model.Estate
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateDao {
    @Query("SELECT * FROM estate")
    fun getEstatesWithCursor(): Cursor

    @Query("SELECT estate.* " +
                 "FROM estate " +
                    "LEFT OUTER JOIN estate_place ON estate_place.estate_id = estate.id " +
                 "WHERE (:statusString = '' OR status_id IN(:status)) " +
                    "AND (:typesString = '' OR type_id IN(:types)) " +
                    "AND (:agentsString = '' OR agent_id IN(:agents)) " +
                    "AND (:placesString = '' OR place_id IN(:places)) " +
                    "AND (:price = 0.0 OR price = :price) " +
                    "AND (:surface = 0.0 OR surface = :surface) " +
                    "AND (:city LIKE '' OR city LIKE :city) " +
                 "GROUP BY estate.id " +
                 "ORDER BY insert_date")
    fun getEstates(statusString: String = "", status: List<Int> = mutableListOf(),
                   typesString: String = "", types: List<Int> = mutableListOf(),
                   agentsString: String = "", agents: List<Int> = mutableListOf(),
                   placesString: String = "", places: List<Int> = mutableListOf(),
                   price: Double = 0.0,
                   surface: Double = 0.0,
                   city: String = ""): Flow<List<Estate>>

    @Query("SELECT * FROM estate WHERE estate.id = :id")
    fun getEstateById(id: Long): Flow<Estate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estate: Estate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForTest(estate: Estate)

    @Delete
    suspend fun delete(estate: Estate)
}