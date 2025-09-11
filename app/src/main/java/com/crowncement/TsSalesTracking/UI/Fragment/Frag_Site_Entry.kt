package com.crowncement.TsSalesTracking.UI.Fragment


import android.Manifest
import android.app.AlertDialog
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesVisitViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesVisitViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.DealerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.RetailerListAdapter
import com.crowncement.TsSalesTracking.data.Model.CallRecordSpinnerData
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerSite
import com.crowncement.TsSalesTracking.data.Model.ProfileForSiteTag
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.databinding.FragSiteEntryBinding
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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.io.ByteArrayOutputStream


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID




class Frag_Site_Entry : Fragment() {


    //  private lateinit var rootView: View
    private var _binding: FragSiteEntryBinding? = null
    private val binding get() = _binding!!


    private  val TAG = "Frag_Site_Entry"




    var division = ""
    var district = ""
    var thana = ""
    var loadingStatus = ""


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    lateinit var retailerViewModel: RetailerViewModel
    lateinit var salesVisitViewModel: SalesVisitViewModel


    private val permissionCode = 101


    var selectedSiteType = ""
    var selectedReferredType = ""


    var sitenum = ""
    lateinit var SiteTypeList: ArrayList<String>
    lateinit var ReferredTypeList: ArrayList<String>
    lateinit var ProjectStatusTypeList: ArrayList<String>


    var currentPhotoPath = ""
    var retailerSetupInfo = ""
    var ProjectStatus =""


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private var selectedCustomer: String = ""


    var fileSizeInMB: Double = 0.0
    var finalFileInKB: Double = 0.0




    lateinit var loadingAnim: Dialog


    lateinit var finalFile: File


    lateinit var clientTypes: ArrayList<String>


    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>
    lateinit var routeList: ArrayList<RouteChild>


    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""
    private var selectedRoute: String = ""
    private var selectedRouteName: String = ""


    lateinit var retaileritemAdapter: RetailerListAdapter
    lateinit var dealeritemAdapter: DealerListAdapter
    lateinit var otheritemAdapter: OtherCustomerListAdapter


    lateinit var retailerLists: ArrayList<RetailerChild>
    lateinit var dealerLists: ArrayList<DealerChild>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>


    var SELECTED_PARTY_ID=""
    var SELECTED_Contractor_ID=""
    var SELECTED_ENIGINEER_ID=""


    var siteArea = 0.0
    var storiedNew = 0
    var storiedRunning = 0
    var cementConsumptionNew =0
    var cementConsumptionRunning = 0
    var brand =""
    var brandPrice = 0.0
    var siteStatus = ""
    var siteRefBy =""
    var refId=""
    var type = ""


    var realPath = ""


    lateinit var uri: Uri


    var resizeCount = 0


    lateinit var  selectedImage:Bitmap
    private val REQUEST_IMAGE_CAPTURE = 1






    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>











    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  rootView = inflater.inflate(R.layout.frag_site_entry, container, false)


        _binding = FragSiteEntryBinding.inflate(inflater, container, false)


        uri = Uri.EMPTY


        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Use binding safely
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )


        initializeBitmapFromFile("site")


        retailerLists = ArrayList()
        dealerLists = ArrayList()
        otherCustomerLists = ArrayList()
        RetailerViewModelFactory()
        SalesVisitViewModelFactory()
        setupViewModel()
        getRetailerSetupInfo()
        type = Utility.getValueByKey(requireActivity(), "slected_party_type").toString()


        if (type=="Engineer"){
            binding.layoutEngineerCode.visibility = View.GONE
            binding.layoutContractorCode.visibility = View.VISIBLE


        }else if(type=="Contractor"){
            binding.layoutEngineerCode.visibility = View.VISIBLE
            binding.layoutContractorCode.visibility = View.GONE
        }else{
            binding.layoutEngineerCode.visibility = View.VISIBLE
            binding.layoutContractorCode.visibility = View.VISIBLE
        }
        prepare_spinner()
        finalFile = File("")
        prepareLocation()
        getLastLocation()


        binding.siteNew.visibility = View.GONE
        binding.siteRunning.visibility = View.GONE
        binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
        binding.txtRefferedPartiesID.visibility = View.GONE
        binding.layoutTsSiteStored.visibility = View.GONE




        onClickListener()




    }


    private fun initializeBitmapFromFile(filePath: String) {
        try {
            // Check if the file exists
            val file = File(filePath)
            if (file.exists()) {
                // Load the Bitmap from the file
                selectedImage = BitmapFactory.decodeFile(filePath)
            } else {
                // Handle the case where the file does not exist
                println("File does not exist: $filePath")
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., out of memory, invalid file format)
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }




    private fun setupViewModel() {


        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)


        salesVisitViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesVisitViewModelFactory())
                .get(SalesVisitViewModel::class.java)




    }


    fun prepare_spinner() {
        getAllCementBrandList()
        prepareType(binding.acTsSiteType)
        //  prepareBrand(rootView.acSiteBrand)
        prepareProjectStatusType(binding.acTsProjectSiteStatus)
        prepareProjectRefferedByType(binding.acTsTsSiteReferredby)
    }




    //todo spinner
    private fun prepareType(ac: AutoCompleteTextView) {


        SiteTypeList = ArrayList()
        SiteTypeList.add("Type")
        SiteTypeList.add("New")
        SiteTypeList.add("Running")




        ac.setText(SiteTypeList[0])


        for (item in SiteTypeList) {
            val pos = SiteTypeList.indexOf(item)
            ac.setText(SiteTypeList[pos])
            // break


        }
        ac.setText(SiteTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                SiteTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        // Set up item click listener for the AutoCompleteTextView


        ac.setOnItemClickListener { parent, _, position, _ ->
            // Check if the selected item is the default "Visit Type"
            if (position == 0) {
                // Hide the layout
                selectedSiteType = SiteTypeList[position].toString()
                binding.siteNew.visibility = View.GONE
                binding.siteRunning.visibility = View.GONE
            } else if ((position == 1)) {
                selectedSiteType = SiteTypeList[position].toString()


                binding.siteNew.visibility = View.VISIBLE
                binding.siteRunning.visibility = View.GONE
            } else if (position == 2) {
                selectedSiteType = SiteTypeList[position].toString()


                // Show the layout
                binding.siteNew.visibility = View.GONE
                binding.siteRunning.visibility = View.VISIBLE
            }
        }




    }




    //loadAllCementSpinner
    private fun loadAllCementSpinner(ac: AutoCompleteTextView,
                                     lists: ArrayList<CallRecordSpinnerData>) {


        val items: ArrayList<String> = ArrayList()
        items.add("Currently using Brand")
        for (item in lists) {
            items.add(item.Item.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()


    }




    //todo Present Site Status
    private fun prepareProjectStatusType(ac: AutoCompleteTextView) {


        ProjectStatusTypeList = ArrayList()
        ProjectStatusTypeList.add("Present Site Status")
        ProjectStatusTypeList.add("Foundation work")
        ProjectStatusTypeList.add("Piling")
        ProjectStatusTypeList.add("Basement")
        ProjectStatusTypeList.add("others")


        ac.setText(ProjectStatusTypeList[0])


        for (item in ProjectStatusTypeList) {
            val pos = ProjectStatusTypeList.indexOf(item)
            ac.setText(ProjectStatusTypeList[pos])
            // break


        }
        ac.setText(ProjectStatusTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ProjectStatusTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()




        ac.setOnItemClickListener { parent, _, position, _ ->
            // Check if the selected item is the default "Visit Type"
            if (position == 4) {
                // Hide the layout
                ProjectStatus = ProjectStatusTypeList[position].toString()


                binding.layoutTsSiteStored.visibility = View.VISIBLE


            } else{
                binding.layoutTsSiteStored.visibility = View.GONE


            }
        }






    }


    private fun prepareProjectRefferedByType(ac: AutoCompleteTextView) {


        ReferredTypeList = ArrayList()


        ReferredTypeList.add("Site referred by")
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
                selectedReferredType = ReferredTypeList[position].toString()
                binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
            } else if ((position == 1) || (position == 2) || (position == 3) || (position == 4)) {
                selectedReferredType = ReferredTypeList[position].toString()


                binding.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                //  rootView.siteRunning.visibility = View.GONE
            } else if ((position == 5) || (position == 6) || (position == 7) || (position == 8)
                || (position == 9) || (position == 10)
            ) {
                selectedReferredType = ReferredTypeList[position].toString()


                // Show the layout
                binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
                // rootView.siteRunning.visibility = View.VISIBLE
                //todo pop up will come




            }
        }




    }


    private fun getAllCementBrandList() {
        salesVisitViewModel.getAllCementList().observe(requireActivity()) {
            when (it.status) {


                Status.LOADING -> {
                }


                Status.SUCCESS -> {


                    it.responseData?.let { res ->
                        // noReasonList = java.util.ArrayList()
                        // noReasonList = res.data
                        loadAllCementSpinner(binding.acSiteBrand, res.data)


                    }
                }
                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                }
            }
        }
    }






    private fun getRetailerSetupInfo() {


        retailerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()


        if (retailerSetupInfo.isNotEmpty()) {




            binding.txtTSOtherArea.text =
                retailerSetupInfo.split(",")[1] + ">" +
                        retailerSetupInfo.split(",")[2] + ">" +
                        retailerSetupInfo.split(",")[3]




        }




    }



    /*
    fun resizer() {




        if (finalFile.isFile) {


            when (resizeCount) {
                0 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(960, 540)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }


                1 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(768, 432)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }


                2 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(640, 360)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }


                3->{
                    if(finalFileInKB>=200){
                        selectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "currentPhotoPath"))
                        selectedImage= getResizedBitmap(selectedImage,600)!!


                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "party"
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        uri = tempUri!!
                        realPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
                    }else{
                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }
                }


                else -> {
                    if(finalFileInKB>=200) {
                        selectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentPhotoPath"
                            )
                        )
                        selectedImage = getResizedBitmap(selectedImage, 500)!!


                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "party"
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        // val tempUri = getImageUri(requireContext(), selectedImage!!, "Site")
                        uri = tempUri!!
                        realPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
                    }else{


                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }


                }
            }


            Utility.commonToast(
                requireActivity(),
                "Image compression done",
                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
                1
            )


            resizeCount += 1
        } else {
            Log.e("Site_Entry", "Error: ")
        }


        binding.resizeImage.isEnabled = true




    }

     */






    fun onClickListener() {
        /*
                   binding.resizeImage.setOnClickListener {
                       //  openCamera()
                     //  if(finalFileInKB>=200) {


                      finalFile = File(getRealPathFromURI(uri)!!)


                       binding.resizeImage.isEnabled = false


                       resizer()
                      // }


                   }




        */
        binding.takeTsSiteimage.setOnClickListener {


            //   openCamera()
            captureImage()


        }


        binding.txtEngineerCode.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                siteReferenceDialog("Engineer")
            }
        }


        binding.txtContractorCode.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                siteReferenceDialog("Contractor")
            }
        }




        binding.btnSaveSite.setOnClickListener {
            var selectedParty = Utility.getValueByKey(requireActivity(),"selectedCustomer")
            Utility.storeKeyValue(
                requireActivity(),
                "selectedCustomer",
                selectedParty
            )
            if (selectedSiteType.isEmpty() || selectedSiteType.equals("New")) {
                if (uiValidationForNewSite()) {
                    if(validationforRefferedType()){
                        selectedReferredType= binding.acTsTsSiteReferredby.text.toString()
                        if (selectedReferredType.equals("Site referred by") || selectedReferredType.equals(
                                "Self finding"
                            ) || selectedReferredType.equals("Trade") || selectedReferredType.equals("Corporate")
                            || selectedReferredType.equals("TS Team")
                        ) {
                            if (validationForRefferedTypeOnetoFour()) {
                                confirmationDialog()
                            }
                        } else if (selectedReferredType.equals("Site referred by") || selectedReferredType.equals(
                                "Engineer"
                            ) || selectedReferredType.equals("Contractor") || selectedReferredType.equals(
                                "Retailer"
                            )
                            || selectedReferredType.equals("Dealer") || selectedReferredType.equals("Mason") || selectedReferredType.equals(
                                "IHBs"
                            )
                        ) {
                            if (validationForRefferedTypeFivetoTen()) {
                                confirmationDialog()
                            }
                        }




                    }}
                //
            } else if (selectedSiteType.isEmpty() || selectedSiteType.equals("Running")) {
                if (uiValidationForRunningSite()) {
                    if(validationforRefferedType()){
                        selectedReferredType= binding.acTsTsSiteReferredby.text.toString()


                        if (selectedReferredType.equals("Self finding") || selectedReferredType.equals("Trade") || selectedReferredType.equals(
                                "Corporate"
                            )
                            || selectedReferredType.equals("TS Team")
                        ) {
                            if (validationForRefferedTypeOnetoFour()) {
                                confirmationDialog()
                            }
                        } else if (selectedReferredType.equals("Engineer") || selectedReferredType.equals(
                                "Contractor"
                            ) || selectedReferredType.equals("Retailer")
                            || selectedReferredType.equals("Dealer") || selectedReferredType.equals("Mason") || selectedReferredType.equals(
                                "IHBs"
                            )
                        ) {
                            if (validationForRefferedTypeFivetoTen()) {
                                confirmationDialog()
                            }
                        }


                    }}
            }




        }






        binding.acTsTsSiteReferredby.setOnItemClickListener { parent, arg1, position, arg3 ->


            if (position > 0) {
                if (ReferredTypeList[position] == "Dealer" || ReferredTypeList[position] == "Retailer"
                    || ReferredTypeList[position] == "Engineer" || ReferredTypeList[position] == "Contractor" ||
                    ReferredTypeList[position] == "IHBs" || ReferredTypeList[position] == "Mason"
                ) {
                    binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
                    binding.txtRefferedPartiesID.visibility = View.VISIBLE


                    siteReferenceDialog(ReferredTypeList[position])


                }
                else if(ReferredTypeList[position] == "Self finding"){
                    var emp = Utility.getValueByKey(requireActivity(), "username").toString()


                    binding.txtTsPartSiteReferredEmp.setText(emp)
                    binding.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                    binding.txtRefferedPartiesID.visibility = View.GONE




                }


                else {
                    binding.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                    binding.txtRefferedPartiesID.visibility = View.GONE
                }
            }


            else{
                binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
                binding.txtRefferedPartiesID.visibility = View.GONE
            }
        }


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


/*
    private fun dispatchTakePictureIntent() {
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

 */


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






    //TODO CAPTURE IMAGES------------------------------------------------------------------------------>






    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun openCamera() {
        if (latitude == 0.0) {
            val location = getLastKnownLocation()
            latitude = location?.latitude ?: 0.0
            longitude = location?.longitude ?: 0.0
        }


        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile("photo", ".jpg", storageDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        currentPhotoPath = imageFile!!.absolutePath
        Utility.storeKeyValue(requireActivity(), "currentPhotoPath", currentPhotoPath)


        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "${requireActivity().packageName}.fileprovider",
            imageFile
        )


        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }




    /*
       override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           super.onActivityResult(requestCode, resultCode, data)
           try {
               if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
                   val path = Utility.getValueByKey(requireActivity(), "currentPhotoPath")
                   val selectedImage = BitmapFactory.decodeFile(path)


                   val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                   val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "party"
                   val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                   Log.e("realPath", getRealPathFromURI(tempUri)!!)
                   uri = tempUri!!


                   realPath = getRealPathFromURI(tempUri)!!
                   finalFile = File(getRealPathFromURI(tempUri)!!)
                   if (path != null) {
                       fileSizeInMB = path.length.toDouble() / (1024 * 1024)
                   }


                   GlobalScope.launch {


                       //  finalFile= Compressor.compress(requireContext(), finalFile)


                       finalFile = Compressor.compress(requireContext(), finalFile) {
                           resolution(1280, 720)
                           quality(80)
                           format(Bitmap.CompressFormat.JPEG)
                           size(204_800) // 200 KB
                       }
                   }


                   // Display captured image
                   binding.tsSiteImage.setImageBitmap(selectedImage)
               } else {
                   Utility.commonToast(requireActivity(), "Failed", "Failed to capture photo", 0)
               }
           }
           catch (e: Exception) {
               Utility.getBaseMessage(
                   requireActivity(),
                   "আপনার ফোনের স্টোরেজ ভর্তি হয়ে গেছে!",
                   "ক্রাউন টাইটান অ্যাপস দিয়ে আজকের তোলা কিছু ছবি মুছে ফেলুন এবং ৮ জিবি জায়গা ফাঁকা করুন।",
                   R.drawable.error_white,
                   2
               )
           }
       }


    */
/////////////////////////////////////////////////////////////////////////////////


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
                binding.tsSiteImage.setImageBitmap(bitmap)




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




    //todo img end




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
            inImage,
            //  getResizedBitmap(inImage, 800),
            imageName,
            null
        )
        return Uri.parse(path)
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


        var oth = retailerSetupInfo.split(",")[0]


        val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer")
        oth = customertype.toString()
        selectedCustomer = oth




        if (oth != "Retailer") {
            txtCDialogTitle.text = "New Site Addition"
            txtBody.text = "Are you sure , you want to add this new site of $oth!"
        }
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()


        cancel.setOnClickListener {
            dialog.dismiss()
        }


        btnCancelR.setOnClickListener {
            dialog.dismiss()
        }


        btnConfirmR.setOnClickListener {


            val empI = Utility.getValueByKey(requireActivity(), "username").toString()




            if (latitude == 0.0) {
                //  latitude = getLastKnownLocation()!!.latitude
                //  longitude = getLastKnownLocation()!!.longitude
                getLastLocation()


            }


            val refferedPersonType = binding.acTsTsSiteReferredby.text.toString()




//todo working
            if (selectedSiteType.equals("New")) {
                siteArea = binding.txtTsSiteArea.text.toString().toDouble()
                storiedNew = binding.txtTsPlanedSiteStored.text.toString().toInt()
                storiedRunning = 0
                cementConsumptionNew = binding.txtTsSiteAproxCemAmount.text.toString().toInt()
                brand = ""
                cementConsumptionRunning = 0
                brandPrice = 0.0
                siteStatus = ""






                if (refferedPersonType.equals("Self finding") || refferedPersonType.equals("Trade") || refferedPersonType.equals(
                        "Corporate"
                    ) || refferedPersonType.equals("TS Team")
                ) {
                    refId = binding.txtTsPartSiteReferredEmp.text.toString()
                    siteRefBy = "Employee"


                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedNew,
                        cementConsumptionNew,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)




                } else if (refferedPersonType.equals("Dealer")) {
                    refId = SELECTED_PARTY_ID
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedNew,
                        cementConsumptionNew,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)
                } else if (refferedPersonType.equals("Retailer")) {
                    refId = SELECTED_PARTY_ID
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedNew,
                        cementConsumptionNew,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)


                } else {
                    refId = SELECTED_PARTY_ID
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedNew,
                        cementConsumptionNew,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)




                }


            }
            else if (selectedSiteType.equals("Running")) {
                siteArea = 0.0
                storiedNew = 0
                if(binding.txtTsSiteStored.text.toString().isNotEmpty()){
                    storiedRunning = binding.txtTsSiteStored.text.toString().toInt()
                }else{
                    storiedRunning = 0
                }


                cementConsumptionNew = 0
                brand = binding.acSiteBrand.text.toString()
                cementConsumptionRunning = binding.txtTsPriPartSiteCemCon.text.toString().toInt()
                brandPrice = binding.txtTsSiteComPrice.text.toString().toDouble()
                siteStatus = binding.acTsProjectSiteStatus.text.toString()
                //}


                if (refferedPersonType.equals("Self finding") || refferedPersonType.equals("Trade") || refferedPersonType.equals(
                        "Corporate"
                    ) || refferedPersonType.equals("TS Team")
                ) {
                    refId = binding.txtTsPartSiteReferredEmp.text.toString()
                    siteRefBy = "Employee"


                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedRunning,
                        cementConsumptionRunning,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)




                } else if (refferedPersonType.equals("Dealer")) {
                    refId = ""
                    refId = SELECTED_PARTY_ID
                    // refId = rootView.txtRefferedPartiesID.text.toString()
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedRunning,
                        cementConsumptionRunning,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)
                } else if (refferedPersonType.equals("Retailer")) {
                    refId = SELECTED_PARTY_ID


                    // refId = rootView.txtRefferedPartiesID.text.toString()
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedRunning,
                        cementConsumptionRunning,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)


                } else {
                    //refId = rootView.txtRefferedPartiesID.text.toString()
                    refId = SELECTED_PARTY_ID
                    siteRefBy = refferedPersonType
                    val otherCusSiteData = prepareOtherCustomerSiteData(
                        siteArea,
                        storiedRunning,
                        cementConsumptionRunning,
                        brand,
                        brandPrice,
                        siteStatus,
                        refId,
                        siteRefBy
                    )
                    SaveSaleBPTsNewSite(empI, oth, otherCusSiteData, dialog)




                }


            }








        }




    }




    private fun prepareOtherCustomerSiteData(
        siteArea: Double,
        storied:Int,
        cementConsumption:Int,
        brand:String,
        brandPrice: Double,
        siteStatus:String,
        refId:String,
        siteRefBy:String


    ): String {


        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()




        val otherCustomerSite = OtherCustomerSite(
            binding.txtTsPartSiteAdd.text.toString(),
            binding.acTsSiteType.text.toString(),
            siteArea,
            storied,
            cementConsumption,
            brand,
            brandPrice,
            siteStatus,
            "",
            "",
            latitude,
            longitude,
            "Bangladesh",
            division,
            district,
            thana,
            siteRefBy,
            refId


        )


        val gson = Gson()
        return gson.toJson(otherCustomerSite)
    }




    private fun SaveSaleBPTsNewSite(
        user: String,
        customerType: String,
        value: String,
        dialog: Dialog
    ) {
        // dialog.dismiss()


        retailerViewModel.SaveSaleBPTsNewSite(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {


                }


                Status.SUCCESS -> {


                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            sitenum = res.bpId.toString()
                            dialog.dismiss()
                            //todo ihbs tag
                            val profileData = prepareListForTag()
                            SaleBPSiteTag(user,profileData,dialog)


                            //todo site img
                            /*
                            if (finalFile.isFile) {


                                uploadTSSiteCover(
                                    user,
                                    res.bpId.toString(),
                                    customerType,
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    dialog
                                )


                            }










                            else {
                               // loadingAnim.dismiss()
                                dialog.dismiss()
                                Toast.makeText(requireContext(), "File missing ", Toast.LENGTH_LONG)
                                    .show()


                            }


                             */




                        } else {
                            //loadingAnim.dismiss()
                            dialog.dismiss()


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
                    dialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }




    ///todo for image upload
    private fun uploadTSSiteCover(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File,
        loading: Dialog
    ) {
        retailerViewModel.uploadTSSiteCover(
            user_id,
            party_image,
            party_code,
            party_lat,
            party_lon
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()

                                    sitenum =party_code
                                    //deletePictureFile(realPath)
                                    // val profileData = prepareListForTag()
                                    // SaleBPSiteTag(user_id,profileData,loading)
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Site added and tagged with the party successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )
                                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                                    navController.navigate(R.id.nav_demo_allSiteList)



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
                                Log.e("Site Add", "Error: " + e.message)
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




    //TODO LOCATION ----------------------------------------------------------------------------------->
    /*
       private fun prepareLocation() {
           val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
               requireContext().packageName, PackageManager.GET_META_DATA
           )
           val value = ai.metaData["com.google.android.geo.API_KEY"]
           val apiKey = value.toString()


           mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
       }


    */


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }








    //
    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        getLastLocation()


    }




    /*
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()


                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude


                        Log.e("Add Customer", "Latitude: $latitude | Longitude:$longitude")


                    }
                }
            } else {
                requestDeviceLocationSettings()
            }


        } else {
            requestPermissions()
        }
    }


     */


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


                    }
                }
            }


        }
        else {
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
                    Log.e("Frag_Site_Entry", "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }
    }


//todo








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




            Log.e(TAG, "onLocationResult: lat: $latitude")
            Log.e(TAG, "onLocationResult: long: $longitude")
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




    //todo latest design validation


    fun uiValidationForNewSite(): Boolean {


        var result = false
        binding.layoutTsPartSiteAdd.isErrorEnabled = false
        binding.layoutTsPartSiteAdd.error = null
        binding.layoutTsSiteType.isErrorEnabled = false
        binding.layoutTsSiteType.error = null
        //for new
        binding.layoutTsSiteArea.isErrorEnabled = false
        binding.layoutTsSiteArea.error = null
        binding.layoutTsPlanedSiteStored.isErrorEnabled = false
        binding.layoutTsPlanedSiteStored.error = null
        binding.layoutTsSiteAproxCemAmount.isErrorEnabled = false
        binding.layoutTsSiteAproxCemAmount.error = null


        //running
        binding.layoutTsSiteCemBrand.isErrorEnabled = false
        binding.layoutTsSiteCemBrand.error = null
        binding.layoutTsSiteComPrice.isErrorEnabled = false
        binding.layoutTsSiteComPrice.error = null
        binding.layoutTsProjectSiteStatus.isErrorEnabled = false
        binding.layoutTsProjectSiteStatus.error = null
        //   rootView.layoutTsSiteStored.isErrorEnabled = false
        //   rootView.layoutTsSiteStored.error = null
        binding.layoutTsPriPartSiteCemCon.isErrorEnabled = false
        binding.layoutTsPriPartSiteCemCon.error = null
        //common
        binding.layoutTsSiteReferredby.isErrorEnabled = false
        binding.layoutTsSiteReferredby.error = null




        /*
        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0


            if(finalFileInKB>=200){
                binding.resizeImage.visibility = View.VISIBLE
            }else{
                binding.resizeImage.visibility = View.GONE


            }
        }


         */
//common
        if (binding.txtTsPartSiteAdd.text.toString().isEmpty()) {
            result = false
            binding.layoutTsPartSiteAdd.isErrorEnabled = true
            binding.layoutTsPartSiteAdd.error = "Please provide site address"
        } else if (binding.acTsSiteType.text.toString().equals("Type") ||
            binding.acTsSiteType.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteType.isErrorEnabled = true
            binding.layoutTsSiteType.error = "Please provide site type"
        }




        //new
        else if (binding.txtTsSiteArea.text.toString().isEmpty()) {
            result = false
            binding.layoutTsSiteArea.isErrorEnabled = true
            binding.layoutTsSiteArea.error = "Site area is required"
        } else if (binding.txtTsPlanedSiteStored.text.toString().isEmpty()) {
            result = false
            binding.layoutTsPlanedSiteStored.isErrorEnabled = true
            binding.layoutTsPlanedSiteStored.error = "Please provide number of story"
        } else if (binding.txtTsSiteAproxCemAmount.text.toString().isEmpty()) {
            result = false
            binding.layoutTsSiteAproxCemAmount.isErrorEnabled = true
            binding.layoutTsSiteAproxCemAmount.error = "Approximate cement consumption is required"
        }


        else if (!finalFile.isFile) {
            Utility.getBaseMessage(
                requireActivity(),
                "Site image is required ",
                "Please attach party image with in 200KB ",
                R.drawable.error_white,
                2
            )
        }


        /*
        else if (!finalFile.isFile||finalFileInKB>200) {
        result = false
        if (!finalFile.isFile) {
            Utility.getBaseMessage(
                requireActivity(),
                "Site image is required ",
                "Please attach party image with in 200KB ",
                R.drawable.error_white,
                2
            )
        }


        else {
            Utility.getBaseMessage(
                requireActivity(),
                "Site image size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    finalFileInKB
                ) + " KB",
                R.drawable
                    .error_white,
                2
            )
            // binding.resizeImage.visibility = View.VISIBLE


        }




    }




         */
        else {
            result = true
        }




        return result


    }


    fun uiValidationForRunningSite(): Boolean {


        var result = false
        binding.layoutTsPartSiteAdd.isErrorEnabled = false
        binding.layoutTsPartSiteAdd.error = null
        binding.layoutTsSiteType.isErrorEnabled = false
        binding.layoutTsSiteType.error = null




        //running
        binding.layoutTsSiteCemBrand.isErrorEnabled = false
        binding.layoutTsSiteCemBrand.error = null
        binding.layoutTsSiteComPrice.isErrorEnabled = false
        binding.layoutTsSiteComPrice.error = null
        binding.layoutTsProjectSiteStatus.isErrorEnabled = false
        binding.layoutTsProjectSiteStatus.error = null
        //   rootView.layoutTsSiteStored.isErrorEnabled = false
        //   rootView.layoutTsSiteStored.error = null
        binding.layoutTsPriPartSiteCemCon.isErrorEnabled = false
        binding.layoutTsPriPartSiteCemCon.error = null
        //common
        binding.layoutTsSiteReferredby.isErrorEnabled = false
        binding.layoutTsSiteReferredby.error = null


        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0
            if(finalFileInKB>=200){
                //  binding.resizeImage.visibility = View.VISIBLE
            }else{
                //   binding.resizeImage.visibility = View.GONE


            }
        }


//common
        if (binding.txtTsPartSiteAdd.text.toString().isEmpty()) {
            result = false
            binding.layoutTsPartSiteAdd.isErrorEnabled = true
            binding.layoutTsPartSiteAdd.error = "Please provide site address"
        } else if (binding.acTsSiteType.text.toString().equals("Type") ||
            binding.acTsSiteType.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteType.isErrorEnabled = true
            binding.layoutTsSiteType.error = "Please provide site type"
        }




        //Running
        else if (binding.acSiteBrand.text.toString().equals("Currently using Brand") ||
            binding.acSiteBrand.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteCemBrand.isErrorEnabled = true
            binding.layoutTsSiteCemBrand.error = "Currently using Brand is required"
        } else if (binding.txtTsSiteComPrice.text.toString().isEmpty()) {
            result = false
            binding.layoutTsSiteComPrice.isErrorEnabled = true
            binding.layoutTsSiteComPrice.error = "Brand price is required."
        } else if (binding.acTsProjectSiteStatus.text.toString().equals("Present Site Status") ||
            binding.acTsProjectSiteStatus.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsProjectSiteStatus.isErrorEnabled = true
            binding.layoutTsProjectSiteStatus.error = "Present Site Status is required"
        }


        else if (binding.acTsProjectSiteStatus.text.toString().equals("others")
            &&binding.txtTsSiteStored.text.toString().isEmpty()) {
            result = false
            binding.layoutTsSiteStored.isErrorEnabled = true
            binding.layoutTsSiteStored.error = "Current number of storied is required"
        }






        else if (
            binding.txtTsPriPartSiteCemCon.text.toString().isEmpty()) {
            result = false
            binding.layoutTsPriPartSiteCemCon.isErrorEnabled = true
            binding.layoutTsPriPartSiteCemCon.error =
                "Approximate remaining cement consumption is required"
        }




        //common
        else if (!finalFile.isFile||finalFileInKB>200) {
            result = false
            if (!finalFile.isFile) {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Site image is required ",
                    "Please attach party image with in 200KB ",
                    R.drawable.error_white,
                    2
                )
            }


            else {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Site image size exceed expected max file size 200 KB ",
                    "After compression your file size is   " + String.format(
                        "%.2f",
                        finalFileInKB
                    ) + " KB",
                    R.drawable
                        .error_white,
                    2
                )
                // binding.resizeImage.visibility = View.VISIBLE


            }
        } else {
            result = true
        }






        return result


    }


    fun validationforRefferedType(): Boolean{
        var result = false
        binding.layoutTsSiteReferredby.isErrorEnabled = false
        binding.layoutTsSiteReferredby.error = null
        if (binding.acTsTsSiteReferredby.text.toString().equals("Site referred by") ||
            binding.acTsTsSiteReferredby.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteReferredby.isErrorEnabled = true
            binding.layoutTsSiteReferredby.error = "Referred person by is required"
        } else {
            result = true
        }






        return result
    }


    fun validationForRefferedTypeOnetoFour(): Boolean {
        var result = false
        binding.layoutTsSiteReferredby.isErrorEnabled = false
        binding.layoutTsSiteReferredby.error = null
        binding.layoutTsPartSiteReferredEmp.isErrorEnabled = false
        binding.layoutTsPartSiteReferredEmp.error = null


        if (binding.acTsTsSiteReferredby.text.toString().equals("Site referred by") ||
            binding.acTsTsSiteReferredby.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteReferredby.isErrorEnabled = true
            binding.layoutTsSiteReferredby.error = "Referred person by is required"
        } else if (binding.txtTsPartSiteReferredEmp.text.toString().isEmpty()) {
            result = false
            binding.layoutTsPartSiteReferredEmp.isErrorEnabled = true
            binding.layoutTsPartSiteReferredEmp.error = "Referred person's EMP ID by is required"
        } else {
            result = true
        }






        return result
    }


    fun validationForRefferedTypeFivetoTen(): Boolean {
        var result = false
        binding.layoutTsSiteReferredby.isErrorEnabled = false
        binding.layoutTsSiteReferredby.error = null


        if (binding.acTsTsSiteReferredby.text.toString().equals("Site referred by") ||
            binding.acTsTsSiteReferredby.text.toString().isEmpty()
        ) {
            result = false
            binding.layoutTsSiteReferredby.isErrorEnabled = true
            binding.layoutTsSiteReferredby.error = "Referred person by is required"
        } else {
            result = true
        }






        return result




    }




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
            //   val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            //  navController.navigate(R.id.nav_Live_Attendance)
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
        // clientTypes.add("Select Client Type")
        //  clientTypes.add("Dealer")
        //  clientTypes.add("Retailer")
        clientTypes.add(selectedText)
        //  clientTypes.add("IHBs")
        //   clientTypes.add("Mason")
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


                        //  loadThana(acThana, res.data,empthanaList)
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
        loadingAnim.show()
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {


                    Status.LOADING -> {
                        loadingAnim.dismiss()
                    }


                    Status.SUCCESS -> {


                        it.responseData?.let { res ->


                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            prepareOtherList(rdRetailer, otherCustomerList, xCode,txtNoDealerInfo, dialog)
                            loadingAnim.dismiss()
                        }
                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
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
                    if(type=="Engineer"){
                        SELECTED_ENIGINEER_ID = items[position].partyCode.toString()
                        binding.txtEngineerCode.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")
                    }
                    else if(type=="Contractor"){
                        SELECTED_Contractor_ID = items[position].partyCode.toString()
                        binding.txtContractorCode.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")


                    }


                    SELECTED_PARTY_ID=items[position].partyCode.toString()
                    binding.txtRefferedPartiesID.text="Party Name: "+items[position].partyName + "("+ SELECTED_PARTY_ID +")\n"+ "Address: "+items[position].partyAdd1
                    dialog.dismiss()




                }


            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
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
                                // if(filteredlist.size>0){
                                filteredlist.add(item)
                                // }


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
                            //   if(filteredlist.size>0) {
                            prepareOtherList(
                                rdRetailer,
                                filteredlist,
                                acClient.text.toString(),
                                txtNoDealerInfo,
                                dialog
                            )
                            //   }
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


    private fun prepareDealerList(rv: RecyclerView, items: ArrayList<DealerChild>, type: String,txtNoDealerInfo:TextView,dialog: Dialog) {


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
                    binding.txtRefferedPartiesID.text="Party Name: "+items[position].dealerName +"("+ SELECTED_PARTY_ID +")\n"+"Address: "+items[position].dealerAdd1
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
                    binding.txtRefferedPartiesID.text="Party Name: "+items[position].partyName +"("+ SELECTED_PARTY_ID +")\n"+"Address: "+items[position].partyAdd1


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






    //site tag




    fun prepareListForTag(): String {


        val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()


        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()


        //  selectedCustomer =

        val ProfileForSiteTag = ProfileForSiteTag(
            partyCode,
            sitenum,
            customertype
        )


        val gson = Gson()
        return gson.toJson(ProfileForSiteTag)
    }

    //todo ihbs tag
    private fun SaleBPSiteTag(
        user: String,
        value: String,
        loading: Dialog
    ) {

        loading.show()


        retailerViewModel.SaleBPSiteTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {


                }
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        if (res.code == "200") {

                            loading.dismiss()
                            if(SELECTED_ENIGINEER_ID.isNotEmpty()){
                                val profileData = prepareListForTagEng()
                                SaleBPSiteTagForEng(user,profileData,loading)


                            }
                            if(SELECTED_Contractor_ID.isNotEmpty()){
                                val profileData = prepareListForTagCon()
                                SaleBPSiteTagForEngOrCon(user,profileData,loading)
                            }


                            if (finalFile.isFile) {


                                uploadTSSiteCover(
                                    user,
                                    sitenum,
                                    selectedCustomer,
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    loading
                                )


                            }






                            else {
                                // loadingAnim.dismiss()
                                loading.dismiss()
                                Toast.makeText(requireContext(), "File missing ", Toast.LENGTH_LONG)
                                    .show()


                            }




                            /*
                            else if(SELECTED_ENIGINEER_ID.isNotEmpty()&&SELECTED_Contractor_ID.isEmpty()){
                                        val profileData = prepareListForTagEng()
                                        SaleBPSiteTagForEngOrCon(user,profileData,loading)
                            } else if(SELECTED_ENIGINEER_ID.isEmpty()&&SELECTED_Contractor_ID.isNotEmpty()){
                                        val profileData = prepareListForTagCon()
                                        SaleBPSiteTagForEngOrCon(user,profileData,loading)
                           }else {


                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Site added and tagged with the party successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)


                                navController.navigate(R.id.nav_demo_allSiteList)
                                loading.dismiss()


                            }


                             */


                            loading.dismiss()


                        } else {
                            // loadingAnim.dismiss()
                            loading.dismiss()

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
                    loading.dismiss()


                    //  loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }




//    override fun onResume() {
//        super.onResume()
//        //OnResume Fragment
//        getLastLocation()
//    }




    //todo for tag
    fun prepareListForTagForEngOrCon(): String {


        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
        var partyCode = ""
        if(selectedCustomer=="Engineer"){
            partyCode = SELECTED_ENIGINEER_ID
        }else if(selectedCustomer=="Contractor"){
            partyCode = SELECTED_Contractor_ID


        }




        val ProfileForSiteTag = ProfileForSiteTag(
            partyCode,
            sitenum,
            selectedCustomer
        )


        val gson = Gson()
        return gson.toJson(ProfileForSiteTag)
    }






    private fun SaleBPSiteTagForEngOrCon (
        user: String,
        value: String,
        dialog: Dialog) {



        retailerViewModel.SaleBPSiteTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {


                }
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        if (res.code == "200") {
                            loadingAnim.dismiss()
                            dialog.dismiss()
                            /*
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Site added and tagged with the party successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_demo_allSiteList)


                             */




                        } else {
                            loadingAnim.dismiss()
                            dialog.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                "Can't tag with contractor",
                                R.drawable.error_white,
                                0
                            )
                            /*
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )


                             */
                        }
                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    dialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }






    fun prepareListForTagEng(): String {


        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()






        val ProfileForSiteTag = ProfileForSiteTag(
            SELECTED_ENIGINEER_ID,
            sitenum,
            "Engineer"
        )


        val gson = Gson()
        return gson.toJson(ProfileForSiteTag)
    }


    private fun SaleBPSiteTagForEng(
        user: String,
        value: String,
        dialog: Dialog) {



        retailerViewModel.SaleBPSiteTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {


                }
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        if (res.code == "200") {
                            val profileData = prepareListForTagCon()

                            /*
                              SaleBPSiteTagForCon(user,profileData,loadingAnim)


                             */


                            /*
                                                        Utility.getBaseMessage(
                                                            requireActivity(),
                                                            "Successful",
                                                            "Site Tag completed successfully",
                                                            R.drawable.ic_checked_green,
                                                            1
                                                        )


                             */
                            dialog.dismiss()
                            loadingAnim.dismiss()
                            /*
                                                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)


                                                        navController.navigate(R.id.nav_dashboard)


                             */


                        }


                        else {

                            dialog.dismiss()

                            loadingAnim.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                "Can't tag with Engineer",
                                R.drawable.error_white,
                                0
                            )
                            /*
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )


                             */
                        }
                    }
                }
                Status.ERROR -> {
                    dialog.dismiss()

                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }




    fun prepareListForTagCon(): String {


        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()






        val ProfileForSiteTag = ProfileForSiteTag(
            SELECTED_Contractor_ID,
            sitenum,
            "Contractor"
        )


        val gson = Gson()
        return gson.toJson(ProfileForSiteTag)
    }


    private fun SaleBPSiteTagForCon(
        user: String,
        value: String,
        dialog: Dialog) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()


        retailerViewModel.SaleBPSiteTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {


                }
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        if (res.code == "200") {




                            /*
                                                        Utility.getBaseMessage(
                                                            requireActivity(),
                                                            "Successful",
                                                            "Site Tag completed successfully",
                                                            R.drawable.ic_checked_green,
                                                            1
                                                        )


                             */
                            loadingAnim.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Site added and tagged with the party successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
//                              val navController = requireActivity().findNavController(R.id.nav_host_fragment)
//                                 navController.navigate(R.id.nav_demo_allSiteList)






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




    //todo img delete
    private fun deletePictureFile(path : String): Boolean {
        var deleted = false
        try {
            //  val res = File(realPath)!!
            val res = File(path)!!


            if (res.isFile) {
                deleted = res.delete()
            }


            when {
                deleted -> {
                    Log.d("Picture", "DELETED: ")
                }
                else -> {
                    Log.d("Picture DELETED", " Not yet")
                }
            }
        } catch (e: Exception) {
            deleted = false
        }


        return deleted


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














    /*
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


    */








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












}

