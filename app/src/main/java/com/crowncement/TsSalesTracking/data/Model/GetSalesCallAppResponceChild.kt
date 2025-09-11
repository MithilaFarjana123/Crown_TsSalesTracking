package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class GetSalesCallAppResponceChild(
    @SerializedName("call_trno"        ) var callTrno      : String? = "",
    @SerializedName("party_code"       ) var partyCode     : String? = "",
    @SerializedName("party_name"       ) var partyName     : String? = "",
    @SerializedName("party_add"        ) var partyAdd      : String? = "",
    @SerializedName("proj_code"        ) var projCode      : String? = "",
    @SerializedName("proj_add"         ) var projAdd       : String? = "",
    @SerializedName("del_date"         ) var delDate       : String? = "",
    @SerializedName("first_del_date"   ) var firstDelDate  : String? = "",
    @SerializedName("conv_status"      ) var convStatus    : String? = "",
    @SerializedName("del_qty"          ) var delQty        : Int?    = 0,
    @SerializedName("app_status"       ) var appStatus     : String? = "",
    @SerializedName("party_mob"        ) var partyMob      : String? = "",
    @SerializedName("ent_date"         ) var entDate       : String? = "",
    @SerializedName("party_hrc1"       ) var partyHrc1     : String? = "",
    @SerializedName("party_hrc2"       ) var partyHrc2     : String? = "",
    @SerializedName("party_hrc3"       ) var partyHrc3     : String? = "",
    @SerializedName("party_hrc4"       ) var partyHrc4     : String? = "",
    @SerializedName("ent_user"         ) var entUser       : String? = "",
    @SerializedName("ent_upd_usr"      ) var entUpdUsr     : String? = "",
    @SerializedName("sales_call_type"  ) var salesCallType : String? = "",
    @SerializedName("remarks"          ) var remarks       : String? = "",
    @SerializedName("discus_sub_top"   ) var discusSubTop  : String? = "",
    @SerializedName("no_achv_reas_det" ) var noAchvReasDet : String? = "",
    @SerializedName("movment_stat"     ) var movmentStat   : String? = "",
    @SerializedName("party_group"      ) var partyGroup    : String? = ""


)
