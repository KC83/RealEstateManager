package com.openclassrooms.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.data.dao.*
import com.openclassrooms.realestatemanager.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Agent::class, Estate::class, EstatePlace::class, EstateImage::class, Place::class, Status::class, Type::class), version = 1, exportSchema = false)
abstract class RealEstateRoomDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun estateDao(): EstateDao
    abstract fun estatePlaceDao(): EstatePlaceDao
    abstract fun estateImageDao(): EstateImageDao
    abstract fun placeDao(): PlaceDao
    abstract fun statusDao(): StatusDao
    abstract fun typeDao(): TypeDao

    companion object {
        @Volatile
        internal var INSTANCE: RealEstateRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope):RealEstateRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, RealEstateRoomDatabase::class.java, "realestate_database")
                        .addCallback(RealEstateRoomDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RealEstateRoomDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val agentDao = database.agentDao()
                    agentDao.insert(Agent(id = 1, fullName = "Vincent Dupond"))
                    agentDao.insert(Agent(id = 2, fullName = "Lisa Martin"))
                    agentDao.insert(Agent(id = 3, fullName = "Olivia Meyer"))
                    agentDao.insert(Agent(id = 4, fullName = "Arthur Legrand"))
                    agentDao.insert(Agent(id = 5, fullName = "Charlotte Lacas"))

                    val statusDao = database.statusDao()
                    statusDao.insert(Status(id = 1, name = "A vendre"))
                    statusDao.insert(Status(id = 2, name = "A louer"))
                    statusDao.insert(Status(id = 3, name = "Vendu"))
                    statusDao.insert(Status(id = 4, name = "Loué"))

                    val typeDao = database.typeDao()
                    typeDao.insert(Type(id = 1, name = "Maison"))
                    typeDao.insert(Type(id = 2, name = "Villa"))
                    typeDao.insert(Type(id = 3, name = "Appartement"))
                    typeDao.insert(Type(id = 4, name = "Loft"))
                    typeDao.insert(Type(id = 5, name = "Terrain"))

                    val placeDao = database.placeDao()
                    placeDao.insert(Place(id = 1, name = "Supermarchés",logo = ""))
                    placeDao.insert(Place(id = 2, name = "Restaurants",logo = ""))
                    placeDao.insert(Place(id = 3, name = "Hôtels",logo = ""))
                    placeDao.insert(Place(id = 4, name = "Banques",logo = ""))
                    placeDao.insert(Place(id = 5, name = "Écoles",logo = ""))
                    placeDao.insert(Place(id = 6, name = "Pharmacies",logo = ""))
                    placeDao.insert(Place(id = 7, name = "Stationnements",logo = ""))
                    placeDao.insert(Place(id = 8, name = "Hôpitaux",logo = ""))
                    placeDao.insert(Place(id = 9, name = "Bureaux de poste",logo = ""))
                    placeDao.insert(Place(id = 10, name = "Stations-service",logo = ""))
                }
            }
        }
    }
}


