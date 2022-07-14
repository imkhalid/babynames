package com.kausTech.network.localDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kausTech.babynames.ui.fragments.Names
import com.kausTech.network.model.Regions

@Database(
    entities = [Names::class,Regions::class],
    version = 1,
    exportSchema = true
)
abstract class NamesDatabase : RoomDatabase()  {

    abstract fun noteDao(): NamesDao
    abstract fun regionDao(): RegionDao

    companion object{
        @Volatile
        private var INSTANCE: NamesDatabase? = null

        fun getDatabase(context: Context?=null): NamesDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    context?.let {  INSTANCE = buildDatabase(it)}?: kotlin.run { throw Exception("COntext is Required") }
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): NamesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NamesDatabase::class.java,
                "names_database"
            )
                .build()
        }

    }
}