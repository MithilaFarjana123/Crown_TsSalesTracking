package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.PaySlipViewModel

class PaySlipViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaySlipViewModel::class.java)) {
            return PaySlipViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}