package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class TypeResponseChild(

    @SerializedName("Code_no"     ) var CodeNo     : String? = null,
    @SerializedName("Code_type"   ) var CodeType   : String? = null,
    @SerializedName("Code_detail" ) var CodeDetail : String? = null,
    @SerializedName("Code_prop"   ) var CodeProp   : String? = null,
    @SerializedName("Code_alt"    ) var CodeAlt    : String? = null,
    @SerializedName("Code_active" ) var CodeActive : String? = null

)
