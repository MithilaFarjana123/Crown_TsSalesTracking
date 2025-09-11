package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class CarReqEntListResponceChild(
    @SerializedName("req_no"     ) var reqNo     : String? = null,
    @SerializedName("entry_date" ) var entryDate : String? = null,
    @SerializedName("veh_reg_no" ) var vehRegNo  : String? = null,
    @SerializedName("veh_type"   ) var vehType   : String? = null,
    @SerializedName("dri_name"   ) var driName   : String? = null,
    @SerializedName("dri_mobile" ) var driMobile : String? = null,
    @SerializedName("from_loc"   ) var fromLoc   : String? = null,
    @SerializedName("to_loc"     ) var toLoc     : String? = null,
    @SerializedName("from_date"  ) var fromDate  : String? = null,
    @SerializedName("to_date"    ) var toDate    : String? = null,
    @SerializedName("start_time" ) var startTime : String? = null,
    @SerializedName("end_time"   ) var endTime   : String? = null,
    @SerializedName("purpose"    ) var purpose   : String? = null,
    @SerializedName("dept_code"  ) var deptCode  : String? = null,
    @SerializedName("emp_code"   ) var empCode   : String? = null,
    @SerializedName("emp_name"   ) var empName   : String? = null,
    @SerializedName("emp_mobile" ) var empMobile : String? = null,
    @SerializedName("no_person"  ) var noPerson  : String? = null,
    @SerializedName("remarks"    ) var remarks   : String? = null,
    @SerializedName("emp_img"    ) var empImg    : String? = null,
    @SerializedName("req_status" ) var reqStatus : String? = null,
    @SerializedName("app_by"     ) var appBy     : String? = null,
    @SerializedName("entry_time" ) var entryTime : String? = null
)
