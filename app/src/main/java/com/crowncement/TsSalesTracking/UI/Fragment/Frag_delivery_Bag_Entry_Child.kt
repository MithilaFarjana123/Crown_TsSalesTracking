package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.GetSalesMoveCallChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerdirectcallinfo
import com.crowncement.TsSalesTracking.databinding.FragDeliveryBagEntryChildBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.util.Calendar
import java.util.Objects


class Frag_delivery_Bag_Entry_Child : Fragment() {



    private var _binding: FragDeliveryBagEntryChildBinding? = null
    private val binding get() = _binding!!

    lateinit var other: GetTSSiteInfoChild
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var loadingAnim: Dialog
    var selectedDate = ""

    lateinit var savedCalldata: ArrayList<GetSalesMoveCallChild>

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragDeliveryBagEntryChildBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        RetailerViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        savedCalldata = ArrayList()

        binding.txtRCallDeliveryDate.setOnClickListener {
            showConditionalDatePicker()
        }


        other = convertOtherInfo(requireActivity())!!
        getSalesMoveCall(other.siteNumber.toString())

        binding.btnSavedemoRCallRecord.setOnClickListener {
            if (delValidation()) {
                if(savedCalldata.size>0) {

                    confirmationDialog()

                 }else{
                     Utility.commonToast(requireActivity(),"warning","Necessary Data Not Found",0)
                 }

            }
        }

    }




    private fun showConditionalDatePicker() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)



        //  val day = 10


        val dialog = DatePickerDialog(requireContext(), { _, selYear, selMonth, selDay ->
            // Handle selected date here
             selectedDate="$selYear-${selMonth + 1}-$selDay"

            binding.txtRCallDeliveryDate.setText(
                Utility.changeDateFormat(
                    selectedDate,
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
                )
            )
        }, year, month, day)

        val minCal = Calendar.getInstance()
        val maxCal = Calendar.getInstance()

        if (day <= 10) {
            // Allow from previous month 1st to today
            minCal.set(Calendar.DAY_OF_MONTH, 1)
            minCal.add(Calendar.MONTH, -1)
            maxCal.set(Calendar.DAY_OF_MONTH, day)
        } else {
            // Restrict to current month only
            minCal.set(Calendar.DAY_OF_MONTH, 1)
            maxCal.set(Calendar.DAY_OF_MONTH, day)
        }

        // Apply date bounds
        dialog.datePicker.minDate = minCal.timeInMillis
        dialog.datePicker.maxDate = maxCal.timeInMillis

        dialog.show()
    }

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


        txtCDialogTitle.text = "Delivery Quantity Submission"
        txtBody.text = "Do you want to Submit this Quantity?"


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


            if (latitude == 0.0) {
                latitude = getLastKnownLocation()!!.latitude
                longitude = getLastKnownLocation()!!.longitude
            }

            val directComprecordData = prepareOtherCustomerSiteData()
            TSCallCompMoveDirectSave(empI, directComprecordData,dialog)



        }


    }


    private fun TSCallCompMoveDirectSave(
        user: String,
        value: String,
        dialog: Dialog
    ) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.TSCallMoveDirectSave(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {


                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Bag Submission completed",
                                R.drawable.ic_checked_green,
                                1
                            )
                            loadingAnim.dismiss()

                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_deliveryBagEntry)


                        } else {
                            loadingAnim.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }
                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun delValidation():Boolean{
        var result = false
        binding.layoutRdeliveryOrderQuantity.isErrorEnabled = false
        binding.layoutRdeliveryOrderQuantity.error = null
        binding.layoutRCallDeliveryDate.isErrorEnabled = false
        binding.layoutRCallDeliveryDate.error = null

       if (binding.txtRdeliveryOrderQuantity.text.toString().isEmpty()||
           binding.txtRdeliveryOrderQuantity.text.toString().toInt()<50){
           result = false
           binding.layoutRdeliveryOrderQuantity.isErrorEnabled = true
           binding.layoutRdeliveryOrderQuantity.error = "Delivery order quantity is must be greater than 50"

       }else if(binding.txtRCallDeliveryDate.text.toString().isEmpty()){
           result = false
           binding.layoutRCallDeliveryDate.isErrorEnabled = true
           binding.layoutRCallDeliveryDate.error = "Delivery date is required"
       }

       else {

           result = true
       }



       return result

   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun convertOtherInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedSiteInfo", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }




    private fun getSalesMoveCall(
        site_id: String
    ) {
        retailerViewModel.getSalesMoveCall(site_id)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            /////////
                            if (res.code == "200") {


                                savedCalldata = ArrayList()



                                savedCalldata = res.data
                                if (savedCalldata.size > 0) {
                                    binding.basicSiteInfo.visibility = View.VISIBLE
                                    binding.siteCode.setText("Site Code : " + savedCalldata.get(0).siteId.toString())
                                    binding.partyCode.setText("Party Code : " + savedCalldata.get(0).partyId.toString())
                                    binding.ConversionStatus.setText(
                                        "Conversion Status : " + savedCalldata.get(
                                            0
                                        ).siteConvStat.toString()
                                    )
                                    //   binding.contact.setText("Contact : "+savedCalldata.get(0).partyContact.toString())
                                } else {

                                    binding.basicSiteInfo.visibility = View.GONE

                                }

                            }


                        }
                        loadingAnim.dismiss()
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        if(it.message=="500") {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Warning",
                                "Please check any delivery bag is pending to your reporting boss",
                                R.drawable.ic_checked_green,
                                1
                            )
                        }
                        loadingAnim.dismiss()

                    }

                }

            }

    }


    private fun prepareOtherCustomerSiteData(): String {

        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        var division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        var district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        var thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()

       var selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        other = convertOtherInfo(requireActivity())!!

        var conversionStatus = savedCalldata.get(0).siteConvStat.toString()
        if (latitude == 0.0) {
            latitude = getLastKnownLocation()!!.latitude
            longitude = getLastKnownLocation()!!.longitude
        }

        if(conversionStatus=="Yes"){
            conversionStatus = "Already converted site"
        }else {
            conversionStatus = savedCalldata.get(0).siteConvStat.toString()
        }


        val OtherCustomerdirectcallinfo =  OtherCustomerdirectcallinfo(
            savedCalldata.get(0).partyId.toString(),
            savedCalldata.get(0).siteId.toString(),
            "Bangladesh",
            division,
            district,
            thana,
            "",
            selectedCustomer,
            "",
            "",
            savedCalldata.get(0).partyContact,
            savedCalldata.get(0).partyName,
            savedCalldata.get(0).partyMobile.toString(),
            "",
            latitude,
            longitude,
            "Reference Call",
            emp,
            savedCalldata.get(0).discussTop.toString(),
            savedCalldata.get(0).discussSubTopic.toString(),
            savedCalldata.get(0).siteRefBy.toString(),
            savedCalldata.get(0).cemType.toString(),
            binding.txtRdeliveryOrderQuantity.text.toString().toInt(),
            savedCalldata.get(0).respOff.toString(),
            "",
            "",
            conversionStatus,
            "",
            selectedDate,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "jpg",
            savedCalldata.get(0).movStatus.toString(),
            savedCalldata.get(0).dealerId.toString(),
            savedCalldata.get(0).retailerId.toString(),
            savedCalldata.get(0).siteCurrStatus.toString(),

        )

        val gson = Gson()
        return gson.toJson(OtherCustomerdirectcallinfo)
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




}