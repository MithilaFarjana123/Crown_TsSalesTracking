package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.LateAppliedResponse
import com.crowncement.TsSalesTracking.data.Model.LateApproveResponse
import com.crowncement.TsSalesTracking.data.Model.LateResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object LateRepository {

    fun getLateList(
        user_id: String,
        user_dtime: String
    ): MutableLiveData<Resource<LateResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val lateDate = RequestBody.create("application/json".toMediaTypeOrNull(), user_dtime)
        val lateRes: MutableLiveData<Resource<LateResponse>> =
            MutableLiveData<Resource<LateResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userLateListForApply(username, lateDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        lateRes.postValue(Resource.success(response))
                    },
                    { error ->
                        lateRes.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LateRepository", "ERROR: " + e.message)
        }
        return lateRes
    }


    fun getAppliedLateList(
        user_id: String,
        user_dtime: String
    ): MutableLiveData<Resource<LateAppliedResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val lateDate = RequestBody.create("application/json".toMediaTypeOrNull(), user_dtime)
        val appliedLateRes: MutableLiveData<Resource<LateAppliedResponse>> =
            MutableLiveData<Resource<LateAppliedResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userAppliedLateList(username, lateDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appliedLateRes.postValue(Resource.success(response))
                    },
                    { error ->
                        appliedLateRes.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("AppliedLateRepository", "ERROR: " + e.message)
        }
        return appliedLateRes
    }


    fun lateApplicationEntry(
        user_id: String,
        lt_date: String,
        lt_iss_date: String,
        lt_purpose: String,
        lt_rem: String,
        lv_not_to: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val lateDate = RequestBody.create("application/json".toMediaTypeOrNull(), lt_date)
        val appliedDate = RequestBody.create("application/json".toMediaTypeOrNull(), lt_iss_date)
        val purpose = RequestBody.create("application/json".toMediaTypeOrNull(), lt_purpose)
        val reason = RequestBody.create("application/json".toMediaTypeOrNull(), lt_rem)
        val notificationTo = RequestBody.create("application/json".toMediaTypeOrNull(), lv_not_to)
        val lateEntry: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.lateEntry(username, lateDate, appliedDate, purpose, reason, notificationTo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        lateEntry.postValue(Resource.success(response))
                    },
                    { error ->
                        lateEntry.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LateRepository", "ERROR: " + e.message)
        }
        return lateEntry
    }


    fun lateApprovalList(
        user_id: String
    ): MutableLiveData<Resource<LateApproveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val approveList: MutableLiveData<Resource<LateApproveResponse>> =
            MutableLiveData<Resource<LateApproveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.lateApprovalList(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approveList.postValue(Resource.success(response))
                    },
                    { error ->
                        approveList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LateRepository", "ERROR: " + e.message)
        }
        return approveList
    }


    fun lateApproval(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val tranNo = RequestBody.create("application/json".toMediaTypeOrNull(), lt_trn_no)
        val appRow = RequestBody.create("application/json".toMediaTypeOrNull(), lt_ap_row)
        val ltDate = RequestBody.create("application/json".toMediaTypeOrNull(), lt_date)
        val lateApprove: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.lateApproval(username,tranNo,appRow,ltDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        lateApprove.postValue(Resource.success(response))
                    },
                    { error ->
                        lateApprove.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LateRepository", "ERROR: " + e.message)
        }
        return lateApprove
    }


    fun lateReject(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_deny_res: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val tranNo = RequestBody.create("application/json".toMediaTypeOrNull(), lt_trn_no)
        val appRow = RequestBody.create("application/json".toMediaTypeOrNull(), lt_ap_row)
        val denyReason = RequestBody.create("application/json".toMediaTypeOrNull(), lt_deny_res)
        val lateApprove: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.lateReject(username,tranNo,appRow,denyReason)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        lateApprove.postValue(Resource.success(response))
                    },
                    { error ->
                        lateApprove.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LateRepository", "ERROR: " + e.message)
        }
        return lateApprove
    }
}