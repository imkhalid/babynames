package com.kausTech.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "regions")
data class Regions(
    val origin_name: String? = "",
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0
)
