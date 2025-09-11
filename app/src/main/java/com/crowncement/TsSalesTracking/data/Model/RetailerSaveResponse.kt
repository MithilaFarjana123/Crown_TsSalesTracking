package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class RetailerSaveResponse(

    @SerializedName("code"       ) var code      : String? = "",
    @SerializedName("message"    ) var message   : String? = "",
    @SerializedName("retaild_id" ) var retaildId : String? = ""

)
