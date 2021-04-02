package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.domain.repository.EstateImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateImageViewModel(private val repository: EstateImageRepository): ViewModel() {
    fun getImagesForAEstate(estateId: Long): LiveData<List<EstateImage>> {
        return repository.getImagesForAEstate(estateId).asLiveData()
    }

    fun insert(estateImage: EstateImage) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(estateImage)
    }

    fun delete(estateImage: EstateImage) = viewModelScope.launch(Dispatchers.Default) {
        repository.delete(estateImage)
    }
}

class EstateImageViewModelFactory(private val repository: EstateImageRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}