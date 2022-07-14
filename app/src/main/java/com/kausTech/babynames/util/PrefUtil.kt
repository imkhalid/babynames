package com.kausTech.babynames.util

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity

class PrefUtil {
    companion object{
        var INSTANCE:SharedPreferences?=null
        fun getPrefs(context:Context?=null):SharedPreferences{
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    context?.let {  INSTANCE = it.getSharedPreferences("MyPRes", ComponentActivity.MODE_PRIVATE)}
                        ?: kotlin.run { throw Exception("Context is Required") }
                }
            }
            return INSTANCE!!
        }
    }

}