package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Agent
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agent ORDER BY full_name")
    fun getAgents(): Flow<List<Agent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agent: Agent)
}