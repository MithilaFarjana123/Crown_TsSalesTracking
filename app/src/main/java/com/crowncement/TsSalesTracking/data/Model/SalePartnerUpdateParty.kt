package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SalePartnerUpdateParty(
    @SerializedName("party_name"      ) var partyName     : String? = "",
    @SerializedName("party_add1"      ) var partyAdd1     : String? = "",
    @SerializedName("party_city"     ) var partyCity   : String? = "",
    @SerializedName("party_zip"       ) var partyZip      : String? = "",
    @SerializedName("party_country"   ) var partyCountry  : String? = "",
    @SerializedName("party_group"     ) var partyGroup    : String? = "",
    @SerializedName("contact_person"  ) var contactPerson : String? = "",
    @SerializedName("party_email"     ) var partyEmail    : String? = "",
    @SerializedName("party_mobile"    ) var partyMobile   : String? = "",
    @SerializedName("party_desig"     )  var partyDesig      : String? = "",
    @SerializedName("party_phone"     )  var partyPhone      : String? = "",
    @SerializedName("number_site"      )  var numberSite      : Int? = 0,
    @SerializedName("number_crown_site" )  var numberCrownSite: Int? = 0,
    @SerializedName("party_cat"         )  var partyCat        : String? = "",
    @SerializedName("party_bdate"       )  var partyBdate      : String? = "",
    @SerializedName("party_mdate"       )  var partyMdate      : String? = "",
    @SerializedName("recom_brand1"      )  var recomBrand1     : String? = "",
    @SerializedName("recom_brand2"      )  var recomBrand2     : String? = "",
    @SerializedName("recom_brand3"      )  var recomBrand3     : String? = "",
    @SerializedName("recom_brand4"      )  var recomBrand4     : String? = "",
    @SerializedName("recom_brand5"      )  var recomBrand5     : String? = "",
    @SerializedName("bus_card_ext"      )  var busCardExt     : String? = "",
    @SerializedName("party_padd"        )  var partyPadd        : String? = "",
    @SerializedName("tot_expyr"        )  var totExpyr        : Double? = 0.0,


    @SerializedName("number_subord"         )  var numberSubord         : Int? = 0,
    @SerializedName("joined_program"        )  var joinedProgram        : String? = "",
    @SerializedName("nid_ext"           )     var nidExt     : String? = "",

    @SerializedName("party_occu"      ) var partyOccu     : String? = "",//ihb occupation
    @SerializedName("relation_hown" ) var relationHown : String? = "", //rltnwithhouseowner
    @SerializedName("resp_phone"     ) var respPhone    : String? = "",

    @SerializedName("nom_nid_ext" ) var nomNidExt : String? = "",
    @SerializedName("ext_str6"    ) var extStr6   : String? = "",
    @SerializedName("ext_str7"    ) var extStr7   : String? = "",
    @SerializedName("ext_str8"    ) var extStr8   : String? = "",
    @SerializedName("ext_str9"    ) var extStr9   : String? = "",
    @SerializedName("ext_str10"   ) var extStr10  : String? = ""

    )
