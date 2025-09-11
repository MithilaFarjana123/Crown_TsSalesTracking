package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MovementPlanResponse(

    @SerializedName("code"       ) var code      : String?         = "",
    @SerializedName("message"    ) var message   : String?         = "",
    @SerializedName("trn_no"     ) var trnNo     : String?         = "",
    @SerializedName("ent_date"   ) var entDate   : String?         = "",
    @SerializedName("pln_yr"     ) var plnYr     : Int?            = 0,
    @SerializedName("pln_mn"     ) var plnMn     : Int?            = 0,
    @SerializedName("emp_no"     ) var empNo     : String?         = "",
    @SerializedName("pln_status" ) var plnStatus : String?         = "",
    @SerializedName("data"       ) var data      : ArrayList<MovementPlanChild1> = arrayListOf()

)
