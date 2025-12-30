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
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.OptionAdapter
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerdirectcallinfo
import com.crowncement.TsSalesTracking.data.Model.TopicResponceChild
import com.crowncement.TsSalesTracking.databinding.FragCallRecordentryBinding
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
import com.google.gson.Gson

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects

class  Frag_Call_RecordEntry : Fragment() , MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener{
    private val TAG = "Frag_Call_RecordEntry"

 //   private lateinit var rootView: View

    private var _binding: FragCallRecordentryBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog

    var division = ""
    var district = ""
    var thana = ""

    var selecteddiscurtion = ""


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    lateinit var retailerViewModel: RetailerViewModel
    private val permissionCode = 101

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    lateinit var nextVisitDate: String

    lateinit var MovementTypeList: ArrayList<String>
    lateinit var RefferedByList: ArrayList<String>
    lateinit var ComSolveStatList: ArrayList<String>
    lateinit var other: OtherCustomerChild
    private lateinit var picker: DatePickerDialog

    lateinit var optionAdapter: OptionAdapter
    lateinit var selectedDiscussionTopic: ArrayList<String>

    var todaydate = " "
    private var selectedCustomer: String = ""



    lateinit var discurtionDatalist: ArrayList<String>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = inflater.inflate(R.layout.frag_call_recordentry, container, false)

        _binding = FragCallRecordentryBinding.inflate(inflater, container, false)


        /*
        RetailerViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"

        binding.txtCallEMP.setText(emp)
        binding.layoutCallEMP.setVisibility(View.GONE)

        prepare_spinner()
        currentDate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()
        getDiscurtionTopic("TS Discussion Topic")



        other = convertOtherInfo(requireActivity())!!
        binding.directcallPartycode.setText(other.partyCode.toString())

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
        binding.partyalladd.setText(division+" > "+district + " > "+thana)


        //  rootView.hideCallRecord.visibility = View.GONE
     //   rootView.hideSiteComplain.visibility = View.GONE


        onclickListener()
*/


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        RetailerViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )

        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"

        binding.txtCallEMP.setText(emp)
        binding.layoutCallEMP.setVisibility(View.GONE)

        prepare_spinner()
        currentDate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()
        getDiscurtionTopic("TS Discussion Topic")



        other = convertOtherInfo(requireActivity())!!
        binding.directcallPartycode.setText(other.partyCode.toString())

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
        binding.partyalladd.setText(division+" > "+district + " > "+thana)


        //  rootView.hideCallRecord.visibility = View.GONE
        //   rootView.hideSiteComplain.visibility = View.GONE


        onclickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }



    override fun OnMultiSpinnerItemSelected(p0: MutableList<String>?) {
        selecteddiscurtion = p0!!.joinToString(separator = ",")
        println("disMultispinner " + selecteddiscurtion)

       // TODO("Not yet implemented")
    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }


    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }


    fun currentDate() {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        val current = formatter.format(date)
        todaydate = current
    }


    fun onclickListener() {

        binding.txtCallNextVisit.setOnClickListener {

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
                    binding.txtCallNextVisit.setText(
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

//        rootView.multiSelectSpinnerCallDis.buildCheckedSpinner(discurtionDatalist){ selectedPositionList, displayString ->
//            // tvSelectedPosition.text = "Selected position:  $selectedPositionList" //if kotlin, python selecteed:returned postion will be 0,2
//            //tvDispString.text = "Display String:  $displayString"
//            selecteddiscurtion= displayString
//        }





        binding.txtCallDirectDisTopic.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                discussionTopicEntryDialog(discurtionDatalist)
            }

        }


        binding.btnSavedemoCallRecord.setOnClickListener {


            if (validationForCall()) {
                confirmationDialog()
            }



/*
            if(!(selectedVisitType.equals("Visit Type")||selectedVisitType.isEmpty())){
                if(selectedVisitType.equals("New Visit")||selectedVisitType.equals("Follow Up Visit")){
                   // confirmDialouge()
                   // confirmDialouge()
                    }

            }




            val navController = requireActivity().findNavController(R.id.nav_host_fragment)

            navController.navigate(R.id.nav_demo_Site_Home)
*/
        }

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
                binding.txtCallDirectDisTopic.setText(selecteddiscurtion)
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


    private fun loadOption(idGRV: GridView, context: Context, planList: ArrayList<String>
    ) {

        if (planList.size > 0) {
            optionAdapter = OptionAdapter(planList, context)
            idGRV.adapter = optionAdapter
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

    fun prepare_spinner(){
        prepareMoveStatusType(binding.acCallMoveStatusType)



    }


    //todo spinner
    private fun prepareMoveStatusType(ac: AutoCompleteTextView) {

        MovementTypeList = ArrayList()
        MovementTypeList.add("Movement Status")
        MovementTypeList.add("Pre Planned")
        MovementTypeList.add("Need Based")
      //   VisitTypeList.add("Complain")


        ac.setText(MovementTypeList[0])

        for (item in MovementTypeList) {
            val pos = MovementTypeList.indexOf(item)
            ac.setText(MovementTypeList[pos])
            // break

        }
        ac.setText(MovementTypeList[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, MovementTypeList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

        // Set up item click listener for the AutoCompleteTextView
        /*
        ac.setOnItemClickListener { parent, _, position, _ ->
            // Check if the selected item is the default "Visit Type"
            if (position == 0) {
                // Hide the layout
                selectedVisitType = VisitTypeList[position].toString()

                rootView.hideCallRecord.visibility = View.GONE
                rootView.hideSiteComplain.visibility = View.GONE
            } else if((position == 1)||(position == 2)){
                selectedVisitType = VisitTypeList[position].toString()

                rootView.hideCallRecord.visibility = View.VISIBLE
                rootView.hideSiteComplain.visibility = View.GONE
            }

            else if(position==3){
                selectedVisitType = VisitTypeList[position].toString()

                // Show the layout
                rootView.hideCallRecord.visibility = View.GONE
                rootView.hideSiteComplain.visibility = View.VISIBLE
            }
        }

         */


    }



    //todo mod of action
    private fun prepareProjectComplainSolveStatType(ac: AutoCompleteTextView) {

        ComSolveStatList = ArrayList()
        ComSolveStatList.add("Complain solve Status")
        ComSolveStatList.add("Yes")
        ComSolveStatList.add("No")
        ComSolveStatList.add("Pending")
        ComSolveStatList.add("Others")


        ac.setText(ComSolveStatList[0])

        for (item in ComSolveStatList) {
            val pos = ComSolveStatList.indexOf(item)
            ac.setText(ComSolveStatList[pos])
            // break

        }

        ac.setText(ComSolveStatList[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ComSolveStatList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    private fun prepareProjectRefByType(ac: AutoCompleteTextView) {

        RefferedByList = ArrayList()
        RefferedByList.add("Site Reffered by")
        RefferedByList.add("Self Finding")
        RefferedByList.add("Engineer")
        RefferedByList.add("Contractor")
        RefferedByList.add("Marketing & Sales (Trade)")
        RefferedByList.add("Marketing & Sales (Corporate)")
        RefferedByList.add("Dealer")
        RefferedByList.add("Retailer")
        RefferedByList.add("Call Center & Monitoring Team")
        RefferedByList.add("Other House Owner")
        RefferedByList.add("TS Engineer (Different Area)")
        RefferedByList.add("Top Management")
        RefferedByList.add("Others")


        ac.setText(RefferedByList[0])

        for (item in RefferedByList) {
            val pos = RefferedByList.indexOf(item)
            ac.setText(RefferedByList[pos])
            // break

        }
        ac.setText(RefferedByList[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, RefferedByList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }



    //todo validation

    private fun validationForCall(): Boolean {

            var result = false
            binding.layoutCallMoveStatusType.isErrorEnabled = false
            binding.layoutCallMoveStatusType.error = null
         //   rootView.layoutSiteId.isErrorEnabled = false
          //  rootView.layoutSiteId.error = null
            binding.layoutCallEMP.isErrorEnabled = false
            binding.layoutCallEMP.error = null
            binding.layoutCallDisTopic.isErrorEnabled = false
            binding.layoutCallDisTopic.error = null


           binding.layoutCallNextVisit.isErrorEnabled = false
           binding.layoutCallNextVisit.error = null


            if (binding.acCallMoveStatusType.text.toString().equals("Movement Status")||
                binding.acCallMoveStatusType.text.toString().isEmpty()) {
                result = false
                binding.layoutCallMoveStatusType.isErrorEnabled = true
                binding.layoutCallMoveStatusType.error = "Please select Movement Type"
            }


            else if (binding.txtCallEMP.text.toString().isEmpty()) {
                result = false
                binding.layoutCallEMP.isErrorEnabled = true
                binding.layoutCallEMP.error = "Please provide visit emp"
            }

            else if (
                selecteddiscurtion ==""
                ) {
                result = false
                binding.layoutCallDisTopic.isErrorEnabled = true
                binding.layoutCallDisTopic.error = "Please selcect Discurtion Topic"
            }


            else if (
                binding.txtCallNextVisit.text.toString().isEmpty()) {
                result = false
                binding.layoutCallNextVisit.isErrorEnabled = true
                binding.layoutCallNextVisit.error ="Please select next visit date."
                //  setError(rootView.layoutTsSiteConverStatus, "Please select the conversion status.")
            }



/*
            else if (!finalFile.isFile) {
                result = false
                Utility.getBaseMessage(
                    requireActivity(),
                    "Customer/site image is required",
                    "Please capture customer/site front view image",
                    R.drawable.error_white,
                    0
                )
            }

 */
            else {

                result = true
            }



            return result

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

        if( oth!= "Retailer"){
            txtCDialogTitle.text="$oth call record "
            txtBody.text="Do you want to give call record of this  $oth!"
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

            val empI = Utility.getValueByKey(requireActivity(), "username").toString()
        //    validationForCall()

            if(latitude==0.0){
                latitude= getLastKnownLocation()!!.latitude
                longitude= getLastKnownLocation()!!.longitude
            }

            val directCallrecordData = prepareOtherCustomerSiteData()
            TSCallMoveDirectSave(empI, directCallrecordData,dialog)




        }



    }

    private fun TSCallMoveDirectSave(
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
                                "Call record completed",
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

    private fun prepareOtherCustomerSiteData(): String {

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
        other = convertOtherInfo(requireActivity())!!
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
       // rootView.directcall_partycode.setText(other.partyCode.toString())

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        var delCurrentDate = "$year-$month-$day"

        val OtherCustomerdirectcallinfo = OtherCustomerdirectcallinfo(
            other.partyCode.toString(),
            "",
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
            binding.txtCallEMP.text.toString(),

            selecteddiscurtion,

            binding.txtSiteAddTopic.text.toString(),
            "",
            "",
            0,
            "",
            "",
            "",
            "",
            nextVisitDate,
            delCurrentDate,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            binding.acCallMoveStatusType.text.toString(),
            "",
            "",
            ""
        )

        val gson = Gson()
        return gson.toJson(OtherCustomerdirectcallinfo)
    }

//location
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
        val mLocationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

    override fun onResume() {
        super.onResume()
        //OnResume Fragment
        getLastLocation()
    }

}