package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
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
import android.os.*
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesVisitViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesVisitViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Adapter.OfflineQuickVisitAdapter
import com.crowncement.TsSalesTracking.data.InternalDB.AppRoomDatabase
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.databinding.FragCustomerHomeBinding
import com.crowncement.TsSalesTracking.services.broadcast.mContext
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation

import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class Frag_Customer_Home : Fragment() {

    private val TAG = "Frag_Customer_Home"
    //lateinit var rootView: View
    private var _binding: FragCustomerHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedCustomer: String = ""
    private val permissionCode = 101
    lateinit var dealer: DealerChild
    lateinit var retailer: RetailerChild
    lateinit var other: OtherCustomerChild
    lateinit var finalFile: File
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var salesVisitViewModel: SalesVisitViewModel
    lateinit var loadingAnim: Dialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var customerCode: String = ""
    var currentPhotoPath = ""
    var user = ""
    var alreadyAddedImageURL = ""
    var offlineSID=0

    lateinit var offlineDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragCustomerHomeBinding.inflate(inflater, container, false)
/*
       // rootView = inflater.inflate(R.layout.frag_customer_home, container, false)

        getPreviouslyStoredValue()


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        binding.retailerDashboardChild.layoutClaim.visibility = View.GONE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()

        when (selectedCustomer) {

            "Retailer" -> {
                retailer = convertRetailerInfo(requireActivity())!!
                customerCode = retailer.partyCode.toString()
                binding.retailerDashboardChild.layoutPayment.visibility = View.INVISIBLE
                alreadyAddedImageURL = retailer.partyImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.VISIBLE
            }

            "Dealer" -> {
                dealer = convertDealerInfo(requireActivity())!!
                customerCode = dealer.dealerCode.toString()
                binding.retailerDashboardChild.layoutPayment.visibility = View.VISIBLE
                alreadyAddedImageURL = dealer.dealerImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.INVISIBLE
            }

            else -> {
                binding.retailerDashboardChild.layoutPayment.visibility = View.INVISIBLE
                other = convertOtherInfo(requireActivity())!!
                customerCode = other.partyCode.toString()
                alreadyAddedImageURL = other.partyImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.INVISIBLE
            }
        }

        prepareBasicInfo()
        RetailerViewModelFactory()
        SalesVisitViewModelFactory()
        setupViewModel()
        onClickListener()
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        getPreviouslyStoredValue()


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        binding.retailerDashboardChild.layoutClaim.visibility = View.GONE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()

        when (selectedCustomer) {

            "Retailer" -> {
                retailer = convertRetailerInfo(requireActivity())!!
                customerCode = retailer.partyCode.toString()
                binding.retailerDashboardChild.layoutPayment.visibility = View.INVISIBLE
                alreadyAddedImageURL = retailer.partyImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.VISIBLE
            }

            "Dealer" -> {
                dealer = convertDealerInfo(requireActivity())!!
                customerCode = dealer.dealerCode.toString()
                binding.retailerDashboardChild.layoutPayment.visibility = View.VISIBLE
                alreadyAddedImageURL = dealer.dealerImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.INVISIBLE
            }

            else -> {
                binding.retailerDashboardChild.layoutPayment.visibility = View.INVISIBLE
                other = convertOtherInfo(requireActivity())!!
                customerCode = other.partyCode.toString()
                alreadyAddedImageURL = other.partyImage.toString()
                binding.retailerDashboardChild.cardDo.visibility = View.INVISIBLE
            }
        }

        prepareBasicInfo()
        RetailerViewModelFactory()
        SalesVisitViewModelFactory()
        setupViewModel()
        onClickListener()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun getPreviouslyStoredValue() {
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
    }

    private fun onClickListener() {

        binding.txtShopDetails.setOnClickListener {
            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
            when (selectedCustomer) {

                "Retailer" -> {

                    navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_retailer_details)
                }
                "Dealer" -> {

                  //  navController.navigate(R.id.nav_dealer_details)
                }
                else -> {

                    navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_other_details)
                }
            }

        }

        binding.retailerDashboardChild.layoutEditProfile.setOnClickListener {
            Utility.storeKeyValue(requireActivity(), "update", "YES")
            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)

            when (selectedCustomer) {
                "Retailer" -> {
                    navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_retailer_details)
                }
                "Dealer" -> {
                    //navController.navigate(R.id.nav_dealer_details)
                }

                else -> {
                    navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_other_details)
                }
            }
        }

        binding.retailerDashboardChild.layoutClaim.setOnClickListener {

            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
            navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_expense_claim)
            //  Utility.getBaseMessage(requireActivity(), "Soon this feature will be available", "Keep calm and wait until the feature release", R.drawable.error_white, 0)
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

            openCamera()
        }

        binding.retailerDashboardChild.layoutPayment.setOnClickListener {

            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
            //   navController.navigate(R.id.nav_payment_save)
            navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_payment_list)
        }

        binding.retailerDashboardChild.layoutSalesOrder.setOnClickListener {
//            Utility.getBaseMessage(
//                requireActivity(),
//                "Soon this feature will be available",
//                "Keep calm and wait until the feature release",
//                R.drawable.error_white,
//                0
//            )

            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
            navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_delivery_program)
        }

        binding.retailerDashboardChild.layoutCallRecord.setOnClickListener {

            if (alreadyAddedImageURL == "") {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Customer Shop/Organization image is required",
                    "",
                    com.crowncement.TsSalesTracking.R.drawable.error_white,
                    0
                )
            } else {
                val prepareSelectCustomer = prepareCallRecordHomeData()
                Utility.storeKeyValue(
                    requireActivity(), "callRecordCustomer", prepareSelectCustomer
                )

                val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
                //navController.navigate(R.id.nav_call_record_home)
            }
        }

        binding.retailerDashboardChild.layoutVisit.setOnClickListener {
            if (alreadyAddedImageURL == "") {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Customer Shop/Organization image is required",
                    "",
                    com.crowncement.TsSalesTracking.R.drawable.error_white,
                    0
                )
            } else {

                //LOCATION Green LIGHT
                if (latitude != 0.0) {
                    binding.locationStatus.visibility = View.VISIBLE
                    if (selectedCustomer == "Select Client Type") {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Invalid Client Type",
                            "Please go back and select valid client type",
                            com.crowncement.TsSalesTracking.R.drawable.error_white,
                            0
                        )
                    } else {
                        addQuickVisit(selectedCustomer)
                    }

                } else {
                    getLastLocations()
                }
            }
        }

        binding.retailerDashboardChild.cardConvert.setOnClickListener {

            if (alreadyAddedImageURL == "") {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Dealer/Retailer image is required",
                    "",
                    com.crowncement.TsSalesTracking.R.drawable.error_white,
                    0
                )
            } else {
                convertOfflineVisit()
            }
        }
    }

    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
        salesVisitViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesVisitViewModelFactory())
                .get(SalesVisitViewModel::class.java)
    }

    private fun convertDealerInfo(activity: Activity): DealerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedDealer", "")
        return gson.fromJson(json, DealerChild::class.java)
    }

    private fun convertRetailerInfo(activity: Activity): RetailerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedRetailer", "")
        return gson.fromJson(json, RetailerChild::class.java)
    }

    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }




    private fun prepareBasicInfo() {

        when (selectedCustomer) {

            "Retailer" -> {
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
                    binding.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity())
                        .load(EndPoint.IMAGE_BASE_URL + retailer.partyImage)
                        .error(com.crowncement.TsSalesTracking.R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .into(binding.imgRetailerHomeCover)
                }
            }
            "Dealer" -> {
                setBasicInfo(
                    dealer.dealerName.toString(),
                    "",
                    dealer.dealerAdd1.toString(),
                    dealer.dealerMobile.toString(),
                    "Dealer Code: " + dealer.dealerCode.toString()
                )

                if (dealer.dealerImage.toString() != "") {
                    binding.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(EndPoint.IMAGE_BASE_URL + dealer.dealerImage)
                        .error(com.crowncement.TsSalesTracking.R.drawable.shop_banner).fitCenter()
                        //.placeholder(R.drawable.shop_banner)
                        .into(binding.imgRetailerHomeCover)
                }
            }
            else -> {
                setBasicInfo(
                    other.partyName.toString(),
                    "",
                    other.partyAdd1.toString(),
                    other.partyMobile.toString(),
                    "Party Code: " + other.partyCode.toString()
                )

                if (other.partyImage.toString() != "") {
                    binding.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                        .error(com.crowncement.TsSalesTracking.R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .into(binding.imgRetailerHomeCover)
                }
            }
        }


    }

    private fun setBasicInfo(
        name: String, owner: String, address: String, mobile: String, code: String
    ) {
        binding.txtShopName.text = name
        binding.txtShopOwner.text = owner
        binding.txtShopAddress.text = "Address: $address"
        binding.txtShopMobile.text = "Mobile: $mobile"
        binding.txtShopCode.text = code
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
            requireActivity(), "com.crowncement.salesforceapp.fileprovider", imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {

            //  if (resultCode == Activity.RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                //   val selectedImage = data.extras!!["data"] as Bitmap?
                var path = Utility.getValueByKey(requireActivity(),"currentPhotoPath")
                val selectedImage = BitmapFactory.decodeFile(path)
               // val selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
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

//    private fun getRealPathFromURI(uri: Uri?): String? {
//        var path = ""
//        if (requireActivity().contentResolver != null) {
//            val cursor = requireActivity().contentResolver.query(uri!!, null, null, null, null)
//            if (cursor != null) {
//                cursor.moveToFirst()
//                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//                path = cursor.getString(idx)
//                cursor.close()
//            }
//        }
//        return path
//    }

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
                                    .error(com.crowncement.TsSalesTracking.R.drawable.shop_banner).fitCenter()
                                    // .placeholder(R.drawable.shop_banner)
                                    .into(binding.imgRetailerHomeCover)
                            }

                            loadingAnim.dismiss()
                            val navController =
                                requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
                            navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_dashboard)

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

//    private fun getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                if (ActivityCompat.checkSelfPermission(
//                        requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    requestPermissions()
//
//                }
//                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//                    val location: Location? = task.result
//                    if (location == null) {
//                        requestNewLocationData()
//                    } else {
//                        currentLocation = LatLng(location.latitude, location.longitude)
//                        latitude = location.latitude
//                        longitude = location.longitude
//                        if (latitude != 0.0) {
//                            rootView.locationStatus.visibility = View.VISIBLE
//                        }
//
//                        Log.e(TAG, "getLastLocation: Latitude" + latitude)
//                        Log.e(TAG, "getLastLocation: longitude" + longitude)
//                    }
//                }
//            } else {
//                requestDeviceLocationSettings()
//            }
//
//        } else {
//            requestPermissions()
//        }
//    }

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
                            binding.locationStatus.visibility = View.VISIBLE
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

    private fun addQuickVisit(customerType: String) {

        val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.quick_visit_confirm, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtCustomerType: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.txtCustomerType)
        val btnQuickSave: MaterialButton = v.findViewById(com.crowncement.TsSalesTracking.R.id.btnQuickSave)
        val btnQuickCancel: MaterialButton = v.findViewById(com.crowncement.TsSalesTracking.R.id.btnQuickCancel)
        val txtTourMessage: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.txtTourMessage)

        txtCustomerType.text = customerType

        var mess = ""
        when (selectedCustomer) {

            "Retailer" -> {
                //    retailer = convertRetailerInfo(requireActivity())!!
                mess =
                    "Do you want to add <b>${retailer.partyName}</b> as your today's sales visit ?"
            }
            "Dealer" -> {
                // dealer = convertDealerInfo(requireActivity())!!
                mess =
                    "Do you want to add <b>${dealer.dealerName}</b> as your today's sales visit ?"
            }
            else -> {
                //    other = convertOtherInfo(requireActivity())!!
                mess = "Do you want to add <b>${other.partyName}</b> as your today's sales visit ?"
            }
        }
        txtTourMessage.text = Html.fromHtml(mess)
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnQuickCancel.setOnClickListener {

            dialog.hide()
        }

        btnQuickSave.setOnClickListener {

            if (latitude > 0.0) {
                prepareQuickVisit(dialog)
            } else {
                getLastLocations()
                // latitude = getLastKnownLocation()!!.latitude
                // longitude = getLastKnownLocation()!!.longitude
                //LOCATION Green LIGHT
                if (latitude != 0.0) {
                    binding.locationStatus.visibility = View.VISIBLE
                } else {
                    prepareLocation()
                    getLastLocation()
                }
                dialog.dismiss()
                Utility.getBaseMessage(
                    requireActivity(),
                    "Location reset successfully",
                    "Please apply quick visit now",
                    com.crowncement.TsSalesTracking.R.drawable.error_white,
                    0
                )
            }
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

    private fun prepareQuickVisit(dialog: Dialog) {

        var profileInfo = ""
        val gson = Gson()

        when (selectedCustomer) {

            "Retailer" -> {

                val re = QuickSalesVisit(
                    retailer.partyName,
                    retailer.partyAdd1,
                    retailer.ownerName,
                    selectedCustomer,
                    retailer.marketPoint,
                    retailer.partyMobile,
                    retailer.outletCat,
                    retailer.geoHrc1,
                    retailer.geoHrc2,
                    retailer.geoHrc3,
                    retailer.geoHrc4,
                    retailer.geoHrc5,
                    retailer.partyZone,
                    latitude,
                    longitude,
                    "",
                    "",
                    retailer.partySell1,
                    retailer.partySell2,
                    retailer.partySell3,
                    retailer.partySell4,
                    retailer.partySell5,
                    retailer.partyStatus
                )


                profileInfo = gson.toJson(re)

            }

            "Dealer" -> {


                val de = QuickSalesVisit(
                    dealer.dealerName,
                    dealer.dealerAdd1,
                    dealer.contactPerson,
                    selectedCustomer,
                    "",
                    dealer.dealerMobile,
                    "",
                    dealer.geoHrc1,
                    dealer.geoHrc2,
                    dealer.geoHrc3,
                    "",
                    "",
                    "",
                    latitude,
                    longitude,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    dealer.dealerState
                )

                profileInfo = gson.toJson(de)

            }

            else -> {

                val oth = QuickSalesVisit(
                    other.partyName,
                    other.partyAdd1,
                    other.contactPerson,
                    selectedCustomer,
                    "",
                    other.partyMobile,
                    "",
                    other.geoHrc1,
                    other.geoHrc2,
                    other.geoHrc3,
                    other.geoHrc4,
                    "",
                    "",
                    latitude,
                    longitude,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
                profileInfo = gson.toJson(oth)
            }
        }

        saveQuickVisit(user, customerCode, profileInfo, dialog)

    }

    private fun saveQuickVisit(user: String, code: String, value: String, dialog: Dialog) {
        dialog.dismiss()
        loadingAnim.show()

        salesVisitViewModel.getSalesVisitSummarySave(user, code, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    loadingAnim.dismiss()

                    it.responseData?.let { res ->

                        if (res.code == "200") {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Sales visit added successfully",
                                com.crowncement.TsSalesTracking.R.drawable.ic_checked_green,
                                1
                            )

                            if(offlineSID!=0){

                                AppRoomDatabase
                                    .getDatabase(mContext!!)
                                    .offlineQuickVisitDao()
                                    .deleteOfflineQuickSID(offlineSID)

                                offlineDialog.hide()
                            }

                            val navController = requireActivity().findNavController(com.crowncement.TsSalesTracking.R.id.nav_host_fragment)
                            navController.navigate(com.crowncement.TsSalesTracking.R.id.nav_dashboard)
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                com.crowncement.TsSalesTracking.R.drawable.error_white,
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



    private fun prepareCallRecordHomeData(): String {

        var customer = ""
        val gson = Gson()

        when (selectedCustomer) {

            "Retailer" -> {

                val re = SalesCallRecordH(
                    retailer.partyCode,// customercode
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    "",
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    retailer.partyName,
                    retailer.partyAdd1,
                    retailer.managerName,
                    selectedCustomer,
                    retailer.marketPoint,
                    retailer.partyMobile,
                    retailer.geoHrc1,
                    retailer.geoHrc2,
                    retailer.geoHrc3,
                    retailer.geoHrc4,
                    retailer.geoHrc5,
                    retailer.partyZone,
                    0.0,
                    0.0
                )
                customer = gson.toJson(re)
            }

            "Dealer" -> {

                val de = SalesCallRecordH(
                    dealer.dealerCode,// customercode
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    "",
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    dealer.dealerName,
                    dealer.dealerAdd1,
                    dealer.contactPerson,
                    selectedCustomer,
                    "",
                    dealer.dealerMobile,
                    dealer.geoHrc1,
                    dealer.geoHrc2,
                    dealer.geoHrc3,
                    "",
                    "",
                    "",
                    0.0,
                    0.0
                )
                customer = gson.toJson(de)

            }

            else -> {

                val oth = SalesCallRecordH(
                    other.partyCode,// customercode
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    "",
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    other.partyName,
                    other.partyAdd1,
                    other.contactPerson,
                    selectedCustomer,
                    "",
                    other.partyMobile,
                    other.geoHrc1,
                    other.geoHrc2,
                    other.geoHrc3,
                    other.geoHrc4,
                    "",
                    "",
                    0.0,
                    0.0
                )
                customer = gson.toJson(oth)
            }
        }
        return customer
    }

    override fun onResume() {
        super.onResume()

        Handler().post {
            var intent = requireActivity().intent
            if (intent.equals(null)) {
                intent = Intent(requireActivity(), Act_Splash_Screen::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
                )
                requireActivity().overridePendingTransition(0, 0)
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0)
                startActivity(intent)
            } else {

                if (!isLocationEnabled()) {
                    requestDeviceLocationSettings()
                }
            }

        }
    }


    private fun convertOfflineVisit() {

        val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.convert_quick_visit, null)
        // val dialog = BottomSheetDialog(requireContext())
        offlineDialog = Dialog(v.context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        Objects.requireNonNull(offlineDialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val QVCancel: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.QVCancel)


        loadQuickVisit(getTodayOfflineQuickVisit(requireActivity()), v)

        offlineDialog.setContentView(v)
        offlineDialog.setCancelable(true)
        offlineDialog.show()

        QVCancel.setOnClickListener {

            offlineDialog.hide()
        }

    }

    private fun getTodayOfflineQuickVisit(activity: Activity): ArrayList<OfflineQuickVisit> {
        val qVisit: ArrayList<OfflineQuickVisit> = AppRoomDatabase
            .getDatabase(activity.applicationContext)
            .offlineQuickVisitDao()
            .getTodayQuickVisit(
                Utility.getCurrentDate("yyyy-MM-dd").toString()
            ) as ArrayList<OfflineQuickVisit>
        return qVisit
    }

    private fun loadQuickVisit(quickVisits: ArrayList<OfflineQuickVisit>, v: View) {

        val txtQNoData: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.txtQNoData)
        val shimmer_container: ShimmerFrameLayout = v.findViewById(com.crowncement.TsSalesTracking.R.id.shimmer_container)
        val rvQList: RecyclerView = v.findViewById(com.crowncement.TsSalesTracking.R.id.rvQList)
        if (quickVisits.size > 0) {
            txtQNoData.visibility = View.GONE
            rvQList.visibility = View.VISIBLE
            prepareDataList(quickVisits, rvQList)

        } else {
            txtQNoData.visibility = View.VISIBLE
            rvQList.visibility = View.GONE
        }
        shimmer_container.visibility = View.GONE
        shimmer_container.stopShimmer()
    }

    private fun prepareDataList(items: ArrayList<OfflineQuickVisit>, rv: RecyclerView) {
        val itemAdapter = OfflineQuickVisitAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(this.requireContext())

        rv.layoutManager = rLayoutmanager
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter


        itemAdapter.setOnItemClickListener(object :
            OfflineQuickVisitAdapter.OnAdapterItemClickListener {
            override fun OnSelect(v: View?, position: Int) {
                latitude= items[position].Latitude!!
                longitude= items[position].Longitude!!
                offlineSID=items[position].QuickVisitSID
                addQuickVisit(selectedCustomer)

            }

            override fun OnIconSelect(v: View?, position: Int) {

                liveLocationBottomSheet(items[position])
            }

        })
    }



    private fun liveLocationBottomSheet(data: OfflineQuickVisit) {
        val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.emp_live_att, null)
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val txtLocationCancel: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.txtLocationCancel)
        val txtLocationMessage: TextView = v.findViewById(com.crowncement.TsSalesTracking.R.id.txtLocationMessage)
        val map: MapView = v.findViewById(com.crowncement.TsSalesTracking.R.id.map_view)

        if (data!=null) {
            val conVoTime = Utility.changeDateFormat(
                data.CreateTime, "HH:mm:ss", "hh:mm:ss a"
            )
            txtLocationMessage.text = "Location updated on ${conVoTime}"

        } else {
            txtLocationMessage.text = "Location updated on : Sorry no data found"
        }


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        var currentLocation: LatLng? = null

        if (data.Latitude!! > 0) {
            currentLocation = LatLng(
                data.Latitude!!,
                data.Longitude!!
            )

            val googleMap: GoogleMap
            MapsInitializer.initialize(requireActivity())

            map.onCreate(dialog.onSaveInstanceState())
            map.onResume() // needed to get the map to display immediately


            map.getMapAsync { googleMap ->

                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentLocation!!)
                        .title("Visited Place")
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15.0f)
                )

                googleMap.setOnMarkerClickListener { // Marker source is clicked

                    true
                }

                googleMap.addCircle(
                    CircleOptions()
                        .center(currentLocation)
                        .radius(60.0).strokeWidth(3f).strokeColor(Color.GREEN)
                        .fillColor(Color.argb(70, 50, 150, 50))
                )

                googleMap.isTrafficEnabled = true
            }
        }

        txtLocationCancel.setOnClickListener {
            dialog.hide()
        }

    }




}