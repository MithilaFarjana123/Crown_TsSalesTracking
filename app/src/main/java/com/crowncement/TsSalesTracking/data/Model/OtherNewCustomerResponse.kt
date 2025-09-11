package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherNewCustomerResponse(

    @SerializedName("code"   ) var code      : String? = "",
    @SerializedName("message") var message   : String? = "",
    @SerializedName("bp_id"  ) var bp_id     : String? = ""
)
