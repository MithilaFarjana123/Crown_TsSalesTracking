package com.crowncement.TsSalesTracking.data.Model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "location_tracking")
data class LocationTracking(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("SID"        )
    @ColumnInfo(name="SID" )
    var SID        : Int  ,
    @SerializedName("Latitude"   )
    @ColumnInfo(name="Latitude" )
    var Latitude   : Double?,
    @SerializedName("Longitude"  )
    @ColumnInfo(name="Longitude" )
    var Longitude  : Double?,
    @SerializedName("CreateBy"   )
    @ColumnInfo(name="CreateBy" )
    var CreateBy   : String?,
    @SerializedName("CreateDate" )
    @ColumnInfo(name="CreateDate" )
    var CreateDate : String?


)
