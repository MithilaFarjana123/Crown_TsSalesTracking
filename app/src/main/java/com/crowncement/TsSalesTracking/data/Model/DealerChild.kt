package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class DealerChild(

    @SerializedName("dealer_code"      ) var dealerCode     : String? = null,
    @SerializedName("dealer_name"      ) var dealerName     : String? = null,
    @SerializedName("dealer_add1"      ) var dealerAdd1     : String? = null,
    @SerializedName("dealer_add2"      ) var dealerAdd2     : String? = null,
    @SerializedName("dealer_city"      ) var dealerCity     : String? = null,
    @SerializedName("dealer_state"     ) var dealerState    : String? = null,
    @SerializedName("dealer_zip"       ) var dealerZip      : String? = null,
    @SerializedName("dealer_country"   ) var dealerCountry  : String? = null,
    @SerializedName("dealer_group"     ) var dealerGroup    : String? = null,
    @SerializedName("dealer_open_date" ) var dealerOpenDate : String? = null,
    @SerializedName("contact_person"   ) var contactPerson  : String? = null,
    @SerializedName("entry_user"       ) var entryUser      : String? = null,
    @SerializedName("last_user"        ) var lastUser       : String? = null,
    @SerializedName("dealer_email"     ) var dealerEmail    : String? = null,
    @SerializedName("contact_desig"    ) var contactDesig   : String? = null,
    @SerializedName("geo_hrc1"         ) var geoHrc1        : String? = null,
    @SerializedName("geo_hrc2"         ) var geoHrc2        : String? = null,
    @SerializedName("geo_hrc3"         ) var geoHrc3        : String? = null,
    @SerializedName("dealer_mobile"    ) var dealerMobile   : String? = null,
    @SerializedName("dealer_phone"     ) var dealerPhone    : String? = null,
    @SerializedName("dealer_lat"       ) var dealerLat      : Double? = null,
    @SerializedName("dealer_lon"       ) var dealerLon      : Double? = null,
    @SerializedName("dealer_image"     ) var dealerImage    : String? = null

)


data class DealerChild1(
    @SerializedName("dealer_name"      ) var dealerName     : String? = null,
    @SerializedName("dealer_add1"      ) var dealerAdd1     : String? = null,
    @SerializedName("dealer_add2"      ) var dealerAdd2     : String? = null,
    @SerializedName("dealer_city"      ) var dealerCity     : String? = null,
    @SerializedName("dealer_state"     ) var dealerState    : String? = null,
    @SerializedName("dealer_zip"       ) var dealerZip      : String? = null,
    @SerializedName("dealer_country"   ) var dealerCountry  : String? = null,
    @SerializedName("dealer_group"     ) var dealerGroup    : String? = null,
    @SerializedName("contact_person"   ) var contactPerson  : String? = null,
    @SerializedName("dealer_email"     ) var dealerEmail    : String? = null,
    @SerializedName("contact_desig"    ) var contactDesig   : String? = null,
    @SerializedName("dealer_mobile"    ) var dealerMobile   : String? = null,
    @SerializedName("dealer_phone"     ) var dealerPhone    : String? = null


)
