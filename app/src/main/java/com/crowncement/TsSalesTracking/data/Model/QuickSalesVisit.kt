package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class QuickSalesVisit(

    @SerializedName("party_name"           ) var partyName         : String? = null,
    @SerializedName("party_add1"           ) var partyAdd1         : String? = null,
    @SerializedName("party_contact"        ) var partyContact      : String? = null,
    @SerializedName("party_type"           ) var partyType         : String? = null,
    @SerializedName("market_point"         ) var marketPoint       : String? = null,
    @SerializedName("party_mobile"         ) var partyMobile       : String? = null,
    @SerializedName("outlet_cat"           ) var outletCat         : String? = null,
    @SerializedName("geo_hrc1"             ) var geoHrc1           : String? = null,
    @SerializedName("geo_hrc2"             ) var geoHrc2           : String? = null,
    @SerializedName("geo_hrc3"             ) var geoHrc3           : String? = null,
    @SerializedName("geo_hrc4"             ) var geoHrc4           : String? = null,
    @SerializedName("geo_hrc5"             ) var geoHrc5           : String? = null,
    @SerializedName("party_zone"           ) var partyZone         : String? = null,
    @SerializedName("party_lat"            ) var partyLat          : Double? = null,
    @SerializedName("party_lon"            ) var partyLon          : Double? = null,
    @SerializedName("party_sap_code_exist" ) var partySapCodeExist : String? = null,
    @SerializedName("party_sap_code"       ) var partySapCode      : String? = null,
    @SerializedName("party_sell_1"         ) var partySell1        : String? = null,
    @SerializedName("party_sell_2"         ) var partySell2        : String? = null,
    @SerializedName("party_sell_3"         ) var partySell3        : String? = null,
    @SerializedName("party_sell_4"         ) var partySell4        : String? = null,
    @SerializedName("party_sell_5"         ) var partySell5        : String? = null,
    @SerializedName("party_status"         ) var partyStatus       : String? = null
)
