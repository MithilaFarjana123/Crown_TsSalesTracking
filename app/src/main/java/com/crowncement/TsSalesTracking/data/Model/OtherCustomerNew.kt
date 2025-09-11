package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomerNew(
    @SerializedName("party_name"     ) var partyName     : String? = "",
    @SerializedName("party_occu"     ) var partyOccu     : String? = "",
    @SerializedName("party_mobile"   ) var partyMobile   : String? = "",
    @SerializedName("party_add1"     ) var partyAdd1     : String? = "",
    @SerializedName("contact_person" ) var contactPerson : String? = "",
    @SerializedName("resp_phone"     ) var respPhone     : String? = "",
    @SerializedName("relation_hown"  ) var relationHown  : String? = "",
    @SerializedName("geo_hrc1"       ) var geoHrc1       : String? = "",
    @SerializedName("geo_hrc2"       ) var geoHrc2       : String? = "",
    @SerializedName("geo_hrc3"       ) var geoHrc3       : String? = "",
    @SerializedName("geo_hrc4"       ) var geoHrc4       : String? = "",
    @SerializedName("party_city"     ) var partyCity     : String? = "",
    @SerializedName("party_zip"      ) var partyZip      : String? = "",
    @SerializedName("party_country"  ) var partyCountry  : String? = "",
    @SerializedName("party_email"    ) var partyEmail    : String? = "",
    @SerializedName("ext_str6"    ) var extStr6   : String? = ""

)
