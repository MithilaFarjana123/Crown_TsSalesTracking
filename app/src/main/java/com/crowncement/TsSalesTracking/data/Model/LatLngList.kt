package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LatLngList(
    @SerializedName("site_number"       ) var siteNumber       : String? = "",
    @SerializedName("site_add"          ) var siteAdd          : String? = "",
    @SerializedName("party_lat"         ) var partyLat         : Double? = 0.0,
    @SerializedName("party_lon"         ) var partyLon         : Double? = 0.0
)
