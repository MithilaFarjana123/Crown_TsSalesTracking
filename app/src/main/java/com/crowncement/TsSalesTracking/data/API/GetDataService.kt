package com.crowncement.TsSalesTracking.data.API

import com.crowncement.TsSalesTracking.data.Model.*
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GetDataService {

    //TODO ------> CHeck APP version
    @Multipart
    @POST("VersionCheck")
    fun getAppVersion(
        @Part("app_name") app_name: RequestBody,
        @Part("cur_date") cur_date: RequestBody

    ): Flowable<AppVersionResponse>?


    @Multipart
    @POST("AppAttHrcList")
    fun getAttApproveList(
        @Part("user_id") user_id: RequestBody,
        @Part("att_date") att_date: RequestBody
    ): Flowable<AttApproveResponse>?

    //todo department
    @GET("GetDept")
    fun savedDepartment(): Flowable<DepartmentResponce>?

    // todo save Meeting room booking
    @Multipart
    @POST("MeetRoomBKSave")
    fun saveMeetingRoomBooking(
        @Part("user_id") user_id: RequestBody,
        @Part("room_info") room_info: RequestBody
    ): Flowable<MeetingRoomBookInfoResponce>?

    //todo get meetingRoomList
    @GET("MeetRoomList")
    fun getMeetingList(): Flowable<MeetingRoomListResponce>?

    //todo MeetRoomBKList
    @Multipart
    @POST("MeetRoomBKList")
    fun getMeetRoomBKList(
        @Part("user_id") user_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody
    ): Flowable<MeetRoomBKListResponce>?

//todo CarReqSave
@Multipart
@POST("CarReqSave")
fun saveCarReqSave(
    @Part("user_id") user_id: RequestBody,
    @Part("req_info") req_info: RequestBody
): Flowable<CarReqSaveresponce>?

//todo CarReqEntList
@Multipart
@POST("CarReqEntList")
fun CarReqEntList(
    @Part("emp_code") emp_code: RequestBody,
    @Part("from_date") from_date: RequestBody,
    @Part("to_date") to_date: RequestBody

): Flowable<CarReqEntListResponce>?

//todo attendence approve
    @Multipart
    @POST("AttUpd4Spvr")
    fun approveSalesPersonAttendance(
        @Part("user_id") user_id: RequestBody,
        @Part("emp_id") emp_id: RequestBody,
        @Part("att_date") att_date: RequestBody
    ): Flowable<CommonSaveResponse>?



    //TODO-------------> SAVE ATTENDANCE

    @Multipart
    @POST("AttendanceEntry")
    fun attendanceEntry(
        @Part("user_id") user_id: RequestBody,
        @Part("att_date") att_date: RequestBody,
        @Part("att_time") att_time: RequestBody,
        @Part("shiftNo") shiftNo: RequestBody,
        @Part("shift") shift: RequestBody,
        @Part att_image: MultipartBody.Part,
        @Part("att_lat") att_lat: RequestBody,
        @Part("att_lon") att_lon: RequestBody,
        @Part("att_type") att_type: RequestBody
    ): Flowable<AttendanceResponse>?






//TODO---------------------> UPDATE ATTENDANCE

    @Multipart
    @POST("AttendanceUpdate")
    fun attendanceUpdate(
        @Part("user_id") user_id: RequestBody,
        @Part("att_date") att_date: RequestBody,
        @Part("att_time") att_time: RequestBody,
        @Part("shiftNo") shiftNo: RequestBody,
        @Part("shift") shift: RequestBody,
        @Part att_image: MultipartBody.Part,
        @Part("att_lat") att_lat: RequestBody,
        @Part("att_lon") att_lon: RequestBody

    ): Flowable<AttendanceResponse>?



    @Multipart
    @POST("GetSalesDivision")
    fun getDivision(
        @Part("xhrc1") country: RequestBody
    ): Flowable<DivisionResponse>?

    @Multipart
    @POST("GetSalesDistrict")
    fun getDistrict(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
    ): Flowable<DistrictResponse>?

    @Multipart
    @POST("GetSalesThana")
    fun getThana(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody
    ): Flowable<ThanaResponse>?

    @Multipart
    @POST("GetSalesRoute")
    fun getSalesRoute(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody,
        @Part("xhrc4") thana: RequestBody
    ): Flowable<RouteResponse>?

    @Multipart
    @POST("GetSalesRetailer")
    fun getRetailerList(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody,
        @Part("xhrc4") thana: RequestBody,
        @Part("xhrc5") route: RequestBody
    ): Flowable<RetailerResponse>?

    @Multipart
    @POST("GetSalesDealer")
    fun getDealerList(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody
    ): Flowable<DealerResponse>?

    @Multipart
    @POST("GetSalesOthPartner")
    fun getOtherCustomerList(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody,
        @Part("xhrc4") thana: RequestBody,
        @Part("xtype") xtype: RequestBody

    ): Flowable<OtherCustomerResponse>?




   //todo getOtherMasonList

    @Multipart
    @POST("GetSalesOthPartner")
    fun getOtherMasonList(
        @Part("xhrc1") country: RequestBody,
        @Part("xhrc2") division: RequestBody,
        @Part("xhrc3") district: RequestBody,
        @Part("xhrc4") thana: RequestBody,
        @Part("xtype") xtype: RequestBody

    ): Flowable<OtherCustomerResponse>?

    @Multipart
    @POST("SaleRetailUpdate")
    fun updateRetailer(
        @Part("user_id") user_id: RequestBody,
        @Part("retail_id") retail_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("SaleBPImageUpload")
    fun uploadCoverPhoto(
        @Part("user_id") user_id: RequestBody,
        @Part("party_code") party_code: RequestBody,
        @Part("party_type") party_type: RequestBody,
        @Part("party_lat") party_lat: RequestBody,
        @Part("party_lon") party_lon: RequestBody,
        @Part att_image: MultipartBody.Part,
    ): Flowable<CoverImageUploadResponse>?

    @Multipart
    @POST("SaleDealerUpdate")
    fun updateDealer(
        @Part("user_id") user_id: RequestBody,
        @Part("dealer_id") dealer_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("SalePartnerUpdate")
    fun updateOtherParty(
        @Part("user_id") user_id: RequestBody,
        @Part("partner_id") partner_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<CommonSaveResponse>?
    

    @Multipart
    @POST("SaleRetailAdd")
    fun saveNewRetailer(
        @Part("user_id") user_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<RetailerSaveResponse>?


    @Multipart
    @POST("SaleOthBPAdd")
    fun saveNewCustomer(
        @Part("user_id") user_id: RequestBody,
        @Part("bp_type") bp_type: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<OtherNewCustomerResponse>?

    @Multipart
    @POST("UploadImgDoc")
    fun uploadVisitingCard(
        @Part("user_id") user_id: RequestBody,
        @Part("party_code") party_code: RequestBody,
        @Part("doc_ext") doc_ext: RequestBody,
        @Part all_images: MultipartBody.Part
    ): Flowable<CommonSaveResponse>?


    @Multipart
    @POST("PartyDocUpload")
    fun uploadCustomerDoc(
        @Part("user_id") user_id: RequestBody,
        @Part("party_code") party_code: RequestBody,
        @Part("party_type") party_type: RequestBody,
        @Part("doc_type") doc_type: RequestBody,
        @Part("doc_ext") doc_ext: RequestBody,
        @Part all_images: MultipartBody.Part
    ): Flowable<CommonSaveResponse>?



    //TODO ------------------------------------------------------UPLOAD LOCATION TRACKING
    @Multipart
    @POST("SendEmpGeoLoc")
    fun uploadOfflineLocationData(
        @Part("user_id") user_id: RequestBody,
        @Part("cap_time") cap_time: RequestBody,
        @Part("cap_lat") cap_lat: RequestBody,
        @Part("cap_lon") cap_lon: RequestBody
    ): Flowable<CommonSaveResponse>?

    @GET("AppEmpList")
    fun getEmployeeList(): Flowable<EmpResponse>?

    @Multipart
    @POST("SendBulkNot")
    fun sendPushMessage(
        @Part("user_id") user_id: RequestBody,
        @Part("not_title") not_title: RequestBody,
        @Part("not_body") not_body: RequestBody,
        @Part("emp_list") emp_list: RequestBody

    ): Flowable<CommonSaveResponse>?


    @Multipart
    @POST("GetCodeType")
    fun getCodeType(
        @Part("code_type") code_type: RequestBody
    ): Flowable<TypeResponse>?

    @Multipart
    @POST("GetCodeSubType")
    fun getCodeSubType(
        @Part("code_sub_type") code_sub_type: RequestBody,
        @Part("exp_type")      exp_type: RequestBody
    ): Flowable<TypeResponse>?

    @Multipart
    @POST("GetHolidayDate")
    fun getHolidayList(
        @Part("user_id")  user_id: RequestBody,
        @Part("year_no")  year_no: RequestBody,
        @Part("month_no") month_no: RequestBody
    ): Flowable<HolidayResponse>?



    @Multipart
    @POST("GetPushMessage")
    fun getPushMessage(
        @Part("user_id") user_id: RequestBody
    ): Flowable<PushMessageResponse>?

    //TODO -----> Attendance Report


    @Multipart
    @POST("AttendanceList")
    fun userAttendanceList(
        @Part("user_id") user_id: RequestBody,
        @Part("att_fdate") att_fdate: RequestBody,
        @Part("att_tdate") att_tdate: RequestBody

    ): Flowable<AttendanceReport>?

    //TODO------------> Late List for apply

    @Multipart
    @POST("LateAttList")
    fun userLateListForApply(
        @Part("user_id") user_id: RequestBody,
        @Part("user_dtime") user_dtime: RequestBody
    ): Flowable<LateResponse>?

    //TODO -----------> APPLIED LATE LIST

    @Multipart
    @POST("LateList")
    fun userAppliedLateList(
        @Part("user_id") user_id: RequestBody,
        @Part("user_dtime") user_dtime: RequestBody
    ): Flowable<LateAppliedResponse>?

    //TODO ---------------------> Late Entry


    @Multipart
    @POST("LateEntry")
    fun lateEntry(
        @Part("user_id") user_id: RequestBody,
        @Part("lt_date") lv_trn_no: RequestBody,
        @Part("lt_iss_date") lv_ap_row: RequestBody,
        @Part("lt_purpose") lv_fdate: RequestBody,
        @Part("lt_rem") lt_rem: RequestBody,
        @Part("lv_not_to") lv_not_to: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO----------------> Late Approval List
    @Multipart
    @POST("LateList4App")
    fun lateApprovalList(
        @Part("user_id") user_id: RequestBody,
    ): Flowable<LateApproveResponse>?

    //TODO----------------> Late Approve


    @Multipart
    @POST("LateApp")
    fun lateApproval(
        @Part("user_id") user_id: RequestBody,
        @Part("lt_trn_no") lt_trn_no: RequestBody,
        @Part("lt_ap_row") lt_ap_row: RequestBody,
        @Part("lt_date") lt_date: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO----------------> Late Reject

    @Multipart
    @POST("LateReject")
    fun lateReject(
        @Part("user_id") user_id: RequestBody,
        @Part("lt_trn_no") lt_trn_no: RequestBody,
        @Part("lt_ap_row") lt_ap_row: RequestBody,
        @Part("lt_deny_res") lt_date: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO Leave List

    @Multipart
    @POST("LeaveList")
    fun userLeaveList(
        @Part("user_id") user_id: RequestBody,
        @Part("lv_year") lv_year: RequestBody,
        @Part("lv_code") lv_code: RequestBody
    ): Flowable<LeaveApplicationResponse>?

    //TODO --------------> GET USER LEAVE INFO (TYPE WISE)

    @Multipart
    @POST("EmpLeaveStatus")
    fun userLeaveTypeInfo(
        @Part("user_id") user_id: RequestBody,
        @Part("user_dtime") user_dtime: RequestBody
    ): Flowable<LeaveTypeSummaryResponse>?

    //TODO --------------> Get user boss

    @Multipart
    @POST("EmpHrc")
    fun getUserHierarchy(
        @Part("user_id") user_id: RequestBody,
        @Part("cur_date") cur_date: RequestBody,
        @Part("rep_type") rep_type: RequestBody
    ): Flowable<HierarchyResponse>?

    //TODO --------------------> Is Subordinate Available

    @Multipart
    @POST("DownCheck")
    fun checkSubordinate(
        @Part("user_id") user_id: RequestBody,
        @Part("cur_date") cur_date: RequestBody,
        @Part("rep_type") rep_type: RequestBody
    ): Flowable<Subordinate>?

    //TODO -------------> LEAVE APPLICATION SAVE

    @Multipart
    @POST("LeaveEntry")
    fun saveLeaveApplication(
        @Part("user_id") user_id: RequestBody,
        @Part("lv_date") lv_date: RequestBody,
        @Part("lv_fdate") lv_fdate: RequestBody,
        @Part("lv_tdate") lv_tdate: RequestBody,
        @Part("lv_code") lv_code: RequestBody,
        @Part("lv_purpose") lv_purpose: RequestBody,
        @Part("lv_add") lv_add: RequestBody,
        @Part("lv_phone") lv_phone: RequestBody,
        @Part("lv_ap_emp") lv_ap_emp: RequestBody,
        @Part("lv_not_to") lv_not_to: RequestBody,
        @Part all_images: ArrayList<MultipartBody.Part>

    ): Flowable<LeaveSaveResponse>?

    //TODO --------------------> Leave Application Approval List

    @Multipart
    @POST("LeaveList4App")
    fun getLeaveApprovalList(
        @Part("user_id") user_id: RequestBody
    ): Flowable<LeaveApproveResponse>?

    //TODO --------------------> Leave Application Approve

    @Multipart
    @POST("LeaveApp")
    fun approveLeaveApplication(
        @Part("user_id") user_id: RequestBody,
        @Part("lv_trn_no") lv_trn_no: RequestBody,
        @Part("lv_ap_row") lv_ap_row: RequestBody,
        @Part("lv_fdate") lv_fdate: RequestBody,
        @Part("lv_tdate") lv_tdate: RequestBody
    ): Flowable<LeaveConfirmResponse>?


    //TODO --------------------> Leave Application Rejection

    @Multipart
    @POST("LeaveReject")
    fun rejectLeaveApplication(
        @Part("user_id") user_id: RequestBody,
        @Part("lv_trn_no") lv_trn_no: RequestBody,
        @Part("lv_ap_row") lv_ap_row: RequestBody,
        @Part("lv_deny_res") lv_deny_res: RequestBody

    ): Flowable<CommonSaveResponse>?


    //TODO TEST MULTIPLE IMAGE UPLOAD

    @Multipart
    @POST("SendMultiImage")
    fun multyImageEntry(
        @Part all_images: ArrayList<MultipartBody.Part>,
        @Part("user_id") user_id: RequestBody,
        @Part("lv_no") att_lat: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO Pay slip Month info
    @Multipart
    @POST("GetEmpSalNo")
    fun getSalaryMonth(
        @Part("user_id") user_id: RequestBody
    ): Flowable<SalaryMonthResponse>?

    //TODO Pay slip Info
    @Multipart
    @POST("EmpPayslip")
    fun getSalaryInfo(
        @Part("user_id") user_id: RequestBody,
        @Part("sal_no") sal_no: RequestBody
    ): Flowable<PaySlipResponse>?

    @Multipart
    @POST("TourList")
    fun getTourList(
        @Part("user_id") user_id: RequestBody,
        @Part("usr_time") usr_time: RequestBody
    ): Flowable<TourResponse>?

    @Multipart
    @POST("TourEntry")
    fun saveTour(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_fdate") tr_fdate: RequestBody,
        @Part("tr_tdate") tr_tdate: RequestBody,
        @Part("tr_iss_date") tr_iss_date: RequestBody,
        @Part("tr_purpose") tr_purpose: RequestBody,
        @Part("tr_rem") tr_rem: RequestBody,
        @Part("tr_not_to") tr_not_to: RequestBody,
        @Part("tr_country") tr_country: RequestBody,
        @Part("tr_add") tr_add: RequestBody,

        ): Flowable<CommonSaveResponse>?

    //TODO Tour Approve List
    @Multipart
    @POST("TourList4App")
    fun getTourApproveList(
        @Part("user_id") user_id: RequestBody
    ): Flowable<TourApproveResponse>?

    //TODO TOUR APPROVE SAVE

    @Multipart
    @POST("TourApp")
    fun tourApprove(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody,
        @Part("tr_ap_row") tr_ap_row: RequestBody,
        @Part("tr_fdate") tr_fdate: RequestBody,
        @Part("tr_tdate") tr_tdate: RequestBody,
    ): Flowable<CommonSaveResponse>?

    //TODO TOUR Reject

    @Multipart
    @POST("TourReject")
    fun tourReject(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody,
        @Part("tr_ap_row") tr_ap_row: RequestBody,
        @Part("tr_deny_res") tr_deny_res: RequestBody

    ): Flowable<CommonSaveResponse>?

    //TODO-----------------------------------------------------------------------------------------TOUR START

    @Multipart
    @POST("TourStartPost")
    fun startTour(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody
    ): Flowable<CommonSaveResponse>?


    //TODO-----------------------------------------------------------------------------------------TOUR STOP

    @Multipart
    @POST("TourStopPost")
    fun stopTour(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody
    ): Flowable<CommonSaveResponse>?

//TODO-----------------------------------------------------------------------------SAVE COMMON EXPENSE CLAIM

    @Multipart
    @POST("TourExpenseSave")
    fun saveCommonExpenseClaim(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody,
        @Part("tr_date") tr_date: RequestBody,
        @Part("tr_exp_type") tr_exp_type: RequestBody,
        @Part("tr_exp_sub") tr_exp_sub: RequestBody,
        @Part("tr_exp_source") tr_exp_source: RequestBody,
        @Part("tr_frm_loc") tr_frm_loc: RequestBody,
        @Part("tr_to_loc") tr_to_loc: RequestBody,
        @Part("tr_pay_mod") tr_pay_mod: RequestBody,
        @Part("tr_amt") tr_amt: RequestBody,
        @Part("tr_rem") tr_rem: RequestBody,
        @Part("doc_ext") doc_ext: RequestBody,
        @Part all_images: MultipartBody.Part
        //   @Part all_images: ArrayList<MultipartBody.Part>
    ): Flowable<CommonSaveResponse>?

    //TODO ----------------------------------------------------------------->Tour Expense Entry List

    @Multipart
    @POST("TourExpenseRecord")
    fun getExpenseList(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody,
        @Part("tr_date") tr_date: RequestBody,
    ): Flowable<ExpenseClaimResponse>?

    //TODO--------------------------------------------------------------------> Tour expense complete

    @Multipart
    @POST("TourExpensePost")
    fun toruExpenseStop(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_trn_no") tr_trn_no: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("TourExpSum")
    fun getTourBillList(
        @Part("user_id") user_id: RequestBody,
        @Part("tr_fdate") tr_fdate: RequestBody,
        @Part("tr_tdate") tr_tdate: RequestBody
    ): Flowable<TourExpenseResponse>?

    //TODO DASHBOARD BANNER IMAGES
    @Multipart
    @POST("PickAdImage")
    fun getDashboardImage(
        @Part("app_proj") app_proj: RequestBody
    ): Flowable<DashboardResponse>?

    //TODO---------------------------------------------------------------------> Sales visit summary list

    @Multipart
    @POST("GetSalesCallTag")
    fun salesVisitSummaryList(
        @Part("user_id") user_id: RequestBody,
        @Part("move_date") move_date: RequestBody
    ): Flowable<SalesVisitSummaryResponse>?

    //TODO---------------------------------------------------------------------> Sales visit summary save

    @Multipart
    @POST("SalesCallMoveTag")
    fun salesVisitSummarySave(
        @Part("user_id") user_id: RequestBody,
        @Part("retail_id") retail_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody
    ): Flowable<CommonSaveResponse>?


    @GET("GetCallRecordDT")
    fun getCallRecordDiscussionTopic(): Flowable<CallRecordSpinnerResponse>?

    @GET("GetCallRecordRNA")
    fun getNoOrderReason(): Flowable<CallRecordSpinnerResponse>?

    @GET("GetCementBrand")
    fun getAllCementBrand(): Flowable<CallRecordSpinnerResponse>?

    @Multipart
    @POST("SalesCallMove4Tag")
    fun saveCallRecord(
        @Part("user_id") user_id: RequestBody,
        @Part("mov_doc") mov_doc: RequestBody,
        @Part("mov_row") mov_row: RequestBody,
        @Part("call_info") call_info: RequestBody

    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("SalesCallRecordSave")
    fun saveCallRecordHome(
        @Part("user_id") user_id: RequestBody,
        @Part("retail_id") retail_id: RequestBody,
        @Part("call_info") call_info: RequestBody

    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("EmpRouteList")
    fun getRouteForMovement(
        @Part("user_id") user_id: RequestBody
    ): Flowable<MovRouteResponse>?

    @Multipart
    @POST("EmpRoutePlanEntry")
    fun saveMovementPlan(
        @Part("user_id") user_id: RequestBody,
        @Part("pl_date") pl_date: RequestBody,
        @Part("route_path") route_path: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("EmpMovePlanList")
    fun movementPlanList(
        @Part("user_id") user_id: RequestBody,
        @Part("pln_yr") pln_yr: RequestBody,
        @Part("pln_mn") pln_mn: RequestBody
    ): Flowable<MovementPlanResponse>?

    @Multipart
    @POST("EmpActMovePlanList")
    fun movementActualPlanList(
        @Part("user_id") user_id: RequestBody,
        @Part("pln_yr") pln_yr: RequestBody,
        @Part("pln_mn") pln_mn: RequestBody
    ): Flowable<MovementPlanResponse>?

    @Multipart
    @POST("EmpActRoutePlanEntry")
    fun saveActualMovementPlan(
        @Part("user_id") user_id: RequestBody,
        @Part("pl_date") pl_date: RequestBody,
        @Part("route_path") route_path: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("ZonalJuniorList")
    fun zonalJuniorList(
        @Part("user_id") user_id: RequestBody,
        @Part("usr_time") usr_time: RequestBody
    ): Flowable<EmpResponse>?

    @Multipart
    @POST("SubmitMovePlan")
    fun submitMovementPlan(
        @Part("user_id") user_id: RequestBody,
        @Part("pln_yr") pln_yr: RequestBody,
        @Part("pln_mn") pln_mn: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("ApproveMovePlan")
    fun approveMovementPlan(
        @Part("user_id") user_id: RequestBody,
        @Part("move_no") move_no: RequestBody
    ): Flowable<CommonSaveResponse>?

    @Multipart
    @POST("EmpDailyActMovePlan")
    fun getActualPlan(
        @Part("user_id")  user_id: RequestBody,
        @Part("plan_date")  plan_date: RequestBody,
    ): Flowable<ActualPlnResponse>?


    //TODO -----> LOGIN

    @Multipart
    @POST("login")
    fun userLogin(
        @Part("user_id") user_id: RequestBody,
        @Part("user_password") user_password: RequestBody,
        @Part("device_id") device_id: RequestBody,
        @Part("fcm_token") fcm_token: RequestBody
    ): Flowable<UserInfo>?



    @Multipart
    @POST("AttendanceCheck")
    fun userAttendanceInfo(
        @Part("user_id") user_id: RequestBody,
        @Part("user_dtime") device_id: RequestBody
    ): Flowable<Attendance>?

    //TODO----------------------------------------------------------------------------------------------> Bank
    //@Multipart
    @GET("GetBank")
    fun getBankName(): Flowable<BankResponse>?


//TODO----------------------------------------------------------------------------------------------> Branch

    @Multipart
    @POST("GetBankBr")
    fun getBranchName(
        @Part("bank_code") bank_code: RequestBody
    ): Flowable<BranchResponse>?

//TODO----------------------------------------------------------------------------------------------> Branch Details

    @Multipart
    @POST("GetBankDetails")
    fun getBranchDetails(
        @Part("bank_code") bank_code: RequestBody,
        @Part("branch_code") branch_code: RequestBody
    ): Flowable<BranchDetailsResponse>?

    //TODO-------------------------------------------------------------------------Save Sales collection


    /*
    @Multipart
    @POST("CollectionEntry")
    fun saveSalesCollection(
        @Part("user_id") user_id: RequestBody,
        @Part("ent_date") ent_date: RequestBody,
        @Part("dp_bank") dp_bank: RequestBody,
        @Part("dp_br") dp_br: RequestBody,
        @Part("dp_acc") dp_acc: RequestBody,
        @Part("dp_brrt") dp_brrt: RequestBody,
        @Part("dp_date") dp_date: RequestBody,
        @Part("dp_acc_nam") dp_acc_nam: RequestBody,
        @Part("cus_code") cus_code: RequestBody,
        @Part("pay_typ") pay_typ: RequestBody,
        @Part("doc_no") doc_no: RequestBody,
        @Part("emp_code") emp_code: RequestBody,
        @Part("dp_amt") dp_amt: RequestBody,
        @Part("dp_rem") dp_rem: RequestBody,
        @Part("doc_ext") doc_ext: RequestBody,
        @Part all_images: MultipartBody.Part
    ): Flowable<CommonSaveResponse>?

     */


    //TODO------------------------------------------------------------------------------------> Sales Collection List
/*
    @Multipart
    @POST("CollectionRecord")
    fun getSalesCollection(
        @Part("user_id") user_id: RequestBody,
        @Part("cus_code") cus_code: RequestBody,
        @Part("dep_fdate") dep_fdate: RequestBody,
        @Part("dep_tdate") dep_tdate: RequestBody
    ): Flowable<SalesCollectionResponse>?


 */
/*
    @Multipart
    @POST("CollectionRecord4SV")
    fun getSalesCollectionListForApprove(
        @Part("user_id")  user_id: RequestBody,
        @Part("dep_fdate")  dep_fdate: RequestBody,
        @Part("dep_tdate")  dep_tdate: RequestBody
    ): Flowable<PaymentCollectionResponse>?

 */

    @Multipart
    @POST("AppCollection4SV")
    fun salesCollectionConfirm(
        @Part("user_id")  user_id: RequestBody,
        @Part("trn_no")  trn_no: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO Check Meal Status
    @Multipart
    @POST("CheckMeal")
    fun checkMealAllocation(
        @Part("user_id") user_id: RequestBody,
        @Part("cur_date") cur_date: RequestBody
    ): Flowable<MealAllocationResponse>?

    //TODO Confirm Meal
    @Multipart
    @POST("EntryMeal")
    fun confirmMealAllocation(
        @Part("user_id") user_id: RequestBody,
        @Part("next_date") cur_date: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO Cancel Meal
    @Multipart
    @POST("CancelMeal")
    fun cancelMealAllocation(
        @Part("user_id") user_id: RequestBody,
        @Part("next_date") cur_date: RequestBody
    ): Flowable<CommonSaveResponse>?

    //TODO Meal Cost
    @Multipart
    @POST("EmpMealSum")
    fun mealCost(
        @Part("user_id") user_id: RequestBody,
        @Part("fdate") fdate: RequestBody,
        @Part("tdate") tdate: RequestBody
    ): Flowable<MealCostResponse>?

    //todo for new party entry
    @Multipart
    @POST("SaleOthBPAdd")
    fun saveNewTSParty(
        @Part("user_id") user_id: RequestBody,
        @Part("bp_type") fdate: RequestBody,
        @Part("profile_info") tdate: RequestBody
    ): Flowable<SaveNewPartyResponce>?

    //todo GetSalesOthPartner ts
    @Multipart
    @POST("GetSalesOthPartner")
    fun GetSalesOthPartner(
        @Part("xhrc1") xhrc1: RequestBody,
        @Part("xhrc2") xhrc2: RequestBody,
        @Part("xhrc3") xhrc3: RequestBody,
        @Part("xhrc4") xhrc4: RequestBody,
        @Part("xtype") xtype: RequestBody

        ): Flowable<GetSalesOthPartnerResponce>?


    //todo for site add

    @Multipart
    @POST("SaleBPSiteAdd")
    fun saveSaleBPNewSite(
        @Part("user_id") user_id: RequestBody,
        @Part("profile_info") info: RequestBody
    ): Flowable<saveSaleBPNewSiteResponce>?


    //todo GetSiteSumInfo
    @Multipart
    @POST("GetSiteSumInfo")
    fun GetSiteSumInfo(
        @Part("site_id") site_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody

    ): Flowable<GetSiteSumInfo>?

    //todo updateSite
    @Multipart
    @POST("UpdPartySite")
    fun updateSaleBPTsNewSite(
        @Part("user_id") user_id: RequestBody,
        @Part("proj_id") proj_id: RequestBody,
        @Part("profile_info") info: RequestBody
    ): Flowable<updateSaleBPNewSiteResponce>?


    //todo sor site img upload ts
    @Multipart
    @POST("TSSiteImageUpload")
    fun uploadTSSiteCover(
        @Part("user_id") user_id: RequestBody,
        @Part att_image: MultipartBody.Part,
        @Part("site_code") site_code: RequestBody,
        @Part("site_lat") site_lat: RequestBody,
        @Part("site_lon") site_lon: RequestBody

    ): Flowable<saveTSSiteImgResponce>?


    //todo complain img upload PartyDocUpload
    @Multipart
    @POST("PartyDocUpload")
    fun uploadComplainDoc(
        @Part("user_id") user_id: RequestBody,
        @Part("party_code") party_code: RequestBody,
        @Part("party_type") party_type: RequestBody,
        @Part("doc_type") doc_type: RequestBody,
        @Part("doc_ext") doc_ext: RequestBody,
        @Part all_images: MultipartBody.Part
    ): Flowable<PartyComplainDocResponce>?


    //todo for get saved site

    @Multipart
    @POST("GetTSSiteInfo")
    fun GetTSSiteInfo(
        @Part("xhrc1") xhrc1: RequestBody,
        @Part("xhrc2") xhrc2: RequestBody,
        @Part("xhrc3") xhrc3: RequestBody,
        @Part("xhrc4") xhrc4: RequestBody

        ): Flowable<GetTSSiteInfoResponce>?


    //todo get taggedSite
    @Multipart
    @POST("GetBPSiteInfo")
    fun GetBPSiteInfo(
        @Part("xparty") xparty: RequestBody

    ): Flowable<GetBPSiteInfoResponce>?




    //todo ts  quickvisit save
    @Multipart
    @POST("TSQuickVisitSave")
    fun TSQuickVisitSave(
        @Part("user_id") user_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody

        ): Flowable<TSQuickVisitSaveResponce>?

//todo for sitetag
@Multipart
@POST("SaleBPSiteTag")
fun SaleBPSiteTag(
    @Part("user_id") user_id: RequestBody,
    @Part("profile_info") info: RequestBody
): Flowable<SaleBPSiteTagResponce>?


//todo for direct call record /complain

    @Multipart
    @POST("TSCallMoveDirectSave")
    fun TSCallMoveDirectSave(
        @Part("user_id") user_id: RequestBody,
        @Part("call_info") call_info: RequestBody
    ): Flowable<TSCallMoveDirectSaveResponce>?


    //todo for tagged site from quickvisit to callrecord
    @Multipart
    @POST("TSCallMove4Tag")
    fun TSCallMove4Tag(
        @Part("user_id") user_id: RequestBody,
        @Part("mov_doc") mov_doc: RequestBody,
        @Part("mov_row") xhrc3: RequestBody,
        @Part("call_info") xhrc4: RequestBody

    ): Flowable<TSCallMove4TagResponce>?


    //todo for getting quick visit list GetTSCallTag

    @Multipart
    @POST("GetTSCallTag")
    fun GetTSCallTag(
        @Part("user_id") user_id: RequestBody,
        @Part("move_date") move_date: RequestBody
    ): Flowable<GetTSCallTagResponce>?


    //for gettopic
    @Multipart
    @POST("GetCodeType")
    fun GetCodeType(
        @Part("code_type") user_id: RequestBody
    ): Flowable<TopicResponce>?

    //to change password
    @Multipart
    @POST("UserPassChange")
    fun UserPassChange(
        @Part("user_id") user_id: RequestBody,
        @Part("device_id") device_id: RequestBody,
        @Part("old_pass") old_pass: RequestBody,
        @Part("new_pass") new_pass: RequestBody

        ): Flowable<UserPassChangeResponce>?


    //todo GetContractorMason
    @Multipart
    @POST("GetContractorMason")
    fun GetContractorMason(
        @Part("contractor_code") contractor_code: RequestBody
    ): Flowable<GetContractorMasonResponce>?

    //tagmason
    @Multipart
    @POST("ContMasonTag")
    fun ContMasonTag(
        @Part("user_id") user_id: RequestBody,
        @Part("profile_info") profile_info: RequestBody

        ): Flowable<MasonTagResponce>?


    //todo attendence absent
    @Multipart
    @POST("UpdAttAbs")
    fun absentAttendance(
        @Part("user_id")    user_id: RequestBody,
        @Part("emp_id")     emp_id: RequestBody,
        @Part("att_date")   att_date: RequestBody,
        @Part("shiftNo")    shiftNo: RequestBody,
        @Part("shift")      shift: RequestBody,
        @Part("reas_rej")   reas_rej: RequestBody
    ): Flowable<CommonSaveResponse>?


    //todo GetEmpEntSum
    @Multipart
    @POST("GetEmpEntSum")
    fun GetEmpEntSum(
        @Part("emp_id")        emp_id: RequestBody,
        @Part("from_date")     from_date: RequestBody,
        @Part("to_date")       to_date: RequestBody
    ): Flowable<GetEmpEntSumResponce>?

//todo eng work sum

    @Multipart
    @POST("GetEngrEntSum")
    fun GetEngrEntSum(
        @Part("engr_id")       engr_id: RequestBody,
        @Part("from_date")     from_date: RequestBody,
        @Part("to_date")       to_date: RequestBody
    ): Flowable<EngWorkSum>?

    //todo con worksum
    @Multipart
    @POST("GetContEntSum")
    fun GetContEntSum(
        @Part("cont_id")        cont_id: RequestBody,
        @Part("from_date")      from_date: RequestBody,
        @Part("to_date")        to_date: RequestBody
    ): Flowable<ConWorkSum>?


    //todo mason work sum
    @Multipart
    @POST("GetMasEntSum")
    fun GetMasEntSum(
        @Part("mas_id")        mas_id: RequestBody,
        @Part("from_date")     from_date: RequestBody,
        @Part("to_date")       to_date: RequestBody
    ): Flowable<MasonEngConSum>?


    //todo search exiting Party by number
    @Multipart
    @POST("SearchByMobile")
    fun SearchByMobile(
        @Part("mob_no")    mob_no: RequestBody
    ): Flowable<SearchByMobile>?

    //todo GetSalesMoveCall
    @Multipart
    @POST("GetSalesMoveCall")
    fun GetSalesMoveCall(
        @Part("site_id")    site_id: RequestBody
    ): Flowable<GetSalesMoveCall>?

    //todo PickDocWPart
    @Multipart
    @POST("PickDocWPart")
    fun PickDocWPart(
        @Part("app_proj") app_proj: RequestBody,
        @Part("doctype") doctype: RequestBody,
        @Part("partName") partName: RequestBody
    ): Flowable<PickDocWPartResponse>?


    //todo PickDocRandom for offer and benifit

    @Multipart
    @POST("PickDocRandom")
    fun getPickDocWPart(
        @Part("app_proj") app_proj: RequestBody,
        @Part("doctype") doctype: RequestBody
    ): Flowable<getPickDocWPartResponse>?


//todo GetSalesCallApp
    @Multipart
    @POST("GetSalesCallApp")
    fun GetSalesCallApp(
        @Part("user_id") user_id: RequestBody
    ): Flowable<GetSalesCallAppResponce>?


    //todo AppSalesCallresponce
    @Multipart
    @POST("AppSalesCall")
    fun AppSalesCall(
        @Part("user_id") user_id: RequestBody,
        @Part("call_trn") call_trn: RequestBody
    ): Flowable<AppSalesCallresponce>?


    //todo CancelSalesCall
    @Multipart
    @POST("CancelSalesCall")
    fun CancelSalesCall(
        @Part("user_id") user_id: RequestBody,
        @Part("call_trn") call_trn: RequestBody,
        @Part("rej_reas") rej_reas: RequestBody
        ): Flowable<CancelSalesCallResponce>?



}


