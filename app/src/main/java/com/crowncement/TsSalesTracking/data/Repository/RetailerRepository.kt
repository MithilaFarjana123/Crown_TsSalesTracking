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

object RetailerRepository {

    fun getDivisionInfo(
        xhrc1: String
    ): MutableLiveData<Resource<DivisionResponse>> {

        val division = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc1)

        val divisionInfo: MutableLiveData<Resource<DivisionResponse>> =
            MutableLiveData<Resource<DivisionResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getDivision(division)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        divisionInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        divisionInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "ERROR login: " + e.message)
        }
        return divisionInfo
    }

    fun getDistrictInfo(
        country: String,
        division: String
    ): MutableLiveData<Resource<DistrictResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)

        val districtInfo: MutableLiveData<Resource<DistrictResponse>> =
            MutableLiveData<Resource<DistrictResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getDistrict(count, div)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        districtInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        districtInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "District: " + e.message)
        }
        return districtInfo
    }


    fun getThanaInfo(
        country: String,
        division: String,
        district: String
    ): MutableLiveData<Resource<ThanaResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)

        val thanaInfo: MutableLiveData<Resource<ThanaResponse>> =
            MutableLiveData<Resource<ThanaResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getThana(count, div, dis)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        thanaInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        thanaInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "Thana: " + e.message)
        }
        return thanaInfo
    }

    fun getRouteInfo(
        country: String,
        division: String,
        district: String,
        thana: String
    ): MutableLiveData<Resource<RouteResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)
        val tha = RequestBody.create("application/json".toMediaTypeOrNull(), thana)

        val routeInfo: MutableLiveData<Resource<RouteResponse>> =
            MutableLiveData<Resource<RouteResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getSalesRoute(count, div, dis, tha)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        routeInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        routeInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "Route: " + e.message)
        }
        return routeInfo
    }


    fun getRetailerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        routeCode: String
    ): MutableLiveData<Resource<RetailerResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)
        val tha = RequestBody.create("application/json".toMediaTypeOrNull(), thana)
        val rou = RequestBody.create("application/json".toMediaTypeOrNull(), routeCode)

        val retailerList: MutableLiveData<Resource<RetailerResponse>> =
            MutableLiveData<Resource<RetailerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getRetailerList(count, div, dis, tha, rou)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        retailerList.postValue(Resource.success(response))
                    },
                    { error ->
                        retailerList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "Route: " + e.message)
        }
        return retailerList
    }


    fun getDealerList(
        country: String,
        division: String,
        district: String

    ): MutableLiveData<Resource<DealerResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)


        val dealerList: MutableLiveData<Resource<DealerResponse>> =
            MutableLiveData<Resource<DealerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.getDealerList(count, div, dis)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        dealerList.postValue(Resource.success(response))
                    },
                    { error ->
                        dealerList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "DealerList: " + e.message)
        }
        return dealerList
    }

    fun getOtherCustomerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xcode: String
    ): MutableLiveData<Resource<OtherCustomerResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)
        val tha = RequestBody.create("application/json".toMediaTypeOrNull(), thana)
        val xc = RequestBody.create("application/json".toMediaTypeOrNull(), xcode)

        val otherCustomerList: MutableLiveData<Resource<OtherCustomerResponse>> =
            MutableLiveData<Resource<OtherCustomerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.getOtherCustomerList(count, div, dis, tha, xc)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCustomerList.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCustomerList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "Other Customer: " + e.message)
        }
        return otherCustomerList
    }

    //todo for mason
    fun getOtherMasonList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xcode: String
    ): MutableLiveData<Resource<OtherCustomerResponse>> {

        val count = RequestBody.create("application/json".toMediaTypeOrNull(), country)
        val div = RequestBody.create("application/json".toMediaTypeOrNull(), division)
        val dis = RequestBody.create("application/json".toMediaTypeOrNull(), district)
        val tha = RequestBody.create("application/json".toMediaTypeOrNull(), thana)
        val xc = RequestBody.create("application/json".toMediaTypeOrNull(), xcode)

        val otherCustomerList: MutableLiveData<Resource<OtherCustomerResponse>> =
            MutableLiveData<Resource<OtherCustomerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.getOtherMasonList(count, div, dis, tha, xc)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCustomerList.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCustomerList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "Other Customer: " + e.message)
        }
        return otherCustomerList
    }

    fun updateRetailer(
        userId: String,
        retailerId: String,
        updateValue: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userId)
        val retailer = RequestBody.create("application/json".toMediaTypeOrNull(), retailerId)
        val value = RequestBody.create("application/json".toMediaTypeOrNull(), updateValue)


        val otherCustomerList: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.updateRetailer(user, retailer, value)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCustomerList.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCustomerList.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Update Retailer", ": " + e.message)
        }
        return otherCustomerList
    }

    fun uploadCoverPhoto(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File
    ): MutableLiveData<Resource<CoverImageUploadResponse>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val p_code = RequestBody.create("application/json".toMediaTypeOrNull(), party_code)
        val p_type = RequestBody.create("application/json".toMediaTypeOrNull(), party_type)
        val latitude = RequestBody.create("application/json".toMediaTypeOrNull(), party_lat)
        val longitude = RequestBody.create("application/json".toMediaTypeOrNull(), party_lon)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), party_image)

        val coverPhoto: MultipartBody.Part =
            MultipartBody.Part.createFormData("party_image", party_image.name, requestFile)


        val uploadImage: MutableLiveData<Resource<CoverImageUploadResponse>> =
            MutableLiveData<Resource<CoverImageUploadResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.uploadCoverPhoto(
                userId,
                p_code,
                p_type,
                latitude,
                longitude,
                coverPhoto
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        uploadImage.postValue(Resource.success(response))
                    },
                    { error ->
                        uploadImage.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRep", "ERROR Upload Cover: " + e.message)
        }
        return uploadImage
    }


    fun updateDealer(
        userId: String,
        dealerId: String,
        updateValue: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userId)
        val dealer = RequestBody.create("application/json".toMediaTypeOrNull(), dealerId)
        val value = RequestBody.create("application/json".toMediaTypeOrNull(), updateValue)


        val updateDealerInfo: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.updateDealer(user, dealer, value)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        updateDealerInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        updateDealerInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Update Dealer", ": " + e.message)
        }
        return updateDealerInfo
    }


    fun updateOther(
        userId: String,
        otherId: String,
        updateValue: String

    ): MutableLiveData<Resource<CommonSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userId)
        val other = RequestBody.create("application/json".toMediaTypeOrNull(), otherId)
        val value = RequestBody.create("application/json".toMediaTypeOrNull(), updateValue)

        val updateOtherInfo: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.updateOtherParty(user, other, value)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        updateOtherInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        updateOtherInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("Update Other", ": " + e.message)
        }
        return updateOtherInfo
    }


    fun saveRetailer(
        userID: String,
        values: String
    ): MutableLiveData<Resource<RetailerSaveResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val retailerAdd: MutableLiveData<Resource<RetailerSaveResponse>> = MutableLiveData<Resource<RetailerSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveNewRetailer(user, infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        retailerAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        retailerAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "retailerAdd: " + e.message)
        }
        return retailerAdd
    }

    fun saveOtherCustomer(
        userID: String,
        cusType: String,
        values: String
    ): MutableLiveData<Resource<OtherNewCustomerResponse>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val type = RequestBody.create("application/json".toMediaTypeOrNull(), cusType)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val otherCusAdd: MutableLiveData<Resource<OtherNewCustomerResponse>> = MutableLiveData<Resource<OtherNewCustomerResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.saveNewCustomer(user,type, infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }




    fun uploadBusinessCard(
        user_id: String,
        party_code: String,
        doc_ext: String,
        all_images: ArrayList<File>

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val partyNo = RequestBody.create("application/json".toMediaTypeOrNull(), party_code)
        val ext = RequestBody.create("application/json".toMediaTypeOrNull(), doc_ext)

        val processImage: ArrayList<MultipartBody.Part> = ArrayList()
        var requestFile: RequestBody
        if (all_images.size > 0) {
            for (item in all_images) {
                // requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), item.absoluteFile)
                requestFile = if (doc_ext != "pdf") {
                    RequestBody.create("image/*".toMediaTypeOrNull(), all_images[0])
                } else {
                    RequestBody.create("application/pdf".toMediaTypeOrNull(), all_images[0])
                }

                processImage.add(
                    MultipartBody.Part.createFormData(
                        "all_images",
                        item.name,
                        requestFile
                    )
                )
            }
        } else {

            requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), "")
            processImage.add(MultipartBody.Part.createFormData("all_images", "", requestFile))
        }

        val saveCommonExpense: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.uploadVisitingCard(
                username,
                partyNo,
                ext,
                processImage[0]

            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCommonExpense.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCommonExpense.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "ERROR: " + e.message)
        }
        return saveCommonExpense
    }


    @SuppressLint("CheckResult")
    fun uploadCustomerFile(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>

    ): MutableLiveData<Resource<CommonSaveResponse>> {
        val username = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val partyCo = RequestBody.create("application/json".toMediaTypeOrNull(), party_code)
        val partyTy = RequestBody.create("application/json".toMediaTypeOrNull(), party_type)
        val docTy = RequestBody.create("application/json".toMediaTypeOrNull(), doc_type)
        val ext = RequestBody.create("application/json".toMediaTypeOrNull(), doc_ext)

        val processImage: ArrayList<MultipartBody.Part> = ArrayList()
        var requestFile: RequestBody
        if (all_images.size > 0) {
            for (item in all_images) {
                requestFile = if (doc_ext != "pdf") {
                    RequestBody.create("image/*".toMediaTypeOrNull(), all_images[0])
                } else {
                    RequestBody.create("application/pdf".toMediaTypeOrNull(), all_images[0])
                }

                processImage.add(
                    MultipartBody.Part.createFormData(
                        "all_images",
                        item.name,
                        requestFile
                    )
                )
            }
        } else {

            requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), "")
            processImage.add(MultipartBody.Part.createFormData("all_images", "", requestFile))
        }

        val saveCommonExpense: MutableLiveData<Resource<CommonSaveResponse>> =
            MutableLiveData<Resource<CommonSaveResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstance.create(
                GetDataService::class.java
            )
            service.uploadCustomerDoc(
                username,
                partyCo,
                partyTy,
                docTy,
                ext,
                processImage[0]

            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        saveCommonExpense.postValue(Resource.success(response))
                    },
                    { error ->
                        saveCommonExpense.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "ERROR: " + e.message)
        }
        return saveCommonExpense
    }


    //todo ts
    fun saveNewOtherCustomer(
        userID: String,
        cusType: String,
        values: String
    ): MutableLiveData<Resource<SaveNewPartyResponce>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val type = RequestBody.create("application/json".toMediaTypeOrNull(), cusType)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val otherCusAdd: MutableLiveData<Resource<SaveNewPartyResponce>> = MutableLiveData<Resource<SaveNewPartyResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.saveNewTSParty(user,type, infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }

//todo fun siteverification

    fun opprtsiteVrfSave(
        userID: String,
        values: String
    ): MutableLiveData<Resource<SiteVerificationSaveResponce>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val otherCusAdd: MutableLiveData<Resource<SiteVerificationSaveResponce>> = MutableLiveData<Resource<SiteVerificationSaveResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.OpprtsiteVrfSave(user, infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo GetSalesOthPartner
    fun GetSalesOthPartner(
        xhrc1: String,
        xhrc2: String,
        xhrc3: String,
        xhrc4: String,
        xtype: String
    ): MutableLiveData<Resource<GetSalesOthPartnerResponce>> {

        val xhrc1 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc1)
        val xhrc2 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc2)
        val xhrc3 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc3)

        val xhrc4 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc4)
        val xtype = RequestBody.create("application/json".toMediaTypeOrNull(), xtype)

        val otherCusAdd: MutableLiveData<Resource<GetSalesOthPartnerResponce>> = MutableLiveData<Resource<GetSalesOthPartnerResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetSalesOthPartner(xhrc1,xhrc2,xhrc3,xhrc4,xtype)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }

//todo update site

    fun updateSaleBPTsNewSite(
        userID: String,
        projId : String,
        values: String
    ): MutableLiveData<Resource<updateSaleBPNewSiteResponce>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val projId = RequestBody.create("application/json".toMediaTypeOrNull(), projId)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val otherCusAdd: MutableLiveData<Resource<updateSaleBPNewSiteResponce>> = MutableLiveData<Resource<updateSaleBPNewSiteResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.updateSaleBPTsNewSite(user,projId,infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo save new site ts
    fun saveSaleBPTsNewSite(
        userID: String,
        values: String
    ): MutableLiveData<Resource<saveSaleBPNewSiteResponce>> {

        val user = RequestBody.create("application/json".toMediaTypeOrNull(), userID)
        val infos = RequestBody.create("application/json".toMediaTypeOrNull(), values)

        val otherCusAdd: MutableLiveData<Resource<saveSaleBPNewSiteResponce>> = MutableLiveData<Resource<saveSaleBPNewSiteResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.saveSaleBPNewSite(user, infos)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }

     //todo GetSiteSumInfo
     fun getSiteSumInfo(
         site_id   : String,
         from_date : String,
         to_date   :String
     ): MutableLiveData<Resource<GetSiteSumInfo>> {

         val site_id = RequestBody.create("application/json".toMediaTypeOrNull(), site_id)
         val from_date = RequestBody.create("application/json".toMediaTypeOrNull(), from_date)
         val to_date = RequestBody.create("application/json".toMediaTypeOrNull(), to_date)

         val otherCusAdd: MutableLiveData<Resource<GetSiteSumInfo>> = MutableLiveData<Resource<GetSiteSumInfo>>()

         try {
             val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                 GetDataService::class.java
             )
             service.GetSiteSumInfo(site_id, from_date,to_date)
                 ?.toObservable()
                 ?.subscribeOn(Schedulers.io())
                 ?.observeOn(AndroidSchedulers.mainThread())
                 ?.subscribe(
                     { response ->
                         otherCusAdd.postValue(Resource.success(response))
                     },
                     { error ->
                         otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                     },
                 )

         } catch (e: Exception) {
             Log.e("RetailerRepository", "otherCusAdd: " + e.message)
         }
         return otherCusAdd
     }



    //todo ts site imgUpload
    fun uploadTSSiteCover(
        user_id: String,
        party_image: File,
        site_code: String,
        site_lat: String,
        site_lon: String
    ): MutableLiveData<Resource<saveTSSiteImgResponce>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val site_code = RequestBody.create("application/json".toMediaTypeOrNull(), site_code)
        val site_lat = RequestBody.create("application/json".toMediaTypeOrNull(), site_lat)
        val site_lon = RequestBody.create("application/json".toMediaTypeOrNull(), site_lon)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), party_image)

        val coverPhoto: MultipartBody.Part =
            MultipartBody.Part.createFormData("site_image", party_image.name, requestFile)


        val uploadImage: MutableLiveData<Resource<saveTSSiteImgResponce>> =
            MutableLiveData<Resource<saveTSSiteImgResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.uploadTSSiteCover(
                userId,
                coverPhoto,
                site_code,
                site_lat,
                site_lon
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        uploadImage.postValue(Resource.success(response))
                    },
                    { error ->
                        uploadImage.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRep", "ERROR Upload Cover: " + e.message)
        }
        return uploadImage
    }


    //todo for get saved site list
    fun GetTSSiteInfo(
        xhrc1: String,
        xhrc2: String,
        xhrc3: String,
        xhrc4: String
    ): MutableLiveData<Resource<GetTSSiteInfoResponce>> {

        val xhrc1 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc1)
        val xhrc2 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc2)
        val xhrc3 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc3)
        val xhrc4 = RequestBody.create("application/json".toMediaTypeOrNull(), xhrc4)


        val otherCusAdd: MutableLiveData<Resource<GetTSSiteInfoResponce>> = MutableLiveData<Resource<GetTSSiteInfoResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetTSSiteInfo(xhrc1, xhrc2,xhrc3,xhrc4)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo get taggedsite

    fun GetBPSiteInfo(
        xparty: String
    ): MutableLiveData<Resource<GetBPSiteInfoResponce>> {

        val xparty = RequestBody.create("application/json".toMediaTypeOrNull(), xparty)



        val otherCusAdd: MutableLiveData<Resource<GetBPSiteInfoResponce>> = MutableLiveData<Resource<GetBPSiteInfoResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetBPSiteInfo(xparty)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo for tag quickvisit to site callrecord
    fun TSCallMove4Tag(
        user_id: String,
        mov_doc: String,
        mov_row: String,
        call_info: String
    ): MutableLiveData<Resource<TSCallMove4TagResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val mov_doc = RequestBody.create("application/json".toMediaTypeOrNull(), mov_doc)
        val mov_row = RequestBody.create("application/json".toMediaTypeOrNull(), mov_row)
        val call_info = RequestBody.create("application/json".toMediaTypeOrNull(), call_info)


        val otherCusAdd: MutableLiveData<Resource<TSCallMove4TagResponce>> = MutableLiveData<Resource<TSCallMove4TagResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.TSCallMove4Tag(user_id, mov_doc,mov_row,call_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }

    //todo ts  quickvisit save
    fun TSQuickVisitSave(
        user_id: String,
        profile_info: String
    ): MutableLiveData<Resource<TSQuickVisitSaveResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val profile_info = RequestBody.create("application/json".toMediaTypeOrNull(), profile_info)


        val otherCusAdd: MutableLiveData<Resource<TSQuickVisitSaveResponce>> = MutableLiveData<Resource<TSQuickVisitSaveResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.TSQuickVisitSave(user_id, profile_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo for site tag

    fun SaleBPSiteTag(
        user_id: String,
        profile_info: String
    ): MutableLiveData<Resource<SaleBPSiteTagResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val profile_info = RequestBody.create("application/json".toMediaTypeOrNull(), profile_info)


        val otherCusAdd: MutableLiveData<Resource<SaleBPSiteTagResponce>> = MutableLiveData<Resource<SaleBPSiteTagResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.SaleBPSiteTag(user_id, profile_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }



  //  todo direct call record /complain TSCallMoveDirectSave
  fun TSCallMoveDirectSave(
      user_id: String,
      call_info: String
  ): MutableLiveData<Resource<TSCallMoveDirectSaveResponce>> {

      val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
      val call_info = RequestBody.create("application/json".toMediaTypeOrNull(), call_info)


      val otherCusAdd: MutableLiveData<Resource<TSCallMoveDirectSaveResponce>> = MutableLiveData<Resource<TSCallMoveDirectSaveResponce>>()

      try {
          val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
              GetDataService::class.java
          )
          service.TSCallMoveDirectSave(user_id, call_info)
              ?.toObservable()
              ?.subscribeOn(Schedulers.io())
              ?.observeOn(AndroidSchedulers.mainThread())
              ?.subscribe(
                  { response ->
                      otherCusAdd.postValue(Resource.success(response))
                  },
                  { error ->
                      otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                  },
              )

      } catch (e: Exception) {
          Log.e("RetailerRepository", "otherCusAdd: " + e.message)
      }
      return otherCusAdd
  }


    //todo for tagged site from quickvisit to callrecord

    fun GetTSCallTag(
        user_id: String,
        move_date: String
    ): MutableLiveData<Resource<GetTSCallTagResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val move_date = RequestBody.create("application/json".toMediaTypeOrNull(), move_date)


        val otherCusAdd: MutableLiveData<Resource<GetTSCallTagResponce>> = MutableLiveData<Resource<GetTSCallTagResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetTSCallTag(user_id, move_date)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }


    //todo for complain doc upload

    fun uploadComplainDoc(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext:String,
        party_image: File
    ): MutableLiveData<Resource<PartyComplainDocResponce>> {
        val userId = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val party_code = RequestBody.create("application/json".toMediaTypeOrNull(), party_code)
        val party_type = RequestBody.create("application/json".toMediaTypeOrNull(), party_type)
        val doc_type = RequestBody.create("application/json".toMediaTypeOrNull(), doc_type)
        val doc_ext = RequestBody.create("application/json".toMediaTypeOrNull(), doc_ext)


        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), party_image)

        val complainPhoto: MultipartBody.Part =
            MultipartBody.Part.createFormData("all_images", party_image.name, requestFile)


        val uploadImage: MutableLiveData<Resource<PartyComplainDocResponce>> =
            MutableLiveData<Resource<PartyComplainDocResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.uploadComplainDoc(
                userId,
                party_code,
                party_type,
                doc_type,
                doc_ext,
                complainPhoto
            )
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        uploadImage.postValue(Resource.success(response))
                    },
                    { error ->
                        uploadImage.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRep", "ERROR Upload Cover: " + e.message)
        }
        return uploadImage
    }




    //getTopic
    fun getTopic(
        code_type: String
    ): MutableLiveData<Resource<TopicResponce>> {

        val code_type = RequestBody.create("application/json".toMediaTypeOrNull(), code_type)


        val otherCusAdd: MutableLiveData<Resource<TopicResponce>> = MutableLiveData<Resource<TopicResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetCodeType(code_type)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        otherCusAdd.postValue(Resource.success(response))
                    },
                    { error ->
                        otherCusAdd.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "otherCusAdd: " + e.message)
        }
        return otherCusAdd
    }



    //todo GetContractorMason
    @SuppressLint("CheckResult")
    fun getContractorMason(
        contractor: String
    ): MutableLiveData<Resource<GetContractorMasonResponce>> {

        val contractor_code = RequestBody.create("application/json".toMediaTypeOrNull(), contractor)


        val masonInfo: MutableLiveData<Resource<GetContractorMasonResponce>> = MutableLiveData<Resource<GetContractorMasonResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetContractorMason(contractor_code)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        masonInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        masonInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "masonInfo: " + e.message)
        }
        return masonInfo
    }

//todo masontag
    @SuppressLint("ContMasonTag")
    fun ContMasonTag(
        user_id: String,
        profile_info: String

        ): MutableLiveData<Resource<MasonTagResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val profile_info = RequestBody.create("application/json".toMediaTypeOrNull(), profile_info)


        val masonInfo: MutableLiveData<Resource<MasonTagResponce>> = MutableLiveData<Resource<MasonTagResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.ContMasonTag(user_id,profile_info)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        masonInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        masonInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "masonInfo: " + e.message)
        }
        return masonInfo
    }




    //todo

    @SuppressLint("ContMasonTag")
    fun GetSalesMoveCall(
        site_id: String

    ): MutableLiveData<Resource<GetSalesMoveCall>> {

        val site_id = RequestBody.create("application/json".toMediaTypeOrNull(), site_id)


        val callInfo: MutableLiveData<Resource<GetSalesMoveCall>> = MutableLiveData<Resource<GetSalesMoveCall>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetSalesMoveCall(site_id)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        callInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "deliveryInfo: " + e.message)
        }
        return callInfo
    }




    //todo PickDocWPart
    @SuppressLint("PickDocWPart")
    fun getPickDocWPart(
        app_proj: String,
        doctype: String,
        partName: String

    ): MutableLiveData<Resource<PickDocWPartResponse>> {

        val app_proj = RequestBody.create("application/json".toMediaTypeOrNull(), app_proj)
        val doctype = RequestBody.create("application/json".toMediaTypeOrNull(), doctype)
        val partName = RequestBody.create("application/json".toMediaTypeOrNull(), partName)


        val callInfo: MutableLiveData<Resource<PickDocWPartResponse>> = MutableLiveData<Resource<PickDocWPartResponse>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.PickDocWPart(app_proj,doctype,partName)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        callInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "deliveryInfo: " + e.message)
        }
        return callInfo
    }



   //todo  getPickDocWPart
   @SuppressLint("PickDocWPart")
   fun GtPickDocWPart(
       app_proj: String,
       doctype: String

   ): MutableLiveData<Resource<getPickDocWPartResponse>> {

       val app_proj = RequestBody.create("application/json".toMediaTypeOrNull(), app_proj)
       val doctype = RequestBody.create("application/json".toMediaTypeOrNull(), doctype)


       val callInfo: MutableLiveData<Resource<getPickDocWPartResponse>> = MutableLiveData<Resource<getPickDocWPartResponse>>()

       try {
           val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
               GetDataService::class.java
           )
           service.getPickDocWPart(app_proj,doctype)
               ?.toObservable()
               ?.subscribeOn(Schedulers.io())
               ?.observeOn(AndroidSchedulers.mainThread())
               ?.subscribe(
                   { response ->
                       callInfo.postValue(Resource.success(response))
                   },
                   { error ->
                       callInfo.postValue(Resource.error(error.message.toString(), null))
                   },
               )

       } catch (e: Exception) {
           Log.e("RetailerRepository", "deliveryInfo: " + e.message)
       }
       return callInfo
   }




    //todo GetSalesCallApp
    @SuppressLint("GetSalesCallApp")
    fun getSalesCallApp(
        user_id : String
    ): MutableLiveData<Resource<GetSalesCallAppResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)


        val callInfo: MutableLiveData<Resource<GetSalesCallAppResponce>> = MutableLiveData<Resource<GetSalesCallAppResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.GetSalesCallApp(user_id)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        callInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "deliveryInfo: " + e.message)
        }
        return callInfo
    }



    //todo AppSalesCall
    @SuppressLint("AppSalesCall")
    fun getAppSalesCall(
        user_id : String,
        call_trn : String
    ): MutableLiveData<Resource<AppSalesCallresponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val call_trn = RequestBody.create("application/json".toMediaTypeOrNull(), call_trn)


        val callInfo: MutableLiveData<Resource<AppSalesCallresponce>> = MutableLiveData<Resource<AppSalesCallresponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.AppSalesCall(user_id,call_trn)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        callInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "deliveryInfo: " + e.message)
        }
        return callInfo
    }



    //todo CancelSalesCall
    @SuppressLint("CancelSalesCall")
    fun GetCancelSalesCall(
        user_id : String,
        call_trn : String,
        rej_reas : String
    ): MutableLiveData<Resource<CancelSalesCallResponce>> {

        val user_id = RequestBody.create("application/json".toMediaTypeOrNull(), user_id)
        val call_trn = RequestBody.create("application/json".toMediaTypeOrNull(), call_trn)
        val rej_reas = RequestBody.create("application/json".toMediaTypeOrNull(), rej_reas)


        val callInfo: MutableLiveData<Resource<CancelSalesCallResponce>> = MutableLiveData<Resource<CancelSalesCallResponce>>()

        try {
            val service: GetDataService = RetrofitClientInstance.retrofitInstanceTS.create(
                GetDataService::class.java
            )
            service.CancelSalesCall(user_id,call_trn,rej_reas)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        callInfo.postValue(Resource.success(response))
                    },
                    { error ->
                        callInfo.postValue(Resource.error(error.message.toString(), null))
                    },
                )

        } catch (e: Exception) {
            Log.e("RetailerRepository", "deliveryInfo: " + e.message)
        }
        return callInfo
    }



}



