package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData

import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.CallRecordSpinnerResponse
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.SalesVisitSummaryResponse
import com.crowncement.TsSalesTracking.data.Model.SearchByMobile
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object SalesVisitRepository {



    fun getSalesSummaryList(
        id: String,
        date: String
    ): MutableLiveData<Resource<SalesVisitSummaryResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val moveDate = RequestBody.create("application/json".toMediaTypeOrNull(), date)

        val salesSummary: MutableLiveData<Resource<SalesVisitSummaryResponse>> = MutableLiveData<Resource<SalesVisitSummaryResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.salesVisitSummaryList(user, moveDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        salesSummary.postValue(Resource.success(response))
                    },
                    { error ->
                        salesSummary.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "ERROR login: " + e.message)
        }
        return salesSummary
    }


    fun salesSummarySave(
        id: String,
        customerCode: String,
        profileInfo: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val code = RequestBody.create("application/json".toMediaTypeOrNull(), customerCode)
        val pro = RequestBody.create("application/json".toMediaTypeOrNull(), profileInfo)

        val salesSummarySave: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.salesVisitSummarySave(user, code,pro)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        salesSummarySave.postValue(Resource.success(response))
                    },
                    { error ->
                        salesSummarySave.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "ERROR login: " + e.message)
        }
        return salesSummarySave
    }


    fun getDiscussionTopic(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {

        val getDisTopic: MutableLiveData<Resource<CallRecordSpinnerResponse>> = MutableLiveData<Resource<CallRecordSpinnerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getCallRecordDiscussionTopic()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        getDisTopic.postValue(Resource.success(response))
                    },
                    { error ->
                        getDisTopic.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "discussion topic " + e.message)
        }
        return getDisTopic
    }

    fun getNoReasonTopic(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {

        val noOrderReason: MutableLiveData<Resource<CallRecordSpinnerResponse>> = MutableLiveData<Resource<CallRecordSpinnerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getNoOrderReason()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        noOrderReason.postValue(Resource.success(response))
                    },
                    { error ->
                        noOrderReason.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "noOrderReason" + e.message)
        }
        return noOrderReason
    }

    fun getAllCement(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {

        val allCement: MutableLiveData<Resource<CallRecordSpinnerResponse>> = MutableLiveData<Resource<CallRecordSpinnerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getAllCementBrand()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        allCement.postValue(Resource.success(response))
                    },
                    { error ->
                        allCement.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "allCement" + e.message)
        }
        return allCement
    }

    fun saveCallRecord(
        id: String,
        moveDoc: String,
        moveRow: String,
        callRow: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val move_doc = RequestBody.create("application/json".toMediaTypeOrNull(), moveDoc)
        val row = RequestBody.create("application/json".toMediaTypeOrNull(), moveRow)
        val data = RequestBody.create("application/json".toMediaTypeOrNull(), callRow)

        val callRecord: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveCallRecord(user, move_doc,row,data)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callRecord.postValue(Resource.success(response))
                    },
                    { error ->
                        callRecord.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "callRecord: " + e.message)
        }
        return callRecord
    }

    fun saveCallRecordHome(
        id: String,
        retailedId: String,
        callRow: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val customerId = RequestBody.create("application/json".toMediaTypeOrNull(), retailedId)
        val data = RequestBody.create("application/json".toMediaTypeOrNull(), callRow)

        val callRecordHome: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveCallRecordHome(user, customerId,data)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callRecordHome.postValue(Resource.success(response))
                    },
                    { error ->
                        callRecordHome.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "callRecordHome: " + e.message)
        }
        return callRecordHome
    }


    //todo SearchByMobile

    fun getSearchByMobile(
        mob_no: String
    ): MutableLiveData<Resource<SearchByMobile>> {

        val mob_no = RequestBody.create("application/json".toMediaTypeOrNull(), mob_no)

        val salesSummary: MutableLiveData<Resource<SearchByMobile>> = MutableLiveData<Resource<SearchByMobile>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.SearchByMobile(mob_no)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        salesSummary.postValue(Resource.success(response))
                    },
                    { error ->
                        salesSummary.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales Visit repository", "ERROR login: " + e.message)
        }
        return salesSummary
    }


}