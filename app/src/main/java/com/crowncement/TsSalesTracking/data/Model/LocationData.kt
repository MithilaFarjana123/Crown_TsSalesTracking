package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LocationData(

    @SerializedName("emp"        ) var emp       : String? = null,
    @SerializedName("docnum"     ) var docnum    : String? = null,
    @SerializedName("loc_row"    ) var locRow    : Int?    = null,
    @SerializedName("loc_intime" ) var locIntime : String? = null,
    @SerializedName("loc_lat"    ) var locLat    : Double? = null,
    @SerializedName("loc_lon"    ) var locLon    : Double? = null,
    @SerializedName("loc_type"   ) var locType   : String? = null
)
