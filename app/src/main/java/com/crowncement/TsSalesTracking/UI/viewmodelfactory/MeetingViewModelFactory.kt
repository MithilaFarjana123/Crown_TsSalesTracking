package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.MeetingViewModel

class MeetingViewModelFactory(): ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingViewModel::class.java)) {
            return MeetingViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}