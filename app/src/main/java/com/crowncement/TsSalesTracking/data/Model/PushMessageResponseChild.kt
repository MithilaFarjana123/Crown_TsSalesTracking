package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class PushMessageResponseChild(

    @SerializedName("sl_no"      ) var slNo      : String? = null,
    @SerializedName("not_title"  ) var notTitle  : String? = null,
    @SerializedName("not_detail" ) var notDetail : String? = null,
    @SerializedName("not_date"   ) var notDate   : String? = null,
    @SerializedName("not_time"   ) var notTime   : String? = null,
    @SerializedName("not_flag"   ) var notFlag   : String? = null,
    @SerializedName("not_rem"    ) var notRem    : String? = null
    
)
