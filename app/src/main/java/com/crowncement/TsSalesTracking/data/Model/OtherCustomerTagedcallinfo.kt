package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomerTagedcallinfo(
    @SerializedName("visit_type"        ) var visitType       : String? = "",
    @SerializedName("visit_with"        ) var visitWith       : String? = "",
    @SerializedName("discuss_topic"     ) var discussTopic    : String? = "",
    @SerializedName("discuss_sub_topic" ) var discussSubTopic : String? = "",
    @SerializedName("site_ref_by"       ) var siteRefBy       : String? = "",
    @SerializedName("cem_type"          ) var cemType         : String? = "",
    @SerializedName("del_qty"           ) var delQty          : Int?    = 0,
    @SerializedName("resp_officer"      ) var respOfficer     : String? = "",
    @SerializedName("reason_no_achv"    ) var reasonNoAchv    : String? = "",

    @SerializedName("reason_details"    ) var reasonDetails   : String? = "",
    @SerializedName("conv_status"       ) var convStatus      : String? = "",
    @SerializedName("next_visit_date"   ) var nextVisitDate   : String? = "",

    @SerializedName("comp_rec_date"     ) var compRecDate     : String? = "",
    @SerializedName("site_status"       ) var siteStatus      : String? = "",
    @SerializedName("comp_info_by"      ) var compInfoBy      : String? = "",
    @SerializedName("comp_topic"        ) var compTopic       : String? = "",
    @SerializedName("comp_sub_topic"    ) var compSubTopic    : String? = "",
    @SerializedName("mode_action"       ) var modeAction      : String? = "",


    @SerializedName("comp_status"       ) var compStatus      : String? = "",
    @SerializedName("remarks"           ) var remarks         : String? = "",

    @SerializedName("comp_img_ext"      ) var compImgExt      : String? = "",
    @SerializedName("move_status"       ) var moveStatus      : String? = "",
    @SerializedName("dealer_id"         ) var dealerId        : String? = "",
    @SerializedName("retailer_id"       ) var retailerId      : String? = "",
    @SerializedName("site_current_status" ) var siteCurrentStatus : String? = ""

    )
