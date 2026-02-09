package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class Data(
/*
    @SerializedName("user_id"                 ) var userId               : String? = "",
    @SerializedName("user_role"               ) var userRole             : String? = "",
    @SerializedName("user_name"               ) var userName             : String? = "",
    @SerializedName("user_mobile"             ) var userMobile           : String? = "",
    @SerializedName("user_location"           ) var userLocation         : String? = "",
    @SerializedName("user_dept"               ) var userDept             : String? = "",
    @SerializedName("user_desig"              ) var userDesig            : String? = "",
    @SerializedName("user_join"               ) var userJoin             : String? = "",
    @SerializedName("user_bid"                ) var userBid              : String? = "",
    @SerializedName("user_profile_image_path" ) var userProfileImagePath : String? = "",
    @SerializedName("att_status"              ) var attStatus            : String? = "",
    @SerializedName("loc_track_status"        ) var locTrackStatus       : String? = "",
    @SerializedName("user_access"             ) var userAccess           : String? = "",
    @SerializedName("fence_distance"          ) var fenceDistance        : Int?    = 0,
    @SerializedName("att_lat"                 ) var attLat               : Double?    = 0.0,
    @SerializedName("att_lon"                 ) var attLon               : Double?    = 0.0,
    @SerializedName("emp_job_hrc1"            ) var empJobHrc1           : String? = "",
    @SerializedName("emp_job_hrc2"            ) var empJobHrc2           : String? = "",
    @SerializedName("emp_job_hrc3"            ) var empJobHrc3           : String? = "",
    @SerializedName("emp_job_hrc4"            ) var empJobHrc4           : String? = "",
    @SerializedName("emp_job_hrc5"            ) var empJobHrc5           : String? = ""

*/

@SerializedName("user_id"                 ) var userId               : String? = "",
@SerializedName("user_role"               ) var userRole             : String? =  "",
@SerializedName("user_name"               ) var userName             : String? =  "",
@SerializedName("user_mobile"             ) var userMobile           : String? =  "",
@SerializedName("user_location"           ) var userLocation         : String? =  "",
@SerializedName("user_dept"               ) var userDept             : String? =  "",
@SerializedName("user_desig"              ) var userDesig            : String? =  "",
@SerializedName("user_join"               ) var userJoin             : String? =  "",
@SerializedName("user_bid"                ) var userBid              : String? =  "",
@SerializedName("user_profile_image_path" ) var userProfileImagePath : String? =  "",
@SerializedName("att_status"              )  var att_status          : String? =  "",
@SerializedName("user_access"             )  var user_access         : String? =  "",
@SerializedName("loc_track_status"        )  var loc_track_status    : String? =  "",
@SerializedName("user_accessn"            )  var user_accessn        : String? =  "",

@SerializedName("fence_distance"          )  var fence_distance      : Int =  0,
@SerializedName("fence_restrict"          )  var fence_restrict      : Boolean =  false,

@SerializedName("emp_job_hrc1"            ) var empJobHrc1           : String? = "",
@SerializedName("emp_job_hrc2"            ) var empJobHrc2           : String? = "",
@SerializedName("emp_job_hrc3"            ) var empJobHrc3           : String? = "",
@SerializedName("emp_job_hrc4"            ) var empJobHrc4           : String? = "",
@SerializedName("emp_job_hrc5"            ) var empJobHrc5           : String? = "",

@SerializedName("att_lat"                 ) var attLat               : Double?    = 0.0,
@SerializedName("att_lon"                 ) var attLon               : Double?    = 0.0



)