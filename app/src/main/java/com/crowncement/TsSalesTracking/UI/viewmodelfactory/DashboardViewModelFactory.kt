package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.DashboardViewModel

class DashboardViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {

            return DashboardViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}