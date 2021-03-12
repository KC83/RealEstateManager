package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Agent
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.repository.AgentRepository
import com.openclassrooms.realestatemanager.domain.repository.EstateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgentViewModel(private val repository: AgentRepository): ViewModel() {
    val allAgents: LiveData<List<Agent>> = repository.allAgents.asLiveData()

    fun insert(agent: Agent) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(agent)
    }

    fun getAgentByName(name: String): Agent? {
        return allAgents.value?.firstOrNull { obj -> obj.fullName == name }
    }
}
class AgentViewModelFactory(private val repository: AgentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}