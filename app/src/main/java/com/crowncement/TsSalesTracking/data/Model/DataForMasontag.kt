package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class DataForMasontag(
    @SerializedName("party_code"  ) var partyCode  : String? = null,
    @SerializedName("vendor_code" ) var vendorCode : String? = null
)
