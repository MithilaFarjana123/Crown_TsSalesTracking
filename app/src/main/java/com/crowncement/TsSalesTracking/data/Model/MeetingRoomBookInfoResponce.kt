package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MeetingRoomBookInfoResponce(

    @SerializedName("code"           ) var code          : String? = null,
    @SerializedName("message"        ) var message       : String? = null,
    @SerializedName("booking_number" ) var bookingNumber : String? = null
)
