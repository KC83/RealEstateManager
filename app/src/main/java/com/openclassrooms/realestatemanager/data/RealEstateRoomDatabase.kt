package com.openclassrooms.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.dao.*
import com.openclassrooms.realestatemanager.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Agent::class, Estate::class, EstatePlace::class, EstateImage::class, Place::class, Status::class, Type::class], version = 1, exportSchema = false)
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
                    placeDao.insert(Place(id = 1, name = "Supermarchés",logo = R.drawable.ic_supermarket))
                    placeDao.insert(Place(id = 2, name = "Restaurants",logo = R.drawable.ic_dining_room))
                    placeDao.insert(Place(id = 3, name = "Poste de police",logo = R.drawable.ic_police_station))
                    placeDao.insert(Place(id = 4, name = "Banques",logo = R.drawable.ic_bank))
                    placeDao.insert(Place(id = 5, name = "Ecoles",logo = R.drawable.ic_school))
                    placeDao.insert(Place(id = 6, name = "Stations-service",logo = R.drawable.ic_fuel_pump))
                    placeDao.insert(Place(id = 7, name = "Stationnements",logo = R.drawable.ic_parking_meter))
                    placeDao.insert(Place(id = 8, name = "Hôpitaux",logo = R.drawable.ic_hospital))
                    placeDao.insert(Place(id = 9, name = "Musée",logo = R.drawable.ic_museum))
                    placeDao.insert(Place(id = 10, name = "Cinéma",logo = R.drawable.ic_cinema))
                }
            }
        }
    }
}


