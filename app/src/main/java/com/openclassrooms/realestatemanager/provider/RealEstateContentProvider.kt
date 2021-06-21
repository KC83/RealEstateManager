package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import org.koin.android.ext.android.inject

class RealEstateContentProvider : ContentProvider() {
    private val database: RealEstateRoomDatabase by inject()

    val AUTHORITY = "com.openclassrooms.realestatemanager.provider"

    val TABLE_AGENT: String = "agent"
    val TABLE_STATUS: String = "status"
    val TABLE_TYPE: String = "type"
    val TABLE_PLACE: String = "place"
    val TABLE_ESTATE: String = "estate"
    val TABLE_ESTATE_IMAGE: String = "estate_image"
    val TABLE_ESTATE_PLACE: String = "estate_place"

    val URI_AGENT: Uri = Uri.parse("content://$AUTHORITY/$TABLE_AGENT")
    val URI_STATUS: Uri = Uri.parse("content://$AUTHORITY/$TABLE_STATUS")
    val URI_TYPE: Uri = Uri.parse("content://$AUTHORITY/$TABLE_TYPE")
    val URI_PLACE: Uri = Uri.parse("content://$AUTHORITY/$TABLE_PLACE")
    val URI_ESTATE: Uri = Uri.parse("content://$AUTHORITY/$TABLE_ESTATE")
    val URI_ESTATE_IMAGE: Uri = Uri.parse("content://$AUTHORITY/$TABLE_ESTATE_IMAGE")
    val URI_ESTATE_PLACE: Uri = Uri.parse("content://$AUTHORITY/$TABLE_ESTATE_PLACE")

    override fun onCreate(): Boolean {
        return true
    }
    override fun getType(uri: Uri): String {
        return when(uri) {
            URI_AGENT -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_AGENT"
            URI_STATUS -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_STATUS"
            URI_TYPE -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_TYPE"
            URI_PLACE -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_PLACE"
            URI_ESTATE -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_ESTATE"
            URI_ESTATE_IMAGE -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_ESTATE_IMAGE"
            URI_ESTATE_PLACE -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_ESTATE_PLACE"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        if (context != null) {
            when(uri) {
                URI_AGENT -> {
                    val cursor: Cursor = database.agentDao().getAgentsWithCursor()
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_STATUS -> {
                    val cursor: Cursor = database.statusDao().getStatusWithCursor()
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_TYPE -> {
                    val cursor: Cursor = database.typeDao().getTypesWithCursor()
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_PLACE -> {
                    val cursor: Cursor = database.placeDao().getPlacesWithCursor()
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_ESTATE -> {
                    val cursor: Cursor = database.estateDao().getEstatesWithCursor()
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_ESTATE_IMAGE -> {
                    var valId: Long = 0
                    if (selectionArgs != null && selectionArgs.isNotEmpty()) {
                        valId = selectionArgs[0].toLong()
                    }

                    val cursor: Cursor = database.estateImageDao().getImagesForAEstateWithCursor(valId)
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
                URI_ESTATE_PLACE -> {
                    var valId: Long = 0
                    if (selectionArgs != null && selectionArgs.isNotEmpty()) {
                        valId = selectionArgs[0].toLong()
                    }

                    val cursor: Cursor = database.estatePlaceDao().getEstatePlacesForAEstateWithCursor(valId)
                    cursor.setNotificationUri(context!!.contentResolver, uri)
                    return cursor
                }
            }
        }

        throw IllegalArgumentException("Failed to query row for uri $uri")
    }
    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }
    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }
    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }
}