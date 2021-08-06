package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "estate_place",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Estate::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("estate_id")
                ),
                ForeignKey(
                        entity = Place::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("place_id")
                )
        ))
data class EstatePlace(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "estate_id") val estateId: Long,
        @ColumnInfo(name = "place_id") val placeId: Long
) : Serializable