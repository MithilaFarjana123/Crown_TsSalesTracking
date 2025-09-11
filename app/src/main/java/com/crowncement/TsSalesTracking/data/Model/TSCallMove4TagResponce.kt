package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TSCallMove4TagResponce(
    @SerializedName("code"    ) var code    : String?           = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("call_num"  ) var callNum  : String? = null
)
