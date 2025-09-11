package com.crowncement.TsSalesTracking.UI.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesCollectionViewModel

class SalesCollectionViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesCollectionViewModel::class.java)) {

            return SalesCollectionViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}