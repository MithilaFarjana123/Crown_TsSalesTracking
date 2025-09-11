package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ThanaChild
    (
    @SerializedName("thn_code")   var thnCode : String? = null,
    @SerializedName("thn_hrc1")   var thnHrc1 : String? = null,
    @SerializedName("thn_hrc2")   var thnHrc2 : String? = null,
    @SerializedName("thn_hrc3")   var thnHrc3 : String? = null,
    @SerializedName("thn_name")   var thnName : String? = null,
    @SerializedName("thn_long")   var thnLong : String? = null,
    @SerializedName("zone_code")  var zoneCode: String? = null
)
