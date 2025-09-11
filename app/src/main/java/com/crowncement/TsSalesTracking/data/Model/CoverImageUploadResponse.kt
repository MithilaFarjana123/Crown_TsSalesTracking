package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class CoverImageUploadResponse(

    @SerializedName("code"        ) var code       : String? = null,
    @SerializedName("message"     ) var message    : String? = null,
    @SerializedName("party_image" ) var partyImage : String? = null

)
