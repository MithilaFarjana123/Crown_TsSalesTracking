package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class updateSaleBPNewSiteResponce(
    @SerializedName("code"    ) var code    : String?           = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("json"    ) var json    : ArrayList<String> = arrayListOf()
)
