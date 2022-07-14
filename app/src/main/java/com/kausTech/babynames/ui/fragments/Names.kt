package com.kausTech.babynames.ui.fragments

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "names")
class Names() {
    @PrimaryKey(autoGenerate = true)
    var key:Long=0L
    @ColumnInfo(name = "id")
    @NonNull
    var id: String = ""

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "name_meaning")
    var name_meaning: String = ""

    @ColumnInfo(name = "gender")
    var gender: String = ""

    @ColumnInfo(name = "rating")
    var rating: Int = 0
    var region: String = ""
}