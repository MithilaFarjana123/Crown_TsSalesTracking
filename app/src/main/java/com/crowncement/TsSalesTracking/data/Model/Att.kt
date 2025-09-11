package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Att(

    @SerializedName("user_code") var user_code: String? = "",
    @SerializedName("att_date") var att_date: String? = "",
    @SerializedName("shift") var shift: String? = "",
    @SerializedName("shift_no") var shift_no: Int? = 0,
    @SerializedName("intime") var intime: String? = "",
    @SerializedName("outtime") var outtime: String? = "",
    @SerializedName("att_status") var att_status: String? = "",
    @SerializedName("bid") var bid: Int? = 0,
    @SerializedName("location") var location: String? = "",
    @SerializedName("att_rem") var att_rem: String? = "",
    @SerializedName("att_ap_stat") var att_ap_stat: String? = "",
    @SerializedName("att_img_in") var att_img_in: String? = "",
    @SerializedName("att_img_out") var att_img_out: String? = "",
    @SerializedName("code") var code: String? = "",
    @SerializedName("message") var message: String? = "",
    @SerializedName("tour_no") var tour_no: String? = "",
    @SerializedName("tour_start_st") var tour_start_st: String? = "",
    @SerializedName("tour_exec_st") var tour_exec_st: String? = "",

    //REPORT

    @SerializedName("user_id"     ) var user_id     : String? = "",
    @SerializedName("att_loc"     ) var att_loc     : String? = "",
    @SerializedName("att_shift"   ) var att_shift   : String? = "",
    @SerializedName("att_shiftno" ) var att_shiftno : Int?    = 0,
    @SerializedName("att_bid"     ) var att_bid     : Int?    = 0,
    @SerializedName("att_stat"    ) var att_stat    : String? = "",
    @SerializedName("att_dfin"    ) var att_dfin    : String? = "",
    @SerializedName("att_dfout"   ) var att_dfout   : String? = "",
    @SerializedName("att_in"   ) var att_in   : String? = "",
    @SerializedName("att_out"   ) var att_out   : String? = ""



):Serializable


