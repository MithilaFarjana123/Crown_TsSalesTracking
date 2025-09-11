package com.crowncement.TsSalesTracking.data.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "offline_attendance")
data class OfflineAttendance(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("AttSID"        )
    @ColumnInfo(name="AttSID" )
    var AttSID        : Int  ,
    @SerializedName("Latitude"   )
    @ColumnInfo(name="Latitude" )
    var Latitude   : Double?,
    @SerializedName("Longitude"  )
    @ColumnInfo(name="Longitude" )
    var Longitude  : Double?,

    @SerializedName("CreateBy"   )
    @ColumnInfo(name="CreateBy" )
    var CreateBy   : String?,


    @SerializedName("AttTime"   )
    @ColumnInfo(name="AttTime" )
    var AttTime   : String?,

    @SerializedName("AttImagePath"   )
    @ColumnInfo(name="AttImagePath" )
    var AttImagePath   : String?,

    @SerializedName("CreateDate" )
    @ColumnInfo(name="CreateDate" )
    var CreateDate : String?,

    @SerializedName("UploadStatus" )
    @ColumnInfo(name="UploadStatus" )
    var UploadStatus : Boolean

)
