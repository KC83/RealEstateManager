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

@Database(entities = arrayOf(Agent::class, Estate::class, EstatePlace::class, Place::class, Status::class, Type::class), version = 1, exportSchema = false)
abstract class RealEstateRoomDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun estateDao(): EstateDao
    abstract fun estatePlaceDao(): EstatePlaceDao
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
                    agentDao.insert(Agent(firstName = "Vincent", lastName = "Dupond"))
                    agentDao.insert(Agent(firstName = "Lisa", lastName = "Martin"))
                    agentDao.insert(Agent(firstName = "Olivia", lastName = "Meyer"))
                    agentDao.insert(Agent(firstName = "Arthur", lastName = "Legrand"))
                    agentDao.insert(Agent(firstName = "Charlotte", lastName = "Lacas"))

                    val statusDao = database.statusDao()
                    statusDao.insert(Status(name = "A vendre"))
                    statusDao.insert(Status(name = "A louer"))
                    statusDao.insert(Status(name = "Vendu"))
                    statusDao.insert(Status(name = "Loué"))

                    val typeDao = database.typeDao()
                    typeDao.insert(Type(name = "Maison"))
                    typeDao.insert(Type(name = "Villa"))
                    typeDao.insert(Type(name = "Appartement"))
                    typeDao.insert(Type(name = "Loft"))
                    typeDao.insert(Type(name = "Terrain"))

                    val placeDao = database.placeDao()
                    placeDao.insert(Place(name = "Supermarchés",logo = ""))
                    placeDao.insert(Place(name = "Restaurants",logo = ""))
                    placeDao.insert(Place(name = "Hôtels",logo = ""))
                    placeDao.insert(Place(name = "Banques",logo = ""))
                    placeDao.insert(Place(name = "Écoles",logo = ""))
                    placeDao.insert(Place(name = "Pharmacies",logo = ""))
                    placeDao.insert(Place(name = "Stationnements",logo = ""))
                    placeDao.insert(Place(name = "Hôpitaux",logo = ""))
                    placeDao.insert(Place(name = "Bureaux de poste",logo = ""))
                    placeDao.insert(Place(name = "Stations-service",logo = ""))
                }
            }
        }
    }
}


