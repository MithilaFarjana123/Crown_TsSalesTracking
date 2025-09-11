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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
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
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragDemoCustomerHomeBinding
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
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Frag_Demo_Customer_Home : Fragment() {

   // private lateinit var rootView: View
    private var _binding: FragDemoCustomerHomeBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Frag_Demo_Customer_Home"
    private var selectedCustomer: String = ""

    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""

    private val permissionCode = 101

    lateinit var other: OtherCustomerChild
    lateinit var finalFile: File
    lateinit var retailerViewModel: RetailerViewModel

    //  lateinit var salesVisitViewModel: SalesVisitViewModel
    lateinit var loadingAnim: Dialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var customerCode: String = ""
    var currentPhotoPath = ""
    var user = ""
    var alreadyAddedImageURL = ""

    private val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragDemoCustomerHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        getPreviouslyStoredValue()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()
//
        selectedDivision = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        selectedDistrict = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        selectedThana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
//


        selectedCustomer = Utility.getValueByKey(requireActivity(), "acCustomer").toString()

        binding.demoMasons.visibility = View.GONE
        binding.ihbUpdate.visibility = View.GONE
        binding.cardPartyWorkSum.visibility = View.GONE

        binding.dpCustomertype.setText("   "+selectedCustomer+" Quick Visit")

    //    binding.dpCustomertype.setText("   "+selectedCustomer+" Quick Visit")
        binding.worksumText.setText("   "+selectedCustomer+" Work Summery")


        if(selectedCustomer=="IHBs"){
            binding.demoPartySiteTag.visibility = View.VISIBLE
            binding.directPartyCallRecord.visibility = View.GONE
            binding.ihbUpdate.visibility = View.VISIBLE
            binding.cardPartyWorkSum.visibility = View.GONE

        }

        else if(selectedCustomer=="Contractor"){
            binding.demoMasons.visibility = View.VISIBLE
            binding.cardPartyWorkSum.visibility = View.VISIBLE

        }else if(selectedCustomer=="Engineer"){
            binding.cardPartyWorkSum.visibility = View.VISIBLE

        }

        //  rootView.direct_partyCallRecord.visibility = View.GONE


        other = convertOtherInfo(requireActivity())!!
        customerCode = other.partyCode.toString()
        alreadyAddedImageURL = other.partyImage.toString()

        prepareBasicInfo()
        setupViewModel()
        RetailerViewModelFactory()


        onClickLisenner()

    }




    override fun onPause() {
        super.onPause()

        prepareLocation()
        getLastLocation()

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

    }

    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }


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

                            _binding?.locationStatus?.visibility = View.VISIBLE

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
                binding.locationStatus.visibility = View.VISIBLE
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


    private fun getPreviouslyStoredValue() {
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()


    }

    fun onClickLisenner() {


        binding.demoPartySiteEntry.setOnClickListener {


            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_demo_allSiteList)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }

        binding.directPartyCallRecord.setOnClickListener {
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_direct_callrecord)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }


        binding.demoMasons.setOnClickListener {

            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_demo_masonList)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }



        binding.cardPartyWorkSum.setOnClickListener {

            if(selectedCustomer == "Engineer"){
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_engWorkSum)
            }
//            else if(selectedCustomer == "Mason"){
//                val navController =
//                    requireActivity().findNavController(R.id.nav_host_fragment)
//                navController.navigate(R.id.nav_masonWorkSum)
//            }
            else if(selectedCustomer == "Contractor"){
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_conWorkSum)
            }
        }



/*
        binding.directPartyComplain.setOnClickListener {

            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_derictcomplain)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }

 */



        binding.ihbUpdate.setOnClickListener {

            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
              navController.navigate(R.id.nav_party_update)
        }





        binding.demoPartySiteTag.setOnClickListener {


            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_Frag_SiteTag)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }


        binding.imgRetailerSetCover.setOnClickListener {
            //LOCATION Green LIGHT
            if (latitude != 0.0) {
                binding.locationStatus.visibility = View.VISIBLE
            } else {
                //  latitude = getLastKnownLocation()!!.latitude
                //  longitude = getLastKnownLocation()!!.longitude
                getLastLocations()
            }

            captureImage()


            //  openCamera()
        }




    }



    private fun getLastLocations() {
        EasyLocation(requireActivity(), object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {

                Utility.commonToast(
                    requireActivity(), "Location Permission Denied", "Permission  denied", 0
                )
            }

            override fun locationSettingFailed() {

                Utility.commonToast(
                    requireActivity(), "Location Settings Failed", "Location settings issue", 0
                )

            }

            override fun getLocation(location: Location) {

                latitude = location.latitude
                longitude = location.longitude
                Log.d("latitude", "latitude: $latitude")
                Log.d("longitude", "longitude: $longitude")
            }
        })


    }


    //api


    private fun prepareBasicInfo() {

        when (selectedCustomer) {

            "Retailer" -> {
                /*
                setBasicInfo(
                    retailer.partyName.toString(),
                    "Proprietor: " + retailer.ownerName.toString(),
                    retailer.partyAdd1.toString(),
                    retailer.partyMobile.toString(),
                    "Party Code: " + retailer.partyCode.toString()
                )
                Log.d(TAG, "------------------------------------------------------\n")
                Log.e(TAG, "Retailer Image: " + retailer.partyImage.toString())
                Log.d(TAG, "------------------------------------------------------\n")

                if (retailer.partyImage.toString() != "") {
                    rootView.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity())
                        .load(Endpoint.IMAGE_BASE_URL + retailer.partyImage)
                        .error(com.google.android.gms.location.R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .into(rootView.imgRetailerHomeCover)
                }

                 */
            }

            "Dealer" -> {

                /*
                    setBasicInfo(
                        dealer.dealerName.toString(),
                        "",
                        dealer.dealerAdd1.toString(),
                        dealer.dealerMobile.toString(),
                        "Dealer Code: " + dealer.dealerCode.toString()
                    )

                    if (dealer.dealerImage.toString() != "") {
                        rootView.imgVerified.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(Endpoint.IMAGE_BASE_URL + dealer.dealerImage)
                            .error(com.google.android.gms.location.R.drawable.shop_banner).fitCenter()
                            //.placeholder(R.drawable.shop_banner)
                            .into(rootView.imgRetailerHomeCover)
                    }

     */


            }

            else -> {

                setBasicInfo(
                    other.partyName.toString(),
                    "",
                    other.partyAdd1.toString(),
                    other.partyMobile.toString(),
                    "Party Code: " + other.partyCode.toString(),
                    other.contactPerson.toString(),
                    other.partyResPhone.toString()
                )

                if (other.partyImage.toString() != "") {
                    binding.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                        .error(R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.imgRetailerHomeCover)
                }
            }
        }


    }

    private fun setBasicInfo(
        name: String, owner: String, address: String, mobile: String,
        code: String,conperson :String ,respersonPhn : String
    ) {
        binding.callIcon.setOnClickListener{
            openDialer(mobile)
            // openDialer("01335094919")
        }
        binding.txtShopName.text = name
        binding.txtShopOwner.text = owner
        binding.txtShopAddress.text = "Address: $address"
        binding.txtShopMobile.text = "Mobile: $mobile"
        binding.txtShopCode.text = code
        binding.txtResponsiblePerson.text = "Responsible Person: $conperson"
        binding.txtResponsiblePerNum.text = "Responsible Person's Number: $respersonPhn"


    }

    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    //TODO----------------------IMAGES---------------------------------------------------------->
    private fun openCamera() {
        //OLD Code
        //val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // startActivityForResult(takePicture, 0)
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
            requireActivity(), "com.crowncement.tecnicalsupport.fileprovider", imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }


    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {

            //  if (resultCode == Activity.RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                //   val selectedImage = data.extras!!["data"] as Bitmap?
                var path = Utility.getValueByKey(requireActivity(),"currentPhotoPath")
                val selectedImage = BitmapFactory.decodeFile(path)
                //val selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                val imName = emp + Utility.getCurrentDate("yyyyMMdd") + "out"
                val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                finalFile = File(getRealPathFromURI(tempUri)!!)

                binding.imgRetailerHomeCover.setImageBitmap(selectedImage)

                val type = Utility.getValueByKey(requireActivity(), "acCustomer").toString()
                if (latitude > 0.0) {
                    uploadCoverPhoto(
                        emp,
                        customerCode,
                        type,
                        latitude.toString(),
                        longitude.toString(),
                        finalFile
                    )
                } else {
                    prepareLocation()
                    getLastLocation()
                    if (latitude != 0.0) {
                        uploadCoverPhoto(
                            emp,
                            customerCode,
                            type,
                            latitude.toString(),
                            longitude.toString(),
                            finalFile
                        )
                    }
                    //THIS SCOPE FOR BELOW ANDROID 7
                    else {
                        Utility.commonToast(
                            requireActivity(),
                            "Failed",
                            "Location reset successfully\n try to take picture again",
                            0
                        )
                        //latitude = getLastKnownLocation()!!.latitude
                        //longitude = getLastKnownLocation()!!.longitude
                        getLastLocations()
                        //LOCATION Green LIGHT
                        if (latitude != 0.0) {
                            binding.locationStatus.visibility = View.VISIBLE
                            uploadCoverPhoto(
                                emp,
                                customerCode,
                                type,
                                latitude.toString(),
                                longitude.toString(),
                                finalFile
                            )
                        }
                    }
                }
            }

        } else {
            Utility.commonToast(requireActivity(), "Failed", "failed to upload shop cover photo", 0)
        }
    }


     */

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
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
        //val bytes = ByteArrayOutputStream()
        //inImage.compress(Bitmap.CompressFormat.PNG, 0, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            // inImage,
            getResizedBitmap(inImage, 800), imageName, null
        )
        return Uri.parse(path)
    }


    private fun uploadCoverPhoto(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File
    ) {
        retailerViewModel.uploadCover(
            user_id, party_code, party_type, party_lat, party_lon, party_image
        ).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        try {
                            if (res.code == "200") {
                                binding.imgVerified.visibility = View.VISIBLE
                                Log.e(
                                    TAG,
                                    "uploadCoverPhoto: " + IMAGE_BASE_URL + res.partyImage.toString()
                                )
                                Glide.with(requireActivity())
                                    .load(EndPoint.IMAGE_BASE_URL + res.partyImage.toString())
                                    .error(R.drawable.shop_banner).fitCenter()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    // .placeholder(R.drawable.shop_banner)
                                    .into(binding.imgRetailerHomeCover)
                            }

                            loadingAnim.dismiss()
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_dashboard)

                        } catch (e: Exception) {
                            loadingAnim.dismiss()
                            Log.e(TAG, "attendanceEntry: " + e.message)
                        }
                    }
                }

                Status.LOADING -> {
                    loadingAnim.show()

                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        //OnResume Fragment
        prepareLocation()
        getLastLocation()
    }



    //todo new img method
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
                binding.imgRetailerHomeCover.setImageBitmap(bitmap)

/*
                if (latitude == 0.0) {
                    latitude = getLastKnownLocation()!!.latitude
                    longitude = getLastKnownLocation()!!.longitude

                    //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
                }

 */

                val emp = Utility.getValueByKey(requireActivity(), "username").toString()

                val type = Utility.getValueByKey(requireActivity(), "acCustomer").toString()
                if (latitude > 0.0) {
                    uploadCoverPhoto(
                        emp,
                        customerCode,
                        type,
                        latitude.toString(),
                        longitude.toString(),
                        finalFile
                    )
                } else {
                    prepareLocation()
                    getLastLocation()
                    if (latitude != 0.0) {
                        uploadCoverPhoto(
                            emp,
                            customerCode,
                            type,
                            latitude.toString(),
                            longitude.toString(),
                            finalFile
                        )
                    }
                    //THIS SCOPE FOR BELOW ANDROID 7
                    else {
                        Utility.commonToast(
                            requireActivity(),
                            "Failed",
                            "Location reset successfully\n try to take picture again",
                            0
                        )

                        getLastLocations()
                        //LOCATION Green LIGHT
                        if (latitude != 0.0) {
                            binding.locationStatus.visibility = View.VISIBLE
                            uploadCoverPhoto(
                                emp,
                                customerCode,
                                type,
                                latitude.toString(),
                                longitude.toString(),
                                finalFile
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


}