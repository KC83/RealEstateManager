package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.domain.repository.*
import com.openclassrooms.realestatemanager.utils.SearchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateViewModel(private val repository: EstateRepository): ViewModel() {
    val allEstates: LiveData<List<EstateModel>> = repository.getAllEstates()

    private val _estateId : MutableLiveData<Long> = MutableLiveData()
    val estateId : LiveData<Long> = _estateId

    fun getEstateById(id: Long): LiveData<EstateModel> {
        return repository.getEstateById(id)
    }

    fun getFilteredEstates(searchItem: SearchItem): LiveData<List<EstateModel>> {
        return repository.getFilteredEstates(searchItem)
    }

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