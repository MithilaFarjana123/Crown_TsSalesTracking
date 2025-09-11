package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MealAllocationResponse(

    @SerializedName("code"     ) var code    : String? = null,
    @SerializedName("day_week" ) var dayWeek : String? = null,
    @SerializedName("menu"     ) var menu    : String? = null,
    @SerializedName("status"   ) var status  : String? = null,
    @SerializedName("Date"     ) var date    : String? = null
)
