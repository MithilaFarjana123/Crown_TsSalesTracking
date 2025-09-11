package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ProfileForSiteTag(

    @SerializedName("party_code"  ) var partyCode  : String? = null,
    @SerializedName("site_number" ) var siteNumber : String? = null,
    @SerializedName("party_type"  ) var partyType  : String? = null
)
