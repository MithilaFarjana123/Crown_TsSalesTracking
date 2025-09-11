package com.crowncement.TsSalesTracking.data.InternalDB


import android.content.Context
import androidx.room.Room

object DatabaseSingleton {

    var database: AppRoomDatabase? = null

    fun getAppDatabase(context: Context): AppRoomDatabase {
        return if (database == null) {
            database = Room.databaseBuilder(context, AppRoomDatabase::class.java, "crown_tec_db").build()
            database!!
        } else {
            database!!
        }
    }
}