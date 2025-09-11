package com.crowncement.TsSalesTracking.data.InternalDB.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crowncement.TsSalesTracking.data.Model.OfflineAttendance

@Dao
interface OfflineAttendanceDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(attendance: OfflineAttendance)

    @Query("Select * from offline_attendance WHERE CreateDate =:CreateDate")
    fun getTodayAttendance(CreateDate: String):OfflineAttendance

    @Query("DELETE  from offline_attendance WHERE CreateDate =:CreateDate")
    fun deleteTodayAttendance(CreateDate: String)
}