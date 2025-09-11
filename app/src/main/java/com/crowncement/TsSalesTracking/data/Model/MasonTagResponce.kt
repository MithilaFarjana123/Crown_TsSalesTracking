package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MasonTagResponce(
    @SerializedName("code"    ) var code    : String?           = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("json"    ) var MasonTagResponcechild    : ArrayList<String> = arrayListOf()
)
