package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class DOInfo(

    @SerializedName("do_number")             var do_number              : String? = "",
    @SerializedName("item_name")             var item_name              : String? = "",
    @SerializedName("stock_quantity")        var stock_quantity         : Int = 0,
    @SerializedName("select_quantity")       var select_quantity        : Int = 0,
    @SerializedName("delivery_to_address")   var delivery_to_address    : String? = "",
    @SerializedName("delivery_from_address") var delivery_from_address  : String? = "",
    @SerializedName("from_lat")              var from_lat               : Double? = 0.0,
    @SerializedName("from_lon")              var from_lon               : Double? = 0.0,
    @SerializedName("to_lat")                var to_lat                 : Double? = 0.0,
    @SerializedName("to_lon")                var to_lon                 : Double? = 0.0

    )
