package com.openclassrooms.realestatemanager.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.OnConflictStrategy
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

    class RealEstateRoomDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            scope.launch {
                // AGENTS
                db.execSQL("DELETE FROM agent")
                db.execSQL("INSERT INTO agent (id, full_name) VALUES ('1', 'Vincent Dupond')")
                db.execSQL("INSERT INTO agent (id, full_name) VALUES ('2', 'Lisa Martin')")
                db.execSQL("INSERT INTO agent (id, full_name) VALUES ('3', 'Olivia Meyer')")
                db.execSQL("INSERT INTO agent (id, full_name) VALUES ('4', 'Arthur Legrand')")
                db.execSQL("INSERT INTO agent (id, full_name) VALUES ('5', 'Charlotte Lacas')")

                // STATUS
                db.execSQL("DELETE FROM status")
                db.execSQL("INSERT INTO status (id, name) VALUES ('1', 'A vendre')")
                db.execSQL("INSERT INTO status (id, name) VALUES ('2', 'A louer')")
                db.execSQL("INSERT INTO status (id, name) VALUES ('3', 'Vendu')")
                db.execSQL("INSERT INTO status (id, name) VALUES ('4', 'Loué')")

                // TYPES
                db.execSQL("DELETE FROM type")
                db.execSQL("INSERT INTO type (id, name) VALUES ('1', 'Maison')")
                db.execSQL("INSERT INTO type (id, name) VALUES ('2', 'Villa')")
                db.execSQL("INSERT INTO type (id, name) VALUES ('3', 'Appartement')")
                db.execSQL("INSERT INTO type (id, name) VALUES ('4', 'Loft')")
                db.execSQL("INSERT INTO type (id, name) VALUES ('5', 'Terrain')")

                // PLACES
                db.execSQL("DELETE FROM place")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('1', 'Supermarchés','"+ R.drawable.ic_supermarket+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('2', 'Restaurants','"+ R.drawable.ic_dining_room+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('3', 'Poste de police','"+ R.drawable.ic_police_station+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('4', 'Banques','"+ R.drawable.ic_bank+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('5', 'Ecoles','"+ R.drawable.ic_school+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('6', 'Stations-service','"+ R.drawable.ic_fuel_pump+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('7', 'Stationnements','"+ R.drawable.ic_parking_meter+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('8', 'Hôpitaux','"+ R.drawable.ic_hospital+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('9', 'Musée','"+ R.drawable.ic_museum+"')")
                db.execSQL("INSERT INTO place (id, name, logo) VALUES ('10', 'Cinéma','"+ R.drawable.ic_cinema+"')")
            }
        }
    }
}


