package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSiteDetailsResponceChild(
    @SerializedName("zid"               ) var zid             : Int?    = 0,
    @SerializedName("xparty"            ) var xparty          : String? = "",
    @SerializedName("xorg"              ) var xorg            : String? = "",
    @SerializedName("xadd1"             ) var xadd1           : String? = "",
    @SerializedName("xgcus"             ) var xgcus           : String? = "",
    @SerializedName("xmobile"           ) var xmobile         : String? = "",
    @SerializedName("xhrc1"             ) var xhrc1           : String? = "",
    @SerializedName("xhrc2"             ) var xhrc2           : String? = "",
    @SerializedName("xhrc3"             ) var xhrc3           : String? = "",
    @SerializedName("xhrc4"             ) var xhrc4           : String? = "",
    @SerializedName("xcontact"          ) var xcontact        : String? = "",
    @SerializedName("xopdate"           ) var xopdate         : String? = "",
    @SerializedName("xoccu"             ) var xoccu           : String? = "",
    @SerializedName("xphone"            ) var xphone          : String? = "",
    @SerializedName("xrelation"         ) var xrelation       : String? = "",
    @SerializedName("xlink"             ) var xlink           : String? = "",
    @SerializedName("xproj"             ) var xproj           : String? = "",
    @SerializedName("siteAdd"           ) var siteAdd         : String? = "",
    @SerializedName("xtypesite"         ) var xtypesite       : String? = "",
    @SerializedName("siteArea"          ) var siteArea        : Int?    = 0,
    @SerializedName("noOfStory"         ) var noOfStory       : Int?    = 0,
    @SerializedName("avgCons"           ) var avgCons         : Int?    = 0,
    @SerializedName("xbrand"            ) var xbrand          : String? = "",
    @SerializedName("cemPrice"          ) var cemPrice        : Int?    = 0,
    @SerializedName("xstatus"           ) var xstatus         : String? = "",
    @SerializedName("cemType"           ) var cemType         : String? = "",
    @SerializedName("convStatus"        ) var convStatus      : String? = "",
    @SerializedName("xlat"              ) var xlat            : Double? = 0.0,
    @SerializedName("xlon"              ) var xlon            : Double? = 0.0,
    @SerializedName("siteImgPath"       ) var siteImgPath     : String? = "",
    @SerializedName("site_curr_status"  ) var siteCurrStatus  : String? = "",




    @SerializedName("site_ref_by"       ) var siteRefBy       : String? = "",
    @SerializedName("site_ref_id"       ) var siteRefId       : String? = "",
    @SerializedName("site_visit_status" ) var siteVisitStatus : String? = "",
    @SerializedName("ihb_name"          ) var ihbName         : String? = "",
    @SerializedName("ihb_add"           ) var ihbAdd          : String? = "",
    @SerializedName("ihb_mobile"        ) var ihbMobile       : String? = ""

)
