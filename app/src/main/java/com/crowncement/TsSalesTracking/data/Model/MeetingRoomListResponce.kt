package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MeetingRoomListResponce(
    @SerializedName("code"    ) var code    : String?         = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MeetingRoomListResponceChild> = arrayListOf()
)
