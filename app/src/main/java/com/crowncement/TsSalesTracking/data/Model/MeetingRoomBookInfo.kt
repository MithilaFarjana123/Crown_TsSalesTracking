package com.crowncement.TsSalesTracking.data.Model

import com.google.gson.annotations.SerializedName

data class MeetingRoomBookInfo(
    @SerializedName("req_date"   ) var reqDate   : String? = null,
    @SerializedName("book_date"  ) var bookDate  : String? = null,
    @SerializedName("room_name"  ) var roomName  : String? = null,
    @SerializedName("start_time" ) var startTime : String? = null,
    @SerializedName("end_time"   ) var endTime   : String? = null,
    @SerializedName("purpose"    ) var purpose   : String? = null,
    @SerializedName("dept_code"  ) var deptCode  : String? = null,
    @SerializedName("emp_code"   ) var empCode   : String? = null,
    @SerializedName("remarks"    ) var remarks   : String? = null
)
