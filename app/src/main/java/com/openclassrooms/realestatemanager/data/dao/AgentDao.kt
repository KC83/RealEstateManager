package com.openclassrooms.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Agent
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agent")
    fun getAgentsWithCursor(): Cursor

    @Query("SELECT * FROM agent ORDER BY full_name")
    fun getAgents(): Flow<List<Agent>>

    @Query("SELECT * FROM agent WHERE agent.id = :id")
    suspend fun getAgentById(id: Long): Agent

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agent: Agent)
}