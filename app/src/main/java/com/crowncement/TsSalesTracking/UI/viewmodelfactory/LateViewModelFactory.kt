package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.crowncement.TsSalesTracking.UI.viewmodel.LateViewModel

class LateViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LateViewModel::class.java)) {
            return LateViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")

    }
}