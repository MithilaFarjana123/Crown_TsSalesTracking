package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class EmpInfo(

    @SerializedName("emp_id"    ) var empId     : String? = "",
    @SerializedName("emp_name"  ) var empName   : String? = "",
    @SerializedName("emp_desig" ) var empDesig  : String? = "",
    @SerializedName("emp_dept"  ) var empDept   : String? = "",
    @SerializedName("emp_cat"   ) var empCat    : String? = "",
    @SerializedName("emp_loc"   ) var empLoc    : String? = "",
    @SerializedName("emp_email" ) var empEmail  : String? = "",
    @SerializedName("emp_mobile") var empMobile : String? = "",
    @SerializedName("emp_status") var empStatus : String? = "",
    @SerializedName("emp_join"  ) var empJoin   : String? = "",
    @SerializedName("emp_bid"   ) var empBid    : Int?    = 0,
    @SerializedName("emp_img"   ) var empImg    : String? = "",
    @SerializedName("emp_zone"  ) var emp_zone : String?  = "",
    @SerializedName("emp_hrc2"  ) var emp_hrc2 : String?  = ""
)
