package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SalesCallRecordH(

    @SerializedName("discuss_topic"        ) var discussTopic      : String? = "",
    @SerializedName("discuss_sub_topic"    ) var discussSubTopic   : String? = "",
    @SerializedName("last_del_dt"          ) var lastDelDt         : String? = "",
    @SerializedName("last_del_vol"         ) var lastDelVol        : Int?    = 0,
    @SerializedName("today_targ_qty"       ) var todayTargQty      : Int?    = 0,
    @SerializedName("vol_achv_qty"         ) var volAchvQty        : Int?    = 0,
    @SerializedName("reason_no_achv"       ) var reasonNoAchv      : String? = "",
    @SerializedName("reason_details"       ) var reasonDetails     : String? = "",
    @SerializedName("vol_achv"             ) var volAchv           : Int?    = 0,
    @SerializedName("remarks"              ) var remarks           : String? = "",
    @SerializedName("visit_type"           ) var visitType         : String? = "",
    @SerializedName("visit_by_ref"         ) var visitByRef        : String? = "",

    @SerializedName("party_sap_code_exist" ) var partySapCodeExist : String? = "",
    @SerializedName("party_sap_code"       ) var partySapCode      : String? = "",
    @SerializedName("party_sell_1"         ) var partySell1        : String? = "",
    @SerializedName("party_sell_2"         ) var partySell2        : String? = "",
    @SerializedName("party_sell_3"         ) var partySell3        : String? = "",
    @SerializedName("party_sell_4"         ) var partySell4        : String? = "",
    @SerializedName("party_sell_5"         ) var partySell5        : String? = "",
    @SerializedName("outlet_cat"           ) var outletCat         : String? = "",



    @SerializedName("party_name"           ) var partyName         : String? = "",
    @SerializedName("party_add1"           ) var partyAdd1         : String? = "",
    @SerializedName("party_contact"        ) var partyContact      : String? = "",
    @SerializedName("party_type"           ) var partyType         : String? = "",
    @SerializedName("market_point"         ) var marketPoint       : String? = "",
    @SerializedName("party_mobile"         ) var partyMobile       : String? = "",
    @SerializedName("geo_hrc1"             ) var geoHrc1           : String? = "",
    @SerializedName("geo_hrc2"             ) var geoHrc2           : String? = "",
    @SerializedName("geo_hrc3"             ) var geoHrc3           : String? = "",
    @SerializedName("geo_hrc4"             ) var geoHrc4           : String? = "",
    @SerializedName("geo_hrc5"             ) var geoHrc5           : String? = "",
    @SerializedName("party_zone"           ) var partyZone         : String? = "",
    @SerializedName("party_lat"            ) var partyLat          : Double? = 0.0,
    @SerializedName("party_lon"            ) var partyLon          : Double? = 0.0

    )
