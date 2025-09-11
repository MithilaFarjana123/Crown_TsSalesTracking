package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class AttApproveResponseChild(


    @SerializedName("emp_id"   )     var empId    : String?                 ="",
    @SerializedName("emp_name" )     var empName  : String?                 ="",
    @SerializedName("emp_desig")     var empDesig : String?                 ="",
    @SerializedName("att_date" )     var attDate  : String?                 ="",
    @SerializedName("att_stat" )     var attStat  : String?                 ="",
    @SerializedName("att_in"   )     var attIn    : String?                 ="",
    @SerializedName("att_out"  )     var attOut   : String?                 ="",
    @SerializedName("emp_img"  )     var empImg   : String?                 ="",
    @SerializedName("emp_img_out"  ) var emp_img_out   : String?                 ="",
    @SerializedName("att_shift"  ) var att_shift   : String?                 ="",
    @SerializedName("att_shftno"  ) var att_shftno   : String?                 ="",
    @SerializedName("loc_data" ) var loc_data : ArrayList<LocationData> = ArrayList()

//    @SerializedName("emp_id"   ) var empId    : String?                 ="",
//    @SerializedName("emp_name" ) var empName  : String?                 ="",
//    @SerializedName("emp_desig") var empDesig : String?                 ="",
//    @SerializedName("att_date" ) var attDate  : String?                 ="",
//    @SerializedName("att_stat" ) var attStat  : String?                 ="",
//    @SerializedName("att_in"   ) var attIn    : String?                 ="",
//    @SerializedName("att_out"  ) var attOut   : String?                 ="",
//    @SerializedName("emp_img"  ) var empImg   : String?                 ="",
//    @SerializedName("loc_data" ) var loc_data : ArrayList<LocationData> = ArrayList()
)
