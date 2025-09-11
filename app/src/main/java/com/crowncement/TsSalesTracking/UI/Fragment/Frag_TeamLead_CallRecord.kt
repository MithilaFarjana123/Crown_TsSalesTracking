package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragTeamLeadCallRecordBinding
import java.util.Calendar


class Frag_TeamLead_CallRecord : Fragment() {

    private var _binding: FragTeamLeadCallRecordBinding? = null
    private val binding get() = _binding!!
    lateinit var retailerViewModel: RetailerViewModel
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var nextVisitDate =""


    var division = ""
    var district = ""
    var thana = ""
    var selectedCustomer = ""
    lateinit var other: OtherCustomerChild



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragTeamLeadCallRecordBinding.inflate(inflater, container, false)


        

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RetailerViewModelFactory()
        setupViewModel()

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"

        onclickListener()

    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }


     fun onclickListener(){
        binding.btnVerify.setOnClickListener{
            if(validation()){
              //  confirmationDialog()
            }
        }
    }


    fun validation(): Boolean {
        var result = false

        //yes
        binding.layoutVerificationInfo.isErrorEnabled = false
        binding.layoutVerificationInfo.error = null

        if(binding.txtVerificationInfo.text.toString().isEmpty()) {
            result = false
            binding.layoutVerificationInfo.isErrorEnabled = true
            binding.layoutVerificationInfo.error = "Verification details is required"
        }else{
            result = true

        }

        return result

    }

/*
    private fun confirmationDialog() {
        val v: View = layoutInflater.inflate(R.layout.add_ts_party_site_comfirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancelR: MaterialButton = v.findViewById(R.id.btnCancelR)
        val btnConfirmR: MaterialButton = v.findViewById(R.id.btnConfirmR)
        val cancel: TextView = v.findViewById(R.id.cancelR)
        val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val txtBody: TextView = v.findViewById(R.id.txtBody)

        //   var oth =retailerSetupInfo.split(",")[0]
        var oth = ""

        val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer")
        oth = customertype.toString()

        if (oth != "Retailer") {
            txtCDialogTitle.text = "$oth call record "
            txtBody.text = "Do you want to give call record of this  $oth!"
        }
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.hide()
        }

        btnCancelR.setOnClickListener {
            dialog.hide()

        }

        btnConfirmR.setOnClickListener {
            //todo
            val empI = Utility.getValueByKey(requireActivity(), "username").toString()

            //   validationForCall()



            if (latitude == 0.0) {
                latitude = getLastKnownLocation()!!.latitude
                longitude = getLastKnownLocation()!!.longitude
            }
            //todo need to add shared pref

            //  var quickvisitinfo = convertQuickVisitInfo(requireActivity())!!

            //  var mov_doc = quickvisitinfo.moveDoc.toString()
            // var mov_row = quickvisitinfo.moveRow.toString()
            val directComprecordData = prepareOtherCustomerSiteData()
            TSCallCompMoveDirectSave(empI, directComprecordData,dialog)


            //  val tagedCallrecordData = prepareOtherCustomerSiteData()
            //  TSCallMove4Tag(empI, directCallrecordData,dialog)
            //  TSCallMove4Tag(empI, mov_doc, mov_row, tagedCallrecordData, dialog)
        }


    }


    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    private fun getLastKnownLocation(): Location? {
        val mLocationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = mLocationManager!!.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return bestLocation
    }

    private fun prepareOtherCustomerSiteData(): String {

        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()

        //   other = convertOtherInfo(requireActivity())!!
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
        //   rootView.directTcall_partycode.setText(other.partyCode.toString())

        other = convertOtherInfo(requireActivity())!!

        //    rootView.directTcall_partycode.setText(other.partyCode.toString())

        var converTionstatus = binding.acRCallConversionStatus.text.toString()
        var cementType = ""
        var deliveryQty = 0
        var tentivive_delivery_or_datebefore_Convertion=""
        var responsible_trade_salesPerson =""
        var reason_of_noAchv = ""
        var follow_up_reason = ""

        //for yes
        if(converTionstatus=="Yes"){
            cementType = binding.acRCallCementType.text.toString()
            deliveryQty = binding.txtRdeliveryOrderQuantity.text.toString().toInt()
            //   var currentDate = Utility.getCurrentDate("yyyy-MM-dd").toString()
            //   binding.txtRCallDeliveryDate.setText(currentDate)
            //  binding.layoutRCallDeliveryDate.visibility = View.GONE
            // tentivive_delivery_or_datebefore_Convertion = currentDate
            tentivive_delivery_or_datebefore_Convertion = deliveryDate
            responsible_trade_salesPerson = binding.txtRPartSiteResPerson.text.toString()
            reason_of_noAchv = ""
            follow_up_reason = ""

        }else if(converTionstatus=="No"){

            cementType = ""
            deliveryQty = 0
            tentivive_delivery_or_datebefore_Convertion = ""
            responsible_trade_salesPerson = ""
            reason_of_noAchv = binding.acRSiteNoAchReason.text.toString()
            follow_up_reason = ""

        }else if(converTionstatus=="Already converted site"){

            cementType = ""
            if(binding.txtRdeliveryQuantity.text.toString().isEmpty()){
                deliveryQty = 0
            }else{
                deliveryQty = binding.txtRdeliveryQuantity.text.toString().toInt()
            }
            responsible_trade_salesPerson = ""
            reason_of_noAchv = ""
            //   tentivive_delivery_or_datebefore_Convertion = ""
            tentivive_delivery_or_datebefore_Convertion = beforeConversinDate
            follow_up_reason = binding.acRCallFollowUp.text.toString()
        }


        val OtherCustomerdirectcallinfo =  OtherCustomerdirectcallinfo(
            other.partyCode.toString(),
            siteinfo.siteNumber.toString(),
            "Bangladesh",
            division,
            district,
            thana,
            "",
            selectedCustomer,
            "",
            "",
            other.contactPerson.toString(),
            other.partyName.toString(),
            other.partyMobile.toString(),
            other.partyAdd1.toString(),
            latitude,
            longitude,
            "Reference Call",
            emp,
            binding.txtVerificationInfo.text.toString(),
            binding.txtRSiteAddTopic.text.toString(),
            "",
            cementType,
            deliveryQty,
            responsible_trade_salesPerson,
            reason_of_noAchv,
            "",
            converTionstatus,
            nextVisitDate,
            tentivive_delivery_or_datebefore_Convertion,
            "",
            "",
            "",
            "",
            "",
            "",
            follow_up_reason,
            "jpg",
            "",
            "",
            "",
            binding.acRCallSiteStatus.text.toString()

        )

        val gson = Gson()
        return gson.toJson(OtherCustomerdirectcallinfo)
    }
    
 */



}