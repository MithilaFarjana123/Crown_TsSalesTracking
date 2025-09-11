package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class Late(

    @SerializedName("lt_emp"     ) var lt_emp     : String? = null,
    @SerializedName("lt_date"    ) var lt_date    : String? = null,
    @SerializedName("lt_time"    ) var lt_time    : String? = null,
    @SerializedName("att_shift"  ) var att_shift  : String? = null,
    @SerializedName("att_loc"    ) var att_loc    : String? = null,
    @SerializedName("att_bid"    ) var att_bid    : Int?    = null,
    @SerializedName("lt_rem"     ) var lt_rem     : String? = null,
    @SerializedName("lt_stat"    ) var lt_stat    : String? = null,
    @SerializedName("lt_ent_stat"    ) var lt_ent_stat    : String? = null,
    @SerializedName("lt_hrcpath" ) var lt_hrcpath : String? = null

)
