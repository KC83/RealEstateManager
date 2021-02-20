package com.openclassrooms.realestatemanager.domain.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.Agent

class AgentRepository(private val agentDao: AgentDao) {
    val allAgents: LiveData<List<Agent>> = agentDao.getAgents()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(agent: Agent) {
        agentDao.insert(agent)
    }
}