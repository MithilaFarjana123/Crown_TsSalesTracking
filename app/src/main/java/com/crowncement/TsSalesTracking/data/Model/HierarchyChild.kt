package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class HierarchyChild(
    @SerializedName("emp_code"   ) var empCode   : String? = null,
    @SerializedName("rep_code"   ) var repCode   : String? = null,
    @SerializedName("rep_name"   ) var repName   : String? = null,
    @SerializedName("hrc_type"   ) var hrcType   : String? = null,
    @SerializedName("hrc_status" ) var hrcStatus : String? = null,
    @SerializedName("app_status" ) var appStatus : String? = null,
    @SerializedName("hrc_fdate"  ) var hrcFdate  : String? = null,
    @SerializedName("hrc_tdate"  ) var hrcTdate  : String? = null,
    @SerializedName("rep_serial" ) var repSerial : Int?    = null
)
