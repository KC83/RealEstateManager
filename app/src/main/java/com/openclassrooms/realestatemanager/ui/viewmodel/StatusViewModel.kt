package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.data.model.Type
import com.openclassrooms.realestatemanager.domain.repository.AgentRepository
import com.openclassrooms.realestatemanager.domain.repository.RealEstateApplication
import com.openclassrooms.realestatemanager.domain.repository.StatusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatusViewModel(private val repository: StatusRepository): ViewModel() {
    val allStatus: LiveData<List<Status>> = repository.allStatus

    fun insert(status: Status) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(status)
    }

    fun getStatusByName(name: String): Status? {
        return allStatus.value?.firstOrNull { obj -> obj.name == name }
    }
}
class StatusViewModelFactory(private val repository: StatusRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}