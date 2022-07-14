package com.kausTech.network.localDb

import androidx.room.Dao
import androidx.room.Query
import com.kausTech.network.model.Regions

@Dao
abstract class RegionDao:BaseDao<Regions>() {
    @Query("SELECT * FROM regions ORDER BY origin_name ASC limit(:limit)")
    abstract suspend fun getRegions(limit:Int): List<Regions>
}