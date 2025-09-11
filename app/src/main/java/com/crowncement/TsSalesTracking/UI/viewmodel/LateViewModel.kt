package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.LateAppliedResponse
import com.crowncement.TsSalesTracking.data.Model.LateApproveResponse
import com.crowncement.TsSalesTracking.data.Model.LateResponse
import com.crowncement.TsSalesTracking.data.Repository.LateRepository

class LateViewModel : ViewModel() {

    private var attLateResMutableLiveData: MutableLiveData<Resource<LateResponse>>? = null
    private var appliedLateResMutableLiveData: MutableLiveData<Resource<LateAppliedResponse>>? =
        null
    private var lateEntryMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var lateApproveListMutableLiveData: MutableLiveData<Resource<LateApproveResponse>>? =
        null
    private var lateApproveMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var lateRejectMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null


    fun getLateList(
        user_id: String,
        user_dtime: String
    ): MutableLiveData<Resource<LateResponse>> {
        attLateResMutableLiveData = LateRepository.getLateList(user_id, user_dtime)
        return attLateResMutableLiveData!!
    }


    fun getAppliedLateList(
        user_id: String,
        user_dtime: String
    ): MutableLiveData<Resource<LateAppliedResponse>> {
        appliedLateResMutableLiveData = LateRepository.getAppliedLateList(user_id, user_dtime)
        return appliedLateResMutableLiveData!!
    }


    fun lateEntry(
        user_id: String,
        lt_date: String,
        lt_iss_date: String,
        lt_purpose: String,
        lt_rem: String,
        lv_not_to: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        lateEntryMutableLiveData = LateRepository.lateApplicationEntry(
            user_id,
            lt_date,
            lt_iss_date,
            lt_purpose,
            lt_rem, lv_not_to
        )
        return lateEntryMutableLiveData!!
    }

    fun lateApproveList(
        user_id: String
    ): MutableLiveData<Resource<LateApproveResponse>> {
        lateApproveListMutableLiveData = LateRepository.lateApprovalList(user_id)
        return lateApproveListMutableLiveData!!
    }

    fun lateApprove(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_date: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        lateApproveMutableLiveData =
            LateRepository.lateApproval(user_id, lt_trn_no, lt_ap_row, lt_date)
        return lateApproveMutableLiveData!!
    }

    fun lateReject(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_deny_res: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        lateRejectMutableLiveData =
            LateRepository.lateReject(user_id, lt_trn_no, lt_ap_row, lt_deny_res)
        return lateRejectMutableLiveData!!
    }


}