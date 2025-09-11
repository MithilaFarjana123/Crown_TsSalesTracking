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

object AttendanceRepository {


    fun getAttendanceInfo(
        id: String,
        date_time: String
    ): MutableLiveData<Resource<Attendance>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val dateTime = RequestBody.create("application/json".toMediaTypeOrNull(), date_time)
        val attInfo: MutableLiveData<Resource<Attendance>> = MutableLiveData<Resource<Attendance>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userAttendanceInfo(username, dateTime)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        attInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return attInfo
    }

    fun getAttendanceList(
        id: String,
        att_fdate: String,
        att_tdate: String
    ): MutableLiveData<Resource<AttendanceReport>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val fdate = RequestBody.create("application/json".toMediaTypeOrNull(), att_fdate)
        val tdate = RequestBody.create("application/json".toMediaTypeOrNull(), att_tdate)

        val attInfo: MutableLiveData<Resource<AttendanceReport>> =
            MutableLiveData<Resource<AttendanceReport>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.userAttendanceList(userId, fdate, tdate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        attInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return attInfo
    }


    fun getAttApproveList(
        id: String,
        att_date: String
    ): MutableLiveData<Resource<AttApproveResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val attDate = RequestBody.create("application/json".toMediaTypeOrNull(), att_date)

        val attInfo: MutableLiveData<Resource<AttApproveResponse>> =
            MutableLiveData<Resource<AttApproveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getAttApproveList(userId, attDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        attInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Attendance", "ERROR Att approve list: " + e.message)
        }
        return attInfo
    }

    fun salesPerAttendanceApproval(
        id: String,
        empID: String,
        att_date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val empId = RequestBody.create("application/json".toMediaTypeOrNull(), empID)
        val attDate = RequestBody.create("application/json".toMediaTypeOrNull(), att_date)

        val approve: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.approveSalesPersonAttendance(userId,empId, attDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Att Approve", "ERROR Att approve: " + e.message)
        }
        return approve
    }

    //todo attendence absent
    fun salesPerAttendanceAbsent(
        userid: String,
        empID: String,
        attDate : String,
        att_shftno : String,
        att_shift : String,
        txtAttAbsentReason  :String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), userid)
        val empId = RequestBody.create("application/json".toMediaTypeOrNull(), empID)
        val attDate = RequestBody.create("application/json".toMediaTypeOrNull(), attDate)
        val att_shftno = RequestBody.create("application/json".toMediaTypeOrNull(), att_shftno)
        val att_shift = RequestBody.create("application/json".toMediaTypeOrNull(), att_shift)
        val txtAttAbsentReason = RequestBody.create("application/json".toMediaTypeOrNull(), txtAttAbsentReason)


        val approve: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.absentAttendance(userId,empId, attDate,att_shftno,att_shift,txtAttAbsentReason)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Att Absent", "ERROR Att absent: " + e.message)
        }
        return approve
    }


    //todo GetEmpEntSum
    fun GetEmpEntSum(
        emp_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<GetEmpEntSumResponce>> {
        val emp_id = RequestBody.create("application/json".toMediaTypeOrNull(), emp_id)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)


        val approve: MutableLiveData<Resource<GetEmpEntSumResponce>> = MutableLiveData<Resource<GetEmpEntSumResponce>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetEmpEntSum(emp_id, from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("EmpEntSum", "ERROR Att absent: " + e.message)
        }
        return approve
    }


    //todo GetEngrEntSum
    fun GetEngrEntSum(
        engr_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<EngWorkSum>> {
        val engr_id = RequestBody.create("application/json".toMediaTypeOrNull(), engr_id)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)



        val approve: MutableLiveData<Resource<EngWorkSum>> = MutableLiveData<Resource<EngWorkSum>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetEngrEntSum(engr_id, from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("EngWorkSum", "ERROR : " + e.message)
        }
        return approve
    }


    //todo GetContEntSum

    fun GetContEntSum(
        cont_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<ConWorkSum>> {
        val cont_id = RequestBody.create("application/json".toMediaTypeOrNull(), cont_id)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)



        val approve: MutableLiveData<Resource<ConWorkSum>> = MutableLiveData<Resource<ConWorkSum>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetContEntSum(cont_id, from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Con WorkSum", "ERROR  : " + e.message)
        }
        return approve
    }


    //todo GetMasEntSum
    fun GetMasEntSum(
        mas_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<MasonEngConSum>> {
        val mas_id = RequestBody.create("application/json".toMediaTypeOrNull(), mas_id)
        val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
        val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)



        val approve: MutableLiveData<Resource<MasonEngConSum>> = MutableLiveData<Resource<MasonEngConSum>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetMasEntSum(mas_id, from_date,to_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        approve.postValue(Resource.success(response))
                    },
                    { error ->
                        approve.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Con WorkSum", "ERROR  : " + e.message)
        }
        return approve
    }

    /////////////////////////////////////////////////////////////////////



    fun attendanceEntry(
        user_id: String,
        att_date: String,
        att_time: String,
        shiftNo: String,
        shift: String,
        att_image: File,
        att_lat: String,
        att_lon: String,
        att_type : String
    ): MutableLiveData<Resource<AttendanceResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val attDate = RequestBody.create("application/json".toMediaTypeOrNull(), att_date)
        val atttime = RequestBody.create("application/json".toMediaTypeOrNull(), att_time)
        val shiftNumber = RequestBody.create("application/json".toMediaTypeOrNull(), shiftNo)
        val shiftName = RequestBody.create("application/json".toMediaTypeOrNull(), shift)
        val lat = RequestBody.create("application/json".toMediaTypeOrNull(), att_lat)
        val lon = RequestBody.create("application/json".toMediaTypeOrNull(), att_lon)
        val att_type = RequestBody.create("application/json".toMediaTypeOrNull(), att_type)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), att_image)

        // val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), att_image)
        val userImage: MultipartBody.Part =
            MultipartBody.Part.createFormData("att_image", att_image.name, requestFile)

        //val userImage  =  MultipartBody.Part.createFormData("Image", att_image.name, requestFile)
        //val requestFile: RequestBody = create(MediaType.parse("multipart/form-data"), file)
        // val body: Part = createFormData.createFormData("Image", file.getName(), requestBody)


        val attEntry: MutableLiveData<Resource<AttendanceResponse>> =
            MutableLiveData<Resource<AttendanceResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.attendanceEntry(
                userId,
                attDate,
                atttime,
                shiftNumber,
                shiftName,
                userImage,
                lat,
                lon,
                att_type
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attEntry.postValue(Resource.success(response))
                    },
                    { error ->
                        attEntry.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("AttendanceRepo", "ERROR Attendance: " + e.message)
        }
        return attEntry
    }

    fun attendanceUpdate(
        user_id: String,
        att_date: String,
        att_time: String,
        shiftNo: String,
        shift: String,
        att_image: File,
        att_lat: String,
        att_lon: String
    ): MutableLiveData<Resource<AttendanceResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val attDate = RequestBody.create("application/json".toMediaTypeOrNull(), att_date)
        val atttime = RequestBody.create("application/json".toMediaTypeOrNull(), att_time)
        val shiftNumber = RequestBody.create("application/json".toMediaTypeOrNull(), shiftNo)

        val lat = RequestBody.create("application/json".toMediaTypeOrNull(), att_lat)
        val lon = RequestBody.create("application/json".toMediaTypeOrNull(), att_lon)
        val shiftName = RequestBody.create("application/json".toMediaTypeOrNull(), shift)

        // val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), att_image)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), att_image)
        val userImage: MultipartBody.Part =
            MultipartBody.Part.createFormData("att_image", att_image.name, requestFile)


        val attEntry: MutableLiveData<Resource<AttendanceResponse>> =
            MutableLiveData<Resource<AttendanceResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.attendanceUpdate(
                userId,
                attDate,
                atttime,
                shiftNumber,
                shiftName,
                userImage,
                lat,
                lon
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        attEntry.postValue(Resource.success(response))
                    },
                    { error ->
                        attEntry.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("LoginRepository", "ERROR login: " + e.message)
        }
        return attEntry
    }






}