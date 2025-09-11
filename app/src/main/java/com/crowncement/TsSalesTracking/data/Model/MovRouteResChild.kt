package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MovRouteResChild(

    @SerializedName("route_code"   ) var routeCode   : String? = "",
    @SerializedName("route_hrc1"   ) var routeHrc1   : String? = "",
    @SerializedName("route_hrc2"   ) var routeHrc2   : String? = "",
    @SerializedName("route_hrc3"   ) var routeHrc3   : String? = "",
    @SerializedName("route_hrc4"   ) var routeHrc4   : String? = "",
    @SerializedName("route_no"     ) var routeNo     : String? = "",
    @SerializedName("route_name"   ) var routeName   : String? = "",
    @SerializedName("route_market" ) var routeMarket : String? = ""

)
