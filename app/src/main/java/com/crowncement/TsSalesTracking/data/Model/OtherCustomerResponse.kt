package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomerResponse(

    @SerializedName("code"    ) var code    : String?                               = "",
    @SerializedName("message" ) var message : String?                               = "",
    @SerializedName("data"    ) var data    : ArrayList<OtherCustomerChild>         = arrayListOf()

)
