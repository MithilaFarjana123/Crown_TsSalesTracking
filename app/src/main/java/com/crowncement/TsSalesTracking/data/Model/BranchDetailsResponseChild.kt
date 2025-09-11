package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class BranchDetailsResponseChild(

    @SerializedName("Bank"         ) var Bank        : String? = null,
    @SerializedName("Branch"       ) var Branch      : String? = null,
    @SerializedName("Account_No"   ) var AccountNo   : String? = null,
    @SerializedName("Routing_No"   ) var RoutingNo   : String? = null,
    @SerializedName("Company_Name" ) var CompanyName : String? = null
)
