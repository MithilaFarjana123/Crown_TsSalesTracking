package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.DashboardResponse
import com.crowncement.TsSalesTracking.data.Repository.DashboardRepository

class DashboardViewModel : ViewModel() {
    private var dashboardImageListMutableLiveData: MutableLiveData<Resource<DashboardResponse>>? = null

    fun getDashboardBannerImage(
        project: String

    ): MutableLiveData<Resource<DashboardResponse>> {
        dashboardImageListMutableLiveData = DashboardRepository.getBannerIMages(project)
        return dashboardImageListMutableLiveData!!
    }


}