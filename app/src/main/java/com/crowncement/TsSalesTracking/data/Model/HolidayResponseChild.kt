package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class HolidayResponseChild(

    @SerializedName("Holiday_Date" ) var HolidayDate : String? = "",
    @SerializedName("Day_Name"     ) var DayName     : String? ="",
    @SerializedName("Holiday_Type" ) var HolidayType : String? ="",
    @SerializedName("Holiday_Desc" ) var HolidayDesc : String? ="",
    @SerializedName("Month_Name"   ) var MonthName   : String? ="",
    @SerializedName("Week_No"      ) var WeekNo      : Int?    = 0

)
