package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class BankImage(

    @SerializedName("BankName"    ) var BankName    : String? = null,
    @SerializedName("BankImage" ) var BankImage : String? = null,
)
