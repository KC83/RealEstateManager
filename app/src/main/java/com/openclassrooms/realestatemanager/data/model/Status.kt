package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status")
data class Status(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "name") val name: String
)