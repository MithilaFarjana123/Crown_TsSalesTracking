package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.UserInfo
import com.crowncement.TsSalesTracking.data.Model.UserPassChangeResponce

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


object LoginRepository {

    fun getUserInfo(
        id: String,
        pass: String,
        deviceId: String,
        fcmToken: String
    ): MutableLiveData<Resource<UserInfo>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val password = RequestBody.create("application/json".toMediaTypeOrNull(), pass)
        val device = RequestBody.create("application/json".toMediaTypeOrNull(), deviceId)
        val token = RequestBody.create("application/json".toMediaTypeOrNull(), fcmToken)

        val userInfo: MutableLiveData<Resource<UserInfo>> = MutableLiveData<Resource<UserInfo>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.userLogin(username, password, device, token)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        userInfo.postValue(Resource.success(response))
                    },
                    { error -> //

                        userInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return userInfo
    }


    //todo UserPassChange
    fun UserPassChange(
        user_id: String,
        device_id: String,
        old_pass: String,
        new_pass: String
    ): MutableLiveData<Resource<UserPassChangeResponce>> {
        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val device_id = RequestBody.create("application/json".toMediaTypeOrNull(), device_id)
        val old_pass = RequestBody.create("application/json".toMediaTypeOrNull(), old_pass)
        val new_pass = RequestBody.create("application/json".toMediaTypeOrNull(), new_pass)

        val userInfo: MutableLiveData<Resource<UserPassChangeResponce>> = MutableLiveData<Resource<UserPassChangeResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.UserPassChange(user_id, device_id, old_pass, new_pass)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        userInfo.postValue(Resource.success(response))
                    },
                    { error -> //

                        userInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return userInfo
    }

}