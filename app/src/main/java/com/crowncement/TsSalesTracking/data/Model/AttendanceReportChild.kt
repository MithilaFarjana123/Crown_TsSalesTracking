package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class AttendanceReportChild(

    @SerializedName("user_id")
    var user_id: String,
    @SerializedName("att_loc")
    var att_loc: String,
    @SerializedName("att_shift")
    var att_shift: String,
    @SerializedName("att_shiftno")
    var att_shiftno: Int,
    @SerializedName("att_bid")
    var att_bid: Int,
    @SerializedName("att_stat")
    var att_stat: String,
    @SerializedName("att_rem")
    var att_rem: String,
    @SerializedName("att_in")
    var att_in: String,
    @SerializedName("att_out")
    var att_out: String,
    @SerializedName("att_dfin")
    var att_dfin: String,
    @SerializedName("att_dfout")
    var att_dfout: String,
    @SerializedName("att_date")
    var att_date: String,
    @SerializedName("att_ap_stat")
    var att_ap_stat: String

)
