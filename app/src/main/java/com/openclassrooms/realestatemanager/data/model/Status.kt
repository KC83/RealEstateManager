package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "status")
data class Status(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "name") val name: String
) : Serializable