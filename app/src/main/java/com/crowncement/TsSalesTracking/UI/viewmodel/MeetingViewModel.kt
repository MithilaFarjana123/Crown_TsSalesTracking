package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.CarReqEntListResponce
import com.crowncement.TsSalesTracking.data.Model.CarReqSaveresponce
import com.crowncement.TsSalesTracking.data.Model.DepartmentResponce
import com.crowncement.TsSalesTracking.data.Model.MeetRoomBKListResponce
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomBookInfoResponce
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomListResponce
import com.crowncement.TsSalesTracking.data.Repository.MeetingRepository

class MeetingViewModel : ViewModel(){
    private var getDepartmentMutableLiveData: MutableLiveData<Resource<DepartmentResponce>>? = null
    private var SaveMeetingDataMutableLiveData: MutableLiveData<Resource<MeetingRoomBookInfoResponce>>? = null
    private var getMeetingListMutableLiveData: MutableLiveData<Resource<MeetingRoomListResponce>>? = null
    private var getMeetRoomBKListDataMutableLiveData: MutableLiveData<Resource<MeetRoomBKListResponce>>? = null
    private var saveCarReqSaveDataMutableLiveData: MutableLiveData<Resource<CarReqSaveresponce>>? = null
    private var CarReqEntListDataMutableLiveData:  MutableLiveData<Resource<CarReqEntListResponce>>? = null

    //Todo get depertment
    fun getDepartmentData(

    ): MutableLiveData<Resource<DepartmentResponce>>? {

        getDepartmentMutableLiveData = MeetingRepository.getSavedDepartment()

        return getDepartmentMutableLiveData
    }


    //TODO save metting data
    fun SaveMeetingData(
        user_id: String,
        room_info: String
    ): MutableLiveData<Resource<MeetingRoomBookInfoResponce>>? {

        SaveMeetingDataMutableLiveData = MeetingRepository.SaveMeetingData(user_id,room_info)

        return SaveMeetingDataMutableLiveData
    }


   //todo getMeetingList
   fun getMeetingList(): MutableLiveData<Resource<MeetingRoomListResponce>>? {

       getMeetingListMutableLiveData = MeetingRepository.getMeetingList()

       return getMeetingListMutableLiveData
   }

    //todo getMeetRoomBKList
    fun getMeetRoomBKList(
        user_id: String,
        from_date: String,
        to_date: String
    ): MutableLiveData<Resource<MeetRoomBKListResponce>>? {

        getMeetRoomBKListDataMutableLiveData = MeetingRepository.getMeetRoomBKList(user_id,from_date,to_date)

        return getMeetRoomBKListDataMutableLiveData
    }


    //todo saveCarReqSave
    fun saveCarReqSave(
        user_id: String,
        req_info: String
    ): MutableLiveData<Resource<CarReqSaveresponce>>? {

        saveCarReqSaveDataMutableLiveData = MeetingRepository.saveCarReqSave(user_id,req_info)

        return saveCarReqSaveDataMutableLiveData
    }


    //todo CarReqEntList
    fun getCarReqEntList(
        emp_code: String,
        from_date: String,
        to_date: String
    ): MutableLiveData<Resource<CarReqEntListResponce>>? {

        CarReqEntListDataMutableLiveData = MeetingRepository.CarReqEntList(emp_code,from_date,to_date)

        return CarReqEntListDataMutableLiveData
    }

}