package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.PaySlipResponse
import com.crowncement.TsSalesTracking.data.Model.SalaryMonthResponse
import com.crowncement.TsSalesTracking.data.Repository.PaySlipRepository

class PaySlipViewModel : ViewModel() {

    private var pauSlipMonthMutableLiveData: MutableLiveData<Resource<SalaryMonthResponse>>? = null
    private var paymentInfoMutableLiveData: MutableLiveData<Resource<PaySlipResponse>>? = null

    fun getSalaryMonthInfo(
        id: String

    ): MutableLiveData<Resource<SalaryMonthResponse>> {
        pauSlipMonthMutableLiveData = PaySlipRepository.getPayslipMonthInfo(id)
        return pauSlipMonthMutableLiveData!!
    }

    fun getSalaryInfo(
        id: String,
        paydCode: String

    ): MutableLiveData<Resource<PaySlipResponse>> {
        paymentInfoMutableLiveData = PaySlipRepository.getPayslipInfo(id, paydCode)
        return paymentInfoMutableLiveData!!
    }

}