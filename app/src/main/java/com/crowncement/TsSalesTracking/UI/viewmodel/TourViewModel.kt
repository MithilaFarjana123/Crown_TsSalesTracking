package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.TourRepository
import java.io.File

class TourViewModel : ViewModel() {

    private var tourListMutableLiveData: MutableLiveData<Resource<TourResponse>>? = null
    private var saveTourMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var tourApproveListMutableLiveData: MutableLiveData<Resource<TourApproveResponse>>? = null
    private var tourApproveMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var tourRejectMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var tourStartMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var tourStopMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var stopExpenseEntryMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var saveCommonExpenseMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var getExpenseListMutableLiveData: MutableLiveData<Resource<ExpenseClaimResponse>>? = null
    private var getTourBillListMutableLiveData: MutableLiveData<Resource<TourExpenseResponse>>? = null

    fun getTourList(
        id: String,
        dateTime: String

    ): MutableLiveData<Resource<TourResponse>> {
        tourListMutableLiveData = TourRepository.getToutList(id, dateTime)
        return tourListMutableLiveData!!
    }


    fun saveTour(
        user_id: String,
        tr_fdate: String,
        tr_tdate: String,
        tr_iss_date: String,
        tr_purpose: String,
        tr_rem: String,
        tr_not_to: String,
        con:String,
        add:String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        saveTourMutableLiveData = TourRepository.saveTour(
            user_id,
            tr_fdate,
            tr_tdate,
            tr_iss_date,
            tr_purpose,
            tr_rem,
            tr_not_to,
            con,
            add
        )
        return saveTourMutableLiveData!!
    }

    fun getTourApproveList(
        user_id: String

    ): MutableLiveData<Resource<TourApproveResponse>> {
        tourApproveListMutableLiveData = TourRepository.tourApproveList(user_id)
        return tourApproveListMutableLiveData!!
    }

    fun tourApprove(
        user_id: String,
        tr_trn_no: String,
        tr_ap_row: String,
        tr_fdate: String,
        tr_tdate: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        tourApproveMutableLiveData = TourRepository.tourApprove(user_id,tr_trn_no,tr_ap_row,tr_fdate,tr_tdate)
        return tourApproveMutableLiveData!!
    }

    fun tourReject(
         user_id: String,
         tr_trn_no: String,
         tr_ap_row: String,
         tr_deny_res: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        tourRejectMutableLiveData = TourRepository.tourReject(user_id,tr_trn_no,tr_ap_row,tr_deny_res)
        return tourRejectMutableLiveData!!
    }


    fun tourStart(
        user_id: String,
        tourNo: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        tourStartMutableLiveData = TourRepository.startTour(user_id,tourNo)
        return tourStartMutableLiveData!!
    }

    fun tourStop(
        user_id: String,
        tourNo: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        tourStopMutableLiveData = TourRepository.stopTour(user_id,tourNo)
        return tourStopMutableLiveData!!
    }

    fun saveCommonExpenseClaim(
        user_id    :String,
        tr_trn_no  :String,
        tr_date    :String,
        tr_exp_type:String,
        tr_exp_sub :String,
        tr_exp_source: String,
        tr_frm_loc :String,
        tr_to_loc  :String,
        tr_pay_mod :String,
        tr_amt     :String,
        tr_rem     :String,
        extension     :String,
        all_images : ArrayList<File>

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        saveCommonExpenseMutableLiveData = TourRepository.commonExpenseClaim(
            user_id,
            tr_trn_no,
            tr_date,
            tr_exp_type,
            tr_exp_sub,
            tr_exp_source,
            tr_frm_loc,
            tr_to_loc,
            tr_pay_mod,
            tr_amt,
            tr_rem,
            extension,
            all_images
        )
        return saveCommonExpenseMutableLiveData!!
    }

    fun getExpenseClaimList(
        user_id: String,
        tourNo: String,
        date:String

    ): MutableLiveData<Resource<ExpenseClaimResponse>> {
        getExpenseListMutableLiveData = TourRepository.getExpenseClaimList(user_id,tourNo,date)
        return getExpenseListMutableLiveData!!
    }



    fun stopExpense(
        user_id: String,
        tourNo: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        stopExpenseEntryMutableLiveData = TourRepository.stopExpenseEntry(user_id,tourNo)
        return stopExpenseEntryMutableLiveData!!
    }

    fun getTourBillList(
        user_id: String,
        from: String,
        to: String
    ): MutableLiveData<Resource<TourExpenseResponse>> {
        getTourBillListMutableLiveData = TourRepository.getTourBill(user_id,from,to)
        return getTourBillListMutableLiveData!!
    }

}