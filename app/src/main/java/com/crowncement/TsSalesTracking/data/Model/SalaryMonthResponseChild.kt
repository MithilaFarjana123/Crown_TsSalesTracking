package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SalaryMonthResponseChild(


    @SerializedName("user_id"  ) var userId  : String? = null,
    @SerializedName("sal_no"   ) var salNo   : Int?    = null,
    @SerializedName("sal_mon"  ) var salMon  : String? = null,
    @SerializedName("pay_typ"  ) var payTyp  : String? = null,
    @SerializedName("sal_stat" ) var salStat : String? = null,
    @SerializedName("emp_sbu"  ) var empSbu  : String? = null
)
