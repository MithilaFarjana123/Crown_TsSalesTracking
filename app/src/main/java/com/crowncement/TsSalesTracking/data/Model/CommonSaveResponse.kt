package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class CommonSaveResponse(
    @SerializedName("code") var code: String? = "",
    @SerializedName("message") var message: String? = "",
    @SerializedName("notific_status") var notific_status: String? = ""
)
