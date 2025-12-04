package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomerChild(

///todo
     @SerializedName("party_code"        ) var partyCode       : String? = null,
     @SerializedName("party_name"        ) var partyName       : String? = null,
     @SerializedName("party_add1"        ) var partyAdd1       : String? = null,
     @SerializedName("party_city"        ) var partyCity       : String? = null,
     @SerializedName("party_zip"         ) var partyZip        : String? = null,
     @SerializedName("party_country"     ) var partyCountry    : String? = null,
     @SerializedName("party_group"       ) var partyGroup      : String? = null,
     @SerializedName("party_open_date"   ) var partyOpenDate   : String? = null,
     @SerializedName("contact_person"    ) var contactPerson   : String? = null,
     @SerializedName("entry_user"        ) var entryUser       : String? = null,
     @SerializedName("last_user"         ) var lastUser        : String? = null,
     @SerializedName("party_email"       ) var partyEmail      : String? = null,
     @SerializedName("geo_hrc1"          ) var geoHrc1         : String? = null,
     @SerializedName("geo_hrc2"          ) var geoHrc2         : String? = null,
     @SerializedName("geo_hrc3"          ) var geoHrc3         : String? = null,
     @SerializedName("geo_hrc4"          ) var geoHrc4         : String? = null,
     @SerializedName("party_mobile"      ) var partyMobile     : String? = "",
     @SerializedName("party_lat"         ) var partyLat        : Double? = 0.0,
     @SerializedName("party_lon"         ) var partyLon        : Double? = 0.0,
     @SerializedName("party_image"       ) var partyImage      : String? = null,
     @SerializedName("perm_add"          ) var permAdd         : String? = null,
     @SerializedName("party_phone"       ) var partyPhone      : String? = "",

     @SerializedName("party_bdate"       ) var partyBdate      : String? = null,
     @SerializedName("work_exp"          ) var workExp         : Double? = 0.0,
     @SerializedName("number_subord"     ) var numberSubord    : Int?    = null,
     @SerializedName("number_site"       ) var numberSite      : Int?    = null,
     @SerializedName("number_crown_site" ) var numberCrownSite : Int?    = null,
     @SerializedName("party_cat"         ) var partyCat        : String? = null,
     @SerializedName("party_desig"       ) var partyDesig      : String? = null,
     @SerializedName("party_mdate"       ) var partyMdate      : String? = null,
     @SerializedName("recom_brand1"      ) var recomBrand1     : String? = null,
     @SerializedName("recom_brand2"      ) var recomBrand2     : String? = null,
     @SerializedName("recom_brand3"      ) var recomBrand3     : String? = null,
     @SerializedName("recom_brand4"      ) var recomBrand4     : String? = null,
     @SerializedName("recom_brand5"      ) var recomBrand5     : String? = null,
     @SerializedName("part_camp"         ) var partCamp        : String? = null,
     @SerializedName("bus_card_img"      ) var busCardImg      : String? = null,



     @SerializedName("cont_id"         ) var contId        : String? = null,
     @SerializedName("cont_id_nam"     ) var contIdNam     : String? = null,
     @SerializedName("mas_nid"         ) var masNid        : String? = null,
     @SerializedName("mas_nom_nam"     ) var masNomNam     : String? = null,
     @SerializedName("mas_nom_mob"     ) var masNomMob     : String? = null,
     @SerializedName("mas_nom_rel"     ) var masNomRel     : String? = null,
     @SerializedName("nid_img"         ) var nidImg        : String? = null,
     @SerializedName("nom_nid_img"     ) var nomNidImg     : String? = null,

     //todo for house owner
     @SerializedName("party_occu"      ) var partyOccu     : String? = null,
     @SerializedName("party_res_phone" ) var partyResPhone : String? = null,
     @SerializedName("party_relat"     ) var partyRelat    : String? = null,
     @SerializedName("nid_no"          ) var nidNo         : String? = ""






//todo end
     /*
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
     @SerializedName("perm_add"        ) var permAdd       : String? = null,
     @SerializedName("party_phone"     ) var partyPhone    : String? = null,

     @SerializedName("cont_id"         ) var contId        : String? = null,
     @SerializedName("cont_id_nam"     ) var contIdNam     : String? = null,
     @SerializedName("mas_nid"         ) var masNid        : String? = null,
     @SerializedName("mas_nom_nam"     ) var masNomNam     : String? = null,
     @SerializedName("mas_nom_mob"     ) var masNomMob     : String? = null,
     @SerializedName("mas_nom_rel"     ) var masNomRel     : String? = null,
     @SerializedName("nid_img"         ) var nidImg        : String? = null,
     @SerializedName("nom_nid_img"     ) var nomNidImg     : String? = null

      */





)

data class OtherCustomerChild1(

     @SerializedName("party_name"      ) var partyName     : String? = "",
     @SerializedName("party_add1"      ) var partyAdd1     : String? = "",
     @SerializedName("party_city"      ) var partyCity     : String? = "",
     @SerializedName("party_zip"       ) var partyZip      : String? = "",
     @SerializedName("party_country"   ) var partyCountry  : String? = "",
     @SerializedName("party_group"     ) var partyGroup    : String? = "",
     @SerializedName("contact_person"  ) var contactPerson : String? = "",
     @SerializedName("party_email"     ) var partyEmail    : String? = "",
     @SerializedName("party_mobile"    ) var partyMobile   : String? = ""
)


data class OtherCustomerChild2(

     @SerializedName("party_name"    ) var partyName     : String? = "",
     @SerializedName("party_add1"    ) var partyAdd1     : String? = "",
     @SerializedName("party_country" ) var partyCountry  : String? = "",
     @SerializedName("contact_person") var contactPerson : String? = "",

     @SerializedName("party_email"   ) var partyEmail    : String? = "",
     @SerializedName("geo_hrc1"      ) var geoHrc1       : String? = "",
     @SerializedName("geo_hrc2"      ) var geoHrc2       : String? = "",
     @SerializedName("geo_hrc3"      ) var geoHrc3       : String? = "",
     @SerializedName("geo_hrc4"      ) var geoHrc4       : String? = "",

     @SerializedName("party_mobile"      ) var partyMobile   : String? = "",
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
     @SerializedName("bus_card_ext"      )  var bus_card_ext     : String? = "",



     @SerializedName("party_city"      ) var partyCity     : String? = "",
     @SerializedName("party_zip"       ) var partyZip      : String? = "",
     @SerializedName("party_group"     ) var partyGroup    : String? = "",

     @SerializedName("party_occu    " ) var partyOccu     : String? = "",
     @SerializedName("relation_hown " ) var relationHown  : String? = "",
     @SerializedName("ext_str6"    ) var extStr6   : String? = ""

)


data class OtherCustomerChild3(

     @SerializedName("party_name"    ) var partyName     : String? = "",
     @SerializedName("party_add1"    ) var partyAdd1     : String? = "",
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
     @SerializedName("nid_ext"           )     var nid_ext     : String? = "",



     @SerializedName("party_city"      ) var partyCity     : String? = "",
     @SerializedName("party_zip"       ) var partyZip      : String? = "",
     @SerializedName("party_group"     ) var partyGroup    : String? = "",


     //for mason nom nid
     @SerializedName("bus_card_ext"      )  var bus_card_ext     : String? = "",
     @SerializedName("recom_brand4"      )  var recom_brand4     : String? = "",
     @SerializedName("nom_nid_ext" ) var nomNidExt : String? = "",
     @SerializedName("ext_str6"    ) var extStr6   : String? = "",
     @SerializedName("ext_str7"    ) var extStr7   : String? = "",
     @SerializedName("ext_str8"    ) var extStr8   : String? = "",
     @SerializedName("ext_str9"    ) var extStr9   : String? = "",
     @SerializedName("ext_str10"   ) var extStr10  : String? = ""





)







