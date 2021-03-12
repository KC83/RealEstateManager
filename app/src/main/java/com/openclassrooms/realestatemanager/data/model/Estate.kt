package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "estate")
data class Estate(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "status") val status: String,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "agent") val agent: String,
        @ColumnInfo(name = "insert_date") val insertDate: String,
        @ColumnInfo(name = "sale_date") val saleDate: String,
        @ColumnInfo(name = "price") val price: Float,
        @ColumnInfo(name = "surface") val surface: Float,
        @ColumnInfo(name = "number_rooms") val numberRooms: Int,
        @ColumnInfo(name = "number_bathrooms") val numberBathrooms: Int,
        @ColumnInfo(name = "number_bedrooms") val numberBedrooms: Int,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "location") val location: String,
        @ColumnInfo(name = "zip_code") val zipCode: String,
        @ColumnInfo(name = "city") val city: String,
        @ColumnInfo(name = "country") val country: String
) : Serializable