package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.DealerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OptionAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.RetailerListAdapter
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.GetTSCallTagResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerdirectcallinfo
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.data.Model.TopicResponceChild
import com.crowncement.TsSalesTracking.databinding.FragRefferenceCallRecordBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson


import java.util.Calendar
import java.util.Locale
import java.util.Objects


class Frag_Refference_CallRecord : Fragment(),
    MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener {

    //  lateinit var rootView: View
    private var _binding: FragRefferenceCallRecordBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Frag_Refference_CallRecord"

    lateinit var loadingAnim: Dialog

    var division = ""
    var district = ""
    var thana = ""
    var selecteddiscurtion =""
    var selectedConversionType = ""

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    lateinit var retailerViewModel: RetailerViewModel
    private val permissionCode = 101
    lateinit var siteinfo: GetTSSiteInfoChild


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    var nextVisitDate =""
    var deliveryDate = ""
    var beforeConversinDate=""


    lateinit var selectedDiscussionTopic: ArrayList<String>


    lateinit var retaileritemAdapter: RetailerListAdapter
    lateinit var dealeritemAdapter: DealerListAdapter
    lateinit var otheritemAdapter: OtherCustomerListAdapter


    lateinit var retailerLists: ArrayList<RetailerChild>
    lateinit var dealerLists: ArrayList<DealerChild>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>
    lateinit var routeList: ArrayList<RouteChild>
    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>
    lateinit var SiteStatusTypeList: ArrayList<String>

    lateinit var clientTypes: ArrayList<String>


    var selectedDivision =""
    var selectedDistrict=""
    var selectedThana=""
    var selectedRoute=""

    var SELECTED_DEALER_ID=""
    var SELECTED_RETAILER_ID=""


    var selectedRouteName=""

    lateinit var FollowUpList: ArrayList<String>
    lateinit var other: OtherCustomerChild



    lateinit var discurtionDatalist: ArrayList<String>



    lateinit var CementTypeList: ArrayList<String>
    lateinit var ProjectConversionTypeList: ArrayList<String>
    lateinit var noAchivementTypeList: ArrayList<String>

    private lateinit var picker: DatePickerDialog

    private var selectedCustomer: String = ""

    lateinit var optionAdapter: OptionAdapter

    var type = ""



    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //  rootView = inflater.inflate(R.layout.frag_refference_call_record, container, false)

        _binding = FragRefferenceCallRecordBinding.inflate(inflater, container, false)



        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        RetailerViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        empTextSetUp()
        retailerLists = ArrayList()
        dealerLists = ArrayList()
        otherCustomerLists = ArrayList()

        forConversionType()

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"

        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        prepare_spinner()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()

        binding.txtRCallEMP.setText(emp)

        getDiscurtionTopic("TS Discussion Topic")



        onclickListener()
    }


    fun forConversionType(){
        binding.RconYes.visibility = View.GONE
        binding.RconNo.visibility = View.GONE
        binding.RconBeforeConverted.visibility = View.GONE
        binding.layoutRdeliverydealerCode.visibility = View.GONE
        binding.layoutRdeliveryRetailerCode.visibility = View.GONE
        //   binding.layoutTRBeforeCondate.visibility = View.GONE

        siteinfo = convertOtherSiteInfo(requireActivity())!!

        type = siteinfo.siteVisitStatus.toString()
      // type = "Yes"
        prepareProjectConversionType(binding.acRCallConversionStatus,type)

//        if(type==""||type=="No"){
          //  prepareProjectConversionType(binding.acRCallConversionStatus,"Already converted site")
//
//        }else{
//
//        }
/*
        if(type.isNotEmpty()){
            if (type.equals("Yes")){
                updateVisibility(1)
            }else if (type.equals("No")){
                updateVisibility(2)
            }else if (type.equals("Already converted site")){
                updateVisibility(3)
            }else{
                updateVisibility(0)

            }
        }

 */
    }
/*
    fun empTextSetUp(){
        if (binding.txtRPartSiteResPerson.text.isNullOrEmpty()) {
            binding.txtRPartSiteResPerson.setText("E")
            binding.txtRPartSiteResPerson.setSelection(binding.txtRPartSiteResPerson.length())
        }

        //TODO EMP ID AUTO TEXT
        binding.txtRPartSiteResPerson.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty()) {
                    binding.txtRPartSiteResPerson.setText("E")
                    binding.txtRPartSiteResPerson.setSelection(binding.txtRPartSiteResPerson.length())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().length == 3) {
                    val givenText = binding.txtRPartSiteResPerson.text.toString() + "-"
                    binding.txtRPartSiteResPerson.setText(givenText)
                    binding.txtRPartSiteResPerson.setSelection(binding.txtRPartSiteResPerson.length())
                }
            }
        })
    }

 */

    fun empTextSetUp() {

        val editText = binding.txtRPartSiteResPerson

        if (editText.text.isNullOrEmpty()) {
            editText.setText("E")
            editText.setSelection(editText.length())
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return

                editText.removeTextChangedListener(this)

                val text = s.toString()

                // Always keep prefix E
                if (!text.startsWith("E")) {
                    editText.setText("E")
                    editText.setSelection(editText.length())
                }
                // Auto add dash after E00
                else if (text.length == 3 && !text.contains("-")) {
                    editText.setText("$text-")
                    editText.setSelection(editText.length())
                }

                editText.addTextChangedListener(this)
            }
        }

        editText.addTextChangedListener(watcher)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    override fun OnMultiSpinnerItemSelected(p0: MutableList<String>?) {
        selecteddiscurtion = p0!!.joinToString(separator = ",")

        println("Multispinner" + selecteddiscurtion)

    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }



    fun onclickListener(){

        //  rootView.multiSelectSpinnerRCallDisTopic

        binding.txtRCallNextVisit.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    nextVisitDate = selectedDate
                    binding.txtRCallNextVisit.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )

                }, year, month, day
            )
            picker.show()
        }
        /*
                binding.txtRCallDeliveryDate.setOnClickListener {

                    val cldr = Calendar.getInstance()
                    val day = cldr[Calendar.DAY_OF_MONTH]
                    val month = cldr[Calendar.MONTH]
                    val year = cldr[Calendar.YEAR]
                    picker = DatePickerDialog(
                        requireContext(),
                        { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                            val monthNumber = monthOfYear + 1
                            val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                            deliveryDate = selectedDate
                            binding.txtRCallDeliveryDate.setText(
                                Utility.changeDateFormat(
                                    selectedDate,
                                    "yyyy-MM-dd",
                                    "dd MMM yyyy"
                                )
                            )


                        }, year, month, day
                    )
                    picker.show()
                }

         */

        binding.txtRCallDeliveryDate.setOnClickListener {
            showConditionalDatePicker()
        }

        /*
                binding.txtRCallDeliveryDate.setOnClickListener {
                    val cldr = Calendar.getInstance()
                    val day = cldr[Calendar.DAY_OF_MONTH]
                    val month = cldr[Calendar.MONTH]
                    val year = cldr[Calendar.YEAR]

                    // Create the DatePickerDialog
                    picker = DatePickerDialog(
                        requireContext(),
                        { _: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                            val monthNumber = monthOfYear + 1
                            val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                            deliveryDate = selectedDate
                            binding.txtRCallDeliveryDate.setText(
                                Utility.changeDateFormat(
                                    selectedDate,
                                    "yyyy-MM-dd",
                                    "dd MMM yyyy"
                                )
                            )
                        },
                        year, month, day
                    )

                    // Calculate 10 days back from today
                    val minDateCalendar = Calendar.getInstance()
                    minDateCalendar.add(Calendar.DAY_OF_YEAR, -10)
                    picker.datePicker.minDate = minDateCalendar.timeInMillis

                    picker.show()
                }

         */
//////////////////////

        binding.txtReforeCondate.setOnClickListener {
            showConditionalDatePickerforbeforeconversion()
        }
        /*
        binding.txtReforeCondate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    beforeConversinDate = selectedDate
                    binding.txtReforeCondate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )


                }, year, month, day
            )
            picker.show()
        }

         */

        binding.txtCallRDisTopic.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {

                discussionTopicEntryDialog(discurtionDatalist)
            }

        }


        binding.txtRdeliveryQuantitydealerCode.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                siteReferenceDialog("Dealer")
            }
        }

        binding.txtRdeliveryQuantityRetailerCode.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                siteReferenceDialog("Retailer")
            }
        }





        binding.btnSavedemoRCallRecord.setOnClickListener {

            if (taggedSiteValidation()) {
                if(binding.acRCallConversionStatus.text.toString().equals("Yes")){
                    if(validationforYes()){
                        confirmationDialog()
                    }
                }else if(binding.acRCallConversionStatus.text.toString().equals("No")){
                    if(validationforNo()){
                        confirmationDialog()
                    }
                }else if(binding.acRCallConversionStatus.text.toString().equals("Already converted site")){
                    if(validationforBeforeConverted()){
                        confirmationDialog()
                    }
                }

            }


        }


    }

    private fun showConditionalDatePickerforbeforeconversion() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        //  val day = 11


        val dialog = DatePickerDialog(requireContext(), { _, selYear, selMonth, selDay ->
            // Handle selected date here
            beforeConversinDate="$selYear-${selMonth + 1}-$selDay"

            binding.txtReforeCondate.setText(
                Utility.changeDateFormat(
                    beforeConversinDate,
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


    private fun showConditionalDatePicker() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        //  val day = 11


        val dialog = DatePickerDialog(requireContext(), { _, selYear, selMonth, selDay ->
            // Handle selected date here
            deliveryDate="$selYear-${selMonth + 1}-$selDay"

            binding.txtRCallDeliveryDate.setText(
                Utility.changeDateFormat(
                    deliveryDate,
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


    @SuppressLint("MissingInflatedId")
    private fun discussionTopicEntryDialog(dataList: ArrayList<String>) {

        selectedDiscussionTopic = ArrayList()
        val v: View = layoutInflater.inflate(R.layout.multi_item_selection, null)
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val btnAdd: MaterialButton = v.findViewById(R.id.btnAcAdd)
        val txtOptionClose: TextView = v.findViewById(R.id.txtOptionClose)
        val optionAcChipGroup: ChipGroup = v.findViewById(R.id.optionAcChipGroup)
        val idGRV: GridView = v.findViewById(R.id.idAcGRV)

        loadOption(idGRV, v.context, dataList)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()


        txtOptionClose.setOnClickListener {

            dialog.dismiss()
        }



        btnAdd.setOnClickListener {

            if (dataList.size == 0) {
                Utility.commonToast(
                    v.context as Activity, "Error", "Please select minimum one option", 2
                )
            } else {
                selecteddiscurtion = selectedDiscussionTopic!!.joinToString(separator = ",")
                binding.txtCallRDisTopic.setText(selecteddiscurtion)
                // dataList.clear()
                dialog.dismiss()
            }

        }

        idGRV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->

            if (!checkExistence(dataList[position], selectedDiscussionTopic)) {

                displayRouteChip(dataList[position], optionAcChipGroup, dataList)
                selectedDiscussionTopic.add(dataList[position])
                optionAdapter.notifyDataSetChanged() // call it here

            } else {
                Utility.commonToast(requireActivity(), "Error", "already added this option", 2)
            }

        }

        dialog.setOnKeyListener { dialogInterface, keyCode, keyEvent ->

            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                dialog.show()
                true
            } else false

        }

    }


    private fun checkExistence(value: String, selectedRouteList: ArrayList<String>): Boolean {
        var res = false
        for (item in selectedRouteList) {
            if (value == item) {
                res = true
                break
            } else {
                res = false
            }
        }
        return res
    }

    private fun displayRouteChip(values: String, chi: ChipGroup, selectedRouteList: ArrayList<String>
    ) {

        if (!checkExistence(values, selectedRouteList)) {
            selectedRouteList.add(values)
        }

        chi.clearCheck()
        val chip = Chip(requireContext())
        val chipText = values
        chip.text = chipText
        // Set the chip icon
        chip.textSize = 11f
        chip.chipIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_cancel_green)
        chip.setChipIconTintResource(R.color.reject)
        chi.addView(chip)
        chip.setOnClickListener {
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 250
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    chi.removeView(it)
                    val selectedChip = chip.text.toString()

                    for (value in selectedRouteList) {
                        if (value.contains(selectedChip)) {
                            val removePosition = selectedRouteList.indexOf(value)
                            selectedRouteList.removeAt(removePosition)
                            break
                        }
                    }

                    //  selectedRouteList.remove(chip.text.toString())

                }

                override fun onAnimationStart(animation: Animation?) {}
            })

            it.startAnimation(anim)
        }
        //   }
    }

    private fun loadOption(idGRV: GridView, context: Context, planList: ArrayList<String>
    ) {

        if (planList.size > 0) {
            optionAdapter = OptionAdapter(planList, context)
            idGRV.adapter = optionAdapter
        }
    }

    //----------------------------------------DEALER CODE---------------------



    private fun siteReferenceDialog(partyType:String) {
        val v: View = layoutInflater.inflate(R.layout.site_ref_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnRefSubmit = v.findViewById<Button>(R.id.btnRefSubmit)

        val acClient = v.findViewById<AutoCompleteTextView>(R.id.acClient)
        val acDivision = v.findViewById<AutoCompleteTextView>(R.id.acDivision)
        val acDistrict = v.findViewById<AutoCompleteTextView>(R.id.acDistrict)
        val acThana = v.findViewById<AutoCompleteTextView>(R.id.acThana)
        val acRoute = v.findViewById<AutoCompleteTextView>(R.id.acRoute)
        val search_customer = v.findViewById<SearchView>(R.id.search_customer)
        val tc_Cancle = v.findViewById<ImageView>(R.id.tc_Cancle)




        val layoutThana = v.findViewById<TextInputLayout>(R.id.layoutThana)
        val layoutRoute = v.findViewById<TextInputLayout>(R.id.layoutRoute)

        val txtRetailerType = v.findViewById<TextView>(R.id.txtRetailerType)
        val txtNoDealerInfo = v.findViewById<TextView>(R.id.txtNoDealerInfo)


        val rdRetailer = v.findViewById<RecyclerView>(R.id.rdRetailer)

        loadClientType(acClient,partyType)
        getDivisionList("Bangladesh",acDivision)
        //todo added for indivigual employee location
        empDisDisthanasetUp()
        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()


        layoutThana.visibility = View.GONE
        layoutRoute.visibility = View.GONE

        when {
            partyType == "Dealer" -> {
                layoutThana.visibility = View.GONE
                layoutRoute.visibility = View.GONE
            }

            partyType == "Retailer" -> {
                layoutThana.visibility = View.VISIBLE
                layoutRoute.visibility = View.VISIBLE

            }

            else -> {
                layoutRoute.visibility = View.GONE
                layoutThana.visibility = View.VISIBLE
            }
        }
        txtRetailerType.text = partyType+ " Information"


        btnRefSubmit.setOnClickListener { view1: View? ->
            dialog.dismiss()
        }
        tc_Cancle.setOnClickListener { view1: View? ->
            dialog.dismiss()
        }


        acClient.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {


            }
        }

        acDivision.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {

                selectedDivision = parent.getItemAtPosition(position).toString()

                getDistrictList("Bangladesh", selectedDivision,acDistrict)
            } else {
                selectedDivision = ""
            }
        }

        acDistrict.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()
                getThanaList(
                    "Bangladesh",
                    acDivision.text.toString(),
                    selectedDistrict,
                    acThana
                )

                if (acClient.text.toString() == "Dealer") {

                    getDealerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        rdRetailer,
                        txtNoDealerInfo,
                        dialog
                    )
                }
            } else {
                selectedDistrict = ""
            }
        }

        acThana.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()
                getRouteList(
                    "Bangladesh",
                    acDivision.text.toString(),
                    acDistrict.text.toString(),
                    selectedThana,
                    acRoute
                )

                if (acClient.text.toString() != "Dealer" && acClient.text.toString() != "Retailer") {


                    getOtherCustomerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        acThana.text.toString(),
                        acClient.text.toString(),
                        txtNoDealerInfo,
                        dialog,
                        rdRetailer
                    )
                }

            } else {
                selectedThana = ""
            }
        }

        acRoute.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0 && routeList.size > 0) {
                //selectedRoute = parent.getItemAtPosition(position).toString()
                selectedRoute = routeList[position - 1].routeNo.toString()
                selectedRouteName = routeList[position - 1].routeNo.toString() + ">" + routeList[position - 1].routeName.toString()
                Log.e("T", "selectedRoute: $selectedRoute")
                Log.e("TAG", "selectedRouteName: $selectedRouteName")

                // Save this value for loading previously selected customer



                if (acClient.text.toString() == "Retailer") {
                    getRetailerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        acThana.text.toString(),
                        selectedRoute,
                        rdRetailer,
                        txtNoDealerInfo,
                        dialog

                    )
                }
            } else {
                selectedRoute = ""
            }
        }

        search_customer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.isNotEmpty()) {
                    filter(query,acClient,txtNoDealerInfo,rdRetailer,dialog)
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText,acClient,txtNoDealerInfo,rdRetailer,dialog)
                }
                return false
            }
        })


    }


    private fun loadClientType(ac: AutoCompleteTextView,selectedText:String) {
        clientTypes = ArrayList()
        //   clientTypes.add("Select Client Type")
        clientTypes.add("Dealer")
        clientTypes.add("Retailer")
        //  clientTypes.add("Engineer")
        //  clientTypes.add("IHBs")
        //  clientTypes.add("Mason")
        //  clientTypes.add("Contractor")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )
//        if (firstTime == 0) {
//            ac.setText(clientTypes[0])
//        }
        ac.setText(clientTypes[clientTypes.indexOf(selectedText)])

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    private fun getDivisionList(value: String,acDivision:AutoCompleteTextView) {
        retailerViewModel.getDivisionInfo(value).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        divisionList = ArrayList()
                        divisionList = res.data
                        loadDivision(acDivision, res.data,empdivisionList)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    /*
    private fun loadDivision(ac: AutoCompleteTextView, lists: ArrayList<DivisionChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Division")
        for (item in lists) {
            items.add(item.divName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


     */
    private fun getDistrictList(country: String, division: String,acDistrict:AutoCompleteTextView) {
        retailerViewModel.getDistrictInfo(country, division).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        acDistrict.isEnabled = true
                        districtList = ArrayList()
                        districtList = res.data
                        loadDistrict(acDistrict, res.data,empdistrictList)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    /*
    private fun loadDistrict(acDistrict: AutoCompleteTextView?, data: ArrayList<DistrictChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select District")
        for (item in data) {
            items.add(item.disName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acDistrict!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


     */


    private fun getThanaList(country: String, division: String, district: String,acThana:AutoCompleteTextView) {
        retailerViewModel.getThanaInfo(country, division, district).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        acThana.isEnabled = true
                        thanaList = ArrayList()
                        thanaList = res.data
                        loadThana(acThana, res.data)

                        //    loadThana(acThana, res.data,empthanaList)

                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }


    private fun loadThana(acThana: AutoCompleteTextView?, data: ArrayList<ThanaChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Thana")
        for (item in data) {
            items.add(item.thnName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acThana!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun getRouteList(country: String, division: String, district: String, thana: String,acRoute:AutoCompleteTextView) {
        retailerViewModel.getRouteInfo(country, division, district, thana)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            acRoute.isEnabled = true
                            routeList = ArrayList()
                            routeList = res.data
                            loadRoute(acRoute, res.data)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }

    private fun loadRoute(acRoute: AutoCompleteTextView?, data: ArrayList<RouteChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Route")
        for (item in data) {
            items.add(item.routeName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acRoute!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getDealerList(country: String, division: String, district: String, rdRetailer: RecyclerView, txtNoDealerInfo:TextView, dialog: Dialog) {
        retailerViewModel.getDealerList(country, division, district)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val dealerList: ArrayList<DealerChild> = res.data
                            dealerLists = dealerList

                            prepareDealerList(rdRetailer, dealerList, "D",txtNoDealerInfo,dialog)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


    private fun getOtherCustomerList(country: String,
                                     division: String,
                                     district: String,
                                     thana: String,
                                     xCode: String,
                                     txtNoDealerInfo:TextView ,
                                     dialog: Dialog,
                                     rdRetailer: RecyclerView
    ) {
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            prepareOtherList(rdRetailer, otherCustomerList, xCode,txtNoDealerInfo, dialog)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }

    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<OtherCustomerChild>,
        type: String,
        txtNoDealerInfo:TextView,
        dialog: Dialog
    ) {

        if (items.size > 0) {
            txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            otheritemAdapter = OtherCustomerListAdapter(items, type)

            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = otheritemAdapter

            otheritemAdapter.setOnItemClickListener(object :
                OtherCustomerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {

                    dialog.dismiss()

                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }



    private fun filter(text: String, acClient:AutoCompleteTextView, txtNoDealerInfo:TextView, rdRetailer: RecyclerView, dialog: Dialog) {

        if (text.isNotEmpty()) {
            when {
                acClient.text.toString() == "Dealer" -> {

                    if (dealerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<DealerChild>()
                        for (item in dealerLists) {
                            if (item.dealerName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.dealerMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No dealer found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {

                            prepareDealerList(rdRetailer, filteredlist, "D",txtNoDealerInfo,dialog)
                        }

                    }
                    else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No data found",
                            R.drawable.error_white,
                            2
                        )
                    }

                }
                acClient.text.toString() == "Retailer" -> {
                    if (retailerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<RetailerChild>()
                        for (item in retailerLists) {
                            if (item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No retailer found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {

                            prepareRetailerList(rdRetailer, filteredlist, "R",dialog,txtNoDealerInfo)
                        }

                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No retailer found",
                            R.drawable.error_white,
                            2
                        )
                    }
                }

                else -> {
                    if (otherCustomerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<OtherCustomerChild>()
                        for (item in otherCustomerLists) {
                            if (item.contactPerson!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No data found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {
                            prepareOtherList(
                                rdRetailer,
                                filteredlist,
                                acClient.text.toString(),
                                txtNoDealerInfo,
                                dialog
                            )
                        }


                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No data found",
                            R.drawable.error_white,
                            2
                        )
                    }
                }
            }
        } else {
            Utility.getBaseMessage(
                requireActivity(),
                "Sorry",
                "No data found",
                R.drawable.error_white,
                0
            )
        }


    }

    private fun prepareDealerList(rv: RecyclerView, items: ArrayList<DealerChild>, type: String, txtNoDealerInfo:TextView, dialog: Dialog) {

        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            txtNoDealerInfo.visibility = View.GONE
            dealeritemAdapter = DealerListAdapter(items, type)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = dealeritemAdapter

            dealeritemAdapter.setOnItemClickListener(object :
                DealerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    //items[position]
                    SELECTED_DEALER_ID=items[position].dealerCode.toString()
                    binding.txtRdeliveryQuantitydealerCode.setText(items[position].dealerName+"("+items[position].dealerCode.toString()+")")
                    dialog.dismiss()

                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }



    private fun prepareRetailerList(
        rv: RecyclerView,
        items: ArrayList<RetailerChild>,
        type: String,
        dialog: Dialog,
        txtNoDealerInfo:TextView

    ) {

        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            txtNoDealerInfo.visibility = View.GONE
            retaileritemAdapter = RetailerListAdapter(items, type)
            //val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            //rv.layoutManager = rLayoutmanager

            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = retaileritemAdapter

            retaileritemAdapter.setOnItemClickListener(object :
                RetailerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {

                    SELECTED_RETAILER_ID=items[position].partyCode.toString()
                    binding.txtRdeliveryQuantityRetailerCode.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")
                    dialog.dismiss()

                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }


    }



    private fun getRetailerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        route: String,
        rdRetailer: RecyclerView,
        txtNoDealerInfo:TextView,
        dialog: Dialog
    ) {
        retailerViewModel.getRetailerList(country, division, district, thana, route)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            retailerLists= ArrayList()

                            val retailerList: ArrayList<RetailerChild> = res.data
                            retailerLists = retailerList

                            prepareRetailerList(rdRetailer, retailerList, "R",dialog,txtNoDealerInfo)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


    //dealer end


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
            // var mov_row = quickvisitinfo.moveRow.toString()if
            if(SELECTED_RETAILER_ID.isNotEmpty()&&SELECTED_RETAILER_ID.isNotEmpty()) {
                val directComprecordData = prepareOtherCustomerSiteData()
                TSCallCompMoveDirectSave(empI, directComprecordData, dialog)
                Toast.makeText(requireActivity(), "retailer  id :"+ SELECTED_RETAILER_ID
                        +" Dealer  id :"+ SELECTED_DEALER_ID
                    , Toast.LENGTH_SHORT).show()
            }else{

                Toast.makeText(requireActivity(), "Failed retailer  id :"+ SELECTED_RETAILER_ID
                    +" Dealer  id :"+ SELECTED_DEALER_ID
                    , Toast.LENGTH_SHORT).show()            }


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
            selecteddiscurtion,
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
            SELECTED_DEALER_ID,
            SELECTED_RETAILER_ID,
            binding.acRCallSiteStatus.text.toString()
        )

        val gson = Gson()
        return gson.toJson(OtherCustomerdirectcallinfo)
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


                            /*
                                                        if(finalFile.isFile) {
                                                            uploadTSComplainCover(
                                                                user,
                                                                res.bpId.toString(),
                                                                "TSCallRecordDoc",
                                                                "documents",
                                                                "jpg",
                                                                finalFile,
                                                                loadingAnim
                                                            )
                                                        }

                             */


                            //   else{
                            // Toast.makeText(requireContext(), "File missing ", Toast.LENGTH_LONG).show()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Complain submission completed",
                                R.drawable.ic_checked_green,
                                1
                            )
                            loadingAnim.dismiss()

                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_demo_Site_Home)
                            //    }


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


    private fun TSCallMove4Tag(
        user_id: String,
        mov_doc: String,
        mov_row: String,
        call_info: String,
        dialog: Dialog
    ) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.TSCallMove4Tag(user_id, mov_doc, mov_row, call_info)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Reference  Call record completed",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                loadingAnim.dismiss()

                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_dashboard)


                                /*
                                                            if(finalFile.isFile) {
                                                                uploadTSSiteCover(
                                                                    user,
                                                                    res.bpId.toString(),
                                                                    customerType,
                                                                    latitude.toString(),
                                                                    longitude.toString(),
                                                                    finalFile,
                                                                    loadingAnim
                                                                )
                                                            }else{
                                                                Toast.makeText(requireContext(), "File missing ", Toast.LENGTH_LONG).show()

                                                            }
                                */



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



    private fun convertQuickVisitInfo(activity: Activity): GetTSCallTagResponceChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("quickVisitInfo", "")
        return gson.fromJson(json, GetTSCallTagResponceChild::class.java)
    }


    private fun taggedSiteValidation():Boolean{
        var result = false
        binding.layoutCallRRefType.isErrorEnabled = false
        binding.layoutCallRRefType.error = null
        binding.layoutCallRDisTopic.isErrorEnabled = false
        binding.layoutCallRDisTopic.error = null
        binding.layoutRCallConStatus.isErrorEnabled = false
        binding.layoutRCallConStatus.error = null
        //  rootView.layoutTCallNextVisit.isErrorEnabled = false
        //  rootView.layoutTCallNextVisit.error = null
        /*
                    //yes
                    rootView.layoutTCallClientType.isErrorEnabled = false
                    rootView.layoutTCallClientType.error = null
                    rootView.layoutTdeliveryOrderQuantity.isErrorEnabled = false
                    rootView.layoutTdeliveryOrderQuantity.error = null
                    rootView.layoutTCallDeliveryDate.isErrorEnabled = false
                    rootView.layoutTCallDeliveryDate.error = null
                    rootView.layoutTPartSiteResPerson.isErrorEnabled = false
                    rootView.layoutTPartSiteResPerson.error = null


                    //no
                    rootView.layoutTSiteNoAchReason.isErrorEnabled = false
                    rootView.layoutTSiteNoAchReason.error = null


                    //before converted
                    rootView.layoutTTBeforeCondate.isErrorEnabled = false
                    rootView.layoutTTBeforeCondate.error = null

                    rootView.layoutTdeliveryQuantity.isErrorEnabled = false
                    rootView.layoutTdeliveryQuantity.error = null

                    rootView.layoutTCallFollowUp.isErrorEnabled = false
                    rootView.layoutTCallFollowUp.error = null

         */




        if (binding.acCallTRRefType.text.toString().equals("Reference Type") ||
            binding.acCallTRRefType.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutCallRRefType.isErrorEnabled = true
            binding.layoutCallRRefType.error = "Please select reference Type"
        }else if(selecteddiscurtion==""){
            result = false
            binding.layoutCallRDisTopic.isErrorEnabled = true
            binding.layoutCallRDisTopic.error = "Discussion type is required"
        }else if(binding.acRCallConversionStatus.text.toString().equals("Conversion  Status") ||
            binding.acRCallConversionStatus.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutRCallConStatus.isErrorEnabled = true
            binding.layoutRCallConStatus.error = "Please select the conversion status."

            //////////
            /*
            //yes
            if(rootView.acTCallConversionStatus.text.toString().equals("Yes")){
                if(rootView.acTCallCementType.text.toString().equals("Cement Type") ||
                    rootView.acTCallCementType.text.toString().isEmpty()
                ) {
                    result = false
                    rootView.layoutTCallClientType.isErrorEnabled = true
                    rootView.layoutTCallClientType.error = "Comment Type is required"
                }else if(rootView.txtTdeliveryOrderQuantity.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTdeliveryOrderQuantity.isErrorEnabled = true
                    rootView.layoutTdeliveryOrderQuantity.error = "Delivery order quantity is required"

                }else if(rootView.txtTCallDeliveryDate.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTCallDeliveryDate.isErrorEnabled = true
                    rootView.layoutTCallDeliveryDate.error = "Tentative delivery date is required"
                }else if(rootView.txtTPartSiteResPerson.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTPartSiteResPerson.isErrorEnabled = true
                    rootView.layoutTPartSiteResPerson.error = "Responsible Trade Sales Person is required"
                }
            }
            //no

            else if(rootView.acTCallConversionStatus.text.toString().equals("No")){
                    if(rootView.acTSiteNoAchReason.text.toString().equals("Reason of No Achievement")||
                        rootView.acTSiteNoAchReason.text.toString().isEmpty()){
                        result = false
                        rootView.layoutTSiteNoAchReason.isErrorEnabled = true
                        rootView.layoutTSiteNoAchReason.error = "Reason of No Achievement is required"
                    }



            }else if(rootView.acTCallConversionStatus.text.toString().equals("Before converted site")){

                if(rootView.txtTBeforeCondate.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTTBeforeCondate.isErrorEnabled = true
                    rootView.layoutTTBeforeCondate.error = "Before conversion date is required"
                }else if(rootView.txtTdeliveryQuantity.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTdeliveryQuantity.isErrorEnabled = true
                    rootView.layoutTdeliveryQuantity.error = "Before delivered quantity date is required"

                }else if(rootView.acTCallFollowUp.text.toString().isEmpty()||
                    rootView.acTCallFollowUp.text.toString().equals("Follow up reason")
                    ){
                    result = false
                    rootView.layoutTCallFollowUp.isErrorEnabled = true
                    rootView.layoutTCallFollowUp.error = "Follow up reason is required"

                }
            }
            */

//////////////////////

        }
        /*
        else if(rootView.txtTCallNextVisit.text.toString().isEmpty()){
            result = false
            rootView.layoutTCallNextVisit.isErrorEnabled = true
            rootView.layoutTCallNextVisit.error = "Please select next visit date."
        }
        */
        else {

            result = true
        }



        return result


    }

    fun validationforYes(): Boolean {
        var result = false

        //yes
        binding.layoutRCallClientType.isErrorEnabled = false
        binding.layoutRCallClientType.error = null
        binding.layoutRdeliveryOrderQuantity.isErrorEnabled = false
        binding.layoutRdeliveryOrderQuantity.error = null
        //first delivery date
        binding.layoutRCallDeliveryDate.isErrorEnabled = false
        binding.layoutRCallDeliveryDate.error = null

        binding.layoutRPartSiteResPerson.isErrorEnabled = false
        binding.layoutRPartSiteResPerson.error = null

        binding.layoutRdeliverydealerCode.isErrorEnabled = false
        binding.layoutRdeliverydealerCode.error = null


        binding.layoutRdeliveryRetailerCode.isErrorEnabled = false
        binding.layoutRdeliveryRetailerCode.error = null


        binding.layoutRCallConSiteStatus.isErrorEnabled = false
        binding.layoutRCallConSiteStatus.error = null



        if(binding.acRCallCementType.text.toString().equals("Cement Type") || binding.acRCallCementType.text.toString().isEmpty()) {
            result = false
            binding.layoutRCallClientType.isErrorEnabled = true
            binding.layoutRCallClientType.error = "Cement Type is required"
        }else if(binding.acRCallSiteStatus.text.toString().equals("Site Status") || binding.acRCallCementType.text.toString().isEmpty()) {
            result = false
            binding.layoutRCallConSiteStatus.isErrorEnabled = true
            binding.layoutRCallConSiteStatus.error = "Site Status is required"
        }


        else if(binding.txtRdeliveryOrderQuantity.text.toString().isEmpty()||
            binding.txtRdeliveryOrderQuantity.text.toString().toInt()<50 ||
            binding.txtRdeliveryOrderQuantity.text.toString().toInt()>3000)
            {
            result = false
            binding.layoutRdeliveryOrderQuantity.isErrorEnabled = true
            binding.layoutRdeliveryOrderQuantity.error = "Delivery order quantity must be in between 50 to 3000"

        }
        /*
        else if(rootView.txtRdeliveryOrderQuantity.text.toString().isNotEmpty()){
            var quantity = rootView.txtRdeliveryOrderQuantity.text.toString().toInt()
            if(quantity<50) {
                result = false
                rootView.layoutRdeliveryOrderQuantity.isErrorEnabled = true
                rootView.layoutRdeliveryOrderQuantity.error =
                    "Delivery order quantity is must be greater than 50"
            }
        }

         */

        //first delivery date
        else if(binding.txtRCallDeliveryDate.text.toString().isEmpty()){
            result = false
            binding.layoutRCallDeliveryDate.isErrorEnabled = true
            binding.layoutRCallDeliveryDate.error = "Delivery date is required"
        }






        else if(binding.txtRPartSiteResPerson.text.toString().isEmpty()||
            binding.txtRPartSiteResPerson.text.toString().equals("E")){

            result = false
            binding.layoutRPartSiteResPerson.isErrorEnabled = true
            binding.layoutRPartSiteResPerson.error = "Responsible Trade Sales Person is required"
        }else if(SELECTED_DEALER_ID=="") {
            result = false
            binding.layoutRdeliverydealerCode.isErrorEnabled = true
            binding.layoutRdeliverydealerCode.error = "Dealer code is required"
        }else if(SELECTED_RETAILER_ID==""){
            binding.layoutRdeliveryRetailerCode.isErrorEnabled = true
            binding.layoutRdeliveryRetailerCode.error = "Retailer code is required"
        }

        else {

            result = true
        }



        return result


    }

    //for no
    fun validationforNo() : Boolean{
        var result = false

        //no
        binding.layoutRSiteNoAchReason.isErrorEnabled = false
        binding.layoutRSiteNoAchReason.error = null
        binding.layoutRCallConSiteStatus.isErrorEnabled = false
        binding.layoutRCallConSiteStatus.error = null




        if(binding.acRSiteNoAchReason.text.toString().equals("Reason of No Achievement")||
            binding.acRSiteNoAchReason.text.toString().isEmpty()){
            result = false
            binding.layoutRSiteNoAchReason.isErrorEnabled = true
            binding.layoutRSiteNoAchReason.error = "Reason of No Achievement is required"
        }else if(binding.acRCallSiteStatus.text.toString().equals("Site Status") || binding.acRCallCementType.text.toString().isEmpty()) {
            result = false
            binding.layoutRCallConSiteStatus.isErrorEnabled = true
            binding.layoutRCallConSiteStatus.error = "Site Status is required"
        }

        else {

            result = true
        }

        return result
    }

    //for beforeconverted
    fun validationforBeforeConverted() : Boolean{
        var result = false

        //before converted
        //  rootView.layoutTRBeforeCondate.isErrorEnabled = false
        //  rootView.layoutTRBeforeCondate.error = null
        binding.layoutTRBeforeCondate.isErrorEnabled = false
        binding.layoutTRBeforeCondate.error = null

        binding.layoutRdeliveryQuantity.isErrorEnabled = false
        binding.layoutRdeliveryQuantity.error = null

        binding.layoutRCallFollowUp.isErrorEnabled = false
        binding.layoutRCallFollowUp.error = null

        binding.layoutRdeliverydealerCode.isErrorEnabled = false
        binding.layoutRdeliverydealerCode.error = null


        binding.layoutRdeliveryRetailerCode.isErrorEnabled = false
        binding.layoutRdeliveryRetailerCode.error = null

        binding.layoutRCallConSiteStatus.isErrorEnabled = false
        binding.layoutRCallConSiteStatus.error = null



        /*
                if(rootView.txtReforeCondate.text.toString().isEmpty()){
                    result = false
                    rootView.layoutTRBeforeCondate.isErrorEnabled = true
                    rootView.layoutTRBeforeCondate.error = "Before conversion date is required"
                }else*/
        /*
        if(binding.txtRdeliveryQuantity.text.toString().isEmpty()
            //|| binding.txtRdeliveryQuantity.text.toString().toInt()<50
            ){
            result = false
            binding.layoutRdeliveryQuantity.isErrorEnabled = true
            binding.layoutRdeliveryQuantity.error = "Delivery order quantity is must be greater than 50"

        }

         */

        //todo changed

        if(binding.txtRdeliveryQuantity.text.toString().isNotEmpty()
            &&( binding.txtRdeliveryQuantity.text.toString().toInt()<50||
            binding.txtRdeliveryQuantity.text.toString().toInt()>3000)){
            result = false
            binding.layoutRdeliveryQuantity.isErrorEnabled = true
            binding.layoutRdeliveryQuantity.error = "Delivery order quantity is must be in between 50 to 3000"
        }

        else if((SELECTED_DEALER_ID=="")
            &&
            (binding.txtRdeliveryQuantity.text.toString().isNotEmpty()
                    && binding.txtRdeliveryQuantity.text.toString().toInt()>0)

        ) {

            result = false
            binding.layoutRdeliverydealerCode.isErrorEnabled = true
            binding.layoutRdeliverydealerCode.error = "Dealer code is required"
        }
/////////////////////////////

        else if(binding.txtReforeCondate.text.toString().isEmpty()
            &&
            (binding.txtRdeliveryQuantity.text.toString().isNotEmpty()
                    && binding.txtRdeliveryQuantity.text.toString().toInt()>0)
        ){
            result = false
            binding.layoutTRBeforeCondate.isErrorEnabled = true
            binding.layoutTRBeforeCondate.error = "Delivery date is required"
        }

        //todo changed
        else if((SELECTED_RETAILER_ID=="")
            &&
            (binding.txtRdeliveryQuantity.text.toString().isNotEmpty()
                    && binding.txtRdeliveryQuantity.text.toString().toInt()>0)
        ){
            binding.layoutRdeliveryRetailerCode.isErrorEnabled = true
            binding.layoutRdeliveryRetailerCode.error = "Retailer code is required"
        }

        else if(binding.acRCallFollowUp.text.toString().isEmpty()||
            binding.acRCallFollowUp.text.toString().equals("Follow up reason")
        ){
            result = false
            binding.layoutRCallFollowUp.isErrorEnabled = true
            binding.layoutRCallFollowUp.error = "Follow up reason is required"

        }else if(binding.acRCallSiteStatus.text.toString().equals("Site Status") || binding.acRCallCementType.text.toString().isEmpty()) {
            result = false
            binding.layoutRCallConSiteStatus.isErrorEnabled = true
            binding.layoutRCallConSiteStatus.error = "Site Status is required"
        }


        else {

            result = true
        }


        return result

    }




    fun prepare_spinner() {
        prepareRefrenceType(binding.acCallTRRefType)
        prepareCementType(binding.acRCallCementType)
        prepareProjectSiteStatusType(binding.acRCallSiteStatus)
        siteinfo = convertOtherSiteInfo(requireActivity())!!

        var type = siteinfo.siteVisitStatus.toString()

        prepareProjectConversionType(binding.acRCallConversionStatus,type)

       // prepareProjectConversionType(binding.acRCallConversionStatus,"Yes")
        prepareProjectFollowUpType(binding.acRCallFollowUp)
        prepareProjectResonOfNoAchivType(binding.acRSiteNoAchReason)


    }

    private fun convertOtherSiteInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }

    private fun prepareProjectSiteStatusType(ac: AutoCompleteTextView) {

        SiteStatusTypeList = ArrayList()
        SiteStatusTypeList.add("Site Status")
        SiteStatusTypeList.add("Running")
        SiteStatusTypeList.add("Work Stop")
        SiteStatusTypeList.add("Completed")


        ac.setText(SiteStatusTypeList[0])

        for (item in SiteStatusTypeList) {
            val pos = SiteStatusTypeList.indexOf(item)
            ac.setText(SiteStatusTypeList[pos])
            // break

        }
        ac.setText(SiteStatusTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                SiteStatusTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }



    //cement type

    private fun prepareRefrenceType(ac: AutoCompleteTextView) {

        CementTypeList = ArrayList()
        CementTypeList.add("Reference Type")
        CementTypeList.add("Over phone")
        CementTypeList.add("person")


        ac.setText(CementTypeList[0])

        for (item in CementTypeList) {
            val pos = CementTypeList.indexOf(item)
            ac.setText(CementTypeList[pos])
            // break

        }
        ac.setText(CementTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                CementTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }



    private fun prepareCementType(ac: AutoCompleteTextView) {

        CementTypeList = ArrayList()
        CementTypeList.add("Cement Type")
        CementTypeList.add("OPC")
        CementTypeList.add("PCC")
        //   CementTypeList.add("PC")


        ac.setText(CementTypeList[0])

        for (item in CementTypeList) {
            val pos = CementTypeList.indexOf(item)
            ac.setText(CementTypeList[pos])
            // break

        }
        ac.setText(CementTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                CementTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }


    private fun prepareProjectFollowUpType(ac: AutoCompleteTextView) {

        FollowUpList = ArrayList()
        FollowUpList.add("Follow up reason")
        FollowUpList.add("New Delivery Order")
        FollowUpList.add("Post Delivery Order")
        FollowUpList.add("Service,Delivery & Quantity Issue")
        FollowUpList.add("Customer Feedback")
        FollowUpList.add("Maintain relationship with customer")
        FollowUpList.add("Construction Supervision")
        FollowUpList.add("Others")




        ac.setText(FollowUpList[0])

        for (item in FollowUpList) {
            val pos = FollowUpList.indexOf(item)
            ac.setText(FollowUpList[pos])
            // break

        }
        ac.setText(FollowUpList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                FollowUpList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }



    private fun prepareProjectResonOfNoAchivType(ac: AutoCompleteTextView) {

        noAchivementTypeList = ArrayList()
        noAchivementTypeList.add("Reason of No Achievement")
        noAchivementTypeList.add("Reasonably High Price than other brand")
        noAchivementTypeList.add("Unavailability of cement")
        noAchivementTypeList.add("Contractor Influenced to other brand")
        noAchivementTypeList.add("Engineer Influenced to other brand")

        noAchivementTypeList.add("Lack of Delivery Arrangement ")
        noAchivementTypeList.add("Credit Issue")
        noAchivementTypeList.add("Dealer/Retailer Influenced to other brand ")
        noAchivementTypeList.add("Quality Issue")
        //new
        noAchivementTypeList.add("Rate Higher Than Competitor")
        noAchivementTypeList.add("Dealer/Retailer Influenced to Competitor Brand")
        noAchivementTypeList.add("Engineer Influenced to Competitor Brand")
        noAchivementTypeList.add("Contractor/mason Influenced to Competitor brand")
        noAchivementTypeList.add("Pre Approved Other Brand(LGC/IP Project)")
        noAchivementTypeList.add("Credit Facility Issue")
        noAchivementTypeList.add("Special Discount Issue")
        noAchivementTypeList.add("Competitor Promotional Offer")
        noAchivementTypeList.add("Work-Specific Brand Preference")
        noAchivementTypeList.add("Out of Dealer/Retail Coverage ")
        noAchivementTypeList.add("Advanced Paid to other Brand")
        noAchivementTypeList.add("One-Stop Purchase Preference")
        noAchivementTypeList.add("Iconic Project Brand Influence")
        noAchivementTypeList.add("Quality Perception")
        noAchivementTypeList.add("Reference/Work-of-Month Influence")
        noAchivementTypeList.add("Specific Brand Dominance")

        noAchivementTypeList.add("Others")



        ac.setText(noAchivementTypeList[0])

        for (item in noAchivementTypeList) {
            val pos = noAchivementTypeList.indexOf(item)
            ac.setText(noAchivementTypeList[pos])
            // break

        }
        ac.setText(noAchivementTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                noAchivementTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }


    //todo conversion status

//////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
        private fun prepareProjectConversionType(ac: AutoCompleteTextView,status:String) {

            ProjectConversionTypeList = ArrayList()
            ProjectConversionTypeList.add("Conversion  Status")
            ProjectConversionTypeList.add("Yes")
            ProjectConversionTypeList.add("No")
            ProjectConversionTypeList.add("Already converted site")



            ac.setText(ProjectConversionTypeList[0])

            if(status!=""){
                ac.setText(ProjectConversionTypeList[ProjectConversionTypeList.indexOf(status)])
            }

          //  ac.setText(ProjectConversionTypeList[0])
            val adapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    ProjectConversionTypeList
                )
            ac.setAdapter(adapter)
            adapter.notifyDataSetChanged()




            // Set up item click listener for the AutoCompleteTextView

            ac.setOnItemClickListener { parent, _, position, _ ->
                // Check if the selected item is the default "Visit Type"
                if (position == 0) {
                    // Hide the layout
                    selectedConversionType = ProjectConversionTypeList[position].toString()

                    binding.RconYes.visibility = View.GONE
                    binding.RconNo.visibility = View.GONE
                    binding.RconBeforeConverted.visibility = View.GONE
                    binding.layoutRdeliverydealerCode.visibility = View.GONE
                    binding.layoutRdeliveryRetailerCode.visibility = View.GONE

                } else if((position == 1)){
                    selectedConversionType = ProjectConversionTypeList[position].toString()

                    binding.RconYes.visibility = View.VISIBLE
                    binding.RconNo.visibility = View.GONE
                    binding.RconBeforeConverted.visibility = View.GONE

                    binding.layoutRdeliverydealerCode.visibility = View.VISIBLE
                    binding.layoutRdeliveryRetailerCode.visibility = View.VISIBLE
                }

                else if(position==2){
                    selectedConversionType = ProjectConversionTypeList[position].toString()

                    // Show the layout
                    binding.RconYes.visibility = View.GONE
                    binding.RconNo.visibility = View.VISIBLE
                    binding.RconBeforeConverted.visibility = View.GONE

                    binding.layoutRdeliverydealerCode.visibility = View.GONE
                    binding.layoutRdeliveryRetailerCode.visibility = View.GONE
                }else if(position==3){
                    selectedConversionType = ProjectConversionTypeList[position].toString()

                    // Show the layout
                    binding.RconYes.visibility = View.GONE
                    binding.RconNo.visibility = View.GONE
                    binding.RconBeforeConverted.visibility = View.VISIBLE

                    binding.layoutRdeliverydealerCode.visibility = View.VISIBLE
                    binding.layoutRdeliveryRetailerCode.visibility = View.VISIBLE
                }
            }




        }

     */

    ///////////////////////////////////////////////////////////////////////////////////////////////////



    private fun prepareProjectConversionType(ac: AutoCompleteTextView, status: String) {

        // Initialize the ProjectConversionTypeList
        ProjectConversionTypeList = ArrayList()
        ProjectConversionTypeList.add("Conversion  Status")
        ProjectConversionTypeList.add("Yes")
        ProjectConversionTypeList.add("No")
        ProjectConversionTypeList.add("Already converted site")

        // Set default text
       // ac.setText(ProjectConversionTypeList[0])

        // Method to handle visibility logic based on position

        // If a valid status is provided, set it and update visibility

        if(status!="") {

            if (status == "Yes") {
                ac.setText(ProjectConversionTypeList[3])
                updateVisibility(3)
            } else if (status == "No") {
                ac.setText(ProjectConversionTypeList[2])
                updateVisibility(2)
            } else if (status == "Already converted site"){
                ac.setText(ProjectConversionTypeList[3])
                updateVisibility(3)
            }
            else {
                ac.setText(ProjectConversionTypeList[0])
                updateVisibility(0)
             //   ac.setText(ProjectConversionTypeList[ProjectConversionTypeList.indexOf(status)])
                // updateVisibility(ProjectConversionTypeList.indexOf(status))

            }
        }


        if(status=="No"||status=="") {
            // Set up the adapter for the AutoCompleteTextView
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ProjectConversionTypeList
            )
            ac.setAdapter(adapter)
            adapter.notifyDataSetChanged()

            // Set up item click listener for user interaction
            ac.setOnItemClickListener { _, _, position, _ ->
                selectedConversionType = ProjectConversionTypeList[position]
                updateVisibility(position) // Update the visibility based on the user's selection
            }
        }



    }




    fun updateVisibility(position: Int) {
        when (position) {

            0 -> { // "Conversion Status"


                binding.RconYes.visibility = View.GONE
                binding.RconNo.visibility = View.GONE
                binding.RconBeforeConverted.visibility = View.GONE
                binding.layoutRdeliverydealerCode.visibility = View.GONE
                binding.layoutRdeliveryRetailerCode.visibility = View.GONE
                binding.layoutTRBeforeCondate.visibility = View.GONE

            }


            1 -> { // "Yes"
                binding.RconYes.visibility = View.VISIBLE
                binding.RconNo.visibility = View.GONE
                binding.RconBeforeConverted.visibility = View.GONE

                binding.layoutRdeliverydealerCode.visibility = View.VISIBLE
                binding.layoutRdeliveryRetailerCode.visibility = View.VISIBLE
                binding.layoutTRBeforeCondate.visibility = View.GONE

            }
            2 -> { // "No"
                binding.RconYes.visibility = View.GONE
                binding.RconNo.visibility = View.VISIBLE
                binding.RconBeforeConverted.visibility = View.GONE

                binding.layoutRdeliverydealerCode.visibility = View.GONE
                binding.layoutRdeliveryRetailerCode.visibility = View.GONE
                binding.layoutTRBeforeCondate.visibility = View.GONE

            }
            3 -> { // "Already converted site"
                binding.RconYes.visibility = View.GONE
                binding.RconNo.visibility = View.GONE
                binding.RconBeforeConverted.visibility = View.VISIBLE

                binding.layoutRdeliverydealerCode.visibility = View.VISIBLE
                binding.layoutRdeliveryRetailerCode.visibility = View.VISIBLE
                binding.layoutTRBeforeCondate.visibility = View.VISIBLE

            }
        }
    }





    //todo location
    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun getLastLocation() {

        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!checkPermissions()) {
                    requestDeviceLocationSettings()
                }

                return
            } else {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d(TAG, "onLocationResult: $latitude | $longitude")
                        if (latitude.toString().isNotEmpty()) {
                            //  rootView.locationStatus.visibility = View.VISIBLE
                        }
                    }
                }
            }

        } else {
            requestDeviceLocationSettings()
        }
    }


    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            requestPermissions()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(), 100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(TAG, "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            return true
        }
        return false
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude

            if (latitude != 0.0) {
                //   rootView.locationStatus.visibility = View.VISIBLE
            }
            Log.e(TAG, "onLocationResult: lat: $latitude")
            Log.e(TAG, "onLocationResult: long: $longitude")
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), permissionCode
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), permissionCode
            )
        }
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


//todo location end

    private fun getDiscurtionTopic(
        code_type: String
    ) {
        retailerViewModel.getTopic(code_type)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            var topicList: ArrayList<TopicResponceChild> = res.data

                            discurtionDatalist = ArrayList()

                            for (i in 0 until res.data.size) {
                                discurtionDatalist.add(topicList[i].CodeNo.toString())
                            }

                            var a = discurtionDatalist.size
                            var b = a


                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


    //todo new
    fun empDisDisthanasetUp(){
        empdivisionList = ArrayList()
        empdistrictList = ArrayList()
        empthanaList = ArrayList()
        emprouteList = ArrayList()


        val div = Utility.getUserInfo(requireActivity())!!.empJobHrc2.toString()
        val dis = Utility.getUserInfo(requireActivity())!!.empJobHrc3.toString()
        val than = Utility.getUserInfo(requireActivity())!!.empJobHrc4.toString()
        val rou = Utility.getUserInfo(requireActivity())!!.empJobHrc5.toString()


        empdivisionList = parseToArrayList(div)
        empdistrictList = parseToArrayList(dis)
        empthanaList = parseToArrayList(than)
        emprouteList = parseToArrayList(rou)




    }












    fun parseToArrayList(input: String): ArrayList<String> {
        //return ArrayList(parseItems(input))
        val items = input
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toCollection(ArrayList())




        return items


    }








    private fun loadRoute(
        acRoute: AutoCompleteTextView?,
        data: ArrayList<RouteChild>,
        emprouteList: ArrayList<String>
    ) {
        // Extract routeName strings, filter to only those in emprouteList
        val filteredList = data
            .map { it.routeName.toString() }
            .filter { it in emprouteList }
            .toMutableList()


        // Ensure the prompt is always first
        filteredList.add(0, "Select Route")


        // Create adapter with filtered values
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filteredList
        )
        acRoute?.setAdapter(adapter)


        // Optionally, set the prompt as default text (won’t filter)
        acRoute?.setText("Select Route", false)


        adapter.notifyDataSetChanged()
    }




    /*
        private fun loadThana(
            acThana: AutoCompleteTextView?,
            data: ArrayList<ThanaChild>,
            empthanaList: ArrayList<String>
        ) {
            // Filter data to only include thnName values present in empthanaList
            val filtered = data
                .map { it.thnName.toString() }
                .filter { it in empthanaList }
                .toMutableList()


            // Add a default prompt at the top
            filtered.add(0, "Select Thana")


            // Build and set the adapter
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                filtered
            )
            acThana?.setAdapter(adapter)


            // Optional: show the prompt initially without triggering filter
            acThana?.setText("Select Thana", false)


            adapter.notifyDataSetChanged()
        }

     */


    private fun loadDistrict(
        acDistrict: AutoCompleteTextView?,
        data: ArrayList<DistrictChild>,
        empdistrictList: ArrayList<String>
    ) {
        // Extract only those disName values present in empdistrictList
        val filteredNames = data
            .map { it.disName.toString() }
            .filter { it in empdistrictList }
            .toMutableList()


        // Ensure there's at least the placeholder option at the top
        filteredNames.add(0, "Select District")


        // Build and set the adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filteredNames
        )
        acDistrict?.setAdapter(adapter)


        // Optionally set the initial hint text so user sees "Select District" before typing
        acDistrict?.setText("Select District", false)


        adapter.notifyDataSetChanged()
    }


    private fun loadDivision(
        ac: AutoCompleteTextView,
        lists: ArrayList<DivisionChild>,
        empDivisionList: ArrayList<String>
    ) {
        // Filter DivisionChild objects whose divName matches any in empDivisionList
        val filtered = lists
            .map { it.divName.toString() }
            .filter { it in empDivisionList }
            .toMutableList()


        // Add the default hint at the top
        filtered.add(0, "Select Division")


        // Build and set the adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filtered
        )
        ac.setAdapter(adapter)


        // Optionally, show the hint as the initial text without filtering it out
        ac.setText("Select Division", false) // false prevents AutoComplete’s filter :contentReference[oaicite:1]{index=1}


        adapter.notifyDataSetChanged()
    }


    //toolbar home
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                //  Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_dashboard)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // Use binding safely


}