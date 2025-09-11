package com.crowncement.TsSalesTracking.data.InternalDB.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crowncement.TsSalesTracking.data.Model.OfflineQuickVisit


@Dao
interface OfflineQuickVisitDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(attendance: OfflineQuickVisit)

    @Query("Select * from offline_quick_visit WHERE CreateDate =:CreateDate")
    fun getTodayQuickVisit(CreateDate: String): List<OfflineQuickVisit>


    @Query("DELETE FROM offline_quick_visit WHERE  QuickVisitSID=:SID")
    fun deleteOfflineQuickSID(SID: Int)


}