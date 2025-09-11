package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeaveApplication(

    @SerializedName("lv_trno"     ) var lvTrno    : String? = "",
    @SerializedName("lv_emp"      ) var lvEmp     : String? = "",
    @SerializedName("lv_type"     ) var lvType    : String? = "",
    @SerializedName("lv_ap_dt"    ) var lvApDt    : String? = "",
    @SerializedName("lv_fdate"    ) var lvFdate   : String? = "",
    @SerializedName("lv_tdate"    ) var lvTdate   : String? = "",
    @SerializedName("lv_days"     ) var lvDays    : Int?    = 0,
    @SerializedName("lv_purpose"  ) var lvPurpose : String? = "",
    @SerializedName("lv_add"      ) var lvAdd     : String? = "",
    @SerializedName("lv_stat"     ) var lvStat    : String? = "",
    @SerializedName("lv_act"      ) var lvAct     : String? = "",
    @SerializedName("lv_phone"    ) var lvPhone   : String? = "",
    @SerializedName("lv_ent_user" ) var lvEntUser : String? = "",
    @SerializedName("lv_canc_ref" ) var lvCancRef : String? = "",
    @SerializedName("lv_pending_to" ) var lv_pending_to : String? = "",
    @SerializedName("lv_hrcpath" ) var lv_hrcpath : String? = ""

):Serializable
