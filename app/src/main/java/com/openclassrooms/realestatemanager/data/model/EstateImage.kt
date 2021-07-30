package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "estate_image", indices = [Index(value = ["uri"], unique = true)])
data class EstateImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "estate_id") val estateId: Long,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "name") var name: String
): Serializable