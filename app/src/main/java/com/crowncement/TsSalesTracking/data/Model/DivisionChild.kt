package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class DivisionChild(

    @SerializedName("div_code") var divCode: String? = null,
    @SerializedName("div_hrc1") var divHrc1: String? = null,
    @SerializedName("div_name") var divName: String? = null,
    @SerializedName("div_long") var divLong: String? = null

)
