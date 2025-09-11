package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MovementPlanChild1(

    @SerializedName("trn_no"        ) var trnNo        : String?                 = "",
    @SerializedName("pln_date"      ) var plnDate      : String?                 = "",
    @SerializedName("route_details" ) var routeDetails : ArrayList<MovementPlanChild2> = arrayListOf()
)
