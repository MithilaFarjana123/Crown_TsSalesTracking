package com.crowncement.TsSalesTracking.data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.MealAllocationResponse
import com.crowncement.TsSalesTracking.data.Model.MealCostResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object MealRepository {

    fun getMealAllocationStatus(
        id: String,
        cur_date: String
    ): MutableLiveData<Resource<MealAllocationResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val currentDate = RequestBody.create("application/json".toMediaTypeOrNull(), cur_date)

        val mealInfo: MutableLiveData<Resource<MealAllocationResponse>> =
            MutableLiveData<Resource<MealAllocationResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.checkMealAllocation(username, currentDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mealInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        mealInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MealRepository", "ERROR: " + e.message)
        }
        return mealInfo
    }

    fun confirmMealAllocation(
        id: String,
        meal_date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val mealDate = RequestBody.create("application/json".toMediaTypeOrNull(), meal_date)

        val mealConfirm: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.confirmMealAllocation(username, mealDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mealConfirm.postValue(Resource.success(response))
                    },
                    { error ->
                        mealConfirm.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MealRepository", "ERROR: " + e.message)
        }
        return mealConfirm
    }

    fun cancelMealAllocation(
        id: String,
        meal_date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val mealDate = RequestBody.create("application/json".toMediaTypeOrNull(), meal_date)

        val mealConfirm: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.cancelMealAllocation(username, mealDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mealConfirm.postValue(Resource.success(response))
                    },
                    { error ->
                        mealConfirm.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MealRepository", "ERROR: " + e.message)
        }
        return mealConfirm
    }

    fun mealCost(
        id: String,
        fDate: String,
        tDate: String
    ): MutableLiveData<Resource<MealCostResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val f_Date = RequestBody.create("application/json".toMediaTypeOrNull(), fDate)
        val t_Date = RequestBody.create("application/json".toMediaTypeOrNull(), tDate)

        val mealConfirm: MutableLiveData<Resource<MealCostResponse>> =
            MutableLiveData<Resource<MealCostResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.mealCost(username, f_Date,t_Date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        mealConfirm.postValue(Resource.success(response))
                    },
                    { error ->
                        mealConfirm.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MealRepository", "ERROR: " + e.message)
        }
        return mealConfirm
    }
}