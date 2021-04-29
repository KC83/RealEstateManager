package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.repository.EstatePlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstatePlaceViewModel(private val repository: EstatePlaceRepository): ViewModel() {
    /*fun getPlacesForAEstate(estateId: Long): LiveData<List<EstatePlace>> {
        return repository.getPlacesForAEstate(estateId).asLiveData()
    }*/

    fun insert(estatePlace: EstatePlace) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(estatePlace)
    }

    fun delete(estatePlace: EstatePlace) = viewModelScope.launch(Dispatchers.Default) {
        repository.delete(estatePlace)
    }
}

class EstatePlaceViewModelFactory(private val repository: EstatePlaceRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstatePlaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstatePlaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}