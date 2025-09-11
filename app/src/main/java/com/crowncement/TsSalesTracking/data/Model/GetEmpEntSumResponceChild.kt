package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetEmpEntSumResponceChild(
//    @SerializedName("emp_id"           ) var empId          : String? = "",
//    @SerializedName("no_of_contractor" ) var noOfContractor : Int?    = 0,
//    @SerializedName("no_of_mason"      ) var noOfMason      : Int?    = 0,
//    @SerializedName("no_of_engineer"   ) var noOfEngineer   : Int?    = 0,
//    @SerializedName("no_of_site"       ) var noOfSite       : Int?    = 0,
//    @SerializedName("no_of_site_conv" ) var noOfSiteConv    : Int?    = 0,
//    @SerializedName("no_of_bags_sell" ) var noOfBagsSell    : Int?    = 0




    @SerializedName("emp_id"             ) var empId           : String? = "",
    @SerializedName("no_of_contractor"   ) var noOfContractor  : Int?    = 0,
    @SerializedName("no_of_mason"        ) var noOfMason       : Int?    = 0,
    @SerializedName("no_of_engineer"     ) var noOfEngineer    : Int?    = 0,
    @SerializedName("no_of_site"         ) var noOfSite        : Int?    = 0,
    @SerializedName("no_of_site_conv"    ) var noOfSiteConv    : Int?    = 0,
    @SerializedName("no_of_bags_consump" ) var noOfBagsConsump : Int?    = 0,
    @SerializedName("no_of_sites_visit"  ) var noOfSitesVisit  : Int?    = 0,
    @SerializedName("no_of_engr_visit"   ) var noOfEngrVisit   : Int?    = 0,
    @SerializedName("no_of_cont_visit"   ) var noOfContVisit   : Int?    = 0,
    @SerializedName("no_of_ihb_entry"    ) var noOfIhbEntry    : Int?    = 0,
    @SerializedName("no_of_comp_hand"    ) var noOfCompHand    : Int?    = 0,
    @SerializedName("no_of_engr_ref_call" ) var noOfEngrRefCall : Int?    = 0,
    @SerializedName("no_of_cont_ref_call" ) var noOfContRefCall : Int?    = 0,
    @SerializedName("no_of_ihb_ref_call"  ) var noOfIhbRefCall  : Int?    = 0
)
