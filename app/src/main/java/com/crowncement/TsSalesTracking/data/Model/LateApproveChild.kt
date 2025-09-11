package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LateApproveChild(

    @SerializedName("lt_trno"        ) var ltTrno       : String? = null,
    @SerializedName("lt_emp"         ) var ltEmp        : String? = null,
    @SerializedName("lt_dt"          ) var ltDt         : String? = null,
    @SerializedName("lt_iss_date"    ) var ltIssDate    : String? = null,
    @SerializedName("lt_purpose"     ) var ltPurpose    : String? = null,
    @SerializedName("lt_rem"         ) var ltRem        : String? = null,
    @SerializedName("lt_stat"        ) var ltStat       : String? = null,
    @SerializedName("lt_act"         ) var ltAct        : String? = null,
    @SerializedName("lt_ent_user"    ) var ltEntUser    : String? = null,
    @SerializedName("lt_pending_to"  ) var ltPendingTo  : String? = null,
    @SerializedName("lt_intime"      ) var ltIntime     : String? = null,
    @SerializedName("lt_emp_name"    ) var ltEmpName    : String? = null,
    @SerializedName("lt_ent_time"    ) var ltEntTime    : String? = null,
    @SerializedName("lt_app_time"    ) var ltAppTime    : String? = null,
    @SerializedName("lt_deny_reason" ) var ltDenyReason : String? = null,
    @SerializedName("lt_app_row"     ) var ltAppRow     : Int?    = null,
    @SerializedName("lt_app_serial"  ) var ltAppSerial  : Int?    = null,
    @SerializedName("lt_rep_usr"     ) var ltRepUsr     : String? = null,
    @SerializedName("lt_app_status"  ) var ltAppStatus  : String? = null,
    @SerializedName("lt_app_type"    ) var ltAppType    : String? = null,
    @SerializedName("lt_emp_pic"     ) var ltEmpPic     : String? = null

)
