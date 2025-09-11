package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ActualPlnResponseChild(

    @SerializedName("trn_no"        ) var trnNo        : String?                 = null,
    @SerializedName("pln_date"      ) var plnDate      : String?                 = null,
    @SerializedName("route_details" ) var routeDetails : ArrayList<MovementPlanChild2> = arrayListOf()
)
