package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class SalesCollectionChild(

    @SerializedName("trn_no"      ) var trnNo      : String? = null,
    @SerializedName("col_ent_dt"  ) var colEntDt   : String? = null,
    @SerializedName("bank_code"   ) var bankCode   : String? = null,
    @SerializedName("br_code"     ) var brCode     : String? = null,
    @SerializedName("acc_no"      ) var accNo      : String? = null,
    @SerializedName("routing_no"  ) var routingNo  : String? = null,
    @SerializedName("depo_date"   ) var depoDate   : String? = null,
    @SerializedName("acc_name"    ) var accName    : String? = null,
    @SerializedName("cus_code"    ) var cusCode    : String? = null,
    @SerializedName("pay_type"    ) var payType    : String? = null,
    @SerializedName("doc_no"      ) var docNo      : String? = null,
    @SerializedName("emp_code"    ) var empCode    : String? = null,
    @SerializedName("depo_amt"    ) var depoAmt    : Int?    = null,
    @SerializedName("user_entry"  ) var userEntry  : String? = null,
    @SerializedName("last_update" ) var lastUpdate : String? = null,
    @SerializedName("dep_rem"     ) var depRem     : String? = null,
    @SerializedName("dep_img"     ) var dep_img    : String? = null,
    @SerializedName("dep_status"  ) var dep_status : String? = null

)
