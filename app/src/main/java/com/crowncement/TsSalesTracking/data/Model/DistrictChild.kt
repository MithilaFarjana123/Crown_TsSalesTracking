package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class DistrictChild(

    @SerializedName("dis_code" ) var disCode : String? = null,
    @SerializedName("dis_hrc1" ) var disHrc1 : String? = null,
    @SerializedName("dis_hrc2" ) var disHrc2 : String? = null,
    @SerializedName("dis_name" ) var disName : String? = null,
    @SerializedName("dis_long" ) var disLong : String? = null
)
