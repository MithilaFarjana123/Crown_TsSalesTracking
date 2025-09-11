package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesVisitViewModel

class SalesVisitViewModelFactory () : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesVisitViewModel::class.java)) {
            return SalesVisitViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")

    }
}