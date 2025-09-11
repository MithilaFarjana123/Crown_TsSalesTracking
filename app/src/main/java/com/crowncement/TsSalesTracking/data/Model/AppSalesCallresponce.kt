package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class AppSalesCallresponce(
    @SerializedName("code"    ) var code    : String? = "",
    @SerializedName("message" ) var message : String? = ""
)
