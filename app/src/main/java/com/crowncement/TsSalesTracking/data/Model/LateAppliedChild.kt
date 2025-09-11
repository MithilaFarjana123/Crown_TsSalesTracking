package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LateAppliedChild(

    @SerializedName("lt_trno"     ) var lt_trno    : String? = null,
    @SerializedName("lt_emp"      ) var lt_emp     : String? = null,
    @SerializedName("lt_ap_dt"    ) var lt_ap_dt    : String? = null,
    @SerializedName("lt_date"     ) var lt_date    : String? = null,
    @SerializedName("lt_purpose"  ) var lt_purpose : String? = null,
    @SerializedName("lt_rem"      ) var lt_rem     : String? = null,
    @SerializedName("lt_stat"     ) var lt_stat    : String? = null,
    @SerializedName("l_act"       ) var l_act      : String? = null,
    @SerializedName("lt_ent_user" ) var lt_ent_user : String? = null,
    @SerializedName("lt_pending_to" ) var lt_pending_to : String? = null,
    @SerializedName("lt_hrcpath" ) var lt_hrcpath : String? = null

)
