package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SiteVerificationData(
    @SerializedName("site_id"              ) var siteId             : String? = "",
    @SerializedName("status"               ) var status             : String? = "",
    @SerializedName("email"                ) var email              : String? = "",
    @SerializedName("retailer_id"          ) var retailerId         : String? = "",
    @SerializedName("actual_delivery_date" ) var actualDeliveryDate : String? = "",
    @SerializedName("actual_delivery_qty"  ) var actualDeliveryQty  : Int? = 0,
    @SerializedName("remarks"              ) var remarks            : String? = ""

)
