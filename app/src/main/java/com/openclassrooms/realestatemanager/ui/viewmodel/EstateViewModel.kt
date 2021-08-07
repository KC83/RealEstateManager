package com.openclassrooms.realestatemanager.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.repository.*
import com.openclassrooms.realestatemanager.utils.SearchItem
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstateViewModel(private val repository: EstateRepository, private val estateImageRepository: EstateImageRepository, private val estatePlaceRepository: EstatePlaceRepository): ViewModel() {
    val allEstates: LiveData<List<EstateModel>> = repository.getAllEstates()

    private val _estateId : MutableLiveData<Long> = MutableLiveData()
    val estateId : LiveData<Long> = _estateId

    fun insert(estate: Estate) {
        repository.insert(estate)
    }

    fun delete(estate: Estate) {
        repository.delete(estate)
    }

    fun getFilteredEstates(searchItem: SearchItem): LiveData<List<EstateModel>> {
        return repository.getFilteredEstates(searchItem)
    }

    fun getEstateById(id: Long): LiveData<EstateModel> {
        return repository.getEstateById(id)
    }

    fun saveEstate(data: Intent?) {
        viewModelScope.launch(Dispatchers.Default) {
            val value = insertEstate(data,repository, estateImageRepository, estatePlaceRepository)
            if (value > 0L) {
                _estateId.postValue(value)
            }
        }
    }

    private suspend fun insertEstate(data: Intent?, estateRepository: EstateRepository, estateImageRepository: EstateImageRepository, estatePlaceRepository: EstatePlaceRepository): Long {
        val estate: Estate = data?.getSerializableExtra(Utils.EXTRA_ESTATE) as Estate
        val estateId = estateRepository.insert(estate)

        // Images
        val estateImages: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_ESTATE_IMAGE) as MutableList<EstateImage>
        val images: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_IMAGE) as MutableList<EstateImage>
        val imagesToKeep: MutableList<Long> = mutableListOf()

        // Remove first image, it's the image for adding new images
        estateImages.drop(1).forEach { image ->
            estateImageRepository.insert(EstateImage(id = image.id, estateId = estateId, uri = image.uri, name = image.name))
            if (image.id > 0) {
                imagesToKeep.add(image.id)
            }
        }
        // Delete estateImage if removed
        images.forEach { estateImage ->
            if (!imagesToKeep.contains(estateImage.id)) {
                estateImageRepository.delete(estateImage)
            }
        }

        // Places
        val placeIds: MutableList<Int>? = data.getSerializableExtra(Utils.EXTRA_PLACE) as MutableList<Int>?
        val estatePlaces: MutableList<EstatePlace>? = data.getSerializableExtra(Utils.EXTRA_ESTATE_PLACE) as MutableList<EstatePlace>?
        val estatePlacesToKeep: MutableList<EstatePlace> = mutableListOf()
        placeIds?.forEach { placeId ->
            // Get estate place already saved
            val estatePlace: EstatePlace? = estatePlaces?.firstOrNull { estatePlace -> estatePlace.placeId == placeId.toLong() }
            if (estatePlace != null) {
                estatePlacesToKeep.add(estatePlace)
            }

            estatePlaceRepository.insert(EstatePlace(id = estatePlace?.id ?: 0L, estateId = estateId, placeId = placeId.toLong()))
        }

        // Delete estatePlace if not checked
        estatePlaces?.forEach { estatePlace ->
            if (!estatePlacesToKeep.contains(estatePlace)) {
                estatePlaceRepository.delete(estatePlace)
            }
        }

        return estateId
    }
}

class EstateViewModelFactory(private val repository: EstateRepository,
                             private val estateImageRepository: EstateImageRepository,
                             private val estatePlaceRepository: EstatePlaceRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateViewModel(repository, estateImageRepository, estatePlaceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}