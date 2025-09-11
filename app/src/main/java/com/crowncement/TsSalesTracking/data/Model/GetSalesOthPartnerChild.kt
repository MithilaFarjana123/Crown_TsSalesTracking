package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSalesOthPartnerChild(

    @SerializedName("party_code"      ) var partyCode     : String? = null,
    @SerializedName("party_name"      ) var partyName     : String? = null,
    @SerializedName("party_add1"      ) var partyAdd1     : String? = null,
    @SerializedName("party_city"      ) var partyCity     : String? = null,
    @SerializedName("party_zip"       ) var partyZip      : String? = null,
    @SerializedName("party_country"   ) var partyCountry  : String? = null,
    @SerializedName("party_group"     ) var partyGroup    : String? = null,
    @SerializedName("party_open_date" ) var partyOpenDate : String? = null,
    @SerializedName("contact_person"  ) var contactPerson : String? = null,
    @SerializedName("entry_user"      ) var entryUser     : String? = null,
    @SerializedName("last_user"       ) var lastUser      : String? = null,
    @SerializedName("party_email"     ) var partyEmail    : String? = null,
    @SerializedName("geo_hrc1"        ) var geoHrc1       : String? = null,
    @SerializedName("geo_hrc2"        ) var geoHrc2       : String? = null,
    @SerializedName("geo_hrc3"        ) var geoHrc3       : String? = null,
    @SerializedName("geo_hrc4"        ) var geoHrc4       : String? = null,
    @SerializedName("party_mobile"    ) var partyMobile   : String? = null,
    @SerializedName("party_lat"       ) var partyLat      : Int?    = null,
    @SerializedName("party_lon"       ) var partyLon      : Int?    = null,
    @SerializedName("party_image"     ) var partyImage    : String? = null,
    @SerializedName("party_occu"      ) var partyOccu     : String? = null,
    @SerializedName("party_res_phone" ) var partyResPhone : String? = null,
    @SerializedName("party_relat"     ) var partyRelat    : String? = null,
    @SerializedName("party_pic"       ) var partyPic      : String? = null
)
