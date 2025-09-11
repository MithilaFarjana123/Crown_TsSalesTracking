package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TourApproveResponseChild(

    @SerializedName("tr_trno"        ) var trTrno       : String? = null,
    @SerializedName("tr_emp"         ) var trEmp        : String? = null,
    @SerializedName("tr_fdt"         ) var trFdt        : String? = null,
    @SerializedName("tr_tdt"         ) var trTdt        : String? = null,
    @SerializedName("tr_iss_date"    ) var trIssDate    : String? = null,
    @SerializedName("tr_purpose"     ) var trPurpose    : String? = null,
    @SerializedName("tr_rem"         ) var trRem        : String? = null,
    @SerializedName("tr_stat"        ) var trStat       : String? = null,
    @SerializedName("tr_note"        ) var trNote       : String? = null,
    @SerializedName("tr_ent_user"    ) var trEntUser    : String? = null,
    @SerializedName("tr_pending_to"  ) var trPendingTo  : String? = null,
    @SerializedName("tr_emp_name"    ) var trEmpName    : String? = null,
    @SerializedName("tr_ent_time"    ) var trEntTime    : String? = null,
    @SerializedName("tr_app_time"    ) var trAppTime    : String? = null,
    @SerializedName("tr_deny_reason" ) var trDenyReason : String? = null,
    @SerializedName("tr_app_row"     ) var trAppRow     : Int?    = null,
    @SerializedName("tr_app_serial"  ) var trAppSerial  : Int?    = null,
    @SerializedName("tr_rep_usr"     ) var trRepUsr     : String? = null,
    @SerializedName("tr_app_status"  ) var trAppStatus  : String? = null,
    @SerializedName("tr_app_type"    ) var trAppType    : String? = null,
    @SerializedName("tr_emp_pic"     ) var trEmpPic     : String? = null


)
