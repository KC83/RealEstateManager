package com.openclassrooms.realestatemanager.utils

data class SearchItem(var status: List<Int> = mutableListOf(),
                      var types: List<Int> = mutableListOf(),
                      var agents: List<Int> = mutableListOf(),
                      var places: List<Int> = mutableListOf(),
                      var price: Double = 0.0,
                      var surface: Double = 0.0,
                      var city: String = "")