package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ConWorkSumChild(
    @SerializedName("cont_id"         ) var contId       : String? = "",
    @SerializedName("no_of_site_conv" ) var noOfSiteConv : Int?    = 0,
    @SerializedName("no_of_bags_sell" ) var noOfBagsSell : Int?    = 0
)
