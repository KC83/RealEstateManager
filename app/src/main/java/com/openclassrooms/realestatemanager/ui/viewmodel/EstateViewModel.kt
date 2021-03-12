package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.domain.repository.EstateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateViewModel(private val repository: EstateRepository): ViewModel() {
    val allEstates: LiveData<List<Estate>> = repository.allEstates.asLiveData()

    private val _estateId : MutableLiveData<Long> = MutableLiveData()
    val estateId : LiveData<Long> = _estateId

    fun insert(estate: Estate) {
        viewModelScope.launch(Dispatchers.Default) {
            _estateId.postValue(repository.insert(estate))
        }
    }

    fun delete(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        repository.delete(estate)
    }
}

class EstateViewModelFactory(private val repository: EstateRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}