package com.crowncement.TsSalesTracking.data.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "offline_quick_visit")
data class OfflineQuickVisit(


    @PrimaryKey(autoGenerate = true)
    @SerializedName("QuickVisitSID")
    @ColumnInfo(name="QuickVisitSID" )
    var QuickVisitSID        : Int  ,

    @SerializedName("ClientType"   )
    @ColumnInfo(name="ClientType" )
    var ClientType   : String?,

    @SerializedName("ClientName"   )
    @ColumnInfo(name="ClientName" )
    var ClientName   : String?,

    @SerializedName("Latitude"   )
    @ColumnInfo(name="Latitude" )
    var Latitude   : Double?,

    @SerializedName("Longitude"  )
    @ColumnInfo(name="Longitude" )
    var Longitude  : Double?,

    @SerializedName("CreateBy")
    @ColumnInfo(name="CreateBy")
    var CreateBy   : String?,

    @SerializedName("CreateDate")
    @ColumnInfo(name="CreateDate")
    var CreateDate   : String?,

    @SerializedName("CreateTime")
    @ColumnInfo(name="CreateTime")
    var CreateTime   : String?,

    @SerializedName("Remarks")
    @ColumnInfo(name="Remarks")
    var Remarks   : String?

)
