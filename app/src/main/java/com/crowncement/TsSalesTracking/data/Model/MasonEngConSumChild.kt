package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MasonEngConSumChild(
    @SerializedName("cont_id"         ) var contId       : String? = null,
    @SerializedName("no_of_site_conv" ) var noOfSiteConv : Int?    = null,
    @SerializedName("no_of_bags_sell" ) var noOfBagsSell : Int?    = null
)
