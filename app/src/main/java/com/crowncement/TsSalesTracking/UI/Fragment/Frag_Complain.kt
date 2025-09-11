package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
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
import android.widget.Button
import android.widget.DatePicker
import android.widget.GridView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerdirectcallinfo
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.data.Model.TopicResponceChild
import com.crowncement.TsSalesTracking.databinding.FragComplainBinding
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
import java.io.ByteArrayOutputStream


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID


class Frag_Complain : Fragment() , MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener{

 //   private lateinit var rootView: View
    private var _binding: FragComplainBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Frag_Complain"
    private lateinit var picker: DatePickerDialog

    lateinit var VisitTypeList: ArrayList<String>
    lateinit var dicurtiontpTypeList: ArrayList<String>
    lateinit var ProjectConversionTypeList: ArrayList<String>
    lateinit var ModOfActionList: ArrayList<String>
    lateinit var ComSolveStatList: ArrayList<String>
    lateinit var MoveList: ArrayList<String>
    lateinit var ComInformedByList: ArrayList<String>
    lateinit var complainList: ArrayList<String>
    lateinit var modeOfActionDatalist: ArrayList<String>

    lateinit var reqList: ArrayList<String>


  //  lateinit var siteinfo: GetTSSiteInfoChild


    lateinit var loadingAnim: Dialog

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    lateinit var retailerViewModel: RetailerViewModel
    private val permissionCode = 101

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


    lateinit var selectedDiscussionTopic: ArrayList<String>
    lateinit var selectedModeOfActionTopic: ArrayList<String>


    lateinit var optionAdapter: OptionAdapter

    var currentdate = ""
    var selecteddiscurtion = ""
    var selectedAction = ""

    var SELECTED_PARTY_ID=""

    var resolveStatus = "Yes"
    var division = ""
    var district = ""
    var thana = ""
    lateinit var other: OtherCustomerChild
    var selectedSiteType = ""
    var selectedReferredType = ""

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    lateinit var complainrecieveDate: String
    lateinit var ResolveDate: String

    var currentPhotoPath = ""
    lateinit var ReferredTypeList: ArrayList<String>

    lateinit var clientTypes: ArrayList<String>


    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""
    private var selectedRoute: String = ""
    private var selectedRouteName: String = ""

    var selectedCustomer = ""
    var selectedComplain = ""
    var selectedModeOfAction = ""
    var REQUEST_IMAGE_CAPTURE = 0
    lateinit var finalFile: File


    lateinit var complainDatalist: ArrayList<String>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_complain, container, false)

        _binding = FragComplainBinding.inflate(inflater, container, false)
/*

        RetailerViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        var emp = Utility.getValueByKey(requireActivity(), "username").toString()



        finalFile = File("")

        complainList = ArrayList()

      //  rootView.txtComEMP.setText(emp)

     //   rootView.layoutCominformedID.setVisibility(View.GONE)
        binding.layoutCominformedID.setVisibility(View.GONE)
        binding.informedpersonId.setVisibility(View.GONE)
        binding.layoutComVisType.visibility=View.GONE
        binding.layoutComSolveStatus.visibility=View.GONE

        //radio
        binding.layoutComNoReason.visibility=View.GONE
        binding.layoutComResolveReq.visibility=View.GONE
        //



       // rootView.layoutComComplainResolveDate.visibility=View.GONE

        binding.layoutComMovStatus.visibility=View.GONE
        binding.acComSolveStatus.visibility=View.GONE


        prepare_spinner()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()




        other = convertOtherInfo(requireActivity())!!

        getComplain("TS Complain Topic")
        getModeOfAction("TS Complain Mode of Action")

        //  rootView.hideCallRecord.visibility = View.GONE
        //   rootView.hideSiteComplain.visibility = View.GONE

        onclickListener()





 */

     //   siteinfo = convertOtherSiteInfo(requireActivity())!!



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




        finalFile = File("")

        complainList = ArrayList()

        //  rootView.txtComEMP.setText(emp)

        //   rootView.layoutCominformedID.setVisibility(View.GONE)
        binding.layoutCominformedID.setVisibility(View.GONE)
        binding.informedpersonId.setVisibility(View.GONE)
        binding.layoutComVisType.visibility=View.GONE
        binding.layoutComSolveStatus.visibility=View.GONE

        //radio
        binding.layoutComNoReason.visibility=View.GONE
        binding.layoutComResolveReq.visibility=View.GONE
        //



        // rootView.layoutComComplainResolveDate.visibility=View.GONE

        binding.layoutComMovStatus.visibility=View.GONE
        binding.acComSolveStatus.visibility=View.GONE


        prepare_spinner()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()




        other = convertOtherInfo(requireActivity())!!

        getComplain("TS Complain Topic")
        getModeOfAction("TS Complain Mode of Action")

        //  rootView.hideCallRecord.visibility = View.GONE
        //   rootView.hideSiteComplain.visibility = View.GONE

        onclickListener()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }













    private fun prepareProjectRefferedByType(ac: AutoCompleteTextView) {

        ReferredTypeList = ArrayList()

        ReferredTypeList.add("Complain informed by")
        //1
        ReferredTypeList.add("Self finding")
        ReferredTypeList.add("Trade")
        ReferredTypeList.add("Corporate")
        ReferredTypeList.add("TS Team")
        //2
        ReferredTypeList.add("Engineer")
        ReferredTypeList.add("Contractor")
        ReferredTypeList.add("Retailer")
        ReferredTypeList.add("Dealer")
        ReferredTypeList.add("Mason")
        ReferredTypeList.add("IHBs")


        ac.setText(ReferredTypeList[0])

        for (item in ReferredTypeList) {
            val pos = ReferredTypeList.indexOf(item)
            ac.setText(ReferredTypeList[pos])
            // break

        }
        ac.setText(ReferredTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ReferredTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        // Set up item click listener for the AutoCompleteTextView

        ac.setOnItemClickListener { parent, _, position, _ ->
            // Check if the selected item is the default "Visit Type"
            if (position == 0) {
                // Hide the layout
                binding.informedpersonId.visibility=View.GONE
                binding.layoutCominformedID.visibility = View.GONE
                selectedReferredType = ReferredTypeList[position].toString()
             //   rootView.layoutTsPartSiteReferredEmp.visibility = View.GONE
            } else if ((position == 1) || (position == 2) || (position == 3) || (position == 4)) {
                selectedReferredType = ReferredTypeList[position].toString()
                binding.informedpersonId.text=""
             //   rootView.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                //  rootView.siteRunning.visibility = View.GONE
            } else if ((position == 5) || (position == 6) || (position == 7) || (position == 8)
                || (position == 9) || (position == 10)
            ) {
                selectedReferredType = ReferredTypeList[position].toString()

                // Show the layout
              //  rootView.layoutTsPartSiteReferredEmp.visibility = View.GONE
                // rootView.siteRunning.visibility = View.VISIBLE
                //todo pop up will come


            }
        }


    }

    private fun prepareComRequirmentType(ac: AutoCompleteTextView) {

        reqList = ArrayList()
        reqList.add("Requirements Type")
        reqList.add("Mobile Lab Visit")
        reqList.add("Hammer Test")
        reqList.add("Compressive Strength Test (Cylinder)")
        reqList.add("Cement Physical Test")
        reqList.add("Cement Chemical Test")
        reqList.add("Visit from Factory Laboratory")
        reqList.add("Sr. Officials Visit ")
        reqList.add("Others")


        ac.setText(reqList[0])

        for (item in reqList) {
            val pos = reqList.indexOf(item)
            ac.setText(reqList[pos])
            // break

        }
        ac.setText(reqList[0])

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reqList)

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()


    }



    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    fun onclickListener() {
        prepareProjectRefferedByType(binding.acComInformedBy)


        binding.takeTsComimage.setOnClickListener {
           // openCamera()
            captureImage()


        }

        binding.txtComComplainRecDate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]

            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    complainrecieveDate = selectedDate
                    binding.txtComComplainRecDate.setText(
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



        binding.txtComComplainResolveDate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            currentdate = "$year-$month-$day"

            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    ResolveDate = selectedDate
                    binding.txtComComplainResolveDate.setText(
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





        binding.acComInformedBy.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {
                if (ReferredTypeList[position] == "Dealer" || ReferredTypeList[position] == "Retailer"
                    || ReferredTypeList[position] == "Engineer" || ReferredTypeList[position] == "Contractor" ||
                    ReferredTypeList[position] == "IHBs" || ReferredTypeList[position] == "Mason"
                ) {
                 //   rootView.layoutTsPartSiteReferredEmp.visibility = View.GONE
                 //   rootView.txtRefferedPartiesID.visibility = View.VISIBLE
                    binding.informedpersonId.visibility = View.VISIBLE
                    binding.layoutCominformedID.visibility = View.GONE
                    siteReferenceDialog(ReferredTypeList[position])

                }
                else if(ReferredTypeList[position] == "Self finding"){
                    var emp = Utility.getValueByKey(requireActivity(), "username").toString()

                    binding.txtCominformedID.setText(emp)

                    binding.layoutCominformedID.visibility = View.VISIBLE
                    binding.informedpersonId.visibility=View.GONE

                }

                else {
                    binding.layoutCominformedID.visibility = View.VISIBLE
                    binding.informedpersonId.visibility=View.GONE
                   // rootView.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                 //   rootView.txtRefferedPartiesID.visibility = View.GONE
                }
            }

            else{
           //     rootView.layoutTsPartSiteReferredEmp.visibility = View.GONE
            //    rootView.txtRefferedPartiesID.visibility = View.GONE
            }
        }


        binding.btnSavedemoComRecord.setOnClickListener {

            if (validationForComplain()) {
                confirmationDialog()
            }

        }



        binding.txtComDisTopic.setOnFocusChangeListener { v, hasFocus ->


           // getComplain("TS Complain Topic")
            if (hasFocus) {
                discussionTopicEntryDialog(complainList)

             //   discussionTopicEntryDialog(complainDatalist)

            }

        }

//todo mutable
        binding.txtComConModOfAction.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
               // prepareMultiSpinnerDataFormodeOfAction()
                modeOfActionTopicEntryDialog(modeOfActionDatalist)
            }

        }


        //radio button
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = binding.root.findViewById(checkedId)
            // Do something with the selected radio button
        }

// To set default selection
        binding.radioButton1.isChecked = true

// To handle click events
        binding.radioButton1.setOnClickListener {

            // Do something when radioButton1 is clicked
            resolveStatus = "Yes"
            binding.layoutComNoReason.visibility=View.GONE
            binding.layoutComResolveReq.visibility=View.GONE
        }

        binding.radioButton2.setOnClickListener {
            // Do something when radioButton2 is clicked
            resolveStatus = "No"
            binding.layoutComNoReason.visibility=View.VISIBLE
            binding.layoutComResolveReq.visibility=View.VISIBLE
        }




    }







    //
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
                selectedComplain = selectedDiscussionTopic!!.joinToString(separator = ",")
                binding.txtComDisTopic.setText(selectedComplain)
             //   dataList.clear()
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

    // //for mode of action
    @SuppressLint("MissingInflatedId")
    private fun modeOfActionTopicEntryDialog(dataList: ArrayList<String>) {

        selectedModeOfActionTopic = ArrayList()
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
                selectedModeOfAction = selectedModeOfActionTopic!!.joinToString(separator = ",")
                binding.txtComConModOfAction.setText(selectedModeOfAction)
               // dataList.clear()
                dialog.dismiss()
            }

        }

        idGRV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->

            if (!checkExistence(dataList[position], selectedModeOfActionTopic)) {

                displayRouteChip(dataList[position], optionAcChipGroup, dataList)
                selectedModeOfActionTopic.add(dataList[position])
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


    //TODO CAPTURE IMAGES------------------------------------------------------------------------------>


    //TODO CAPTURE IMAGES------------------------------------------------------------------------------>
    private fun openCamera() {
        if(latitude==0.0){
            latitude= getLastKnownLocation()!!.latitude
            longitude= getLastKnownLocation()!!.longitude
        }

        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoPath = imageFile!!.absolutePath
        var storePath = Utility.storeKeyValue(requireActivity(),"currentPhotoPath",currentPhotoPath)

        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.tecnicalsupport.fileprovider",
            imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }

/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {

            if (resultCode == Activity.RESULT_OK) {
                var path = Utility.getValueByKey(requireActivity(),"currentPhotoPath")
                val selectedImage = BitmapFactory.decodeFile(path)
                //val selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "party"
                val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                finalFile = File(getRealPathFromURI(tempUri)!!)
                //only for TS
                binding.tsComplainImage.setImageBitmap(selectedImage)

                /*
                                if(retailerSetupInfo.split(",")[0] == "Retailer"){
                                    rootView.imgSetRetailerImage.setImageBitmap(selectedImage)
                                }
                                else{
                                    rootView.tsComplainImage.setImageBitmap(selectedImage)
                                }

                 */


            }

        } else {
            Utility.commonToast(
                requireActivity(),
                "Failed",
                "failed to capture party cover photo",
                0
            )
        }
    }


 */
    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)!!
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {

        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            // inImage,
            getResizedBitmap(inImage, 800),
            imageName,
            null
        )
        return Uri.parse(path)
    }





//image end


    fun validationForComplain(): Boolean {
        var result = false

        binding.layoutComVisType.isErrorEnabled = false
        binding.layoutComVisType.error = null
       // rootView.layoutComMovStatus.isErrorEnabled = false
       // rootView.layoutComMovStatus.error = null
        binding.layoutComInformedBy.isErrorEnabled = false
        binding.layoutComInformedBy.error = null
        binding.layoutComComplainRecDate.isErrorEnabled = false
        binding.layoutComComplainRecDate.error = null
        binding.layoutComDisTopic.isErrorEnabled = false
        binding.layoutComDisTopic.error = null
        binding.layoutComSiteAddTopic.isErrorEnabled = false
        binding.layoutComSiteAddTopic.error = null
        binding.layoutComConModOfAction.isErrorEnabled = false
        binding.layoutComConModOfAction.error = null


        //for uniqe
        binding.layoutComSolveStatus.isErrorEnabled = false
        binding.layoutComSolveStatus.error = null
        binding.layoutComComplainResolveDate.isErrorEnabled = false
        binding.layoutComComplainResolveDate.error = null

        binding.layoutComNoReason.isErrorEnabled = false
        binding.layoutComNoReason.error = null

        binding.layoutComResolveReq.isErrorEnabled = false
        binding.layoutComResolveReq.error = null


            //need to fix

        //  else if (rootView.acComDisTopic.text.toString().isEmpty()|| rootView.acComDisTopic.text.toString().equals("Complain Type")
         if (selectedComplain=="") {
            result = false
             binding.layoutComDisTopic.isErrorEnabled = true
             binding.layoutComDisTopic.error = "Please select complain type"
        }


         else if (binding.acComplVistype.text.toString().equals("Type")||
             binding.acComplVistype.text.toString().isEmpty()) {
            result = false
             binding.layoutComVisType.isErrorEnabled = true
             binding.layoutComVisType.error = "Please select visit Type"
        }
/*
        else if (rootView.acComMovStatus.text.toString().isEmpty()||
            rootView.acComMovStatus.text.toString().equals("Movement Status")
            ){
            result = false
            rootView.layoutComMovStatus.isErrorEnabled = true
            rootView.layoutComMovStatus.error = "Please provide movement status"
            // setError(rootView.layoutTsSiteType, "Please provide site type")
        }
        */
        else if (binding.acComInformedBy.text.toString().isEmpty()||
             binding.acComInformedBy.text.toString().equals("Complain informed by")
        ){
            result = false
             binding.layoutComInformedBy.isErrorEnabled = true
             binding.layoutComInformedBy.error = "Complain informed person is required"
            // setError(rootView.layoutTsSiteType, "Please provide site type")
        }
        else if (binding.txtComComplainRecDate.text.toString().isEmpty()) {
            result = false
             binding.layoutComComplainRecDate.isErrorEnabled = true
             binding.layoutComComplainRecDate.error = "Complain reslove date is required."
        }

        else if (selectedModeOfAction=="") {
            result = false
             binding.layoutComConModOfAction.isErrorEnabled = true
             binding.layoutComConModOfAction.error = "Mode of Action Type is required"
            // setError(rootView.layoutTsSiteCemBrand, "Brand is required")
        }
        else if (binding.acComSolveStatus.text.toString().isEmpty()) {
            result = false
             binding.layoutComSolveStatus.isErrorEnabled = true
             binding.layoutComSolveStatus.error = "Complain solve Status is required"
            // setError(rootView.layoutTsSiteCemBrand, "Brand is required")
        }

        else if (binding.txtComComplainResolveDate.text.toString().isEmpty()) {
            result = false
             binding.layoutComComplainResolveDate.isErrorEnabled = true
             binding.layoutComComplainResolveDate.error = "Tentative resolve date is required"
        }

         else if ((resolveStatus=="No")&&binding.txtComNoReason.text.toString().isEmpty()){
             result = false
             binding.layoutComNoReason.isErrorEnabled = true
             binding.layoutComNoReason.error = "Reason is required"

         }
         else if ((resolveStatus=="No")&&binding.acComResolveReq.text.toString().equals("Requirements Type")){
             result = false
             binding.layoutComResolveReq.isErrorEnabled = true
             binding.layoutComResolveReq.error = "Please select the requirement"
         }



        //todo
         else if (!finalFile.isFile) {
             result = false
             Utility.getBaseMessage(
                 requireActivity(),
                 "Complain image is required",
                 "Please capture complain  image",
                 R.drawable.error_white,
                 0
             )
         }



        //remarks optional

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
     //   siteinfo = convertOtherSiteInfo(requireActivity())!!

        val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer")
        oth = customertype.toString()

        if( oth!= "Retailer"){
            txtCDialogTitle.text="$oth Complain record "
            txtBody.text="Do you want to save Complain  of this  $oth!"
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
          //  validationForComplain()

            if(latitude==0.0){
                latitude= getLastKnownLocation()!!.latitude
                longitude= getLastKnownLocation()!!.longitude
            }

            val directComprecordData = prepareOtherCustomerCompSiteData()
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


                            else{
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
                                navController.navigate(R.id.nav_dashboard)
                            }


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


    ///todo for complain image upload

    private fun uploadTSComplainCover(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        party_image: File,
        loading: Dialog
    ) {
        retailerViewModel.uploadComplainDocCover(
            user_id,
            party_code,
            party_type,
            doc_type,
            doc_ext,
            party_image
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {

                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Complain added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )

                                    loadingAnim.dismiss()

                                    val navController =
                                        requireActivity().findNavController(R.id.nav_host_fragment)
                                    navController.navigate(R.id.nav_dashboard)

                                } else {

                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Complain Submission", "Error: " + e.message)
                            }

                        }
                    }
                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun prepareOtherCustomerCompSiteData(): String {
        var emp = Utility.getValueByKey(requireActivity(), "username").toString()

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
        other = convertOtherInfo(requireActivity())!!
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
       // rootView.informedpersonId.setText(SELECTED_PARTY_ID)
  //      siteinfo = convertOtherSiteInfo(requireActivity())!!



        if(binding.acComInformedBy.text.toString().equals("Self finding")||
            binding.acComInformedBy.text.toString().equals("Trade")||
            binding.acComInformedBy.text.toString().equals("Corporate")||
            binding.acComInformedBy.text.toString().equals("TS Team")){
            SELECTED_PARTY_ID= binding.txtCominformedID.text.toString()
        }






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
            other.partyPhone.toString(),
            other.partyName.toString(),
            other.partyMobile.toString(),
            other.partyAdd1.toString(),
            latitude,
            longitude,

            "Complain",
            emp,
            "",
            "",
            "",
            "",
            0,
            "",
            "",
            binding.txtComNoReason.text.toString(),
            "",
            ResolveDate,
            complainrecieveDate,
            "",
            SELECTED_PARTY_ID,
            selectedComplain,
            binding.txtComAddTopic.text.toString(),
            selectedModeOfAction,

            binding.acComSolveStatus.text.toString(),

            binding.acComResolveReq.text.toString(),
            "jpg",

            resolveStatus,
            "",
            ""

        )

        val gson = Gson()
        return gson.toJson(OtherCustomerdirectcallinfo)
    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    fun prepare_spinner(){
        prepareVisitType(binding.acComplVistype)
        prepareMovStatustopic(binding.acComMovStatus)
        prepareComInformedByType(binding.acComInformedBy)

        prepareProjectComplainSolveStatType(binding.acComSolveStatus)
        prepareComRequirmentType(binding.acComResolveReq)

    }


    //todo spinner
    private fun prepareVisitType(ac: AutoCompleteTextView) {

        VisitTypeList = ArrayList()

        VisitTypeList.add("Complain")


        ac.setText(VisitTypeList[0])

        for (item in VisitTypeList) {
            val pos = VisitTypeList.indexOf(item)
            ac.setText(VisitTypeList[pos])
            // break

        }
        ac.setText(VisitTypeList[0])

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, VisitTypeList)

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()



    }

    private fun prepareMovStatustopic(ac: AutoCompleteTextView) {
        MoveList = ArrayList()
        //  VisitTypeList.add("Visit Type")
        MoveList.add("Movement Status")
        MoveList.add("Pre Planned")
        MoveList.add("Need Based")


        ac.setText(MoveList[0])

        for (item in MoveList) {
            val pos = MoveList.indexOf(item)
            ac.setText(MoveList[pos])
            // break

        }
        ac.setText(MoveList[0])

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, MoveList)

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun prepareComInformedByType(ac: AutoCompleteTextView) {
        ComInformedByList = ArrayList()
        //  VisitTypeList.add("Visit Type")
        ComInformedByList.add("Complain informed by")
     //   ComInformedByList.add("Pre Planned")
     //   ComInformedByList.add("Need Based")


        ac.setText(ComInformedByList[0])

        for (item in ComInformedByList) {
            val pos = ComInformedByList.indexOf(item)
            ac.setText(ComInformedByList[pos])
            // break

        }
        ac.setText(ComInformedByList[0])

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ComInformedByList)

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }





    //todo conversion status




    //todo mod of action
    private fun prepareProjectModOfActionType(ac: AutoCompleteTextView) {

        ModOfActionList = ArrayList()
        ModOfActionList.add("Mode of Action")
//        ModOfActionList.add("Schimdt Rebound Hammer Test")
//        ModOfActionList.add("Concrete Compressive Strength (Cylinder)")
//        ModOfActionList.add("Slump Test")
//        ModOfActionList.add("Cement Sample Collection")
//        ModOfActionList.add("Mobile Concrete Lab Visit")
//        ModOfActionList.add("Others")



        ac.setText(ModOfActionList[0])

        for (item in ModOfActionList) {
            val pos = ModOfActionList.indexOf(item)
            ac.setText(ModOfActionList[pos])
            // break

        }
        ac.setText(ModOfActionList[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ModOfActionList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    //todo mod of action
    private fun prepareProjectComplainSolveStatType(ac: AutoCompleteTextView) {

        ComSolveStatList = ArrayList()
       // ComSolveStatList.add("Complain solve Status")

        ComSolveStatList.add("Pending")
     //   ComSolveStatList.add("Others")




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

    //for spinnner of complainer id
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



    val layoutThana = v.findViewById<TextInputLayout>(R.id.layoutThana)
    val layoutRoute = v.findViewById<TextInputLayout>(R.id.layoutRoute)

    val txtRetailerType = v.findViewById<TextView>(R.id.txtRetailerType)
    val txtNoDealerInfo = v.findViewById<TextView>(R.id.txtNoDealerInfo)


    val rdRetailer = v.findViewById<RecyclerView>(R.id.rdRetailer)

    loadClientType(acClient,partyType)
    getDivisionList("Bangladesh",acDivision)

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
        //   val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        //  navController.navigate(R.id.nav_Live_Attendance)
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
            selectedRouteName =
                routeList[position - 1].routeNo.toString() + ">" + routeList[position - 1].routeName.toString()
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
        clientTypes.add("Select Client Type")
        clientTypes.add("Dealer")
        clientTypes.add("Retailer")
        clientTypes.add("Engineer")
        clientTypes.add("IHBs")
        clientTypes.add("Mason")
        clientTypes.add("Contractor")

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
                        loadDivision(acDivision, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

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
                        loadDistrict(acDistrict, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

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

    private fun getDealerList(country: String, division: String, district: String,rdRetailer:RecyclerView,txtNoDealerInfo:TextView,dialog: Dialog) {
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
                                     rdRetailer:RecyclerView
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

                    SELECTED_PARTY_ID=items[position].partyCode.toString()
                    binding.informedpersonId.text ="Complainer ID : "+items[position].partyName.toString()+"("+SELECTED_PARTY_ID+")"

                  //  rootView.txtRefferedPartiesID.text="Party Name: "+items[position].partyName + "("+ SELECTED_PARTY_ID +")\n"+ "Address: "+items[position].partyAdd1
                    dialog.dismiss()

                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    fun prepareOtherObject(info: OtherCustomerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedOther", json)
        mPrefs.commit()
    }

    private fun filter(text: String,acClient:AutoCompleteTextView, txtNoDealerInfo:TextView,rdRetailer:RecyclerView,dialog: Dialog) {

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
                    SELECTED_PARTY_ID=items[position].dealerCode.toString()
                    binding.informedpersonId.text ="Complainer ID : "+items[position].dealerName.toString()+"("+SELECTED_PARTY_ID+")"
                    // rootView.txtRefferedPartiesID.text="Party Name: "+items[position].dealerName +"("+ SELECTED_PARTY_ID +")\n"+"Address: "+items[position].dealerAdd1
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

                    SELECTED_PARTY_ID=items[position].partyCode.toString()
                    binding.informedpersonId.text ="Complainer ID : "+items[position].partyName.toString()+"("+SELECTED_PARTY_ID+")"
                //    rootView.txtRefferedPartiesID.text="Party Name: "+items[position].partyName +"("+ SELECTED_PARTY_ID +")\n"+"Address: "+items[position].partyAdd1

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
        rdRetailer:RecyclerView,
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

    override fun OnMultiSpinnerItemSelected(p0: MutableList<String>?) {
       // TODO("Not yet implemented")
        selectedComplain = p0!!.joinToString(separator = ",")
        println("Multispinner " + selectedComplain)
    }





    private fun getComplain(
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


                            complainList = ArrayList()



                            for (i in 0 until res.data.size) {
                                complainList.add(topicList[i].CodeNo.toString())
                            }



                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


    private fun getModeOfAction(
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


                            modeOfActionDatalist = ArrayList()



                            for (i in 0 until res.data.size) {
                                modeOfActionDatalist.add(topicList[i].CodeNo.toString())
                            }


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



    //todo new capture img
    fun captureImage() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }


            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }


            else -> {
                requestPermissions()
            }
        }
    }




    private fun showCameraPermissionRationale() {
        AlertDialog.Builder(requireContext()).setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ ->


            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
    }


    private fun dispatchTakePictureIntent() {
        if(Build.VERSION.SDK_INT >= 15){
            try {


                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                photoFile?.also {

                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.crowncement.tecnicalsupport.fileprovider",
                        it
                    )

                    //  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //  startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }

            } catch (e: Exception) {

                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()


            }

        }
        else{
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                context?.let {
                    takePictureIntent.resolveActivity(it.packageManager)?.also {
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }

                        photoFile?.also {

                            val photoURI: Uri = FileProvider.getUriForFile(
                                requireContext(),
                                "com.crowncement.tecnicalsupport.fileprovider",
                                it
                            )

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            }
        }




    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getOutputDirectory()


        return File.createTempFile(
            "JPEG_${timeStamp}_bill_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, "CrownTitanAttendance").apply {
            mkdirs()
        }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
    }

    //todo new img
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            Log.e("IMAGE", "Image saved to $currentPhotoPath")


            try{


                val imgFile = compressImage(currentPhotoPath)


                if (imgFile.exists()) {
                    val imageSize = (imgFile.length() / 1024.0)
                    // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                    //  finalFile.add(imgFile)
                    finalFile = imgFile
                }


                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding.tsComplainImage.setImageBitmap(bitmap)




                if (latitude == 0.0) {
                    getLastLocation()


                    //   latitude = getLastKnownLocation()!!.latitude
                    //   longitude = getLastKnownLocation()!!.longitude
                }










            }
            catch (e: Exception) {
                Utility.commonToast(requireActivity(), e.message.toString(), e.message.toString(), 0)


            }






        }


        else {
            Toast.makeText(requireActivity(), "ERROR in image processing", Toast.LENGTH_LONG).show()
        }




    }



    fun compressImage(imagePath: String, maxSizeKB: Int = 50): File {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)


        // Calculate inSampleSize (power of 2) to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, 960, 960)
        options.inJustDecodeBounds = false


        // Decode bitmap with inSampleSize set
        var bitmap = BitmapFactory.decodeFile(imagePath, options)


        // Compress with quality adjustment
        var outputStream = ByteArrayOutputStream()
        var quality = 50 // Start with 90% quality


        do {
            outputStream.reset() // Clear the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // Reduce quality by 5% each iteration
        } while (outputStream.toByteArray().size > maxSizeKB * 960 && quality > 10)


        // If still too large, resize further
        if (outputStream.toByteArray().size > maxSizeKB * 960) {
            val scaleFactor =
                Math.sqrt((maxSizeKB * 960).toDouble() / outputStream.toByteArray().size)
            val newWidth = (bitmap.width * scaleFactor).toInt()
            val newHeight = (bitmap.height * scaleFactor).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        }
        return convertBitmapToFile(
            BitmapFactory.decodeByteArray(
                outputStream.toByteArray(),
                0,
                outputStream.toByteArray().size
            )
        )
    }


    private fun convertBitmapToFile(imageBitmap: Bitmap): File {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("collection", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        stream.flush()
        stream.close()
        return file
    }




    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1


        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }


        return inSampleSize
    }


}


