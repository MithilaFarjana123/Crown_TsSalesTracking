package com.crowncement.TsSalesTracking.data.InternalDB.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crowncement.TsSalesTracking.data.Model.LocationTracking


@Dao
interface LocationTrackingDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(item: LocationTracking)

    @Query("Select * FROM  location_tracking ORDER BY SID DESC")
    fun getAllLocationInfo(): List<LocationTracking?>?

    @Query("DELETE FROM location_tracking WHERE  SID=:SID")
    fun deleteLocationBySID(SID: Int)

    @Query("DELETE FROM location_tracking WHERE CreateBy=:name")
    fun deleteAllTrackingData(name: String)

    @Query("DELETE FROM location_tracking")
    fun deleteAllData()
}