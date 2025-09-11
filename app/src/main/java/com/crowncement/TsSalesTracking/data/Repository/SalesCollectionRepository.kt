package com.crowncement.TsSalesTracking.data.Repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.Model.*

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object SalesCollectionRepository {


    fun getBankInfo(): MutableLiveData<Resource<BankResponse>> {

        val bankInfo: MutableLiveData<Resource<BankResponse>> =
            MutableLiveData<Resource<BankResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getBankName()
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        bankInfo.postValue(Resource.success(response))
                    },
                    { error -> //
                        bankInfo.postValue(Resource.error(error.message.toString(), null))

                    },
                )

        } catch (e: Exception) {
            Log.e("Sales_Collection_Repo", "ERROR login: " + e.message)
        }
        return bankInfo
    }

    fun getBranchInfo(bankCode:String): MutableLiveData<Resource<BranchResponse>> {

        val bank = RequestBody.create("application/json".toMediaTypeOrNull(), bankCode)

        val branchInfo: MutableLiveData<Resource<BranchResponse>> = MutableLiveData<Resource<BranchResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getBranchName(bank)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        branchInfo.postValue(Resource.success(response))
                    },
                    { error -> //
                        branchInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Sales_Collection_Repo", "Branch Error: " + e.message)
        }
        return branchInfo
    }

    fun getBranchDetails(bankCode:String,branchCode:String): MutableLiveData<Resource<BranchDetailsResponse>> {

        val bank = RequestBody.create("application/json".toMediaTypeOrNull(), bankCode)
        val branch = RequestBody.create("application/json".toMediaTypeOrNull(), branchCode)

        val branchDetails: MutableLiveData<Resource<BranchDetailsResponse>> = MutableLiveData<Resource<BranchDetailsResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getBranchDetails(bank,branch)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        branchDetails.postValue(Resource.success(response))
                    },
                    { error -> //
                        branchDetails.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Sales_Collection_Repo", "branchDetails Error: " + e.message)
        }
        return branchDetails
    }

    fun salesCollectionEntry(
        user_id         : String,
        ent_date        : String,
        dp_bank         : String,
        dp_br           : String,
        dp_acc          : String,
        dp_brrt         : String,
        dp_date         : String,
        dp_acc_nam      : String,
        cus_code        : String,
        pay_typ         : String,
        doc_no          : String,
        emp_code        : String,
        dp_amt          : String,
        dp_rem          : String,
        extension       : String,
        all_images      : File

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val userId          = RequestBody.create("application/json".toMediaTypeOrNull(), user_id       )
        val entDate         = RequestBody.create("application/json".toMediaTypeOrNull(), ent_date      )
        val bank            = RequestBody.create("application/json".toMediaTypeOrNull(), dp_bank       )
        val branch          = RequestBody.create("application/json".toMediaTypeOrNull(), dp_br         )
        val accountNo       = RequestBody.create("application/json".toMediaTypeOrNull(), dp_acc        )
        val route           = RequestBody.create("application/json".toMediaTypeOrNull(), dp_brrt       )
        val depositDate     = RequestBody.create("application/json".toMediaTypeOrNull(), dp_date       )
        val payTo           = RequestBody.create("application/json".toMediaTypeOrNull(), dp_acc_nam    )
        val customerCode    = RequestBody.create("application/json".toMediaTypeOrNull(), cus_code      )
        val payTYpe         = RequestBody.create("application/json".toMediaTypeOrNull(), pay_typ       )
        val docno           = RequestBody.create("application/json".toMediaTypeOrNull(), doc_no        )
        val empcode         = RequestBody.create("application/json".toMediaTypeOrNull(), emp_code      )
        val amount          = RequestBody.create("application/json".toMediaTypeOrNull(), dp_amt        )
        val emarks          = RequestBody.create("application/json".toMediaTypeOrNull(), dp_rem        )
        val ext          = RequestBody.create("application/json".toMediaTypeOrNull(), extension        )


        val requestFile     = RequestBody.create("image/*".toMediaTypeOrNull(),         all_images    )
        val attachedFile: MultipartBody.Part = MultipartBody.Part.createFormData("all_images", all_images.name, requestFile)
        val saveCollection: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveSalesCollection(
               userId,
               entDate,
               bank,
               branch,
               accountNo,
               route,
               depositDate,
               payTo,
               customerCode,
               payTYpe,
               docno,
               empcode,
               amount,
               emarks,
                ext,
               attachedFile
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCollection.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCollection.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("SalesCollectionRepo", "saveCollection Save: " + e.message)
        }
        return saveCollection
    }


    fun getSalesCollectionList(
        user_id         : String,
        cus_code        : String,
        dep_fdate         : String,
        dep_tdate           : String,

    ): MutableLiveData<Resource<SalesCollectionResponse>> {
        val userId       = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val customerCode = RequestBody.create("application/json".toMediaTypeOrNull(), cus_code)
        val fmDate       = RequestBody.create("application/json".toMediaTypeOrNull(), dep_fdate)
        val tDate        = RequestBody.create("application/json".toMediaTypeOrNull(), dep_tdate)

        val saveCollectionList: MutableLiveData<Resource<SalesCollectionResponse>> =
            MutableLiveData<Resource<SalesCollectionResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getSalesCollection(
                userId,
                customerCode,
                fmDate,
                tDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCollectionList.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCollectionList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("SalesCollectionRepo", "saveCollection List: " + e.message)
        }
        return saveCollectionList
    }

    @SuppressLint("CheckResult")
    fun getPaymentType(code:String): MutableLiveData<Resource<TypeResponse>> {

        val payment = RequestBody.create("application/json".toMediaTypeOrNull(), code)

        val payType: MutableLiveData<Resource<TypeResponse>> = MutableLiveData<Resource<TypeResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getCodeType(payment)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        payType.postValue(Resource.success(response))
                    },
                    { error -> //
                        payType.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Collection_Repository", "Paytype: " + e.message)
        }
        return payType
    }


    @SuppressLint("CheckResult")
    fun getSalesCollectionListForApprove(
        user_id         : String,
        dep_fdate         : String,
        dep_tdate           : String,

        ): MutableLiveData<Resource<PaymentCollectionResponse>> {
        val userId       = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val fmDate       = RequestBody.create("application/json".toMediaTypeOrNull(), dep_fdate)
        val tDate        = RequestBody.create("application/json".toMediaTypeOrNull(), dep_tdate)

        val saveCollectionList: MutableLiveData<Resource<PaymentCollectionResponse>> =
            MutableLiveData<Resource<PaymentCollectionResponse>>()
        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getSalesCollectionListForApprove(
                userId,
                fmDate,
                tDate)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCollectionList.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCollectionList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("SalesCollectionRepo", "Collection apr List: " + e.message)
        }
        return saveCollectionList
    }



    @SuppressLint("CheckResult")
    fun confirmSalesCollection(id:String, tranNo:String): MutableLiveData<Resource<CommonSaveResponse>> {

        val eid = RequestBody.create("application/json".toMediaTypeOrNull(), id)
        val tran = RequestBody.create("application/json".toMediaTypeOrNull(), tranNo)

        val payType: MutableLiveData<Resource<CommonSaveResponse>> = MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.salesCollectionConfirm(eid,tran)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        payType.postValue(Resource.success(response))
                    },
                    { error -> //
                        payType.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Collection_Repository", "confirmCollec: " + e.message)
        }
        return payType
    }

}