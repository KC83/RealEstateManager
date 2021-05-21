package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.Agent
import kotlinx.coroutines.flow.Flow

class AgentRepository(private val agentDao: AgentDao) {
    val allAgents: Flow<List<Agent>> = agentDao.getAgents()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(agent: Agent) {
        agentDao.insert(agent)
    }
}