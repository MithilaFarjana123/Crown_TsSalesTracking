package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.AttendanceRepository
import java.io.File

class AttendanceViewModel : ViewModel(){

    private var attMutableLiveData: MutableLiveData<Resource<Attendance>>? = null
    private var attReportMutableLiveData: MutableLiveData<Resource<AttendanceReport>>? = null
    private var attApproveListMutableLiveData: MutableLiveData<Resource<AttApproveResponse>>? = null
    private var attApproveMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var attendanceAbsentMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var attEntryMutableLiveData: MutableLiveData<Resource<AttendanceResponse>>? = null
    private var attUpdateMutableLiveData: MutableLiveData<Resource<AttendanceResponse>>? = null
    private var getEmpEntSumMutableLiveData: MutableLiveData<Resource<GetEmpEntSumResponce>>? = null
    private var getEngWorkSumMutableLiveData: MutableLiveData<Resource<EngWorkSum>>? = null
    private var getConWorkSumMutableLiveData: MutableLiveData<Resource<ConWorkSum>>? = null
    private var getMasWorkSumMutableLiveData: MutableLiveData<Resource<MasonEngConSum>>? = null




    fun getAttendanceReportList(
        id: String,
        att_fdate: String,
        att_tdate: String
    ): MutableLiveData<Resource<AttendanceReport>> {
        attReportMutableLiveData = AttendanceRepository.getAttendanceList(id, att_fdate, att_tdate)
        return attReportMutableLiveData!!
    }

    fun getAttInfo(id: String, dateTime: String): MutableLiveData<Resource<Attendance>> {

        attMutableLiveData = AttendanceRepository.getAttendanceInfo(id, dateTime)

        return attMutableLiveData!!
    }

    fun attApproveList(
        id: String,
        date: String
    ): MutableLiveData<Resource<AttApproveResponse>> {
        attApproveListMutableLiveData = AttendanceRepository.getAttApproveList(id, date)
        return attApproveListMutableLiveData!!
    }

    fun attApprove(
        id: String,
        empID:String,
        date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        attApproveMutableLiveData = AttendanceRepository.salesPerAttendanceApproval(id,empID, date)
        return attApproveMutableLiveData!!
    }

    //todo attendence absent
    fun attAbsent(
        userid: String,
        empID: String,
        attDate : String,
        att_shftno : String,
        att_shift : String,
        txtAttAbsentReason  :String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        attendanceAbsentMutableLiveData = AttendanceRepository.salesPerAttendanceAbsent(
            userid,empID, attDate,att_shftno,att_shift,txtAttAbsentReason)
        return attendanceAbsentMutableLiveData!!
    }

    //todo GetEmpEntSum
    fun getEmpEntSum(
        emp_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<GetEmpEntSumResponce>> {
        getEmpEntSumMutableLiveData = AttendanceRepository.GetEmpEntSum(
            emp_id,from_date, to_date)
        return getEmpEntSumMutableLiveData!!
    }

    // todo GetEngrEntSum
    fun getEngrEntSum(
        engr_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<EngWorkSum>> {
        getEngWorkSumMutableLiveData = AttendanceRepository.GetEngrEntSum(
            engr_id,from_date, to_date)
        return getEngWorkSumMutableLiveData!!
    }

    //todo GetContEntSum
    fun getContEntSum(
        cont_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<ConWorkSum>> {
        getConWorkSumMutableLiveData = AttendanceRepository.GetContEntSum(
            cont_id,from_date, to_date)
        return getConWorkSumMutableLiveData!!
    }


    //todo GetMasEntSum
    fun GetMasEntSum(
        mas_id: String,
        from_date: String,
        to_date  :String
    ): MutableLiveData<Resource<MasonEngConSum>> {
        getMasWorkSumMutableLiveData = AttendanceRepository.GetMasEntSum(
            mas_id,from_date, to_date)
        return getMasWorkSumMutableLiveData!!
    }

    /////////////////////////////////////

    fun attEntry(
        id: String,
        attDate: String,
        attTime: String,
        shiftNo: String,
        shift: String,
        attImage: File,
        att_lat: String,
        att_lon: String,
        att_type : String

    ): MutableLiveData<Resource<AttendanceResponse>> {

        attEntryMutableLiveData =
            AttendanceRepository.attendanceEntry(
                id,
                attDate,
                attTime,
                shiftNo,
                shift,
                attImage,
                att_lat,
                att_lon,
                att_type
            )

        return attEntryMutableLiveData!!
    }

    fun attUpdate(
        id: String,
        attDate: String,
        attTime: String,
        shiftNo: String,
        shift: String,
        attImage: File,
        att_lat: String,
        att_lon: String
    ): MutableLiveData<Resource<AttendanceResponse>> {

        attUpdateMutableLiveData = AttendanceRepository.attendanceUpdate(
            id,
            attDate,
            attTime,
            shiftNo,
            shift,
            attImage,
            att_lat,
            att_lon
        )

        return attUpdateMutableLiveData!!
    }

}