package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LeaveSaveResponse(

    @SerializedName("code"           ) var code          : String? = null,
    @SerializedName("message"        ) var message       : String? = null,
    @SerializedName("notific_status" ) var notificStatus : String? = null

)
