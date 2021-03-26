package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Agent
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.data.model.Type
import com.openclassrooms.realestatemanager.domain.repository.AgentRepository
import com.openclassrooms.realestatemanager.domain.repository.StatusRepository
import com.openclassrooms.realestatemanager.domain.repository.TypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TypeViewModel(private val repository: TypeRepository): ViewModel() {
    val allTypes: LiveData<List<Type>> = repository.allTypes.asLiveData()

    fun insert(type: Type) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(type)
    }

    fun getTypeByName(name: String): Type? {
        return allTypes.value?.firstOrNull { obj -> obj.name == name }
    }
}
class TypeViewModelFactory(private val repository: TypeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TypeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TypeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
