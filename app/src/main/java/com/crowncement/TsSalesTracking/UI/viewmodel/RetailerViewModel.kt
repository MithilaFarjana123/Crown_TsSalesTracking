package com.crowncement.TsSalesTracking.UI.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crowncement.TsSalesTracking.common.Resource
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.RetailerRepository

import java.io.File

class RetailerViewModel : ViewModel() {

    private var divisionListMutableLiveData: MutableLiveData<Resource<DivisionResponse>>? = null
    private var districtMutableLiveData: MutableLiveData<Resource<DistrictResponse>>? = null
    private var thanaListMutableLiveData: MutableLiveData<Resource<ThanaResponse>>? = null
    private var routeListMutableLiveData: MutableLiveData<Resource<RouteResponse>>? = null


    private var retailerListMutableLiveData: MutableLiveData<Resource<RetailerResponse>>? = null
    private var dealerListMutableLiveData: MutableLiveData<Resource<DealerResponse>>? = null
    private var otherCustomerListMutableLiveData: MutableLiveData<Resource<OtherCustomerResponse>>? =
        null


    private var updateMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var updateDealerMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var updateOtherMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? = null
    private var uploadCoverMutableLiveData: MutableLiveData<Resource<CoverImageUploadResponse>>? =null
    private var saveRetailerMutableLiveData: MutableLiveData<Resource<RetailerSaveResponse>>? =null
    private var saveOtherCustomerMutableLiveData: MutableLiveData<Resource<OtherNewCustomerResponse>>? =null
    private var UploadDocMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? =null

    private var cusDocMutableLiveData: MutableLiveData<Resource<CommonSaveResponse>>? =null


    //ts
    private var saveTSOtherCustomerMutableLiveData: MutableLiveData<Resource<SaveNewPartyResponce>>? =null
    private var GetSalesOthPartnerResponceMutableLiveData: MutableLiveData<Resource<GetSalesOthPartnerResponce>>? =null
    private var SaveSaleBPTsNewSiteMutableLiveData: MutableLiveData<Resource<saveSaleBPNewSiteResponce>>? =null
    private var updateSaleBPTsNewSiteMutableLiveData: MutableLiveData<Resource<updateSaleBPNewSiteResponce>>? =null

    private var OpprtsiteVrfSaveMutableLiveData: MutableLiveData<Resource<SiteVerificationSaveResponce>>? =null


    private var uploadTSSiteCoverMutableLiveData: MutableLiveData<Resource<saveTSSiteImgResponce>>? =null
    private var GetTSSiteInfoMutableLiveData: MutableLiveData<Resource<GetTSSiteInfoResponce>>? =null
    private var TSCallMove4TagMutableLiveData: MutableLiveData<Resource<TSCallMove4TagResponce>>? =null
    private var TSQuickVisitSaveResponceMutableLiveData : MutableLiveData<Resource<TSQuickVisitSaveResponce>>? =null
    private var SaleBPSiteTagMutableLiveData : MutableLiveData<Resource<SaleBPSiteTagResponce>>? =null
    private var TSCallMoveDirectSaveMutableLiveData : MutableLiveData<Resource<TSCallMoveDirectSaveResponce>>? =null
    private var GetTSCallTagMutableLiveData : MutableLiveData<Resource<GetTSCallTagResponce>>? =null
    private var uploadComplainDocCoverMutableLiveData : MutableLiveData<Resource<PartyComplainDocResponce>>? =null
    private var GetBPSiteInfoMutableLiveData : MutableLiveData<Resource<GetBPSiteInfoResponce>>? =null
    private var TopicMutableLiveData : MutableLiveData<Resource<TopicResponce>>? =null

    private var getContractorMasonMutableLiveData : MutableLiveData<Resource<GetContractorMasonResponce>>? =null
    private var ContMasonTagMutableLiveData : MutableLiveData<Resource<MasonTagResponce>>? =null

    private var GetSiteSumInfoMutableLiveData : MutableLiveData<Resource<GetSiteSumInfo>>? =null

    private var GetSalesMoveCallMutableLiveData : MutableLiveData<Resource<GetSalesMoveCall>>? =null
    private var PickDocWPartMutableLiveData : MutableLiveData<Resource<PickDocWPartResponse>>? =null
    private var getPickDocWPartMutableLiveData : MutableLiveData<Resource<getPickDocWPartResponse>>? =null

    private var getSalesCallAppMutableLiveData : MutableLiveData<Resource<GetSalesCallAppResponce>>? =null

    private var getAppSalesCallMutableLiveData : MutableLiveData<Resource<AppSalesCallresponce>>? =null
    private var getCancelSalesCallMutableLiveData : MutableLiveData<Resource<CancelSalesCallResponce>>? =null




    fun getDivisionInfo(country: String): MutableLiveData<Resource<DivisionResponse>> {
        divisionListMutableLiveData = RetailerRepository.getDivisionInfo(country)
        return divisionListMutableLiveData!!
    }

    fun getDistrictInfo(
        country: String,
        division: String
    ): MutableLiveData<Resource<DistrictResponse>> {
        districtMutableLiveData = RetailerRepository.getDistrictInfo(country, division)
        return districtMutableLiveData!!
    }

    fun getThanaInfo(
        country: String,
        division: String,
        district: String
    ): MutableLiveData<Resource<ThanaResponse>> {
        thanaListMutableLiveData = RetailerRepository.getThanaInfo(country, division, district)
        return thanaListMutableLiveData!!
    }

    fun getRouteInfo(
        country: String,
        division: String,
        district: String,
        thana: String
    ): MutableLiveData<Resource<RouteResponse>> {
        routeListMutableLiveData =
            RetailerRepository.getRouteInfo(country, division, district, thana)
        return routeListMutableLiveData!!
    }

    //-------------------------------------------CROWN CUSTOMER LIST----------------------------------------

    fun getRetailerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        route: String
    ): MutableLiveData<Resource<RetailerResponse>> {
        retailerListMutableLiveData =
            RetailerRepository.getRetailerList(country, division, district, thana, route)
        return retailerListMutableLiveData!!
    }

    fun getDealerList(
        country: String,
        division: String,
        district: String
    ): MutableLiveData<Resource<DealerResponse>> {
        dealerListMutableLiveData = RetailerRepository.getDealerList(country, division, district)
        return dealerListMutableLiveData!!
    }

    fun getOtherList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xcode: String
    ): MutableLiveData<Resource<OtherCustomerResponse>> {
        otherCustomerListMutableLiveData =
            RetailerRepository.getOtherCustomerList(country, division, district, thana, xcode)
        return otherCustomerListMutableLiveData!!
    }

    //todo for mason
    fun getOtherMasonList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xcode: String
    ): MutableLiveData<Resource<OtherCustomerResponse>> {
        otherCustomerListMutableLiveData =
            RetailerRepository.getOtherMasonList(country, division, district, thana, xcode)
        return otherCustomerListMutableLiveData!!
    }


    fun updateRetailer(
        user: String,
        retailer: String,
        value: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        updateMutableLiveData = RetailerRepository.updateRetailer(user, retailer, value)
        return updateMutableLiveData!!
    }

    fun uploadCover(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File
    ): MutableLiveData<Resource<CoverImageUploadResponse>> {
        uploadCoverMutableLiveData =
            RetailerRepository.uploadCoverPhoto(
                user_id,
                party_code,
                party_type,
                party_lat,
                party_lon,
                party_image
            )
        return uploadCoverMutableLiveData!!
    }

    fun updateDealer(
        user: String,
        dealer: String,
        value: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        updateDealerMutableLiveData = RetailerRepository.updateDealer(user, dealer, value)
        return updateDealerMutableLiveData!!
    }

    fun updateOther(
        user: String,
        other: String,
        value: String
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        updateOtherMutableLiveData = RetailerRepository.updateOther(user, other, value)
        return updateOtherMutableLiveData!!
    }

    fun saveRetailer(
        user: String,
        value: String
    ): MutableLiveData<Resource<RetailerSaveResponse>> {
        saveRetailerMutableLiveData = RetailerRepository.saveRetailer(user, value)
        return saveRetailerMutableLiveData!!
    }

    fun saveNewCustomer(
        user: String,
        type: String,
        value: String
    ): MutableLiveData<Resource<OtherNewCustomerResponse>> {
        saveOtherCustomerMutableLiveData = RetailerRepository.saveOtherCustomer(user, type,value)
        return saveOtherCustomerMutableLiveData!!
    }


    fun uploadBusinessCard(
        user_id: String,
        party_code: String,
        doc_ext: String,
        all_images: ArrayList<File>
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        UploadDocMutableLiveData = RetailerRepository.uploadBusinessCard(user_id, party_code,doc_ext,all_images)
        return UploadDocMutableLiveData!!
    }

    fun uploadCusFile(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>
    ): MutableLiveData<Resource<CommonSaveResponse>> {
        cusDocMutableLiveData = RetailerRepository.uploadCustomerFile(user_id, party_code,party_type,doc_type,doc_ext,all_images)
        return cusDocMutableLiveData!!
    }


    //todo newly
    fun saveNewOtherCustomer(
        user: String,
        type: String,
        value: String
    ): MutableLiveData<Resource<SaveNewPartyResponce>> {
        saveTSOtherCustomerMutableLiveData = RetailerRepository.saveNewOtherCustomer(user, type,value)
        return saveTSOtherCustomerMutableLiveData!!
    }


    //todo siteverification
    fun OpprtsiteVrfSave(
        user: String,
        value: String
    ): MutableLiveData<Resource<SiteVerificationSaveResponce>> {
        OpprtsiteVrfSaveMutableLiveData = RetailerRepository.opprtsiteVrfSave(user, value)
        return OpprtsiteVrfSaveMutableLiveData!!
    }



    ////todo ts GetSalesOthPartner
    fun GetSalesOthPartnerTS(
        xhrc1: String,
        xhrc2: String,
        xhrc3: String,
        xhrc4: String,
        xtype: String
    ): MutableLiveData<Resource<GetSalesOthPartnerResponce>> {
        GetSalesOthPartnerResponceMutableLiveData = RetailerRepository.GetSalesOthPartner(xhrc1, xhrc2,xhrc3,xhrc4,xtype)
        return GetSalesOthPartnerResponceMutableLiveData!!
    }


    //todo for site add
    fun SaveSaleBPTsNewSite(
        user: String,
        value: String
    ): MutableLiveData<Resource<saveSaleBPNewSiteResponce>> {
        SaveSaleBPTsNewSiteMutableLiveData = RetailerRepository.saveSaleBPTsNewSite(user,value)
        return SaveSaleBPTsNewSiteMutableLiveData!!
    }


    //todo for getSiteSumInfo
    fun getSiteSumInfo(
        site_id   : String,
        from_date : String,
        to_date   :String

    ): MutableLiveData<Resource<GetSiteSumInfo>> {
        GetSiteSumInfoMutableLiveData = RetailerRepository.getSiteSumInfo(site_id,from_date,to_date)
        return GetSiteSumInfoMutableLiveData!!
    }


    //todo UpdateSite
    fun UpdateSaleBPTsNewSite(
        user: String,
        projId : String,
        value: String
    ): MutableLiveData<Resource<updateSaleBPNewSiteResponce>> {
        updateSaleBPTsNewSiteMutableLiveData = RetailerRepository.updateSaleBPTsNewSite(user,projId,value)
        return updateSaleBPTsNewSiteMutableLiveData!!
    }


    //todo ts uploadTSSiteCover
    fun uploadTSSiteCover(
        user_id: String,
        party_image: File,
        site_code: String,
        site_lat: String,
        site_lon: String
    ): MutableLiveData<Resource<saveTSSiteImgResponce>> {
        uploadTSSiteCoverMutableLiveData =
            RetailerRepository.uploadTSSiteCover(
                user_id,
                party_image,
                site_code,
                site_lat,
                site_lon

            )
        return uploadTSSiteCoverMutableLiveData!!
    }


    //todo for get saved site info

    fun getSavedSiteList(
        xhrc1: String,
        xhrc2: String,
        xhrc3: String,
        xhrc4: String
    ): MutableLiveData<Resource<GetTSSiteInfoResponce>> {
        GetTSSiteInfoMutableLiveData = RetailerRepository.GetTSSiteInfo(xhrc1,xhrc2,xhrc3,xhrc4)
        return GetTSSiteInfoMutableLiveData!!
    }
//todo get taggedsite

    fun GetBPSiteInfoList(
                xparty: String
    ): MutableLiveData<Resource<GetBPSiteInfoResponce>> {
        GetBPSiteInfoMutableLiveData = RetailerRepository.GetBPSiteInfo(xparty)
        return GetBPSiteInfoMutableLiveData!!
    }


    //todo ts call record
    fun TSCallMove4Tag(
        user_id: String,
        mov_doc: String,
        mov_row: String,
        call_info: String
    ): MutableLiveData<Resource<TSCallMove4TagResponce>> {
        TSCallMove4TagMutableLiveData = RetailerRepository.TSCallMove4Tag(user_id,mov_doc,mov_row,call_info)
        return TSCallMove4TagMutableLiveData!!
    }


    //todo ts  quickvisit save

    fun TSQuickVisitSave(
        user_id: String,
        profile_info: String
    ): MutableLiveData<Resource<TSQuickVisitSaveResponce>> {
        TSQuickVisitSaveResponceMutableLiveData = RetailerRepository.TSQuickVisitSave(user_id,profile_info)
        return TSQuickVisitSaveResponceMutableLiveData!!
    }


    //todo for site tag
    fun SaleBPSiteTag(
        user_id: String,
        profile_info: String
    ): MutableLiveData<Resource<SaleBPSiteTagResponce>> {
        SaleBPSiteTagMutableLiveData = RetailerRepository.SaleBPSiteTag(user_id,profile_info)
        return SaleBPSiteTagMutableLiveData!!
    }



    //  todo direct call record /complain TSCallMoveDirectSave
    fun TSCallMoveDirectSave(
        user_id: String,
        call_info: String
    ): MutableLiveData<Resource<TSCallMoveDirectSaveResponce>> {
        TSCallMoveDirectSaveMutableLiveData = RetailerRepository.TSCallMoveDirectSave(user_id,call_info)
        return TSCallMoveDirectSaveMutableLiveData!!
    }


    //todo for tagged site from quickvisit to callrecord


    fun GetTSCallTag(
        user_id: String,
        move_date: String
    ): MutableLiveData<Resource<GetTSCallTagResponce>> {
        GetTSCallTagMutableLiveData = RetailerRepository.GetTSCallTag(user_id,move_date)
        return GetTSCallTagMutableLiveData!!
    }

    //todo for complain doc upload

    fun uploadComplainDocCover(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        party_image: File
    ): MutableLiveData<Resource<PartyComplainDocResponce>> {
        uploadComplainDocCoverMutableLiveData =
            RetailerRepository.uploadComplainDoc(
                user_id,
                party_code,
                party_type,
                doc_type,
                doc_ext,
                party_image

            )
        return uploadComplainDocCoverMutableLiveData!!
    }





    fun getTopic(
        code_type: String
    ): MutableLiveData<Resource<TopicResponce>> {
        TopicMutableLiveData =
            RetailerRepository.getTopic(
                code_type
            )
        return TopicMutableLiveData!!
    }


    //todo GetContractorMason

    fun getContractorMason(
        contractor_code: String
    ): MutableLiveData<Resource<GetContractorMasonResponce>> {
        getContractorMasonMutableLiveData =
            RetailerRepository.getContractorMason(
                contractor_code
            )
        return getContractorMasonMutableLiveData!!
    }


//TODO MASONtag
    fun ContMasonTag(
    user_id: String,
    profile_info: String
    ): MutableLiveData<Resource<MasonTagResponce>> {
    ContMasonTagMutableLiveData =
            RetailerRepository.ContMasonTag(
                user_id,profile_info
            )
        return ContMasonTagMutableLiveData!!
    }

//todo GetSalesMoveCall
@SuppressLint("SuspiciousIndentation")
fun getSalesMoveCall(
    site_id: String
    ): MutableLiveData<Resource<GetSalesMoveCall>> {
    GetSalesMoveCallMutableLiveData = RetailerRepository.GetSalesMoveCall(site_id)
        return GetSalesMoveCallMutableLiveData!!
    }




    //todo PickDocWPart
    @SuppressLint("PickDocWPart")
    fun PickDocWPart(
        app_proj: String,
        doctype: String,
        partName: String
    ): MutableLiveData<Resource<PickDocWPartResponse>> {
        PickDocWPartMutableLiveData = RetailerRepository.getPickDocWPart(app_proj,doctype,partName)
        return PickDocWPartMutableLiveData!!
    }



    //todo GtPickDocWPart
    @SuppressLint("PickDocWPart")
    fun getPickDocWPart(
        app_proj: String,
        doctype: String
    ): MutableLiveData<Resource<getPickDocWPartResponse>> {
        getPickDocWPartMutableLiveData = RetailerRepository.GtPickDocWPart(app_proj,doctype)
        return getPickDocWPartMutableLiveData!!
    }


    //todo getSalesCallApp

    @SuppressLint("GetSalesCallApp")
    fun GetSalesCallApp(
        user_id: String
    ): MutableLiveData<Resource<GetSalesCallAppResponce>> {
        getSalesCallAppMutableLiveData = RetailerRepository.getSalesCallApp(user_id)
        return getSalesCallAppMutableLiveData!!
    }


    //todo AppSalesCall
    fun getAppSalesCall(
        user_id: String,
        call_trn : String
    ): MutableLiveData<Resource<AppSalesCallresponce>> {
        getAppSalesCallMutableLiveData = RetailerRepository.getAppSalesCall(user_id,call_trn)
        return getAppSalesCallMutableLiveData!!
    }


//todo CancelSalesCall
fun getCancelSalesCall(
    user_id : String,
    call_trn : String,
    rej_reas : String
): MutableLiveData<Resource<CancelSalesCallResponce>> {
    getCancelSalesCallMutableLiveData = RetailerRepository.GetCancelSalesCall(user_id,call_trn,rej_reas)
    return getCancelSalesCallMutableLiveData!!
}



}

