package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetTSMovLocationResponceChild(
    @SerializedName("xrow"   ) var xrow   : String? = "",
    @SerializedName("xroute" ) var xroute : String? = "",
    @SerializedName("zact"   ) var zact   : Int?    = 0
)
