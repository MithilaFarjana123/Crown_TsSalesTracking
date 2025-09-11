package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class PaySlipChild(

    @SerializedName("emp_sbu"  ) var empSbu  : String? = null,
    @SerializedName("bank_nam" ) var bankNam : String? = null,
    @SerializedName("acc_no"   ) var accNo   : String? = null,
    @SerializedName("emp_stat" ) var empStat : String? = null,
    @SerializedName("sal_no"   ) var salNo   : Int?    = null,
    @SerializedName("basic"    ) var basic   : Int?    = null,
    @SerializedName("cons"     ) var cons    : Int?    = null,
    @SerializedName("hr"       ) var hr      : Int?    = null,
    @SerializedName("conv"     ) var conv    : Int?    = null,
    @SerializedName("medic"    ) var medic   : Int?    = null,
    @SerializedName("cpf_earn" ) var cpfEarn : Int?    = null,
    @SerializedName("arrear"   ) var arrear  : Int?    = null,
    @SerializedName("bonus"    ) var bonus   : Int?    = null,
    @SerializedName("absent"   ) var absent  : Int?    = null,
    @SerializedName("oth_earn" ) var othEarn : Int?    = null,
    @SerializedName("ait_ded"  ) var aitDed  : Int?    = null,
    @SerializedName("adv_ded"  ) var advDed  : Int?    = null,
    @SerializedName("cpf_ded"  ) var cpfDed  : Int?    = null,
    @SerializedName("pf_com"   ) var pfCom   : Int?    = null,
    @SerializedName("motor"    ) var motor   : Int?    = null,
    @SerializedName("lvpn_ded" ) var lvpnDed : Int?    = null,
    @SerializedName("ex_mob"   ) var exMob   : Int?    = null,
    @SerializedName("lunch"    ) var lunch   : Int?    = null,
    @SerializedName("oth_ded"  ) var othDed  : Int?    = null,
    @SerializedName("tot_earn" ) var totEarn : Int?    = null,
    @SerializedName("tot_ded"  ) var totDed  : Int?    = null,
    @SerializedName("tot_net"  ) var totNet  : Int?    = null
)
