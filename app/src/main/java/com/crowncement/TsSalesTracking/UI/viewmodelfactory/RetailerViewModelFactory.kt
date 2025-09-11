package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel


class RetailerViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetailerViewModel::class.java)) {
            return RetailerViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}