package com.crowncement.TsSalesTracking.data.InternalDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.crowncement.TsSalesTracking.data.InternalDB.DAO.LocationTrackingDAO
import com.crowncement.TsSalesTracking.data.InternalDB.DAO.OfflineAttendanceDAO
import com.crowncement.TsSalesTracking.data.InternalDB.DAO.OfflineQuickVisitDAO
import com.crowncement.TsSalesTracking.data.Model.LocationTracking
import com.crowncement.TsSalesTracking.data.Model.OfflineAttendance
import com.crowncement.TsSalesTracking.data.Model.OfflineQuickVisit



@Database(
    entities = [LocationTracking::class, OfflineAttendance::class, OfflineQuickVisit::class],
    version = 3,
    exportSchema = false
)

abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun locationTrackingDao(): LocationTrackingDAO
    abstract fun offlineAttendanceDao(): OfflineAttendanceDAO
    abstract fun offlineQuickVisitDao(): OfflineQuickVisitDAO

    companion object {

        private var INSTANCE: RoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance as AppRoomDatabase
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "crown_tec_db"
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

