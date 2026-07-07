package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSiteVrfInfoChild(
    @SerializedName("entry_time"           ) var entryTime          : String? = "",
    @SerializedName("update_time"          ) var updateTime         : String? = "",
    @SerializedName("site_id"              ) var siteId             : String? = "",
    @SerializedName("status"               ) var status             : String? = "",
    @SerializedName("entry_user"           ) var entryUser          : String? = "",
    @SerializedName("update_user"          ) var updateUser         : String? = "",
    @SerializedName("party_id"             ) var partyId            : String? = "",
    @SerializedName("actual_delivery_date" ) var actualDeliveryDate : String? = "",
    @SerializedName("actual_delivery_qty"  ) var actualDeliveryQty  : Int?    = 0,
    @SerializedName("remarks"              ) var remarks            : String? = ""
)
