package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SiteVerificationSaveResponce(
    @SerializedName("code"    ) var code    : String? = "",
    @SerializedName("message" ) var message : String? = ""
)
