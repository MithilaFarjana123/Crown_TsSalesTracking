package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
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
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.SalePartnerUpdateParty
import com.crowncement.TsSalesTracking.databinding.FragPartyUpdateBinding
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
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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


class Frag_Party_Update : Fragment() {

    private var _binding: FragPartyUpdateBinding? = null
    private val binding get() = _binding!!

    lateinit var other: OtherCustomerChild
    private var customerCode: String = ""
    lateinit var RelationTypeList: ArrayList<String>
    var currentPhotoPath = ""
    var retailerSetupInfo = ""

    var realPath = " "

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val permissionCode = 101
    lateinit var finalFile: File
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var loadingAnim: Dialog


    lateinit var  selectedImage:Bitmap


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    var fileSizeInMB: Double = 0.0
    var finalFileInKB: Double = 0.0

    lateinit var uri: Uri
    var resizeCount: Int = 0
    var REQUEST_IMAGE_CAPTURE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragPartyUpdateBinding.inflate(inflater, container, false)

        uri = Uri.EMPTY

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        finalFile = File("")

        binding.txtOMobile.isEnabled =false
        binding.txtOResPersonMo.isEnabled =false
        prepareLocation()
        getLastLocation()
        RetailerViewModelFactory()
        setupViewModel()
        other = convertOtherInfo(requireActivity())!!
        customerCode = other.partyCode.toString()
        binding.acOResPersonRtn.setOnTouchListener { _, _ ->
            prepareRelationType(binding.acOResPersonRtn)
            false
        }
        getRetailerSetupInfo()


        onClickListener()
        setPreviousValue()

    }

    fun onClickListener(){

        if (finalFileInKB >= 200) {
            binding.resizeImage.visibility = View.VISIBLE
        } else if (finalFileInKB < 200) {
            binding.resizeImage.visibility = View.GONE
        }
        binding.resizeImage.setOnClickListener {
            //  openCamera()
         //   if(finalFileInKB>=200) {
            finalFile = File(getRealPathFromURI(uri)!!)

            binding.resizeImage.isEnabled = false

            resizer()
          //  }

        }

        binding.imgAddOtherCover.setOnClickListener {
           // openCamera()

            captureImage()
        }

        binding.btnUpdateParty.setOnClickListener {

            if (otherUIValidation()&& other.partyCode != "") {
                confirmationDialog()
            }


        }


    }



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

                3-> if(finalFileInKB>=200) {
                    selectedImage = BitmapFactory.decodeFile(
                        Utility.getValueByKey(
                            requireActivity(),
                            "currentPhotoPath"
                        )
                    )
                    selectedImage = getResizedBitmap(selectedImage, 600)!!
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
            Log.e("Party_Update", "Error: ")
        }

        binding.resizeImage.isEnabled = true

    }


    private fun confirmationDialog() {
        val v: View = layoutInflater.inflate(R.layout.add_retailer_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancelR: MaterialButton = v.findViewById(R.id.btnCancelR)
        val btnConfirmR: MaterialButton = v.findViewById(R.id.btnConfirmR)
        val cancel: TextView = v.findViewById(R.id.cancelR)
        val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val txtBody: TextView = v.findViewById(R.id.txtBody)
        txtCDialogTitle.setText("Party Update")

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

            if(latitude==0.0){
                latitude= getLastKnownLocation()!!.latitude
                longitude= getLastKnownLocation()!!.longitude
            }

            val otherCusData = prepareOtherPartyUpdateData1()
            updateOtherParty(empI, other.partyCode.toString(),otherCusData)
            dialog.dismiss()

        }



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

                            if (finalFile.isFile && other.partyImage.toString() == "") {
                           // if (finalFile.isFile) {
                                uploadCoverPhoto(
                                    user,
                                    partyCode,
                                    retailerSetupInfo.split(",")[0],
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    loadingAnim
                                )
                            }

                             else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Updated",
                                    "Party information has been updated successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_retiler_list)
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
                                    loading.dismiss()
                                    deletePictureFile(realPath)
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Party added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )
                                    val navController =
                                        requireActivity().findNavController(R.id.nav_host_fragment)
                                    navController.navigate(R.id.nav_retiler_list)
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
                                Log.e("Customer Add", "Error: " + e.message)
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


    private fun getRetailerSetupInfo() {

        retailerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

        if (retailerSetupInfo.isNotEmpty()) {

            binding.txtTSOtherArea.text =
                retailerSetupInfo.split(",")[1] + ">" +
                        retailerSetupInfo.split(",")[2] + ">" +
                        retailerSetupInfo.split(",")[3]

        }


    }




    private fun prepareOtherPartyUpdateData1(): String {

        var dob = ""
        var mar = ""

        var rltnwithhouseowner = ""
        if(binding.acOResPersonRtn.text.toString()!="Relation with H/W"){
            rltnwithhouseowner = binding.acOResPersonRtn.text.toString()
        }else {
            rltnwithhouseowner =""
        }

        val UpdateParty = SalePartnerUpdateParty(
            binding.txtONAme.text.toString(),
            binding.txtOAdd.text.toString(),
            "",
            other.partyZip,
            "Bangladesh",
            other.partyGroup,
            binding.txtOResPerson.text.toString(),
            "",
            binding.txtOMobile.text.toString(),
            "",
            "",
            0,
            0,
            retailerSetupInfo.split(",")[0],
            dob,
            mar,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            0.0,
            0,
            "",
            "",
            binding.txtOccupation.text.toString(),
            rltnwithhouseowner,
            binding.txtOResPersonMo.text.toString(),
            "",
            binding.txtNIDNum.text.toString(),

            "",
            "",
            "",
            ""


        )
/*
        val otherCustomer = OtherCustomerChild2(
            binding.txtONAme.text.toString(),
            binding.txtOAdd.text.toString(),
            "Bangladesh",
            binding.txtOResPerson.text.toString(),
            "",
            "Bangladesh",
            //   engineerSetupInfo.split(",")[1],
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            //   engineerSetupInfo.split(",")[2],
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            //  engineerSetupInfo.split(",")[3],
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            binding.txtOMobile.text.toString(),
            "",

            binding.txtOResPersonMo.text.toString(),
            0,
            0,
            retailerSetupInfo.split(",")[0],
            dob,
            mar,
            "",
            "",
            "",
            "",
            other.partyCity,
            other.partyZip,
            other.partyGroup,
            binding.txtOccupation.text.toString(),
            binding.acOResPersonRtn.text.toString()
        )

 */

        val gson = Gson()
        return gson.toJson(UpdateParty)
    }


    private fun otherUIValidation(): Boolean {

        var result = false
        binding.layoutOName.isErrorEnabled = false
        binding.layoutOName.error = null
        binding.layoutOOccupation.isErrorEnabled = false
        binding.layoutOOccupation.error = null
        binding.layoutOMo.isErrorEnabled = false
        binding.layoutOMo.error = null
        binding.layoutOAdd.isErrorEnabled = false
        binding.layoutOAdd.error = null

        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0
            if (finalFileInKB >= 200) {
                binding.resizeImage.visibility = View.VISIBLE

            } else {
                binding.resizeImage.visibility = View.GONE
            }
        }


        if (binding.txtONAme.text.toString().isEmpty()) {
            result = false
            binding.layoutOName.isErrorEnabled = true
            binding.layoutOName.error = "Please provide party name"
        }else if (binding.txtOccupation.text.toString().isEmpty()) {
            result = false
            binding.layoutOOccupation.isErrorEnabled = true
            binding.layoutOOccupation.error = "Please provide party  Occupation"
        } else if (binding.txtOMobile.text.toString().isEmpty()||
            binding.txtOMobile.text.toString().length<11) {
            result = false
            binding.layoutOMo.isErrorEnabled = true
            binding.layoutOMo.error = "Customer mobile is required"
        }

        else if (binding.txtOAdd.text.toString().isEmpty()) {
            result = false
            binding.layoutOAdd.isErrorEnabled = true
            binding.layoutOAdd.error = "Please provide party  address"
        }

       // else if (!finalFile.isFile) {
        else if ((!finalFile.isFile && other.partyImage.toString()=="") || finalFileInKB > 200 ){

                result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Party image is required (max 200 MB)",
                "Your actual file size is "+String.format("%.2f", fileSizeInMB)+
                        "MB and compressed file size is "+String.format("%.2f", finalFileInKB) +" KB",                R.drawable.error_white,
                2
            )
        } else {
            result = true
        }

        return result
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

    fun setPreviousValue(){

        binding.partyCode.setText("party code : "+other.partyCode.toString())
        binding.txtONAme.setText(other.partyName.toString())
        binding.txtOMobile.setText(other.partyMobile.toString())
        binding.txtNIDNum.setText(other.nidNo.toString())

        binding.txtOAdd.setText(other.partyAdd1.toString())
        binding.txtOccupation.setText(other.partyOccu.toString())

        binding.txtOResPerson.setText(other.contactPerson.toString())
        binding.txtOResPersonMo.setText(other.partyResPhone.toString())
        binding.acOResPersonRtn.setText(other.partyRelat.toString())

        if (other.partyImage != "") {


            Glide
                .with(this)
                .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                .error(R.drawable.default_user_image)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imgSetOtherPartyImage)
        }



    }


    private fun prepareRelationType(ac: AutoCompleteTextView) {

        RelationTypeList = ArrayList()
        RelationTypeList.add("Relation with H/W")
        RelationTypeList.add("Contractor")
        RelationTypeList.add("Relative")
        RelationTypeList.add("Project Engineer")
        RelationTypeList.add("Mason")
        RelationTypeList.add("Site Manager")

        ac.setText(RelationTypeList[0])

        for (item in RelationTypeList) {
            val pos = RelationTypeList.indexOf(item)
            ac.setText(RelationTypeList[pos])
            // break

        }
        ac.setText(RelationTypeList[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, RelationTypeList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

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
            "com.crowncement.TsSalesTracking.fileprovider",
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

            if (resultCode != Activity.RESULT_CANCELED) {

                if (resultCode == Activity.RESULT_OK) {
                    var path = Utility.getValueByKey(requireActivity(), "currentPhotoPath")
                    selectedImage = BitmapFactory.decodeFile(path)
                    // val selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                    val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                    val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "party"
                    val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                    uri = tempUri!!
                    Log.e("realPath", getRealPathFromURI(tempUri)!!)
                    realPath = getRealPathFromURI(tempUri)!!
                    finalFile = File(getRealPathFromURI(tempUri)!!)

                    if (path != null) {
                        fileSizeInMB = path.length.toDouble() / (1024 * 1024)
                    }

                    GlobalScope.launch {

                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(1280, 720)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(204_800) // 200 KB
                        }

                        //   finalFile= Compressor.compress(requireContext(), finalFile)
                    }
                    //only for TS
                    binding.imgSetOtherPartyImage.setImageBitmap(selectedImage)

                }


            }

            else {
                Utility.commonToast(
                    requireActivity(),
                    "Failed",
                    "failed to capture party cover photo",
                    0
                )
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
            // getResizedBitmap(inImage, 800),
            // inImage,
            imageName,
            null
        )
        return Uri.parse(path)
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



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
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




    //todo new capture img
    fun captureImage() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }


            else -> {


                /*
                Utility.getBaseMessage(requireActivity(),
                    "warning",
                    "Camera permission required",
                    R.drawable.worning,
                    0)

                 */


                requestPermissions()

            }
        }
    }

/*
    private fun requestPermissions() {


        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA
            ),
            permissionCode
        )


    }

 */


    private fun showCameraPermissionRationale() {
        AlertDialog.Builder(requireContext()).setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ ->

            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Handle error
                null
            }

            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.crowncement.TsSalesTracking.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            // Handle the case where no camera app is available
        }
    }



    /*
        private fun dispatchTakePictureIntent() {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                context?.let {
                    takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
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
                       //     val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                          //  takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                        //    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

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
                binding.imgSetOtherPartyImage.setImageBitmap(bitmap)


                if (latitude == 0.0) {
                    latitude = getLastKnownLocation()!!.latitude
                    longitude = getLastKnownLocation()!!.longitude

                    //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
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



}