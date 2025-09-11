package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class LeaveApproveChild(


    @SerializedName("lv_trno"        ) var lvTrno       : String? = null,
    @SerializedName("lv_emp"         ) var lvEmp        : String? = null,
    @SerializedName("lv_type"        ) var lvType       : String? = null,
    @SerializedName("lv_ap_dt"       ) var lvApDt       : String? = null,
    @SerializedName("lv_fdate"       ) var lvFdate      : String? = null,
    @SerializedName("lv_tdate"       ) var lvTdate      : String? = null,
    @SerializedName("lv_days"        ) var lvDays       : Int?    = null,
    @SerializedName("lv_purpose"     ) var lvPurpose    : String? = null,
    @SerializedName("lv_add"         ) var lvAdd        : String? = null,
    @SerializedName("lv_stat"        ) var lvStat       : String? = null,
    @SerializedName("lv_act"         ) var lvAct        : String? = null,
    @SerializedName("lv_phone"       ) var lvPhone      : String? = null,
    @SerializedName("lv_ent_user"    ) var lvEntUser    : String? = null,
    @SerializedName("lv_canc_ref"    ) var lvCancRef    : String? = null,
    @SerializedName("lv_pending_to"  ) var lvPendingTo  : String? = null,
    @SerializedName("lv_emp_name"    ) var lvEmpName    : String? = null,
    @SerializedName("lv_ent_time"    ) var lvEntTime    : String? = null,
    @SerializedName("lv_app_time"    ) var lvAppTime    : String? = null,
    @SerializedName("lv_deny_reason" ) var lvDenyReason : String? = null,
    @SerializedName("lv_app_row"     ) var lvAppRow     : Int?    = null,
    @SerializedName("lv_app_serial"  ) var lvAppSerial  : Int?    = null,
    @SerializedName("lv_rep_usr"     ) var lvRepUsr     : String? = null,
    @SerializedName("lv_app_status"  ) var lvAppStatus  : String? = null,
    @SerializedName("lv_app_type"    ) var lvAppType    : String? = null,
    @SerializedName("lv_emp_pic"     ) var lvEmpPic     : String? = null

)
