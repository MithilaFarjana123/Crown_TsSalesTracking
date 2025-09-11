package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSiteSumInfoChild(
    //@SerializedName("site_id"        ) var siteId       : String? = "",
//    @SerializedName("first_del_date" ) var firstDelDate : String? = "",
//    @SerializedName("no_of_del"      ) var noOfDel      : Int?    = 0,
//    @SerializedName("visit_call_no"  ) var visitCallNo  : Int?    = 0,
//    @SerializedName("ref_call_no"    ) var refCallNo    : Int?    = 0,
//    @SerializedName("comp_call_no"   ) var compCallNo   : Int?    = 0



    @SerializedName("site_id"         ) var siteId       : String? = "",
    @SerializedName("first_del_date"  ) var firstDelDate : String? = "",
    @SerializedName("no_of_first_del" ) var noOfFirstDel : Int?    = 0,
    @SerializedName("no_of_del"       ) var noOfDel      : Int?    = 0,
    @SerializedName("visit_call_no"   ) var visitCallNo  : Int?    = 0,
    @SerializedName("ref_call_no"     ) var refCallNo    : Int?    = 0,
    @SerializedName("comp_call_no"    ) var compCallNo   : Int?    = 0,
    @SerializedName("last_del_date"   ) var lastDelDate  : String? = "",
    @SerializedName("no_of_last_del"  ) var noOfLastDel  : Int?    = 0
)
