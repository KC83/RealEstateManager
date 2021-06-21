package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider
import kotlinx.coroutines.CoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declare
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ContentProviderTest: KoinTest {
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var database: RealEstateRoomDatabase

    private val scope: CoroutineScope by inject()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context

        // Declare Koin for RealEstateRoomDatabase
        database = declare {
            Room.inMemoryDatabaseBuilder(context, RealEstateRoomDatabase::class.java)
                .addCallback(RealEstateRoomDatabase.RealEstateRoomDatabaseCallback(scope))
                .allowMainThreadQueries()
                .build()
        }

        contentResolver = context.contentResolver
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun getItems() {
        RealEstateRoomDatabase.RealEstateRoomDatabaseCallback(scope)

        // Agents
        var cursor: Cursor = contentResolver.query(RealEstateContentProvider().URI_AGENT,null,null,null,null) as Cursor
        assertEquals(5,cursor.count)

        // Status
        cursor = contentResolver.query(RealEstateContentProvider().URI_STATUS,null,null,null,null) as Cursor
        assertEquals(4,cursor.count)

        // Types
        cursor = contentResolver.query(RealEstateContentProvider().URI_TYPE,null,null,null,null) as Cursor
        assertEquals(5,cursor.count)

        // Places
        cursor = contentResolver.query(RealEstateContentProvider().URI_PLACE,null,null,null,null) as Cursor
        assertEquals(10,cursor.count)
    }

    @Test
    fun checkEstates() {
        // Check if database is empty
        var cursorEstate: Cursor = contentResolver.query(RealEstateContentProvider().URI_ESTATE,null,null,null,null) as Cursor
        var cursorEstateImage: Cursor = contentResolver.query(RealEstateContentProvider().URI_ESTATE_IMAGE,null,null,null,null) as Cursor
        var cursorEstatePlace: Cursor = contentResolver.query(RealEstateContentProvider().URI_ESTATE_PLACE,null,null,null,null) as Cursor

        assertEquals(0,cursorEstate.count)
        assertEquals(0,cursorEstateImage.count)
        assertEquals(0,cursorEstatePlace.count)

        // Create estate
        val estate = Estate(1,1,2,3,"01/01/2021","",500000F,275F,8,2,3,"La description du bien","5 Avenue de la Paix","75001","Paris","France")
        database.estateDao().insertForTest(estate)

        val estateImage = EstateImage(1,1,"uriEstateImage","Image du bien")
        database.estateImageDao().insertForTest(estateImage)

        val estatePlace = EstatePlace(1,1,5)
        database.estatePlaceDao().insertForTest(estatePlace)

        // Get elements
        val selectionArgs: Array<String> = arrayOf("1")

        cursorEstate = contentResolver.query(RealEstateContentProvider().URI_ESTATE,null,null,null,null) as Cursor
        cursorEstateImage = contentResolver.query(RealEstateContentProvider().URI_ESTATE_IMAGE,null,null,selectionArgs,null) as Cursor
        cursorEstatePlace = contentResolver.query(RealEstateContentProvider().URI_ESTATE_PLACE,null,null,selectionArgs,null) as Cursor

        // Check if database is not empty
        assertEquals(1,cursorEstate.count)
        assertEquals(1,cursorEstateImage.count)
        assertEquals(1,cursorEstatePlace.count)

        // Check values of the estate
        cursorEstate.moveToFirst()
        assertEquals("1",cursorEstate.getString(cursorEstate.getColumnIndex("status_id")))
        assertEquals("2",cursorEstate.getString(cursorEstate.getColumnIndex("type_id")))
        assertEquals("3",cursorEstate.getString(cursorEstate.getColumnIndex("agent_id")))
        assertEquals("01/01/2021",cursorEstate.getString(cursorEstate.getColumnIndex("insert_date")))
        assertEquals("500000",cursorEstate.getString(cursorEstate.getColumnIndex("price")))
        assertEquals("275",cursorEstate.getString(cursorEstate.getColumnIndex("surface")))
        assertEquals("8",cursorEstate.getString(cursorEstate.getColumnIndex("number_rooms")))
        assertEquals("2",cursorEstate.getString(cursorEstate.getColumnIndex("number_bathrooms")))
        assertEquals("3",cursorEstate.getString(cursorEstate.getColumnIndex("number_bedrooms")))
        assertEquals("La description du bien",cursorEstate.getString(cursorEstate.getColumnIndex("description")))
        assertEquals("5 Avenue de la Paix",cursorEstate.getString(cursorEstate.getColumnIndex("location")))
        assertEquals("75001",cursorEstate.getString(cursorEstate.getColumnIndex("zip_code")))
        assertEquals("Paris",cursorEstate.getString(cursorEstate.getColumnIndex("city")))
        assertEquals("France",cursorEstate.getString(cursorEstate.getColumnIndex("country")))

        // Check values of the estate image
        cursorEstateImage.moveToFirst()
        assertEquals("1",cursorEstateImage.getString(cursorEstateImage.getColumnIndex("estate_id")))
        assertEquals("uriEstateImage",cursorEstateImage.getString(cursorEstateImage.getColumnIndex("uri")))
        assertEquals("Image du bien",cursorEstateImage.getString(cursorEstateImage.getColumnIndex("name")))

        // Check values of the estate place
        cursorEstatePlace.moveToFirst()
        assertEquals("1",cursorEstatePlace.getString(cursorEstatePlace.getColumnIndex("estate_id")))
        assertEquals("5",cursorEstatePlace.getString(cursorEstatePlace.getColumnIndex("place_id")))
    }
}