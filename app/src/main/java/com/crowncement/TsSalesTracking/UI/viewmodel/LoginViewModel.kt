package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.UserInfo
import com.crowncement.TsSalesTracking.data.Model.UserPassChangeResponce
import com.crowncement.TsSalesTracking.data.Repository.LoginRepository

class LoginViewModel : ViewModel() {

    private var mutableLiveData: MutableLiveData<Resource<UserInfo>>? = null
    private var UserPassChangemutableLiveData: MutableLiveData<Resource<UserPassChangeResponce>>? = null


    init {
      //  mutableLiveData = LoginRepository.getUserInfo()
    }

    fun getUserInfo(id:String,pass:String,deviceId:String,token:String): MutableLiveData<Resource<UserInfo>> {

        mutableLiveData = LoginRepository.getUserInfo(id, pass, deviceId,token)

        return mutableLiveData!!
    }


   //todo UserPassChange
   fun UserPassChange(id:String,device_id:String,old_pass:String,new_pass:String): MutableLiveData<Resource<UserPassChangeResponce>> {

       UserPassChangemutableLiveData = LoginRepository.UserPassChange(id, device_id, old_pass,new_pass)

       return UserPassChangemutableLiveData!!
   }
}