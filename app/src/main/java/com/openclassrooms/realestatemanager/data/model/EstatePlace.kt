package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estate_place")
data class EstatePlace(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "estate_id") val estateId: Long,
        @ColumnInfo(name = "place_id") val placeId: Long
)