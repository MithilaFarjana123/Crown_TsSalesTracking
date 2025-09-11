package com.crowncement.TsSalesTracking.UI.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.SalesCollectionRepository

import java.io.File

class SalesCollectionViewModel : ViewModel() {

    private var bankListMutableLiveData: MutableLiveData<Resource<BankResponse>>? = null
    private var branchListMutableLiveData: MutableLiveData<Resource<BranchResponse>>? = null
    private var branchDetailsMutableLiveData: MutableLiveData<Resource<BranchDetailsResponse>>? = null
    private var saveSalesCollectionMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var salesCollectionListMutableLiveData: MutableLiveData<Resource<SalesCollectionResponse>>? = null
    private var payTypeMutableLiveData: MutableLiveData<Resource<TypeResponse>>? = null
    private var paymentCollectionAppMutableLiveData: MutableLiveData<Resource<PaymentCollectionResponse>>? = null
    private var confirmPaymentMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null

    fun getBankList(
    ): MutableLiveData<Resource<BankResponse>> {
        bankListMutableLiveData = SalesCollectionRepository.getBankInfo()
        return bankListMutableLiveData!!
    }

    fun getBranchList(
        bankCode: String
    ): MutableLiveData<Resource<BranchResponse>> {
        branchListMutableLiveData = SalesCollectionRepository.getBranchInfo(bankCode)
        return branchListMutableLiveData!!
    }

    fun getBranchDetails(
        bankCode: String, branchCode: String
    ): MutableLiveData<Resource<BranchDetailsResponse>> {
        branchDetailsMutableLiveData =
            SalesCollectionRepository.getBranchDetails(bankCode, branchCode)
        return branchDetailsMutableLiveData!!
    }

    fun saveSalesCollection(

        user_id: String,
        ent_date: String,
        dp_bank: String,
        dp_br: String,
        dp_acc: String,
        dp_brrt: String,
        dp_date: String,
        dp_acc_nam: String,
        cus_code: String,
        pay_typ: String,
        doc_no: String,
        emp_code: String,
        dp_amt: String,
        dp_rem: String,
        extension: String,
        all_images: File

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        saveSalesCollectionMutableLiveData = SalesCollectionRepository.salesCollectionEntry(

            user_id,
            ent_date,
            dp_bank,
            dp_br,
            dp_acc,
            dp_brrt,
            dp_date,
            dp_acc_nam,
            cus_code,
            pay_typ,
            doc_no,
            emp_code,
            dp_amt,
            dp_rem,
            extension,
            all_images
        )
        return saveSalesCollectionMutableLiveData!!
    }

    fun getSalesCollectionList(
        empId: String, customerCode: String, fDate: String, tDate: String
    ): MutableLiveData<Resource<SalesCollectionResponse>> {
        salesCollectionListMutableLiveData =
            SalesCollectionRepository.getSalesCollectionList(empId, customerCode, fDate, tDate)
        return salesCollectionListMutableLiveData!!
    }

    fun getPaymentType(code: String): MutableLiveData<Resource<TypeResponse>> {
        payTypeMutableLiveData = SalesCollectionRepository.getPaymentType(code)
        return payTypeMutableLiveData!!
    }

    //getSalesCollectionListForApprove
    fun getCollectionListForApprove(
        id: String,
        from: String,
        to: String
    ): MutableLiveData<Resource<PaymentCollectionResponse>> {
        paymentCollectionAppMutableLiveData =
            SalesCollectionRepository.getSalesCollectionListForApprove(id, from, to)
        return paymentCollectionAppMutableLiveData!!
    }

    fun confirmPaymentCollection(id: String,tranId:String): MutableLiveData<Resource<CommonSaveResponse>> {
        confirmPaymentMutableLiveData = SalesCollectionRepository.confirmSalesCollection(id,tranId)
        return confirmPaymentMutableLiveData!!
    }
}