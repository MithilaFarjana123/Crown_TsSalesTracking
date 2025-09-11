package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class RouteChild(
    @SerializedName("route_code"   ) var routeCode   : String? = null,
    @SerializedName("route_hrc1"   ) var routeHrc1   : String? = null,
    @SerializedName("route_hrc2"   ) var routeHrc2   : String? = null,
    @SerializedName("route_hrc3"   ) var routeHrc3   : String? = null,
    @SerializedName("route_hrc4"   ) var routeHrc4   : String? = null,
    @SerializedName("route_no"     ) var routeNo     : String? = null,
    @SerializedName("route_name"   ) var routeName   : String? = null,
    @SerializedName("route_market" ) var routeMarket : String? = null
)
