package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ActualPlnResponse(

    @SerializedName("code"       ) var code      : String?         = null,
    @SerializedName("message"    ) var message   : String?         = null,
    @SerializedName("trn_no"     ) var trnNo     : String?         = null,
    @SerializedName("ent_date"   ) var entDate   : String?         = null,
    @SerializedName("pln_yr"     ) var plnYr     : Int?            = null,
    @SerializedName("pln_mn"     ) var plnMn     : Int?            = null,
    @SerializedName("emp_no"     ) var empNo     : String?         = null,
    @SerializedName("pln_status" ) var plnStatus : String?         = null,
    @SerializedName("data"       ) var data      : ArrayList<ActualPlnResponseChild> = arrayListOf()




)
