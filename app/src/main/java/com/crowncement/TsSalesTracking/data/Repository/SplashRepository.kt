package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.AppVersionResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object SplashRepository {

    fun getUserInfo(
        id: String,
        pass: String
    ): MutableLiveData<Resource<AppVersionResponse>> {
        val app_name = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val cur_date = RequestBody.create("application/json".toMediaTypeOrNull(), pass)
        val appInfo: MutableLiveData<Resource<AppVersionResponse>> =
            MutableLiveData<Resource<AppVersionResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getAppVersion(app_name, cur_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))
                    },
                    { error -> //
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return appInfo
    }

}


