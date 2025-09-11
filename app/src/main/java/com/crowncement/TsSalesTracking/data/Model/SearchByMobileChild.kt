package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SearchByMobileChild(
    @SerializedName("party_code"      ) var partyCode     : String? = "",
    @SerializedName("party_name"      ) var partyName     : String? = "",
    @SerializedName("party_add1"      ) var partyAdd1     : String? = "",
    @SerializedName("party_city"      ) var partyCity     : String? = "",
    @SerializedName("party_zip"       ) var partyZip      : String? = "",
    @SerializedName("party_country"   ) var partyCountry  : String? = "",
    @SerializedName("party_group"     ) var partyGroup    : String? = "",
    @SerializedName("party_open_date" ) var partyOpenDate : String? = "",
    @SerializedName("contact_person"  ) var contactPerson : String? = "",
    @SerializedName("entry_user"      ) var entryUser     : String? = "",
    @SerializedName("last_user"       ) var lastUser      : String? = "",
    @SerializedName("party_email"     ) var partyEmail    : String? = "",
    @SerializedName("geo_hrc1"        ) var geoHrc1       : String? = "",
    @SerializedName("geo_hrc2"        ) var geoHrc2       : String? = "",
    @SerializedName("geo_hrc3"        ) var geoHrc3       : String? = "",
    @SerializedName("geo_hrc4"        ) var geoHrc4       : String? = "",
    @SerializedName("party_mobile"    ) var partyMobile   : String? = "",
    @SerializedName("party_lat"       ) var partyLat      : Double? = 0.0,
    @SerializedName("party_lon"       ) var partyLon      : Double? = 0.0,
    @SerializedName("party_image"     ) var partyImage    : String? = ""
)
