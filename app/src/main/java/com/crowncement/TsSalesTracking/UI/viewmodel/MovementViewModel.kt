package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.MovementRepository

class MovementViewModel : ViewModel() {

    private var getEmpRouteMutableLiveData: MutableLiveData<Resource<MovRouteResponse>>?     = null
    private var saveMovementMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>?  = null
    private var movementListMutableLiveData: MutableLiveData<Resource<MovementPlanResponse>>?= null
    private var movementAcListMutableLiveData: MutableLiveData<Resource<MovementPlanResponse>>?= null
    private var saveAcMovementMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>?= null
    private var getZonalJuniorMutableLiveData: MutableLiveData<Resource<EmpResponse>>?= null

    private var submitMonthlyMovementMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>?= null
    private var approveMonthlyMovementMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>?= null
    private var actualMovementPlanMutableLiveData: MutableLiveData<Resource<ActualPlnResponse>>?= null

    private var GetTSMovLocationMutableLiveData: MutableLiveData<Resource<GetTSMovLocationResponce>>?= null


    fun getRoute(user_id: String): MutableLiveData<Resource<MovRouteResponse>>? {
        getEmpRouteMutableLiveData = MovementRepository.getEmpRouteList(user_id)
        return getEmpRouteMutableLiveData
    }

    fun saveMovementPlan(user_id: String,date: String,route: String): MutableLiveData<Resource<CommonSaveResponse>>? {
        saveMovementMutableLiveData = MovementRepository.saveMovementPlan(user_id,date,route)
        return saveMovementMutableLiveData
    }

    fun movementPlanList(user_id: String,year: String,month: String): MutableLiveData<Resource<MovementPlanResponse>>? {
        movementListMutableLiveData = MovementRepository.movementPlanList(user_id,year,month)
        return movementListMutableLiveData
    }

    //
    fun GetTSMovLocationList(user_id: String): MutableLiveData<Resource<GetTSMovLocationResponce>>? {
        GetTSMovLocationMutableLiveData = MovementRepository.getTSMovLocationList(user_id)
        return GetTSMovLocationMutableLiveData
    }

    fun movementAcPlanList(user_id: String,year: String,month: String): MutableLiveData<Resource<MovementPlanResponse>>? {
        movementAcListMutableLiveData = MovementRepository.movementAcPlanList(user_id,year,month)
        return movementAcListMutableLiveData
    }

    fun saveActualMovementPlan(user_id: String,date: String,route: String): MutableLiveData<Resource<CommonSaveResponse>>? {
        saveAcMovementMutableLiveData = MovementRepository.saveActualMovementPlan(user_id,date,route)
        return saveAcMovementMutableLiveData
    }

    fun getZonalJunior(user_id: String,dateTime: String): MutableLiveData<Resource<EmpResponse>>? {
        getZonalJuniorMutableLiveData = MovementRepository.getZonalJuniorList(user_id,dateTime)
        return getZonalJuniorMutableLiveData
    }



    fun submitMovementMonthlyPlan(user_id: String,year: String,month: String): MutableLiveData<Resource<CommonSaveResponse>>? {
        submitMonthlyMovementMutableLiveData = MovementRepository.submitMovementMonthlyPlan(user_id,year,month)
        return submitMonthlyMovementMutableLiveData
    }

    fun approveMovementMonthlyPlan(user_id: String,movNo: String): MutableLiveData<Resource<CommonSaveResponse>>? {
        approveMonthlyMovementMutableLiveData = MovementRepository.approveMovementMonthlyPlan(user_id,movNo)
        return approveMonthlyMovementMutableLiveData
    }

    fun actualMovementPlanList(user_id: String,movNo: String): MutableLiveData<Resource<ActualPlnResponse>>? {
        actualMovementPlanMutableLiveData = MovementRepository.getActualPlanList(user_id,movNo)
        return actualMovementPlanMutableLiveData
    }

}