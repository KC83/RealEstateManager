package com.openclassrooms.realestatemanager.data.model

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "estate_image",
        foreignKeys = arrayOf(
            ForeignKey(
                entity = Estate::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("estate_id"),
                onDelete = ForeignKey.CASCADE
            )
        ),
        indices = [Index(value = ["uri"], unique = true)]
)
data class EstateImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "estate_id") val estateId: Long,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "name") var name: String
): Serializable