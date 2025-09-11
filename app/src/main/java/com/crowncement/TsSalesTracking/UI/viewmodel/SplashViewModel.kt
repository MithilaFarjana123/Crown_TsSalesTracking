package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.AppVersionResponse
import com.crowncement.TsSalesTracking.data.Repository.SplashRepository

class SplashViewModel : ViewModel() {

    private var mutableLiveData: MutableLiveData<Resource<AppVersionResponse>>? = null

    fun getAppVersion(
        appName: String,
        dateTime: String
    ): MutableLiveData<Resource<AppVersionResponse>>? {

        mutableLiveData = SplashRepository.getUserInfo(appName, dateTime)

        return mutableLiveData
    }

}