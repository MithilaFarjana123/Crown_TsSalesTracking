package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.DashboardResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object DashboardRepository {

    fun getBannerIMages(
        app_project: String
    ): MutableLiveData<Resource<DashboardResponse>> {
        val proj = RequestBody.create("application/json".toMediaTypeOrNull(), app_project)

        val dashboardImg: MutableLiveData<Resource<DashboardResponse>> = MutableLiveData<Resource<DashboardResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getDashboardImage(proj)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        dashboardImg.postValue(Resource.success(response))
                    },
                    { error ->
                        dashboardImg.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("dashboardImg", "ERROR: " + e.message)
        }
        return dashboardImg
    }

}