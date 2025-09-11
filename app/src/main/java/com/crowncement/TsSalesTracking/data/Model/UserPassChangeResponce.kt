package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class UserPassChangeResponce(

    @SerializedName("code"    ) var code    : String? = null,
    @SerializedName("message" ) var message : String? = null
)
