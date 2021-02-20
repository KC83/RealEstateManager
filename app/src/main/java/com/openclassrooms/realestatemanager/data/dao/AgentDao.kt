package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.Agent

@Dao
interface AgentDao {
    @Query("SELECT * FROM agent ORDER BY last_name")
    fun getAgents(): LiveData<List<Agent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agent: Agent)
}