package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Place
import com.openclassrooms.realestatemanager.domain.repository.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceViewModel(private val repository: PlaceRepository): ViewModel() {
    val allPlaces: LiveData<List<Place>> = repository.allPlaces.asLiveData()

    fun insert(place: Place) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(place)
    }
}

class PlaceViewModelFactory(private val repository: PlaceRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}