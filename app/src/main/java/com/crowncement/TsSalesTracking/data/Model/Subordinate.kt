package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class Subordinate(

    @SerializedName("code"    ) var code    : String? = null,
    @SerializedName("message" ) var message : String? = null

)
