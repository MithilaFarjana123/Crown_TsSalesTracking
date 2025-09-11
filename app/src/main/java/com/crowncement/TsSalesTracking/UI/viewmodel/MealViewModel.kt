package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.MealAllocationResponse
import com.crowncement.TsSalesTracking.data.Model.MealCostResponse
import com.crowncement.TsSalesTracking.data.Repository.MealRepository


class MealViewModel : ViewModel() {
    private var mealStatusMutableLiveData: MutableLiveData<Resource<MealAllocationResponse>>? = null
    private var mealConfirmMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var mealCancelMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var mealCostMutableLiveData: MutableLiveData<Resource<MealCostResponse>>? = null


    fun getMealInfo(
        id: String,
        mealDate: String
    ): MutableLiveData<Resource<MealAllocationResponse>> {
        mealStatusMutableLiveData = MealRepository.getMealAllocationStatus(id, mealDate)
        return mealStatusMutableLiveData!!
    }

    fun confirmMeal(
        id: String,
        mealDate: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        mealConfirmMutableLiveData = MealRepository.confirmMealAllocation(id, mealDate)
        return mealConfirmMutableLiveData!!
    }

    fun cancelMeal(
        id: String,
        mealDate: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        mealCancelMutableLiveData = MealRepository.cancelMealAllocation(id, mealDate)
        return mealCancelMutableLiveData!!
    }


    fun mealCost(
        id: String,
        fDate: String,
        tDate: String
    ): MutableLiveData<Resource<MealCostResponse>> {
        mealCostMutableLiveData = MealRepository.mealCost(id, fDate, tDate)
        return mealCostMutableLiveData!!
    }

}