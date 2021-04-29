package com.openclassrooms.realestatemanager.data.model

import java.io.Serializable

data class EstateModel(val estate: Estate, val type: Type, val status: Status, val agent: Agent, val images: MutableList<EstateImage>, val estatePlaces: MutableList<EstatePlace>, val places: List<Place>) : Serializable