package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TourResponseChild(


    @SerializedName("tr_trno"       ) var trTrno         : String? = null,
    @SerializedName("tr_emp"        ) var trEmp          : String? = null,
    @SerializedName("tr_ap_dt"      ) var trApDt         : String? = null,
    @SerializedName("tr_fdate"      ) var trFdate        : String? = null,
    @SerializedName("tr_tdate"      ) var trTdate        : String? = null,
    @SerializedName("tr_purpose"    ) var trPurpose      : String? = null,
    @SerializedName("tr_rem"        ) var trRem          : String? = null,
    @SerializedName("tr_stat"       ) var trStat         : String? = null,
    @SerializedName("tr_note"       ) var trNote         : String? = null,
    @SerializedName("tr_day"        ) var trDay          : Int?    = null,
    @SerializedName("tr_attype"     ) var trAttype       : String? = null,
    @SerializedName("tr_ent_user"   ) var trEntUser      : String? = null,
    @SerializedName("tr_pending_to" ) var trPendingTo    : String? = null,
    @SerializedName("tr_hrcpath"    ) var trHrcpath      : String? = null,
    @SerializedName("tour_start_st" ) var tour_start_st  : String? = null,
    @SerializedName("tour_exec_st"  ) var tour_exec_st   : String? = null


)
