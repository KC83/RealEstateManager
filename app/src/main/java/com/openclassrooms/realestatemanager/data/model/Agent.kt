package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "agent")
data class Agent(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "full_name") val fullName: String
) : Serializable