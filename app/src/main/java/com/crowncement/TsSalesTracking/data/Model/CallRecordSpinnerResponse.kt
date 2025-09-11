package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class CallRecordSpinnerResponse(

    @SerializedName("code"    ) var code    : String?         = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<CallRecordSpinnerData> = arrayListOf()

)

