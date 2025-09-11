package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MealCost(


    @SerializedName("user_id" ) var userId : String? = null,
    @SerializedName("ln_date" ) var lnDate : String? = null,
    @SerializedName("ln_stat" ) var lnStat : String? = null,
    @SerializedName("ln_qty"  ) var lnQty  : String?    = null,
    @SerializedName("ln_rate" ) var lnRate : String? = null,
    @SerializedName("ln_ovh"  ) var lnOvh  : String?    = null


)
