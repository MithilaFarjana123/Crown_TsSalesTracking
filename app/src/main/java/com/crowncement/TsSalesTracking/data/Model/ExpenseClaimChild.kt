package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class ExpenseClaimChild(

    @SerializedName("tr_trn_no"   ) var trTrnNo   : String? = null,
    @SerializedName("tr_date"     ) var trDate    : String? = null,
    @SerializedName("tr_doc_no"   ) var trDocNo   : String? = null,
    @SerializedName("tr_row"      ) var trRow     : Int?    = null,
    @SerializedName("tr_exp_type" ) var trExpType : String? = null,
    @SerializedName("tr_exp_sub"  ) var trExpSub  : String? = null,
    @SerializedName("tr_frm_loc"  ) var trFrmLoc  : String? = null,
    @SerializedName("tr_to_loc"   ) var trToLoc   : String? = null,
    @SerializedName("tr_pay_typ"  ) var trPayTyp  : String? = null,
    @SerializedName("tr_rem"      ) var trRem     : String? = null,
    @SerializedName("tr_exp_amt"  ) var trExpAmt  : Int?    = null,
    @SerializedName("tr_real_amt" ) var trRealAmt : Int?    = null,
    @SerializedName("user_entry"  ) var userEntry : String? = null,
    @SerializedName("tr_img"      ) var trImg     : String? = null,
    @SerializedName("tr_status"   ) var trStatus  : String? = null

)
