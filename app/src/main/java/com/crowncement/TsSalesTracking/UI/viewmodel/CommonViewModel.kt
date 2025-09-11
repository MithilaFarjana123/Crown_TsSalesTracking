package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.CommonRepository

class CommonViewModel : ViewModel() {

    private var locationTrackingMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? =
        null
    private var employeeInfoMutableLiveData: MutableLiveData<Resource<EmpResponse>>? = null
    private var sendNotificationMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? =
        null
    private var pushMessageMutableLiveData: MutableLiveData<Resource<PushMessageResponse>>? = null
    private var typeMutableLiveData: MutableLiveData<Resource<TypeResponse>>? = null
    private var subTypeMutableLiveData: MutableLiveData<Resource<TypeResponse>>? = null
    private var holidayListMutableLiveData: MutableLiveData<Resource<HolidayResponse>>? = null

    fun uploadLocationData(
        user_id: String,
        cap_time: String,
        cap_lat: Double,
        cap_lon: Double
    ): MutableLiveData<Resource<CommonSaveResponse>>? {

        locationTrackingMutableLiveData = CommonRepository.uploadLocation(
            user_id,
            cap_time,
            cap_lat.toString(),
            cap_lon.toString()
        )
        return locationTrackingMutableLiveData
    }

    fun getEmployeeInfo(): MutableLiveData<Resource<EmpResponse>>? {
        employeeInfoMutableLiveData = CommonRepository.getAllEmployee()
        return employeeInfoMutableLiveData
    }


    fun sendNotification(
        userID: String,
        title: String,
        body: String,
        empList: String
    ): MutableLiveData<Resource<CommonSaveResponse>>? {
        sendNotificationMutableLiveData =
            CommonRepository.sendPushMessage(userID, title, body, empList)
        return sendNotificationMutableLiveData
    }

    fun getPushNotification(userID: String): MutableLiveData<Resource<PushMessageResponse>>? {
        pushMessageMutableLiveData = CommonRepository.getPushMessage(userID)
        return pushMessageMutableLiveData
    }

    fun getDataByType(typeName: String): MutableLiveData<Resource<TypeResponse>>? {
        typeMutableLiveData = CommonRepository.getDataByType(typeName)
        return typeMutableLiveData
    }

    fun getSubDataByType(
        typeName: String,
        subType: String
    ): MutableLiveData<Resource<TypeResponse>>? {
        subTypeMutableLiveData = CommonRepository.getDataBySubType(typeName, subType)
        return subTypeMutableLiveData
    }


    fun getHolidayList(
        emp: String, year: String, month: String
    ): MutableLiveData<Resource<HolidayResponse>>? {
        holidayListMutableLiveData = CommonRepository.getHolidayList(emp, year, month)
        return holidayListMutableLiveData
    }

}