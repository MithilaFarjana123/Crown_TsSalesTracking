package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OfferInfo(

    @SerializedName("OfferTitle")
    var OfferTitle: String? = "",
    @SerializedName("OfferDetails")
    var OfferDetails: String? = "",
    @SerializedName("CreateDate")
    var CreateDate: String? = "",
    @SerializedName("CreateBy")
    var CreateBy: String? = "",
    @SerializedName("OfferImages")
    var OfferImages: List<String>? = null


)
