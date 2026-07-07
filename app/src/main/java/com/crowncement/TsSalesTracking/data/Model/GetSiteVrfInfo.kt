package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSiteVrfInfo(
    @SerializedName("code"    ) var code    : String?         = "",
    @SerializedName("message" ) var message : String?         = "",
    @SerializedName("data"    ) var data    : ArrayList<GetSiteVrfInfoChild> = arrayListOf()
)
