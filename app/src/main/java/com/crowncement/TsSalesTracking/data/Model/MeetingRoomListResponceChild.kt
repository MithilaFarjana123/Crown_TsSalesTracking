package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MeetingRoomListResponceChild(
    @SerializedName("room_no"     ) var roomNo     : String? = null,
    @SerializedName("room_loc"    ) var roomLoc    : String? = null,
    @SerializedName("room_faci"   ) var roomFaci   : String? = null,
    @SerializedName("app_req"     ) var appReq     : String? = null,
    @SerializedName("perm_level"  ) var permLevel  : String? = null,
    @SerializedName("room_status" ) var roomStatus : String? = null,
    @SerializedName("room_desc"   ) var roomDesc   : String? = null,
    @SerializedName("room_cap"    ) var roomCap    : String? = null
)
