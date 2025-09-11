package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class CarReqSaveresponce(
    @SerializedName("code"           ) var code          : String? = null,
    @SerializedName("message"        ) var message       : String? = null,
    @SerializedName("requisition_no" ) var requisitionNo : String? = null
)
