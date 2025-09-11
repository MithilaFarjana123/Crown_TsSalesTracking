package com.crowncement.TsSalesTracking.data.Repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.CarReqEntListResponce
import com.crowncement.TsSalesTracking.data.Model.CarReqSaveresponce
import com.crowncement.TsSalesTracking.data.Model.DepartmentResponce
import com.crowncement.TsSalesTracking.data.Model.MeetRoomBKListResponce
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomBookInfoResponce
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomListResponce
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object MeetingRepository {

    //TODO getDepartment
    fun getSavedDepartment(): MutableLiveData<Resource<DepartmentResponce>> {

        val appInfo: MutableLiveData<Resource<DepartmentResponce>> =
            MutableLiveData<Resource<DepartmentResponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceMetting.create(
                GetDataService::class.java
            )
            service.savedDepartment()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }


    //TODO saveMeetingData
    fun SaveMeetingData(
        user_id: String,
        room_info: String

    ): MutableLiveData<Resource<MeetingRoomBookInfoResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val room_info = RequestBody.create("application/json".toMediaTypeOrNull(), room_info)


        val appInfo: MutableLiveData<Resource<MeetingRoomBookInfoResponce>> =
            MutableLiveData<Resource<MeetingRoomBookInfoResponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveMeetingRoomBooking(user_id,room_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }


    //todo get meetingList

    fun getMeetingList(): MutableLiveData<Resource<MeetingRoomListResponce>> {

        val appInfo: MutableLiveData<Resource<MeetingRoomListResponce>> =
            MutableLiveData<Resource<MeetingRoomListResponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getMeetingList()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }



    //todo MeetRoomBKList
    fun getMeetRoomBKList(
        user_id: String,
        from_date: String,
        to_date: String
    ): MutableLiveData<Resource<MeetRoomBKListResponce>> {


        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)



        val appInfo: MutableLiveData<Resource<MeetRoomBKListResponce>> =
            MutableLiveData<Resource<MeetRoomBKListResponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getMeetRoomBKList(user_id,from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }


    //todo saveCarReqSave
    fun saveCarReqSave(
        user_id: String,
        req_info: String
    ): MutableLiveData<Resource<CarReqSaveresponce>> {


        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val req_info = RequestBody.create("application/json".toMediaTypeOrNull(), req_info)



        val appInfo: MutableLiveData<Resource<CarReqSaveresponce>> =
            MutableLiveData<Resource<CarReqSaveresponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveCarReqSave(user_id,req_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }


    //todo CarReqEntList
    @SuppressLint("CheckResult")
    fun CarReqEntList(
        emp_code: String,
        from_date: String,
        to_date: String
    ): MutableLiveData<Resource<CarReqEntListResponce>> {


        val emp_code = RequestBody.create("application/json".toMediaTypeOrNull(), emp_code)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)



        val appInfo: MutableLiveData<Resource<CarReqEntListResponce>> =
            MutableLiveData<Resource<CarReqEntListResponce>>()


        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.CarReqEntList(emp_code,from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        appInfo.postValue(Resource.success(response))

                    },
                    { error ->
                        appInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("MeetingRepository", "ERROR : " + e.message)
        }
        return appInfo
    }


}