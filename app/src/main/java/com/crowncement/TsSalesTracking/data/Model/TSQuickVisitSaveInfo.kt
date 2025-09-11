package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TSQuickVisitSaveInfo(


    @SerializedName("party_number"  ) var partyNumber  : String? = null,
    @SerializedName("site_number"   ) var siteNumber   : String? = null,
    @SerializedName("party_contact" ) var partyContact : String? = null,
    @SerializedName("party_type"    ) var partyType    : String? = null,
    @SerializedName("market_point"  ) var marketPoint  : String? = null,
    @SerializedName("party_mobile"  ) var partyMobile  : String? = null,
    @SerializedName("party_name"    ) var partyName    : String? = null,
    @SerializedName("party_add"     ) var partyAdd     : String? = null,
    @SerializedName("geo_hrc1"      ) var geoHrc1      : String? = null,
    @SerializedName("geo_hrc2"      ) var geoHrc2      : String? = null,
    @SerializedName("geo_hrc3"      ) var geoHrc3      : String? = null,
    @SerializedName("geo_hrc4"      ) var geoHrc4      : String? = null,
    @SerializedName("party_lat"     ) var partyLat     : Double? = 0.0,
    @SerializedName("party_lon"     ) var partyLon     : Double? = 0.0


)
