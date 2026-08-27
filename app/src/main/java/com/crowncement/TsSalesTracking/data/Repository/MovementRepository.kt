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
import okhttp3.RequestBody

object MovementRepository {

    fun getEmpRouteList(
        user_id: String

    ): MutableLiveData<Resource<MovRouteResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val routeInfo: MutableLiveData<Resource<MovRouteResponse>> = MutableLiveData<Resource<MovRouteResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getRouteForMovement(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        routeInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        routeInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "ERROR: " + e.message)
        }
        return routeInfo
    }

    fun saveMovementPlan(
        user_id: String,
        mDate: String,
        routes: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val date = RequestBody.create("application/json".toMediaTypeOrNull(), mDate)
        val route = RequestBody.create("application/json".toMediaTypeOrNull(), routes)


        val saveMovement: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveMovementPlan(username,date,route)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveMovement.postValue(Resource.success(response))
                    },
                    { error ->
                        saveMovement.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "saveMovement ERROR: " + e.message)
        }
        return saveMovement
    }

    fun movementPlanList(
        user_id: String,
        mYear: String,
        mDate: String

    ): MutableLiveData<Resource<MovementPlanResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val year = RequestBody.create("application/json".toMediaTypeOrNull(), mYear)
        val date = RequestBody.create("application/json".toMediaTypeOrNull(), mDate)

        val movementList: MutableLiveData<Resource<MovementPlanResponse>> = MutableLiveData<Resource<MovementPlanResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.movementPlanList(username,year,date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        movementList.postValue(Resource.success(response))
                    },
                    { error ->
                        movementList.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "movementList ERROR: " + e.message)
        }
        return movementList
    }


    //todo individual employee route
    fun getTSMovLocationList(
        user_id: String

    ): MutableLiveData<Resource<GetTSMovLocationResponce>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)

        val movementList: MutableLiveData<Resource<GetTSMovLocationResponce>> = MutableLiveData<Resource<GetTSMovLocationResponce>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTSSite.create(
                GetDataService::class.java
            )
            service.GetTSMovLocation(username)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        movementList.postValue(Resource.success(response))
                    },
                    { error ->
                        movementList.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "movementList ERROR: " + e.message)
        }
        return movementList
    }

    fun movementAcPlanList(
        user_id: String,
        mYear: String,
        mDate: String

    ): MutableLiveData<Resource<MovementPlanResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val year = RequestBody.create("application/json".toMediaTypeOrNull(), mYear)
        val date = RequestBody.create("application/json".toMediaTypeOrNull(), mDate)

        val movementList: MutableLiveData<Resource<MovementPlanResponse>> = MutableLiveData<Resource<MovementPlanResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.movementActualPlanList(username,year,date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        movementList.postValue(Resource.success(response))
                    },
                    { error ->
                        movementList.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "movementAcList ERROR: " + e.message)
        }
        return movementList
    }


    fun saveActualMovementPlan(
        user_id: String,
        mDate: String,
        routes: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val date = RequestBody.create("application/json".toMediaTypeOrNull(), mDate)
        val route = RequestBody.create("application/json".toMediaTypeOrNull(), routes)

        val saveMovement: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveActualMovementPlan(username,date,route)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveMovement.postValue(Resource.success(response))
                    },
                    { error ->
                        saveMovement.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "saveMovement ERROR: " + e.message)
        }
        return saveMovement
    }


    fun getZonalJuniorList(
        user_id: String,
        dateTime: String

    ): MutableLiveData<Resource<EmpResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val dateT = RequestBody.create("application/json".toMediaTypeOrNull(), dateTime)


        val getEmp: MutableLiveData<Resource<EmpResponse>> = MutableLiveData<Resource<EmpResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.zonalJuniorList(username,dateT)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        getEmp.postValue(Resource.success(response))
                    },
                    { error ->
                        getEmp.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "Zonal Junior ERROR: " + e.message)
        }
        return getEmp
    }


    fun submitMovementMonthlyPlan(
        user_id: String,
        year: String,
        month: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val yearT = RequestBody.create("application/json".toMediaTypeOrNull(), year)
        val monthT = RequestBody.create("application/json".toMediaTypeOrNull(), month)


        val submitMovement: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.submitMovementPlan(username,yearT,monthT)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        submitMovement.postValue(Resource.success(response))
                    },
                    { error ->
                        submitMovement.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "submit monthly plan ERROR: " + e.message)
        }
        return submitMovement
    }

    fun approveMovementMonthlyPlan(
        user_id: String,
        movementNo: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val movNo = RequestBody.create("application/json".toMediaTypeOrNull(), movementNo)

        val approveMovement: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.approveMovementPlan(username,movNo)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approveMovement.postValue(Resource.success(response))
                    },
                    { error ->
                        approveMovement.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "Approve monthly plan ERROR: " + e.message)
        }
        return approveMovement
    }


    fun getActualPlanList(
        user_id: String,
        date: String

    ): MutableLiveData<Resource<ActualPlnResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val mDate = RequestBody.create("application/json".toMediaTypeOrNull(), date)

        val planL: MutableLiveData<Resource<ActualPlnResponse>> = MutableLiveData<Resource<ActualPlnResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getActualPlan(username,mDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        planL.postValue(Resource.success(response))
                    },
                    { error ->
                        planL.postValue(Resource.error(error.message.toString(), null))
                    },
                )
        } catch (e: Exception) {
            Log.e("MovementRepository", "Actual Move Plan: " + e.message)
        }
        return planL
    }
}