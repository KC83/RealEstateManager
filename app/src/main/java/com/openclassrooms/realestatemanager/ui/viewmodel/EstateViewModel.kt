package com.openclassrooms.realestatemanager.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.domain.repository.EstateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateViewModel(private val repository: EstateRepository): ViewModel() {
    val allEstates: LiveData<List<Estate>> = repository.allEstates
    fun insert(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(estate)
    }
    fun delete(estate: Estate) = viewModelScope.launch(Dispatchers.Default) {
        repository.delete(estate)
    }
}