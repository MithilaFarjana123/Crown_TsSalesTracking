package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.CallRecordSpinnerResponse
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.GetTSCallTagResponce
import com.crowncement.TsSalesTracking.data.Model.SalesVisitSummaryResponse
import com.crowncement.TsSalesTracking.data.Model.SearchByMobile
import com.crowncement.TsSalesTracking.data.Repository.SalesVisitRepository

class SalesVisitViewModel : ViewModel() {

    private var salesVisitSummaryListMutableLiveData: MutableLiveData<Resource<SalesVisitSummaryResponse>>? =
        null
    private var salesVisitSummarySaveMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? =
        null
    private var getCallRecordDiscussionMutableLiveData: MutableLiveData<Resource<CallRecordSpinnerResponse>>? =
        null
    private var getNoOrderReasonMutableLiveData: MutableLiveData<Resource<CallRecordSpinnerResponse>>? =
        null
    private var getAllCementMutableLiveData: MutableLiveData<Resource<CallRecordSpinnerResponse>>? =
        null
    private var saveCallRecordMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null

    private var saveCallRecordHomeMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null

    private var GetTSCallTagMutableLiveData : MutableLiveData<Resource<GetTSCallTagResponce>>? =null

    private var getSearchPartyMutableLiveData : MutableLiveData<Resource<SearchByMobile>>? =null

//todo
    fun getSearchByMobile(
    mob_no: String
    ): MutableLiveData<Resource<SearchByMobile>> {
    getSearchPartyMutableLiveData =
            SalesVisitRepository.getSearchByMobile(mob_no)
        return getSearchPartyMutableLiveData!!
    }


    fun getSalesVisitSummaryList(
        empId: String, moveDate: String
    ): MutableLiveData<Resource<SalesVisitSummaryResponse>> {
        salesVisitSummaryListMutableLiveData =
            SalesVisitRepository.getSalesSummaryList(empId, moveDate)
        return salesVisitSummaryListMutableLiveData!!
    }

    fun getSalesVisitSummarySave(
        empId: String,
        code: String,
        profile: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        salesVisitSummarySaveMutableLiveData =
            SalesVisitRepository.salesSummarySave(empId, code, profile)
        return salesVisitSummarySaveMutableLiveData!!
    }


    fun getCallRecordDiscussionList(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {
        getCallRecordDiscussionMutableLiveData = SalesVisitRepository.getDiscussionTopic()
        return getCallRecordDiscussionMutableLiveData!!
    }

    fun getNoOrderReasonList(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {
        getNoOrderReasonMutableLiveData = SalesVisitRepository.getNoReasonTopic()
        return getNoOrderReasonMutableLiveData!!
    }

    fun getAllCementList(): MutableLiveData<Resource<CallRecordSpinnerResponse>> {
        getAllCementMutableLiveData = SalesVisitRepository.getAllCement()
        return getAllCementMutableLiveData!!
    }


    fun saveCallRecord(
        id: String,
        moveDoc: String,
        moveRow: String,
        data: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        saveCallRecordMutableLiveData =
            SalesVisitRepository.saveCallRecord(id, moveDoc, moveRow, data)
        return saveCallRecordMutableLiveData!!
    }


    fun saveCallRecordHome(
        id: String,
        customerId: String,
        data: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        saveCallRecordHomeMutableLiveData =
            SalesVisitRepository.saveCallRecordHome(id, customerId, data)
        return saveCallRecordHomeMutableLiveData!!
    }



}