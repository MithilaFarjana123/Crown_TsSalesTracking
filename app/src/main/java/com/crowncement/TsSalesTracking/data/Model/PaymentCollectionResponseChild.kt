package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class PaymentCollectionResponseChild(

    @SerializedName("trn_no"      ) var trnNo      : String? = "",
    @SerializedName("col_ent_dt"  ) var colEntDt   : String? = "",
    @SerializedName("bank_code"   ) var bankCode   : String? = "",
    @SerializedName("br_code"     ) var brCode     : String? = "",
    @SerializedName("acc_no"      ) var accNo      : String? = "",
    @SerializedName("routing_no"  ) var routingNo  : String? = "",
    @SerializedName("depo_date"   ) var depoDate   : String? = "",
    @SerializedName("acc_name"    ) var accName    : String? = "",
    @SerializedName("cus_code"    ) var cusCode    : String? = "",
    @SerializedName("pay_type"    ) var payType    : String? = "",
    @SerializedName("doc_no"      ) var docNo      : String? = "",
    @SerializedName("emp_code"    ) var empCode    : String? = "",
    @SerializedName("depo_amt"    ) var depoAmt    : Int?    = 0,
    @SerializedName("user_entry"  ) var userEntry  : String? = "",
    @SerializedName("last_update" ) var lastUpdate : String? = "",
    @SerializedName("dep_rem"     ) var depRem     : String? = "",
    @SerializedName("dep_img"     ) var depImg     : String? = "",
    @SerializedName("dep_status"  ) var depStatus  : String? = ""

)
