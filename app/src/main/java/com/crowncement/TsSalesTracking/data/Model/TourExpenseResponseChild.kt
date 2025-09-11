package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TourExpenseResponseChild(

    @SerializedName("tour_no"  ) var tourNo  : String?  = "",
    @SerializedName("from_date") var fromDate: String?  = "",
    @SerializedName("to_date"  ) var toDate  : String?  = "",
    @SerializedName("tot_hour" ) var totHour : Double?     = 0.0,
    @SerializedName("tot_dist" ) var totDist : Double?     = 0.0,
    @SerializedName("tot_amt"  ) var totAmt  : Double?     = 0.0

)
