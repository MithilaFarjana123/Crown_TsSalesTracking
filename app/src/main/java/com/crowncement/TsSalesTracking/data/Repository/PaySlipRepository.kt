package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.PaySlipResponse
import com.crowncement.TsSalesTracking.data.Model.SalaryMonthResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object PaySlipRepository {

    fun getPayslipMonthInfo(
        user_id: String
    ): MutableLiveData<Resource<SalaryMonthResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)


        val salaryMonthInfo: MutableLiveData<Resource<SalaryMonthResponse>> =
            MutableLiveData<Resource<SalaryMonthResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getSalaryMonth(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        salaryMonthInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        salaryMonthInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("PaySlipRepository", "ERROR: " + e.message)
        }
        return salaryMonthInfo
    }

    fun getPayslipInfo(
        user_id: String,
        payCode: String
    ): MutableLiveData<Resource<PaySlipResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val payC = RequestBody.create("application/json".toMediaTypeOrNull(), payCode)

        val salaryInfo: MutableLiveData<Resource<PaySlipResponse>> =
            MutableLiveData<Resource<PaySlipResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getSalaryInfo(username,payC)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        salaryInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        salaryInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("PaySlipRepository", "ERROR: " + e.message)
        }
        return salaryInfo
    }
}