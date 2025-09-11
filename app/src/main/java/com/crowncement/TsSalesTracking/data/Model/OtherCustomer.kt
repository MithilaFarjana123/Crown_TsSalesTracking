package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomer(

    @SerializedName("party_name"    ) var partyName     : String? = "",
    @SerializedName("party_add1"    ) var partyAdd1     : String? = "",
    @SerializedName("party_city"    ) var partyCity     : String? = "",
    @SerializedName("party_zip"     ) var partyZip      : String? = "",
    @SerializedName("party_country" ) var partyCountry  : String? = "",
    @SerializedName("contact_person") var contactPerson : String? = "",
    @SerializedName("party_email"   ) var partyEmail    : String? = "",
    @SerializedName("geo_hrc1"      ) var geoHrc1       : String? = "",
    @SerializedName("geo_hrc2"      ) var geoHrc2       : String? = "",
    @SerializedName("geo_hrc3"      ) var geoHrc3       : String? = "",
    @SerializedName("geo_hrc4"      ) var geoHrc4       : String? = "",
    @SerializedName("party_mobile"  ) var partyMobile   : String? = "",



    @SerializedName("party_desig"       )  var party_desig      : String? = "",
    @SerializedName("party_phone"       )  var party_phone      : String? = "",
    @SerializedName("number_site"       )  var number_site      : Int? = 0,
    @SerializedName("number_crown_site" )  var number_crown_site: Int? = 0,
    @SerializedName("party_cat"         )  var party_cat        : String? = "",
    @SerializedName("party_bdate"       )  var party_bdate      : String? = "",
    @SerializedName("party_mdate"       )  var party_mdate      : String? = "",
    @SerializedName("recom_brand1"      )  var recom_brand1     : String? = "",
    @SerializedName("recom_brand2"      )  var recom_brand2     : String? = "",
    @SerializedName("recom_brand3"      )  var recom_brand3     : String? = "",
    @SerializedName("recom_brand4"      )  var recom_brand4     : String? = "",
    @SerializedName("recom_brand5"      )  var recom_brand5     : String? = "",
    @SerializedName("bus_card_ext"      )  var bus_card_ext     : String? = "",

    //CONTRACTOR DATABASE

    @SerializedName("party_padd"     )  var party_padd       : String? = "",
    @SerializedName("tot_expyr"      )  var tot_expyr        : Double = 0.0,
    @SerializedName("number_subord"  )  var number_subord    : Int = 0,
    @SerializedName("joined_program" )  var joined_program   : String? = "",
    @SerializedName("nid_ext"        )  var nid_ext          : String? = "",
    @SerializedName("party_group"    )  var party_group      : String? = "",

    //for contractor nominee
    @SerializedName("nom_nid_ext" ) var nomNidExt : String? = "",
    @SerializedName("ext_str6"    ) var extStr6   : String? = "",
    @SerializedName("ext_str7"    ) var extStr7   : String? = "",
    @SerializedName("ext_str8"    ) var extStr8   : String? = "",
    @SerializedName("ext_str9"    ) var extStr9   : String? = "",
    @SerializedName("ext_str10"   ) var extStr10  : String? = ""

)
