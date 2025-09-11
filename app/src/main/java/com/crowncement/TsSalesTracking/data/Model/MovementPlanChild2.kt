package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MovementPlanChild2(

    @SerializedName("pln_row"    ) var plnRow    : Int?    = 0,
    @SerializedName("pln_emp"    ) var plnEmp    : String? = "",
    @SerializedName("pln_date"   ) var plnDate   : String? = "",
    @SerializedName("pln_zone"   ) var plnZone   : String? = "",
    @SerializedName("rt_hrc1"    ) var rtHrc1    : String? = "",
    @SerializedName("rt_hrc2"    ) var rtHrc2    : String? = "",
    @SerializedName("rt_hrc3"    ) var rtHrc3    : String? = "",
    @SerializedName("rt_hrc4"    ) var rtHrc4    : String? = "",
    @SerializedName("rt_hrc5"    ) var rtHrc5    : String? = "",
    @SerializedName("route_name" ) var routeName : String? = "",
    @SerializedName("rt_long"    ) var rtLong    : String? = "",
    @SerializedName("rt_code"    ) var rtCode    : String? = ""
)
