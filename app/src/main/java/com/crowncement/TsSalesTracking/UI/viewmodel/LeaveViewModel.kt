package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.LeaveRepository

import java.io.File

class LeaveViewModel : ViewModel() {

    private var leaveListMutableLiveData: MutableLiveData<Resource<LeaveApplicationResponse>>? =
        null
    private var leaveTypeMutableLiveData: MutableLiveData<Resource<LeaveTypeSummaryResponse>>? =
        null
    private var userHierarchyMutableLiveData: MutableLiveData<Resource<HierarchyResponse>>? = null
    private var subordinateMutableLiveData: MutableLiveData<Resource<Subordinate>>? = null
    private var leaveSaveMutableLiveData: MutableLiveData<Resource<LeaveSaveResponse>>? = null
    private var leaveApprovalMutableLiveData: MutableLiveData<Resource<LeaveApproveResponse>>? =
        null
    private var leaveApproveConfirmMutableLiveData: MutableLiveData<Resource<LeaveConfirmResponse>>? =
        null
    private var leaveRejectionMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var uploadMultipleImageLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null

    fun getLeaveList(
        user_id: String,
        lv_year: String,
        lv_code: String
    ): MutableLiveData<Resource<LeaveApplicationResponse>> {
        leaveListMutableLiveData = LeaveRepository.getLeaveList(user_id, lv_year, lv_code)
        return leaveListMutableLiveData!!
    }

    fun getLeaveTypeInfo(
        user_id: String,
        dateTime: String,
    ): MutableLiveData<Resource<LeaveTypeSummaryResponse>> {
        leaveTypeMutableLiveData = LeaveRepository.getLeaveTypeInfo(user_id, dateTime)
        return leaveTypeMutableLiveData!!
    }

    fun getUserHierarchy(
        user_id: String,
        dateTime: String,
        type: String,
    ): MutableLiveData<Resource<HierarchyResponse>> {
        userHierarchyMutableLiveData = LeaveRepository.getUserHierarchy(user_id, dateTime, type)
        return userHierarchyMutableLiveData!!
    }


    fun checkSubordinate(
        user_id: String,
        dateTime: String,
        type: String,
    ): MutableLiveData<Resource<Subordinate>> {
        subordinateMutableLiveData = LeaveRepository.checkSubordinate(user_id, dateTime, type)
        return subordinateMutableLiveData!!
    }

    fun saveLeaveApplication(
        user_id: String,
        lv_date: String,
        lv_fdate: String,
        lv_tdate: String,
        lv_code: String,
        lv_purpose: String,
        lv_add: String,
        lv_phone: String,
        lv_ap_emp: String,
        lv_not_to: String,
        allImages: ArrayList<File>
    ): MutableLiveData<Resource<LeaveSaveResponse>> {
        leaveSaveMutableLiveData = LeaveRepository.saveLeaveApplication(
            user_id,
            lv_date,
            lv_fdate,
            lv_tdate,
            lv_code,
            lv_purpose,
            lv_add,
            lv_phone,
            lv_ap_emp,
            lv_not_to,
            allImages
        )
        return leaveSaveMutableLiveData!!
    }

    fun leaveApprovalList(
        user_id: String
    ): MutableLiveData<Resource<LeaveApproveResponse>> {
        leaveApprovalMutableLiveData = LeaveRepository.leaveApprovalList(user_id)
        return leaveApprovalMutableLiveData!!
    }

    fun leaveApproveConfirm(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_fdate: String,
        lv_tdate: String
    ): MutableLiveData<Resource<LeaveConfirmResponse>> {
        leaveApproveConfirmMutableLiveData =
            LeaveRepository.leaveApproveSave(user_id, lv_trn_no, lv_ap_row, lv_fdate, lv_tdate)
        return leaveApproveConfirmMutableLiveData!!
    }

    fun leaveRejection(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_deny_res: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        leaveRejectionMutableLiveData =
            LeaveRepository.rejectLeaveApplication(user_id, lv_trn_no, lv_ap_row, lv_deny_res)
        return leaveRejectionMutableLiveData!!
    }

    fun uploadImages(
        images: ArrayList<File>,
        user_id: String,
        att_lat: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        uploadMultipleImageLiveData = LeaveRepository.uploadMultipleImages(images, user_id, att_lat)
        return uploadMultipleImageLiveData!!
    }


}