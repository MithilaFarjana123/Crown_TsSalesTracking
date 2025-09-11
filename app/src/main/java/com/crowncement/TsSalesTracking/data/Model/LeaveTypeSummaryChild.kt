package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LeaveTypeSummaryChild(

    @SerializedName("lv_emp"   ) var lvEmp   : String? = "",
    @SerializedName("lv_code"  ) var lvCode  : String? = "",
    @SerializedName("emp_cat"  ) var empCat  : String? = "",
    @SerializedName("lv_alc"   ) var lvAlc   : Int?    = 0,
    @SerializedName("lv_tkn"   ) var lvTkn   : Int?    = 0,
    @SerializedName("lv_due"   ) var lvDue   : Int?    = 0,
    @SerializedName("lv_cfd"   ) var lvCfd   : Int?    = 0,
    @SerializedName("lv_fdate" ) var lvFdate : String? = "",
    @SerializedName("lv_tdate" ) var lvTdate : String? = "",
    @SerializedName("emp_join" ) var empJoin : String? = "",
    @SerializedName("lv_yesno" ) var lvYesno : String? = null,
    @SerializedName("lv_minlv" ) var lvMinlv : Int?    = null
)
