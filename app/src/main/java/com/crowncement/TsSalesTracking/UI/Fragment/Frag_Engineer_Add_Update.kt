package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
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
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesVisitViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesVisitViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.CallRecordSpinnerData
import com.crowncement.TsSalesTracking.data.Model.OtherCustomer
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild2
import com.crowncement.TsSalesTracking.databinding.FragEngineerAddUpdateBinding
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
import com.robin.locationgetter.EasyLocation
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID


class Frag_Engineer_Add_Update : Fragment() {

  //  private lateinit var rootView: View

    private var _binding: FragEngineerAddUpdateBinding? = null
    private val binding get() = _binding!!



    lateinit var salesVisitViewModel: SalesVisitViewModel
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var brandList: ArrayList<CallRecordSpinnerData>

    lateinit var outletList: ArrayList<String>

    lateinit var cardFile: ArrayList<File>

    var selectedOutlet = ""
    var brand1 = ""
    var brand2 = ""
    var brand3 = ""
    var engineerSetupInfo = ""
    var user = ""
    var partyCategory = ""


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    lateinit var finalFile: File
    lateinit var vCardFile: File

    var currentPhotoPath = ""
    var currentPhotoIDPath = ""
    var fileType = ""
    lateinit var attachFile: File
    lateinit var pdfFile: File

    private val permissionCode = 101
    val REQUEST_CODE = 200

    lateinit var other: OtherCustomerChild

    var extension = ""
    lateinit var loadingAnim: Dialog

    var fileSizeInMB: Double = 0.0
    var finalFileInKB: Double = 0.0

    var cardFileInMB: Double = 0.0
    var cardFileInKB: Double = 0.0


    lateinit var uri: Uri
    lateinit var vuri: Uri


    var DOB = ""
    var marriage_day = ""
    var party_code = ""
    lateinit var picker: DatePickerDialog

    var resizeCount: Int = 0
    var visresizeCount: Int = 0


    var visitingCardPhotoPath = ""


    lateinit var  selectedImage:Bitmap

    var realPath = ""
    var visSelectionType = ""
    lateinit var  visCardselectedImage:Bitmap

    private val REQUEST_IMAGE_CAPTURE = 1

    private var PRO_PIC_CAMERA_REQ_CODE = 0
    private var VCARD_PIC_CAMERA_REQ_CODE = 3
    private var VCARD_ATTACH_REQUEST_CODE = 2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = inflater.inflate(R.layout.frag_engineer_add_update, container, false)
        _binding = FragEngineerAddUpdateBinding.inflate(inflater, container, false)

/*

        finalFile = File("")
        cardFile = ArrayList()
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        prepareLocation()
        getLastLocation()


        setPreviouslySaveValueForUpdate()

        SalesVisitViewModelFactory()
        RetailerViewModelFactory()
        setupViewModel()

        getAllCementBrandList()

        loadOutletCategory(binding.acECategory)

        eventClickListener()

 */

        uri = Uri.EMPTY
        vuri = Uri.EMPTY

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely



        finalFile = File("")
        attachFile = File("")
        vCardFile = File("")

        cardFile = ArrayList()
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        prepareLocation()
        getLastLocation()


        setPreviouslySaveValueForUpdate()

        SalesVisitViewModelFactory()
        RetailerViewModelFactory()
        setupViewModel()

        getAllCementBrandList()

        loadOutletCategory(binding.acECategory)

        eventClickListener()

        onClickLisener()

    }


    fun onClickLisener(){

        if (finalFileInKB >= 200) {
          //  binding.resizeImage.visibility = View.VISIBLE
        } else if (finalFileInKB < 200) {
         //   binding.resizeImage.visibility = View.GONE
        }
//todo visiting  card

        if (cardFileInKB >= 200) {
         //   binding.resizeVisitingCard.visibility = View.VISIBLE
        } else if (cardFileInKB < 200) {
          //  binding.resizeVisitingCard.visibility = View.GONE
        }





    }




    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    override fun onResume() {
        loadOutletCategory(binding.acECategory)
        super.onResume()
    }

    private fun setPreviouslySaveValueForUpdate() {

        val operationStatus = Utility.getValueByKey(requireActivity(), "engineer_update")
        engineerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

        if (operationStatus != "") {
            other = convertOtherInfo(requireActivity())!!
        }

        if (operationStatus == "YES" && other.partyCode.toString() != "") {
            //UPDATE
            //Get select item info
            binding.btnAddEngineer.text = "Update Engineer"
            selectedOutlet = other.partyCat.toString()

            party_code = other.partyCode.toString()

            binding.txtEnOrga.setText(other.partyName.toString())
            binding.txtEnOAdd.setText(other.partyAdd1.toString())
            binding.txtEnName.setText(other.contactPerson.toString())
            binding.txtEPartyEmail.setText(other.partyEmail.toString())

            binding.txtEnMobile.setText(other.partyMobile.toString())
            binding.txtEnDesi.setText(other.partyDesig.toString())
            binding.txtNIDNUm.setText(other.nidNo.toString())

            binding.txtEn2Mobile.setText(other.partyPhone.toString())
            binding.txtEOngoing.setText(other.numberSite!!.toString())
            binding.txtECrown.setText(other.numberCrownSite!!.toString())

            partyCategory = other.partyCat.toString()

            if (other.partyBdate.toString() != "1900-01-01" && other.partyBdate.toString() != "2999-12-31") {
                binding.txtEDOB.setText(
                    Utility.changeDateFormat(
                        other.partyBdate.toString(),
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    ).toString()
                )
            } else {
                binding.txtEDOB.setText("")
            }


            if (other.partyMdate.toString() != "1900-01-01" && other.partyMdate.toString() != "2999-12-31") {
                binding.txtMDate.setText(
                    Utility.changeDateFormat(
                        other.partyMdate.toString(),
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    ).toString()
                )

            } else {
                binding.txtMDate.setText("")
            }

            binding.txtEBrand1.setText(other.recomBrand1.toString())
            binding.txtEBrand2.setText(other.recomBrand2.toString())
            binding.txtEBrand3.setText(other.recomBrand3.toString())



            binding.engSbp.setText("Party Code : "+other.partyCode)
            binding.createDate.setText("Registration Date : "+ other.partyOpenDate)

            if (other.partyImage != "") {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgUserName)
            }
            if (other.busCardImg != "") {

                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + other.busCardImg)
                    .error(R.drawable.ic_baseline_broken_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgSetVisitingImage)
            }


        } else {
            //SAVE
            binding.btnAddEngineer.text = "Submit"
        }
    }

    private fun eventClickListener() {


        binding.txtEDOB.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    DOB = selectedDate
                    binding.txtEDOB.setText(
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

        binding.txtMDate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    marriage_day = selectedDate
                    binding.txtMDate.setText(
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

        binding.acECategory.setOnItemClickListener { parent, arg1, position, arg3 ->

            selectedOutlet = if (position > 0 && outletList.size > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }

        binding.txtEBrand1.setOnItemClickListener { parent, arg1, position, arg3 ->

            brand1 = if (position > 0 && brandList.size > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }

        binding.txtEBrand2.setOnItemClickListener { parent, arg1, position, arg3 ->

            brand2 = if (position > 0 && brandList.size > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }

        binding.txtEBrand3.setOnItemClickListener { parent, arg1, position, arg3 ->

            brand3 = if (position > 0 && brandList.size > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }


        binding.imgSelectImage.setOnClickListener {
           // openCamera()
            //openCamera1()
            captureImage(PRO_PIC_CAMERA_REQ_CODE)

        }

/*
        binding.pdfAddVisiting.setOnClickListener {
            openPDFFile()
        }


 */

        binding.imgAddVisiting.setOnClickListener {
            openGalleryForImages()
        }
        binding.addCamera.setOnClickListener {
          //  openCameraVisitingCard()

            captureImage(VCARD_PIC_CAMERA_REQ_CODE)
        }

        binding.btnAddEngineer.setOnClickListener {
            //  if (other.partyCode!!.isEmpty()) {
            if (party_code == "") {
                //SAVE
                if (saveUIValidation()) {
                    confirmStart("Add New Engineer")
                }

            } else {
                //UPDATE
                if (updateUIValidation() && party_code != "") {

                    confirmStart("Update Engineer Information")

                }

            }

        }
    }


    private fun prepareEngineerUpdateData(): String {

        var dob = ""
        var mar = ""
        if (binding.txtEDOB.text.toString().isNotEmpty()) {
            dob = Utility.changeDateFormat(
                binding.txtEDOB.text.toString(),
                "dd MMM yyyy",
                "yyyy-MM-dd"
            ).toString()
        }

        if (binding.txtMDate.text.toString().isNotEmpty()) {
            mar = Utility.changeDateFormat(
                binding.txtMDate.text.toString(),
                "dd MMM yyyy",
                "yyyy-MM-dd"
            ).toString()
        }

        val otherCustomer = OtherCustomerChild2(
            binding.txtEnOrga.text.toString(),
            binding.txtEnOAdd.text.toString(),
            "Bangladesh",
            binding.txtEnName.text.toString(),
            binding.txtEPartyEmail.text.toString(),
            "Bangladesh",
            //   engineerSetupInfo.split(",")[1],
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            //   engineerSetupInfo.split(",")[2],
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            //  engineerSetupInfo.split(",")[3],
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            binding.txtEnMobile.text.toString(),
            binding.txtEnDesi.text.toString(),
            binding.txtEn2Mobile.text.toString(),
            binding.txtEOngoing.text.toString().toInt(),
            binding.txtECrown.text.toString().toInt(),
            selectedOutlet,
            dob,
            mar,
            binding.txtEBrand1.text.toString(),
            binding.txtEBrand2.text.toString(),
            binding.txtEBrand3.text.toString(),
            extension,
            other.partyCity,
            other.partyZip,
            other.partyGroup,
            "",
            "",
            binding.txtNIDNUm.text.toString()


        )

        val gson = Gson()
        return gson.toJson(otherCustomer)
    }


    private fun updateOtherParty(user: String, partyCode: String, value: String) {

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.updateOther(user, partyCode, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    loadingAnim.dismiss()

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            //    if (finalFile.isFile && other.partyImage.toString() == "") {
                            if (finalFile.isFile) {
                                uploadCoverPhoto(
                                    user,
                                    partyCode,
                                    "Engineer",
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    loadingAnim
                                )
                            }

                            if (cardFile.size > 0) {
                                uploadBusinessCard(
                                    user,
                                    partyCode,
                                    extension,
                                    cardFile,
                                    loadingAnim
                                )
                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Updated",
                                    "Engineer information has been updated successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            }

                        }
                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    //todo was not commented
                 //   binding.btnUpdateOther.isEnabled = true
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        salesVisitViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesVisitViewModelFactory()).get(
                SalesVisitViewModel::class.java
            )

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }


    private fun getAllCementBrandList() {
        salesVisitViewModel.getAllCementList().observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {
                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        brandList = ArrayList()
                        brandList = res.data
                        loadAllCementSpinner(binding.txtEBrand1, res.data)
                        loadAllCementSpinner(binding.txtEBrand2, res.data)
                        loadAllCementSpinner(binding.txtEBrand3, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadAllCementSpinner(
        ac: AutoCompleteTextView,
        lists: ArrayList<CallRecordSpinnerData>
    ) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Brand")
        for (item in lists) {
            items.add(item.Item.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun loadOutletCategory(ac: AutoCompleteTextView) {

        outletList = ArrayList()
        outletList.add("Select engineer category")
        outletList.add("A")
        outletList.add("B")
        outletList.add("C")
        outletList.add("D")


        ac.setText(outletList[0])

        for (item in outletList) {
            if (partyCategory != "" && item == partyCategory) {
                val pos = outletList.indexOf(item)
                ac.setText(outletList[pos])
                // break
            }
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, outletList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }


    //TODO CAPTURE IMAGES-------------------------------------------------------------------------->


    private fun openCamera1() {

        fileType = "CAMERA"
        if (latitude == 0.0) {
            //  location()
            getLastLocations()
        }
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            Log.i("ERROR", "IOException")
        }
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                requireContext(), "com.crowncement.TsSalesTracking.fileprovider",
                photoFile
            )
            currentPhotoPath = photoFile.absolutePath
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, 0)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "IMG-"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "$imageFileName.jpg")
    }





    private fun openCamera() {
        fileType = "CAMERA"
        if (latitude == 0.0) {
            //  location()
            getLastLocations()
        }
        val fileName = "IMG"
        val storageDirectory: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            currentPhotoPath = imageFile!!.absolutePath
            val imageUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.crowncement.TsSalesTracking.fileprovider",
                imageFile
            )
            Utility.storeKeyValue(requireActivity(),"currentPhotoPath",currentPhotoPath)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            startActivityForResult(intent, 0)
            //storage/emulated/0/Android/data/com.crowncement.salesforceapp/files/Pictures/IMG2399521268364836531.jpg
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e("ERROR", "openCamera: ${e.message}")
        }
    }


    private fun getLastLocations() {
        EasyLocation(requireActivity(), object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {

                Utility.commonToast(
                    requireActivity(),
                    "Location Permission Denied",
                    "Permission  denied",
                    0
                )
            }

            override fun locationSettingFailed() {

                Utility.commonToast(
                    requireActivity(),
                    "Location Settings Failed",
                    "Location settings issue",
                    0
                )

            }

            override fun getLocation(location: Location) {

                //   Log.i("Location_lat_lng"," latitude ${location?.latitude} longitude ${location?.longitude}")
                latitude = location.latitude
                longitude = location.longitude
                Log.d("latitude", "latitude: $latitude")
                Log.d("longitude", "longitude: $longitude")


            }
        })


    }

    private fun openCameraVisitingCard() {
        fileType = "CAMERAID"
        if (latitude == 0.0) {
            //   location()
            getLastLocations()
        }

        val fileName = "photo.jpg"
        val storageDirectory: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoIDPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.TsSalesTracking.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(),"VisCardcurrentPhotoIDPath",currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 130)
    }



    private fun openGalleryForImages() {
        fileType = "ATTACH"

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), VCARD_ATTACH_REQUEST_CODE)

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, VCARD_ATTACH_REQUEST_CODE)
        }
    }

    private fun openPDFFile() {
        fileType = "PDF"
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, ".pdf")
        pdfIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(pdfIntent, 12)
    }

/*
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

            cardFile = ArrayList()

            if (resultCode != Activity.RESULT_CANCELED) {
                // Toast.makeText(requireContext(), fileType, Toast.LENGTH_LONG).show()
                when (requestCode) {
                    //camera profile picture
                    0 -> {
                        if (fileType == "CAMERA") {
                            currentPhotoPath =
                                Utility.getValueByKey(requireActivity(), "currentPhotoPath")
                                    .toString()
                            selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            realPath = getRealPathFromURI(tempUri)!!
                            finalFile = File(getRealPathFromURI(tempUri)!!)
                            uri = tempUri!!
                            if (currentPhotoPath != null) {
                                fileSizeInMB = currentPhotoPath.length.toDouble() / (1024 * 1024)
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
                            binding.imgUserName.setImageBitmap(selectedImage)
                            fileType = ""
                        } else {
                            fileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to capture engineer image",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }
                    //attach
                    200 -> {
                        if (fileType == "ATTACH" && data!!.data != null) {
                            val imagePath: String = data.data!!.path!!
                            Log.e("imagePath", imagePath)
                            val imgUri: Uri = data.data!!

                            vuri = imgUri!!
                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {
                                path.mkdirs()
                                attachFile =
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                Log.e(
                                    "path",
                                    "onActivityResult: ${
                                        ImagePathUtils.getRealPath(
                                            requireContext(),
                                            imgUri
                                        )!!
                                    }"
                                )
                                extension =
                                    ImagePathUtils.getRealPath(requireContext(), imgUri).toString()
                                        .split(".")[1]
                                binding.txtPdfName.text = ""
                                binding.imgSetVisitingImage.setImageDrawable(null)
                                cardFileInMB = attachFile.length().toDouble() / (1024 * 1024)
                                visSelectionType = "attach"

                                GlobalScope.launch {
                                    attachFile = Compressor.compress(requireContext(), attachFile) {
                                        resolution(1280, 720)
                                        quality(80)
                                        format(Bitmap.CompressFormat.JPEG)
                                        size(204_800) // 200 KB
                                    }

                                    // attachFile= Compressor.compress(requireContext(), attachFile)
                                }
                                cardFile.add(attachFile)
                                setImage(attachFile, binding.imgSetVisitingImage)
                                fileType = ""


                            } catch (e: Exception) {
                                fileType = ""
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            fileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach image",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //pdf
                    12 -> {
                        if (fileType == "PDF") {
                            val uri: Uri = data!!.data!!
                            val uriString = uri.toString()
                            val myFile =
                                FileUtility().from(requireContext().applicationContext, uri)

                            //Get File name
                            var displayName: String? = null
                            if (uriString.startsWith("content://")) {
                                var cursor: Cursor? = null
                                try {
                                    cursor =
                                        requireActivity().contentResolver.query(
                                            uri,
                                            null,
                                            null,
                                            null,
                                            null
                                        )
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    }
                                } finally {
                                    cursor!!.close()
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile!!.name
                            }
                            pdfFile = myFile!!
                            binding.imgSetVisitingImage.setImageDrawable(null)
                            extension = "pdf"
                            binding.txtPdfName.text = displayName
                            GlobalScope.launch {

                                //  pdfFile= Compressor.compress(requireContext(), pdfFile)

                                pdfFile = Compressor.compress(requireContext(), pdfFile) {
                                    resolution(1280, 720)
                                    quality(80)
                                    format(Bitmap.CompressFormat.JPEG)
                                    size(204_800) // 200 KB
                                }

                            }
                            cardFile.add(pdfFile)
                            binding.imgSetVisitingImage.setBackgroundResource(R.drawable.ic_attached_pdf)
                            binding.imgSetVisitingImage.scaleType = ImageView.ScaleType.FIT_CENTER
                            fileType = ""

                        } else {

                            fileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach pdf file",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //camera visiting card
                    130 -> {
                        if (fileType == "CAMERAID") {
                            currentPhotoIDPath = Utility.getValueByKey(
                                requireActivity(),
                                "VisCardcurrentPhotoIDPath"
                            ).toString()
                            visCardselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                            // val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri =
                                getImageUri(requireContext(), visCardselectedImage!!, imName)
                            var vfile = File(getRealPathFromURI(tempUri)!!)
                            attachFile = vfile

                            vuri = tempUri!!
                            visSelectionType = "camera"

                            if (currentPhotoPath != null) {
                                cardFileInMB = currentPhotoPath.length.toDouble() / (1024 * 1024)
                            }

                            GlobalScope.launch {

                                vfile = Compressor.compress(requireContext(), vfile) {
                                    resolution(1280, 720)
                                    quality(80)
                                    format(Bitmap.CompressFormat.JPEG)
                                    size(204_800) // 200 KB
                                }

                                //   vfile= Compressor.compress(requireContext(), vfile)

                            }
                            cardFile.add(vfile)
                            extension = "jpg"
                            binding.txtPdfName.text = ""
                            binding.imgSetVisitingImage.setImageDrawable(null)
                            binding.imgSetVisitingImage.setImageBitmap(visCardselectedImage)
                            fileType = ""

                        } else {
                            fileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to capture visiting card",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    else -> {
                        fileType = ""
                        Utility.commonToast(
                            requireActivity(),
                            "Failed",
                            "failed to process image or pdf file",
                            0
                        )
                    }
                }
            }

        } catch (e: Exception) {
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



    private fun setImage(imgFile: File, v: ImageView) {
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
        }
    }

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
            "Engineer"
        )
        return Uri.parse(path)
    }


    //TODO CAPTURE IMAGES END---------------------------------------------------------------------->


    //TODO LOCATION ------------------------------------------------------------------------------->

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

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
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(
                        "Frag Add Cus",
                        "requestDeviceLocationSettings: " + sendEx.message.toString()
                    )
                }
            }
        }
    }

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
            ) == PackageManager.PERMISSION_GRANTED &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            val currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude

            Log.e("New Customer", "latitude: $latitude longitude:  $longitude")

        }
    }

//    private fun getLastKnownLocation(): Location? {
//        val mLocationManager: LocationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val providers: List<String> = mLocationManager.getProviders(true)
//        var bestLocation: Location? = null
//        for (provider in providers) {
//            val location = mLocationManager.getLastKnownLocation(provider) ?: continue
//            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
//                bestLocation = location
//            }
//        }
//        return bestLocation
//    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            permissionCode
        )
    }

    //TODO LOCATION END---------------------------------------------------------------------------->


    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }


    private fun prepareEngineerData(): String {

        val otherCustomer = OtherCustomer(
            binding.txtEnOrga.text.toString(),
            binding.txtEnOAdd.text.toString(),
            "",
            "",
            "Bangladesh",
            binding.txtEnName.text.toString(),
            binding.txtEPartyEmail.text.toString(),
            "Bangladesh",
            engineerSetupInfo.split(",")[1],
            engineerSetupInfo.split(",")[2],
            engineerSetupInfo.split(",")[3],
            binding.txtEnMobile.text.toString(),
            binding.txtEnDesi.text.toString(),
            binding.txtEn2Mobile.text.toString(),
            binding.txtEOngoing.text.toString().toInt(),
            binding.txtECrown.text.toString().toInt(),
            binding.acECategory.text.toString(),
            DOB,
            marriage_day,
            binding.txtEBrand1.text.toString(),
            binding.txtEBrand2.text.toString(),
            binding.txtEBrand3.text.toString(),
            "",
            "",
            extension,
            "",
            0.0,
            0,
            "",
            "",
            "",
            "",
            binding.txtNIDNUm.text.toString()


            )

        val gson = Gson()
        return gson.toJson(otherCustomer)
    }


    private fun saveNewCustomer(user: String, customerType: String, value: String) {

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.saveNewCustomer(user, customerType, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            uploadCoverPhoto(
                                user,
                                res.bp_id.toString(),
                                "Engineer",
                                latitude.toString(),
                                longitude.toString(),
                                finalFile,
                                loadingAnim
                            )
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

    private fun uploadCoverPhoto(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File,
        loading: Dialog
    ) {
        retailerViewModel.uploadCover(
            user_id,
            party_code,
            party_type,
            party_lat,
            party_lon,
            party_image
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {

                                    if (cardFile.size > 0) {
                                        uploadBusinessCard(
                                            user_id,
                                            party_code,
                                            extension,
                                            cardFile,
                                            loading
                                        )
                                    } else {
                                        loading.dismiss()
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Successful",
                                            "Engineer added successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                        val navController =
                                            requireActivity().findNavController(R.id.nav_host_fragment)
                                        navController.navigate(R.id.nav_dashboard)
                                    }

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
                                Log.e("Engineer Add", "Error: " + e.message)
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


    private fun uploadBusinessCard(
        user_id: String,
        party_code: String,
        extension: String,
        party_image: ArrayList<File>,
        loading: Dialog
    ) {
        retailerViewModel.uploadBusinessCard(
            user_id,
            party_code,
            extension,
            party_image
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()
                                    if (party_code != "") {
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Updated",
                                            "Engineer information has been updated successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                    } else {
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Successful",
                                            "Engineer added successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                    }

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
                                Log.e("Engineer Add", "Error: " + e.message)
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


    private fun removeUIErrorSign(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = null
    }

    private fun setError(field: TextInputLayout, message: String) {

        field.isErrorEnabled = true
        field.error = message
    }

    private fun saveUIValidation(): Boolean {

        var result = false
        //name
        //  rootView.txtEnName.setTextColor(Color.parseColor("#000000"))
        // rootView.txtEnName.error = null
        //designation
        // rootView.txtEnDesi.setText("")
        // rootView.txtEnDesi.setTextColor(Color.parseColor("#000000"))
        // rootView.txtEnDesi.error = null

        //name
        removeUIErrorSign(binding.layoutEnName)
        //desi
        removeUIErrorSign(binding.layoutEnDesi)
        //orga
        removeUIErrorSign(binding.layoutEnOrga)
        //address
        removeUIErrorSign(binding.layoutEAdd)
        //mobile
        removeUIErrorSign(binding.layoutEMo)
        //mobile2
        removeUIErrorSign(binding.layoutE2Mo)
        //email
        removeUIErrorSign(binding.layoutEEmail)
        //ongoing
        removeUIErrorSign(binding.layoutEOngoing)
        //crown
        removeUIErrorSign(binding.layoutECrown)
        //category
        removeUIErrorSign(binding.layoutEnCat)
        //brand 1
        binding.txtEBrand1.setTextColor(Color.parseColor("#000000"))
        //brand2
        binding.txtEBrand2.setTextColor(Color.parseColor("#000000"))


//        if (rootView.txtEnName.text.toString().isEmpty()) {
//            result = false
//            rootView.txtEnName.setText("Please provide engineer name")
//            rootView.txtEnName.setTextColor(Color.parseColor("#FC9E14"))
//        } else if (rootView.txtEnDesi.text.toString().isEmpty()) {
//            result = false
//
//            rootView.txtEnDesi.setText("Please provide engineer designation")
//            rootView.txtEnDesi.setTextColor(Color.parseColor("#FC9E14"))
//        }

        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0
            if (finalFileInKB >= 200) {
              //  binding.resizeImage.visibility = View.VISIBLE

            } else {
              //  binding.resizeImage.visibility = View.GONE
            }
        }

        if(attachFile.isFile){
            cardFileInMB = attachFile.length() / 1024.0
            if (cardFileInKB >= 200) {
              //  binding.resizeVisitingCard.visibility = View.VISIBLE
            } else if (cardFileInKB < 200) {
             //   binding.resizeVisitingCard.visibility = View.GONE
            }
        }

        if (binding.txtEnName.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnName, "Please provide engineer full name as per NID")

        } else if (binding.txtEnDesi.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnDesi, "Please provide engineer designation")
        } else if (binding.txtEnOrga.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnOrga, "Provide engineer organization info")
        } else if (binding.txtEnOAdd.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEAdd, "Provide details address")
        } else if (binding.txtEnMobile.text.toString()
                .isEmpty() || binding.txtEnMobile.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutEMo, "Invalid mobile number")
        } else if (binding.txtEn2Mobile.text.toString()
                .isNotEmpty() && binding.txtEn2Mobile.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutE2Mo, "Invalid mobile number")
        } else if (binding.txtEPartyEmail.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEEmail, "Engineer email is required")
        } else if (binding.txtEOngoing.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEOngoing, "Required")
        } else if (binding.txtECrown.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutECrown, "Required")
        } else if (selectedOutlet == "") {
            result = false
            setError(binding.layoutEnCat, "Select engineer category")
        } else if (binding.acECategory.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnCat, "Engineer category is required")
        } else if (binding.txtEBrand1.text.toString().isEmpty()) {
            result = false
            binding.txtEBrand1.setText("Engineer recommended first brand")
            binding.txtEBrand1.setTextColor(Color.parseColor("#FC9E14"))
        } else if (binding.txtEBrand2.text.toString().isEmpty()) {
            result = false
            binding.txtEBrand2.setText("Engineer recommended second brand")
            binding.txtEBrand2.setTextColor(Color.parseColor("#FC9E14"))
        } else if (!finalFile.isFile || fileSizeInMB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Engineer image required ",
                "Please attach engineer's  image  ",
                /*
                "After compression your file size is   " + String.format(
                    "%.2f",
                    finalFileInKB
                ) + " KB",

                 */
                R.drawable.error_white,
                0
            )
        }
        else if ( cardFileInMB > 200){
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Engineer visiting card required",
                "Please attach engineer's  visiting card  ",
                /*
                "After compression your file size is   " + String.format(
                    "%.2f",
                    cardFileInKB
                ) + " KB",

                 */
                R.drawable.error_white,
                0
            )
        }
        else {
            result = true
        }

        return result

    }

    private fun updateUIValidation(): Boolean {

        var result = false
        //name
        removeUIErrorSign(binding.layoutEnName)
        //desi
        removeUIErrorSign(binding.layoutEnDesi)
        //orga
        removeUIErrorSign(binding.layoutEnOrga)
        //address
        removeUIErrorSign(binding.layoutEAdd)
        //mobile
        removeUIErrorSign(binding.layoutEMo)
        //mobile2
        removeUIErrorSign(binding.layoutE2Mo)
        //email
        removeUIErrorSign(binding.layoutEEmail)
        //ongoing
        removeUIErrorSign(binding.layoutEOngoing)
        //crown
        removeUIErrorSign(binding.layoutECrown)
        //category
        removeUIErrorSign(binding.layoutEnCat)
        //brand 1
        binding.txtEBrand1.setTextColor(Color.parseColor("#000000"))
        //brand2
        binding.txtEBrand2.setTextColor(Color.parseColor("#000000"))

        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0
            if (finalFileInKB >= 200) {
              //  binding.resizeImage.visibility = View.VISIBLE

            } else {
             //   binding.resizeImage.visibility = View.GONE
            }
        }
        if(attachFile.isFile){
            cardFileInKB = attachFile.length() / 1024.0
            if (cardFileInKB >= 200) {
              //  binding.resizeVisitingCard.visibility = View.VISIBLE
            } else if (cardFileInKB < 200) {
              //  binding.resizeVisitingCard.visibility = View.GONE
            }
        }


        if (binding.txtEnName.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnName, "Please provide engineer full name as per NID")

        } else if (binding.txtEnDesi.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnDesi, "Please provide engineer designation")
        } else if (binding.txtEnOrga.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnOrga, "Provide engineer organization info")
        } else if (binding.txtEnOAdd.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEAdd, "Provide details address")
        } else if (binding.txtEnMobile.text.toString()
                .isEmpty() || binding.txtEnMobile.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutEMo, "Invalid mobile number")
        } else if (binding.txtEn2Mobile.text.toString()
                .isNotEmpty() && binding.txtEn2Mobile.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutE2Mo, "Invalid mobile number")
        } else if (binding.txtEPartyEmail.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEEmail, "Engineer email is required")
        } else if (binding.txtEOngoing.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEOngoing, "Required")
        } else if (binding.txtECrown.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutECrown, "Required")
        } else if (selectedOutlet == "") {
            result = false
            setError(binding.layoutEnCat, "Select engineer category")
        } else if (binding.acECategory.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutEnCat, "Engineer category is required")
        } else if (binding.txtEBrand1.text.toString().isEmpty()) {
            result = false
            binding.txtEBrand1.setText("Engineer recommended first brand")
            binding.txtEBrand1.setTextColor(Color.parseColor("#FC9E14"))
        } else if (binding.txtEBrand2.text.toString().isEmpty()) {
            result = false
            binding.txtEBrand2.setText("Engineer recommended second brand")
            binding.txtEBrand2.setTextColor(Color.parseColor("#FC9E14"))
        }
        //check image
        else if (other.partyImage.toString() == "") {
            if (!finalFile.isFile || fileSizeInMB > 200 ) {

                result = false
                Utility.getBaseMessage(
                    requireActivity(),
                    "Engineer image required ",
                    "Please attach engineer's  image  ",
                    /*
                    "Engineer image size exceed expected max file size 200 KB",
                    "After compression your file size is   " +String.format("%.2f", fileSizeInMB) +" KB",
                      */
                    R.drawable.error_white,
                    2
                )
            }
            else if ( cardFileInMB > 200){
                result = false
                Utility.getBaseMessage(
                    requireActivity(),
                    "Engineer visiting card required",
                    "Please attach engineer's  visiting card  ",
                    R.drawable.error_white,
                    0
                )
            }
            else {
                result = true
            }

        } else {
            result = true
        }

        return result

    }


    private fun confirmStart(title: String) {
        val v: View = layoutInflater.inflate(R.layout.engineer_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtETitle: TextView = v.findViewById(R.id.txtETitle)
        txtETitle.text = title

        val btnECancel: MaterialButton = v.findViewById(R.id.btnECancel)
        val btnEAdd: MaterialButton = v.findViewById(R.id.btnEAdd)
        if (title == "Update Engineer Information") {
            btnEAdd.text = "Update"
        }
        val Ecancel: TextView = v.findViewById(R.id.Ecancel)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnECancel.setOnClickListener {
            dialog.dismiss()
        }

        Ecancel.setOnClickListener {
            dialog.dismiss()
        }

        btnEAdd.setOnClickListener {
            dialog.dismiss()
            if (title == "Update Engineer Information") {
                val updateData = prepareEngineerUpdateData()
                updateOtherParty(user, party_code, updateData)
            } else {
                val data = prepareEngineerData()
                saveNewCustomer(user, "Engineer", data)
            }
        }
    }












    //todo new capture img
    fun captureImage(requestCode: Int ) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent(requestCode)
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
        android.app.AlertDialog.Builder(requireContext()).setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ ->

            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            context?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile(requestCode)
                    } catch (ex: IOException) {
                        null
                    }

                    photoFile?.also {

                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.crowncement.TsSalesTracking.fileprovider",
                            it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, requestCode)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(requestCode: Int): File {
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
            if(requestCode==PRO_PIC_CAMERA_REQ_CODE){
                currentPhotoPath = absolutePath

            }else if(requestCode==VCARD_PIC_CAMERA_REQ_CODE){
                //   currentPhotoPath = absolutePath
                visitingCardPhotoPath = absolutePath
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, "CrownTitanAttendance").apply {
            mkdirs()
        }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
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



    //todo new img
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == RESULT_OK) {

            Log.e("IMAGE", "Image saved to $currentPhotoPath")

            try{
                when (requestCode) {

                    PRO_PIC_CAMERA_REQ_CODE -> {


                        val imgFile = compressImage(currentPhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            finalFile = imgFile
                        }

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.imgUserName.setImageBitmap(bitmap)


                        if (latitude == 0.0) {
                            //  latitude = getLastKnownLocation()!!.latitude
                            //  longitude = getLastKnownLocation()!!.longitude
                            getLastLocation()

                        }

                    }


                    VCARD_PIC_CAMERA_REQ_CODE -> {


                            val imgFile = compressImage(visitingCardPhotoPath)
                        vCardFile

                            if (imgFile.exists()) {
                                val imageSize = (imgFile.length() / 1024.0)
                                // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                //  finalFile.add(imgFile)
                                vCardFile = imgFile
                            }
                            cardFile.add(vCardFile)

                            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                            binding.imgSetVisitingImage.setImageBitmap(bitmap)
                        }



                    VCARD_ATTACH_REQUEST_CODE -> {

                        if (data?.clipData != null) {
                            val count = data.clipData!!.itemCount

                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                                val path: File =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                try {
                                    path.mkdirs()
                                    val file =
                                        File(
                                            ImagePathUtils.getRealPath(
                                                requireContext(),
                                                imageUri
                                            )!!
                                        )
                                  //  nuri = imageUri!!
                                  //  nidSelectionType = "attach"
                                    attachFile = file


                                  //  var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                    val imgFile = compressImage(attachFile.absolutePath)



                                    if (imgFile.exists()) {
                                        val imageSize = (imgFile.length() / 1024.0)
                                        // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                        //  finalFile.add(imgFile)
                                        attachFile = imgFile
                                    }
                                    Utility.saveFileToSharedPreferences(
                                        requireContext(),
                                        attachFile, "engVisAttach"
                                    )

                                    cardFile.add(attachFile)

                                 //   nidfinalFileInKB = nidFile.get(0).length() / 1024.0
                                    // nidfinalFile.length() / 1024.0

                                    extension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $extension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                                    // setImage(file, binding.imgNidFile)
                                    Glide.with(requireActivity())
                                        .load(attachFile)
                                        .into(binding.imgSetVisitingImage)

                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                        } else if (data?.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!


                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                             //   nuri = imgUri!!
                              //  nidSelectionType == "attach"
                                attachFile = file
                                val imgFile = compressImage(attachFile.absolutePath)
                                attachFile = imgFile
/*
                                GlobalScope.launch {

                                    //     nidfinalFile = Compressor.compress(requireContext(), nidfinalFile)

                                    attachFile =
                                        Compressor.compress(requireContext(), attachFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                }

 */
                                Utility.saveFileToSharedPreferences(
                                    requireContext(),
                                    attachFile, "engVisAttach"
                                )
                                cardFile.add(attachFile)
                               // nidfinalFileInKB = nidFile.get(0).length() / 1024.0


                                extension = "jpg"
                                //Set image to view

                                Glide.with(requireActivity())
                                    .load(attachFile)
                                    //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                    .into(binding.imgSetVisitingImage)

                                //   binding.imgNidFile.setImageURI(Uri.fromFile(nidfinalFile))

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Please select minimum one photo ",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }



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





    fun createFileFromImageUri(context: Context, imageUri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = createTempImageFile(context)

        return try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun createTempImageFile(context: Context): File {
        val tempFileName = "temp_image.jpg" // Replace with your desired file name and extension
        val storageDir =
            context.cacheDir // Use cache directory or external storage depending on your needs
        return File.createTempFile(tempFileName, null, storageDir)
    }






}