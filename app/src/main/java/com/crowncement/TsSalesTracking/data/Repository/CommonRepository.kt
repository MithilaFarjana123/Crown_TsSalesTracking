package com.crowncement.TsSalesTracking.data.Repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object CommonRepository {


    fun uploadLocation(
        user_id: String,
        cap_time: String,
        cap_lat: String,
        cap_lon: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val time = RequestBody.create("application/json".toMediaTypeOrNull(), cap_time)
        val lat = RequestBody.create("application/json".toMediaTypeOrNull(), cap_lat)
        val lon = RequestBody.create("application/json".toMediaTypeOrNull(), cap_lon)

        val locationTrac: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.uploadOfflineLocationData(username, time, lat, lon)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        locationTrac.postValue(Resource.success(response))
                    },
                    { error ->
                        locationTrac.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("TourRepository", "ERROR: " + e.message)
        }
        return locationTrac
    }

    fun getAllEmployee(): MutableLiveData<Resource<EmpResponse>> {

        val empInfo: MutableLiveData<Resource<EmpResponse>> = MutableLiveData<Resource<EmpResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(GetDataService::class.java)
            service.getEmployeeList()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        empInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        empInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("CommonRepository", "ERROR: " + e.message)
        }
        return empInfo
    }

    fun sendPushMessage(userID:String,title:String,body:String,empList:String): MutableLiveData<Resource<CommonSaveResponse>> {

        val username = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val ti = RequestBody.create("application/json".toMediaTypeOrNull(), title)
        val bo = RequestBody.create("application/json".toMediaTypeOrNull(), body)
        val empL = RequestBody.create("application/json".toMediaTypeOrNull(), empList)

        val pushMessage: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(GetDataService::class.java)
            service.sendPushMessage(username,ti,bo,empL)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        pushMessage.postValue(Resource.success(response))
                    },
                    { error ->
                        pushMessage.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("CommonRepository", "ERROR: " + e.message)
        }
        return pushMessage
    }


    @SuppressLint("CheckResult")
    fun getPushMessage(userID:String): MutableLiveData<Resource<PushMessageResponse>> {

        val username = RequestBody.create("application/json".toMediaTypeOrNull(), userID)

        val pushMessage: MutableLiveData<Resource<PushMessageResponse>> = MutableLiveData<Resource<PushMessageResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(GetDataService::class.java)
            service.getPushMessage(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        pushMessage.postValue(Resource.success(response))
                    },
                    { error ->
                        pushMessage.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("CommonRepository", "ERROR: " + e.message)
        }
        return pushMessage
    }



    @SuppressLint("CheckResult")
    fun getDataByType(code:String): MutableLiveData<Resource<TypeResponse>> {

        val type = RequestBody.create("application/json".toMediaTypeOrNull(), code)

        val mType: MutableLiveData<Resource<TypeResponse>> = MutableLiveData<Resource<TypeResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getCodeType(type)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mType.postValue(Resource.success(response))
                    },
                    { error -> //
                        mType.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Collection_Repository", "Paytype: " + e.message)
        }
        return mType
    }

    @SuppressLint("CheckResult")
    fun getDataBySubType(code:String,subType:String): MutableLiveData<Resource<TypeResponse>> {

        val type = RequestBody.create("application/json".toMediaTypeOrNull(), code)
        val type1 = RequestBody.create("application/json".toMediaTypeOrNull(), subType)

        val mType: MutableLiveData<Resource<TypeResponse>> = MutableLiveData<Resource<TypeResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getCodeSubType(type,type1)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mType.postValue(Resource.success(response))
                    },
                    { error -> //
                        mType.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Common_Repo", "getDataBySubType: " + e.message)
        }
        return mType
    }

    @SuppressLint("CheckResult")
    fun getHolidayList(emp:String, year:String, month:String): MutableLiveData<Resource<HolidayResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), emp)
        val yr = RequestBody.create("application/json".toMediaTypeOrNull(), year)
        val mo = RequestBody.create("application/json".toMediaTypeOrNull(), month)

        val holiday: MutableLiveData<Resource<HolidayResponse>> = MutableLiveData<Resource<HolidayResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getHolidayList(user,yr,mo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        holiday.postValue(Resource.success(response))
                    },
                    { error -> //
                        holiday.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Common_Repository", "Holiday: " + e.message)
        }
        return holiday
    }
}