package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object LeaveRepository {

    //Get user leave list
    fun getLeaveList(
        user_id: String,
        lv_year: String,
        lv_code: String
    ): MutableLiveData<Resource<LeaveApplicationResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val leaveYear = RequestBody.create("application/json".toMediaTypeOrNull(), lv_year)
        val leaveType = RequestBody.create("application/json".toMediaTypeOrNull(), lv_code)


        val attInfo: MutableLiveData<Resource<LeaveApplicationResponse>> =
            MutableLiveData<Resource<LeaveApplicationResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userLeaveList(username, leaveYear, leaveType)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        attInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return attInfo
    }


//GEt leave allocation

    fun getLeaveTypeInfo(
        user_id: String,
        user_dtime: String
    ): MutableLiveData<Resource<LeaveTypeSummaryResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val dateTime = RequestBody.create("application/json".toMediaTypeOrNull(), user_dtime)

        val leaveTypeInfo: MutableLiveData<Resource<LeaveTypeSummaryResponse>> =
            MutableLiveData<Resource<LeaveTypeSummaryResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userLeaveTypeInfo(username, dateTime)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        leaveTypeInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        leaveTypeInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return leaveTypeInfo
    }


    //Get User Hierarchy
    fun getUserHierarchy(
        user_id: String,
        user_dtime: String,
        repoType: String
    ): MutableLiveData<Resource<HierarchyResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val dateTime = RequestBody.create("application/json".toMediaTypeOrNull(), user_dtime)
        val type = RequestBody.create("application/json".toMediaTypeOrNull(), repoType)

        val hierarchyInfo: MutableLiveData<Resource<HierarchyResponse>> =
            MutableLiveData<Resource<HierarchyResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getUserHierarchy(username, dateTime, type)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        hierarchyInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        hierarchyInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return hierarchyInfo
    }

    //Check Sub ordinate
    fun checkSubordinate(
        user_id: String,
        user_dtime: String,
        repoType: String
    ): MutableLiveData<Resource<Subordinate>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val dateTime = RequestBody.create("application/json".toMediaTypeOrNull(), user_dtime)
        val type = RequestBody.create("application/json".toMediaTypeOrNull(), repoType)

        val subordinate: MutableLiveData<Resource<Subordinate>> =
            MutableLiveData<Resource<Subordinate>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.checkSubordinate(username, dateTime, type)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        subordinate.postValue(Resource.success(response))
                    },
                    { error ->
                        subordinate.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return subordinate
    }

    //save leave application

    fun saveLeaveApplication(
        user_id: String,
        lv_date: String,
        lv_fdate: String,
        lv_tdate: String,
        lv_code: String,
        lv_purpose: String,
        lv_add: String,
        lv_phone: String,
        lv_ap_emp: String,
        lv_not_to: String,
        allImages: ArrayList<File>
    ): MutableLiveData<Resource<LeaveSaveResponse>> {
        val userCode = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val lvDate = RequestBody.create("application/json".toMediaTypeOrNull(), lv_date)
        val from = RequestBody.create("application/json".toMediaTypeOrNull(), lv_fdate)
        val to = RequestBody.create("application/json".toMediaTypeOrNull(), lv_tdate)
        val lvCode = RequestBody.create("application/json".toMediaTypeOrNull(), lv_code)
        val lvPurpose = RequestBody.create("application/json".toMediaTypeOrNull(), lv_purpose)
        val adress = RequestBody.create("application/json".toMediaTypeOrNull(), lv_add)
        val phone = RequestBody.create("application/json".toMediaTypeOrNull(), lv_phone)
        val alternateBoss = RequestBody.create("application/json".toMediaTypeOrNull(), lv_ap_emp)
        val notification_to = RequestBody.create("application/json".toMediaTypeOrNull(), lv_not_to)

        val processImage: ArrayList<MultipartBody.Part> = ArrayList()
        var requestFile: RequestBody
        if (allImages.size > 0) {
            for (item in allImages) {
                requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), item.absoluteFile)
                processImage.add(
                    MultipartBody.Part.createFormData(
                        "all_images",
                        item.name,
                        requestFile
                    )
                )
            }
        } else {
            requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), "")
            processImage.add(MultipartBody.Part.createFormData("all_images", "", requestFile))

        }

        val saveResponse: MutableLiveData<Resource<LeaveSaveResponse>> =
            MutableLiveData<Resource<LeaveSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveLeaveApplication(
                userCode,
                lvDate,
                from,
                to,
                lvCode,
                lvPurpose,
                adress,
                phone,
                alternateBoss,
                notification_to,
                processImage
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveResponse.postValue(Resource.success(response))
                    },
                    { error ->
                        saveResponse.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return saveResponse
    }


    //Check Sub ordinate
    fun leaveApprovalList(
        user_id: String

    ): MutableLiveData<Resource<LeaveApproveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val approvalList: MutableLiveData<Resource<LeaveApproveResponse>> =
            MutableLiveData<Resource<LeaveApproveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getLeaveApprovalList(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approvalList.postValue(Resource.success(response))
                    },
                    { error ->
                        approvalList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return approvalList
    }

    //Leave Approve Save

    //Check Sub ordinate
    fun leaveApproveSave(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_fdate: String,
        lv_tdate: String

    ): MutableLiveData<Resource<LeaveConfirmResponse>> {
        //val rowNo = RequestBody.create("application/json".toMediaTypeOrNull(), lv_ap_row)
        val username = user_id.toRequestBody("application/json".toMediaTypeOrNull())
        val tranNo = lv_trn_no.toRequestBody("application/json".toMediaTypeOrNull())
        val rowNo = lv_ap_row.toRequestBody("application/json".toMediaTypeOrNull())
        val fromDate = lv_fdate.toRequestBody("application/json".toMediaTypeOrNull())
        val toDate = lv_tdate.toRequestBody("application/json".toMediaTypeOrNull())

        val approveConfirm: MutableLiveData<Resource<LeaveConfirmResponse>> =
            MutableLiveData<Resource<LeaveConfirmResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.approveLeaveApplication(username, tranNo, rowNo, fromDate, toDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approveConfirm.postValue(Resource.success(response))
                    },
                    { error ->
                        approveConfirm.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return approveConfirm
    }


    fun rejectLeaveApplication(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_deny_res: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val username = user_id.toRequestBody("application/json".toMediaTypeOrNull())
        val tranNo = lv_trn_no.toRequestBody("application/json".toMediaTypeOrNull())
        val rowNo = lv_ap_row.toRequestBody("application/json".toMediaTypeOrNull())
        val denyReason = lv_deny_res.toRequestBody("application/json".toMediaTypeOrNull())


        val rejectLeaveApp: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.rejectLeaveApplication(username, tranNo, rowNo, denyReason)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        rejectLeaveApp.postValue(Resource.success(response))
                    },
                    { error ->
                        rejectLeaveApp.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LeaveRepository", "ERROR: " + e.message)
        }
        return rejectLeaveApp
    }


    //--------------------------------IMAGE UPLOAD-------------------------------------------------

    fun uploadMultipleImages(
        allImages: ArrayList<File>,
        user_id: String,
        xlvtrno: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val intro = RequestBody.create("application/json".toMediaTypeOrNull(), xlvtrno)

        val processImage: ArrayList<MultipartBody.Part> = ArrayList()
        //---------------------------------------------------------------------------------
        for (item in allImages) {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), item.absoluteFile)
            processImage.add(
                MultipartBody.Part.createFormData(
                    "all_images",
                    item.name,
                    requestFile
                )
            )

        }
        //---------------------------------------------------------------------------------
//        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), allImages[])
//
//        val userImage: MultipartBody.Part =
//            MultipartBody.Part.createFormData("all_images", allImages.get().name, requestFile)


        val multipleImages: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.multyImageEntry(
                processImage,
                userId,
                intro
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        multipleImages.postValue(Resource.success(response))
                    },
                    { error ->
                        multipleImages.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("AttendanceRepo", "ERROR Attendance: " + e.message)
        }
        return multipleImages
    }


}