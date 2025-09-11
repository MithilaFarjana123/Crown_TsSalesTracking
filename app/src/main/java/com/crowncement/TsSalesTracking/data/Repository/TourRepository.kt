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
import java.io.File

object TourRepository {

    fun getToutList(
        user_id: String,
        dateTime: String
    ): MutableLiveData<Resource<TourResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val dateT = RequestBody.create("application/json".toMediaTypeOrNull(), dateTime)


        val tourInfo: MutableLiveData<Resource<TourResponse>> =
            MutableLiveData<Resource<TourResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getTourList(username, dateT)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        tourInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        tourInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return tourInfo
    }

    fun saveTour(
        user_id: String,
        tr_fdate: String,
        tr_tdate: String,
        tr_iss_date: String,
        tr_purpose: String,
        tr_rem: String,
        tr_not_to: String,
        country: String,
        address: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val fDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_fdate)
        val tDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_tdate)
        val aDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_iss_date)
        val pur = RequestBody.create("application/json".toMediaTypeOrNull(), tr_purpose)
        val re = RequestBody.create("application/json".toMediaTypeOrNull(), tr_rem)
        val noti = RequestBody.create("application/json".toMediaTypeOrNull(), tr_not_to)
        val con = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val add = RequestBody.create("application/json".toMediaTypeOrNull(), address)


        val tourSave: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveTour(username, fDate, tDate, aDate, pur, re, noti, con, add)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        tourSave.postValue(Resource.success(response))
                    },
                    { error ->
                        tourSave.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return tourSave
    }


    fun tourApproveList(
        user_id: String
    ): MutableLiveData<Resource<TourApproveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val approveList: MutableLiveData<Resource<TourApproveResponse>> =
            MutableLiveData<Resource<TourApproveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getTourApproveList(username)
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
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return approveList
    }


    fun tourApprove(
        user_id: String,
        tr_trn_no: String,
        tr_ap_row: String,
        tr_fdate: String,
        tr_tdate: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val trnNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)
        val appRow = RequestBody.create("application/json".toMediaTypeOrNull(), tr_ap_row)
        val fDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_fdate)
        val tDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_tdate)

        val tourApprove: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java

            )
            service.tourApprove(username, trnNo, appRow, fDate, tDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        tourApprove.postValue(Resource.success(response))
                    },
                    { error ->
                        tourApprove.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return tourApprove
    }

    fun tourReject(
        user_id: String,
        tr_trn_no: String,
        tr_ap_row: String,
        tr_deny_res: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val trnNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)
        val appRow = RequestBody.create("application/json".toMediaTypeOrNull(), tr_ap_row)
        val denyReason = RequestBody.create("application/json".toMediaTypeOrNull(), tr_deny_res)

        val tourDeny: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.tourReject(username, trnNo, appRow, denyReason)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        tourDeny.postValue(Resource.success(response))
                    },
                    { error ->
                        tourDeny.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return tourDeny
    }


    fun startTour(
        user_id: String,
        tr_trn_no: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val travelNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)

        val stTour: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.startTour(username, travelNo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        stTour.postValue(Resource.success(response))
                    },
                    { error ->
                        stTour.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return stTour
    }


    fun stopTour(
        user_id: String,
        tr_trn_no: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val travelNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)

        val stTour: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.stopTour(username, travelNo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        stTour.postValue(Resource.success(response))
                    },
                    { error ->
                        stTour.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return stTour
    }


    fun commonExpenseClaim(
        user_id: String,
        tr_trn_no: String,
        tr_date: String,
        tr_exp_type: String,
        tr_exp_sub: String,
        tr_exp_source: String,
        tr_frm_loc: String,
        tr_to_loc: String,
        tr_pay_mod: String,
        tr_amt: String,
        tr_rem: String,
        extension: String,
        allImages: ArrayList<File>

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val travelNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)
        val trDate = RequestBody.create("application/json".toMediaTypeOrNull(), tr_date)
        val trExpType = RequestBody.create("application/json".toMediaTypeOrNull(), tr_exp_type)
        val vehicle = RequestBody.create("application/json".toMediaTypeOrNull(), tr_exp_sub)
        val source = RequestBody.create("application/json".toMediaTypeOrNull(), tr_exp_source)
        val fromLoc = RequestBody.create("application/json".toMediaTypeOrNull(), tr_frm_loc)
        val toLoc = RequestBody.create("application/json".toMediaTypeOrNull(), tr_to_loc)
        val paymentMode = RequestBody.create("application/json".toMediaTypeOrNull(), tr_pay_mod)
        val amount = RequestBody.create("application/json".toMediaTypeOrNull(), tr_amt)
        val remarks = RequestBody.create("application/json".toMediaTypeOrNull(), tr_rem)
        val ext = RequestBody.create("application/json".toMediaTypeOrNull(), extension)

//        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(),         all_images   )
//        val attachedFile: MultipartBody.Part = MultipartBody.Part.createFormData("all_images", "", requestFile)

        val processImage: ArrayList<MultipartBody.Part> = ArrayList()
        var requestFile: RequestBody
        if (allImages.size > 0) {
            for (item in allImages) {
                // requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), item.absoluteFile)
                requestFile = if (extension != "pdf") {
                    RequestBody.create("image/*".toMediaTypeOrNull(), allImages[0])
                } else {
                    RequestBody.create("application/pdf".toMediaTypeOrNull(), allImages[0])
                }

                processImage.add(
                    MultipartBody.Part.createFormData(
                        "all_images",
                        item.name,
                        requestFile
                    )
                )
            }
        } else {

            requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), "")
            //  requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), "")
            processImage.add(MultipartBody.Part.createFormData("all_images", "", requestFile))
        }

        val saveCommonExpense: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveCommonExpenseClaim(
                username,
                travelNo,
                trDate,
                trExpType,
                vehicle,
                source,
                fromLoc,
                toLoc,
                paymentMode,
                amount,
                remarks,
                //  attachedFile
                ext,
                processImage[0]

            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCommonExpense.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCommonExpense.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return saveCommonExpense
    }


    fun getExpenseClaimList(
        user_id: String,
        tr_trn_no: String,
        tr_date: String,
    ): MutableLiveData<Resource<ExpenseClaimResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val travelNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)
        val date = RequestBody.create("application/json".toMediaTypeOrNull(), tr_date)

        val claimList: MutableLiveData<Resource<ExpenseClaimResponse>> =
            MutableLiveData<Resource<ExpenseClaimResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getExpenseList(username, travelNo, date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        claimList.postValue(Resource.success(response))
                    },
                    { error ->
                        claimList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return claimList
    }

    fun stopExpenseEntry(
        user_id: String,
        tr_trn_no: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val travelNo = RequestBody.create("application/json".toMediaTypeOrNull(), tr_trn_no)

        val stTour: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.toruExpenseStop(username, travelNo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        stTour.postValue(Resource.success(response))
                    },
                    { error ->
                        stTour.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return stTour
    }

    fun getTourBill(
        user_id: String,
        tr_fdate: String,
        tr_tdate: String
    ): MutableLiveData<Resource<TourExpenseResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val from = RequestBody.create("application/json".toMediaTypeOrNull(), tr_fdate)
        val to = RequestBody.create("application/json".toMediaTypeOrNull(), tr_tdate)
        val tourBill: MutableLiveData<Resource<TourExpenseResponse>> =
            MutableLiveData<Resource<TourExpenseResponse>>()
        try {
            val service: GetDataService =
                RetrofitClientInstance.retrofitInstance.create(GetDataService::class.java)
            service.getTourBillList(username, from, to)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        tourBill.postValue(Resource.success(response))
                    },
                    { error ->
                        tourBill.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("tourBill", "ERROR: " + e.message)
        }
        return tourBill
    }
}