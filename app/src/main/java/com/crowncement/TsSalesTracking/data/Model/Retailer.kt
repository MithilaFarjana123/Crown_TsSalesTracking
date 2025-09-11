package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class Retailer(

    @SerializedName("party_name"       ) var partyName       : String? = "",
    @SerializedName("party_add1"       ) var partyAdd1       : String? ="",
    @SerializedName("party_add2"       ) var partyAdd2       : String? ="",
    @SerializedName("contact_person"   ) var contactPerson   : String? ="",
    @SerializedName("party_email"      ) var partyEmail      : String? ="",
    @SerializedName("contact_desig"    ) var contactDesig    : String? ="",
    @SerializedName("geo_hrc1"         ) var geoHrc1         : String? ="",
    @SerializedName("geo_hrc2"         ) var geoHrc2         : String? ="",
    @SerializedName("geo_hrc3"         ) var geoHrc3         : String? ="",
    @SerializedName("geo_hrc4"         ) var geoHrc4         : String? ="",
    @SerializedName("geo_hrc5"         ) var geoHrc5         : String? ="",
    @SerializedName("dealer_code"      ) var dealerCode      : String? ="",
    @SerializedName("outlet_stat"      ) var outletStat      : String? ="",
    @SerializedName("market_point"     ) var marketPoint     : String? ="",
    @SerializedName("owner_name"       ) var ownerName       : String? ="",
    @SerializedName("party_mobile"     ) var partyMobile     : String? ="",
    @SerializedName("party_phone"      ) var partyPhone      : String? ="",
    @SerializedName("manager_name"     ) var managerName     : String? ="",
    @SerializedName("manager_contact1" ) var managerContact1 : String? ="",
    @SerializedName("manager_contact2" ) var managerContact2 : String? ="",
    @SerializedName("shop_front_len"   ) var shopFrontLen    : String? ="",
    @SerializedName("shop_area"        ) var shopArea        : String? ="",
    @SerializedName("mon_avg_sale"     ) var monAvgSale      : String? ="",
    @SerializedName("outlet_cat"       ) var outletCat       : String? ="",
    @SerializedName("party_sell_1"     ) var partySell1      : String? ="",
    @SerializedName("party_sell_2"     ) var partySell2      : String? ="",
    @SerializedName("party_sell_3"     ) var partySell3      : String? ="",
    @SerializedName("party_sell_4"     ) var partySell4      : String? ="",
    @SerializedName("party_sell_5"     ) var partySell5      : String? ="",
    @SerializedName("party_status"     ) var partyStatus     : String? ="",
    @SerializedName("party_sap_code"   ) var partySapCode    : String? =""

)
