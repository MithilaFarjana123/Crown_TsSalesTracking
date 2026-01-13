package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetTSSiteInfoChild(

    @SerializedName("party_code"        ) var partyCode        : String? = "",
    @SerializedName("party_name"        ) var partyName        : String? = "",
    @SerializedName("party_mobile"      ) var partyMobile      : String? = "",
    @SerializedName("party_occu"        ) var partyOccu        : String? = "",
    @SerializedName("party_add1"        ) var partyAdd1        : String? = "",
    @SerializedName("party_open_date"   ) var partyOpenDate    : String? = "",
    @SerializedName("party_image"       ) var partyImage       : String? = "",
    @SerializedName("party_type"        ) var partyType        : String? = "",
    @SerializedName("geo_hrc1"          ) var geoHrc1          : String? = "",
    @SerializedName("geo_hrc2"          ) var geoHrc2          : String? = "",
    @SerializedName("geo_hrc3"          ) var geoHrc3          : String? = "",
    @SerializedName("geo_hrc4"          ) var geoHrc4          : String? = "",
    @SerializedName("contact_person"    ) var contactPerson    : String? = "",
    @SerializedName("contact_phone"     ) var contactPhone     : String? = "",
    @SerializedName("relation_hown"     ) var relationHown     : String? = "",
    @SerializedName("site_number"       ) var siteNumber       : String? = "",
    @SerializedName("site_add"          ) var siteAdd          : String? = "",
    @SerializedName("site_type"         ) var siteType         : String? = "",
    @SerializedName("site_area"         ) var siteArea         : Int?    = 0,
    @SerializedName("no_of_story"       ) var noOfStory        : Int?    = 0,
    @SerializedName("site_avg_cons"     ) var siteAvgCons      : Int?    = 0,
    @SerializedName("brand"             ) var brand            : String? = "",
    @SerializedName("brand_price"       ) var brandPrice       : Int?    = 0,
    @SerializedName("status"            ) var status           : String? = "",
    @SerializedName("cement_type"       ) var cementType       : String? = "",
    @SerializedName("conversion_status" ) var conversionStatus : String? = "",
    @SerializedName("party_lat"         ) var partyLat         : Double? = 0.0,
    @SerializedName("party_lon"         ) var partyLon         : Double? = 0.0,
    @SerializedName("site_image"        ) var siteImage        : String? = "",
    @SerializedName("site_ref_by"       ) var siteRefBy        : String? = "",
    @SerializedName("site_ref_id"       ) var siteRefId        : String? = "",


    ///this was added
    @SerializedName("site_visit_status" ) var siteVisitStatus : String? = "",
    @SerializedName("first_del_date"    ) var firstDelDate     : String? = ""




)
