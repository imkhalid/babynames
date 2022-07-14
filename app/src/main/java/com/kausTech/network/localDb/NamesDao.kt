package com.kausTech.network.localDb

import androidx.room.Dao
import androidx.room.Query
import com.kausTech.babynames.ui.fragments.Names
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NamesDao :BaseDao<Names>() {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    abstract suspend fun addNote(names: Names)
//
    @Query("SELECT * FROM names ORDER BY name ASC limit(:limit)")
    abstract suspend fun getNames(limit:Int): List<Names>

    @Query("SELECT DISTINCT region FROM names ORDER BY region ASC")
    abstract suspend fun getRegions(): List<String>

    @Query("SELECT * FROM names Where region=:region")
    abstract suspend fun getNamesByRegion(region:String): List<Names>
//
//    @Update
//    abstract suspend fun updateNote(note: Names)
//
//    @Delete
//    abstract suspend fun deleteNote(note: Names)
}