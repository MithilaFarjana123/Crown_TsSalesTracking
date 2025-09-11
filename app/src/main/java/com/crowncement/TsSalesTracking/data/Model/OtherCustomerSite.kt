package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class OtherCustomerSite(
    @SerializedName("proj_add"      ) var projAdd     : String? = "",
    @SerializedName("site_type"     ) var siteType    : String? = "",
    @SerializedName("proj_area"     ) var projArea    : Double? = 0.0,
    @SerializedName("proj_no_story" ) var projNoStory : Int? = 0,
    @SerializedName("proj_avg_cons" ) var projAvgCons : Int? = 0,//new
    @SerializedName("prop_brand"    ) var propBrand   : String? = "",
    @SerializedName("brand_price"   ) var brandPrice  : Double? = 0.0,
    @SerializedName("proj_status"   ) var projStatus  : String? = "",
    @SerializedName("cem_type"      ) var cemType     : String? = "",
    @SerializedName("conv_stat"     ) var convStat    : String? = "",
    @SerializedName("proj_lat"      ) var projLat     : Double? = 0.0,
    @SerializedName("proj_lon"      ) var projLon     : Double? = 0.0,
    @SerializedName("geo_hrc1"      ) var geoHrc1     : String? = "",
    @SerializedName("geo_hrc2"      ) var geoHrc2     : String? = "",
    @SerializedName("geo_hrc3"      ) var geoHrc3     : String? = "",
    @SerializedName("geo_hrc4"      ) var geoHrc4     : String? = "",
    @SerializedName("site_ref_people" ) var siteRefPeople : String? = "",
    @SerializedName("site_ref_id"     ) var siteRefId     : String? = ""
  //  @SerializedName("client_type"    ) var clientType    : String? = ""


)
