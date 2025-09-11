package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LateResponse(
    @SerializedName("code") var code: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Late> = arrayListOf()

) : Serializable
