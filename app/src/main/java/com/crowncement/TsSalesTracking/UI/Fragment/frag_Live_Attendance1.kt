package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper

import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.UI.viewmodel.AttendanceViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.AttendanceViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getAttInfo
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.common.Utility.storeKeyValue
import com.crowncement.TsSalesTracking.data.Model.Att
import com.crowncement.TsSalesTracking.data.Model.AttendanceResponse
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.FragLiveAttendance1Binding
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class frag_Live_Attendance1 : Fragment() {

    private var _binding: FragLiveAttendance1Binding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_CAPTURE = 1


    private lateinit var atteViewModel: AttendanceViewModel
    //  private lateinit var rootView: View
    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101
    var PERMISSION_ALL = 1
    private lateinit var finalFile: File

    private lateinit var att: Att
    private lateinit var loadingAnim: Dialog
    var currentPhotoPath = ""

    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val TAG = "Frag_Live_Attendance"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //   rootView = inflater.inflate(R.layout.frag_live_attendance1, container, false)
        _binding = FragLiveAttendance1Binding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        initiateUI()

        disableButton()

        onButtonClickListener()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    private fun initiateUI() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //appPermission()
        getLastLocation()
        AttendanceViewModelFactory()
        setupViewModel()

        att = getAttInfo(requireActivity())!!

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        setIntimeIfExist()
    }

    private fun setIntimeIfExist() {
        if (att.intime != null && att.intime!!.isNotEmpty()) {
            binding.btnSubmitCheckout.visibility = View.VISIBLE
            binding.btnSubmitCheckout.text = "Check Out"

            binding.txtInTime.text = Utility.changeDateFormat(
                att.intime,
                "yyyy-MM-dd HH:mm:ss",
                "EEE dd MMM yyyy hh:mm:ss a"
            )

        } else {
          //  openCamera()
            captureImage()
        }
    }


    private fun onButtonClickListener() {
        binding.btnSubmitCheckout.setOnClickListener {
          //  openCamera()
            captureImage()
        }
    }

    private fun disableButton() {
        if (att.intime != "" && att.outtime != "" && !(att.outtime!!.contains("1900-01-01"))) {

            binding.txtOutTime.text = Utility.changeDateFormat(
                att.outtime,
                "yyyy-MM-dd HH:mm:ss",
                "EEE dd MMM yyyy hh:mm:ss a"
            )
            binding.btnSubmitCheckout.visibility = View.GONE
        }
    }

    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory())
            .get(AttendanceViewModel::class.java)
    }

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()


        // Initializing Map
        // val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        //  mapFragment.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
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
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            Log.d(TAG, "onLocationResult: $latitude | $longitude")
        }
    }



    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ALL
        )
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if ((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getLastLocation()
        }
    }

    override fun onResume() {
        super.onResume()

        Handler().post {
            var intent = requireActivity().intent
            if (intent.equals(null)) {
                intent = Intent(requireActivity(), Act_Splash_Screen::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
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

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(TAG, "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {

            //   if (resultCode == RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
              //  if(isTimeAutomatic(requireActivity())==true) {
                if(isTimeAutomatic(requireContext())==true||getDeviceTimeZone(requireContext())==true) {

                    try {
                        loadingAnim.show()
                        //  val selectedImage = data.extras!!["data"] as Bitmap?
                   val path=   getValueByKey(requireActivity(),"currentPhotoPath")
                        val selectedImage = BitmapFactory.decodeFile(path)

                        if (latitude == 0.0) {
                            latitude = getLastKnownLocation()!!.latitude
                            longitude = getLastKnownLocation()!!.longitude

                          //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
                        }

                        //checkout
                        if (!(att.intime!!.isEmpty()) && !(att.intime.equals(""))) {
                            val emp = getValueByKey(requireActivity(), "username")
                            val imName = emp + getCurrentDate("yyyyMMdd") + "out"
                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            finalFile = File(getRealPathFromURI(tempUri)!!)
                            binding.imgSetPic.setImageBitmap(selectedImage)


                            attendanceUpdate(
                                emp!!, getCurrentDate("yyyy-MM-dd").toString(),
                                getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                                att.shift_no.toString(),
                                att.shift.toString(),
                                File(finalFile.toString()),
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                        //check in
                        else {
                            val empI = getValueByKey(requireActivity(), "username")
                            val imName = empI + getCurrentDate("yyyyMMdd") + "in"

                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            finalFile = File(getRealPathFromURI(tempUri)!!)
                            binding.imgSetPic.setImageBitmap(selectedImage)

                            attendanceEntry(
                                empI!!,
                                getCurrentDate("yyyy-MM-dd").toString(),
                                getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                                att.shift_no.toString(),
                                att.shift.toString(),
                                File(finalFile.toString()),
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                    } catch (e: Exception) {
                        Utility.commonToast(requireActivity(), "Failed", e.message.toString(), 0)
                    }
                }else{
                       timeWarningDialog()
                }

            }

        } else {
            Utility.commonToast(requireActivity(), "Failed", "failed to take picture", 0)
        }
    }


 */

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
        // val bytes = ByteArrayOutputStream()
        // inImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            getResizedBitmap(inImage, 800),
            //resizeImage(inImage),
            // inImage,
            imageName,
            null
        )
        return Uri.parse(path)
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        try {
            if (requireActivity().contentResolver != null) {
                val cursor = requireActivity().contentResolver.query(uri!!, null, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cursor.getString(idx)
                    cursor.close()
                }
            }
        }
        catch (e:Exception){
            Utility.commonToast(requireActivity(),"Error","Can not process image",0)
        }

        return path
    }

    private fun resizeImage(yourBitmap: Bitmap): Bitmap {
        // val resized: Bitmap
        return Bitmap.createScaledBitmap(yourBitmap, 900, 600, false)
        // rootView.imgSet.setImageBitmap(resized)
    }


    private fun openCamera() {
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
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.TsSalesTracking.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(),"currentPhotoPath",currentPhotoPath)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
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
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
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
                        binding.imgLocation.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            requestDeviceLocationSettings()
        }
    }


    //SAVE ATTENDANCE............................................................
    private fun attendanceEntry(
        id: String,
        attDate: String,
        attTime: String,
        shiftNo: String,
        shift: String,
        attImage: File,
        latitude: String,
        longitude: String
    ) {
        atteViewModel.attEntry(id, attDate, attTime, shiftNo, shift, attImage, latitude, longitude,"Online")
            .observe(this) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            try {
                                loadingAnim.dismiss()
                                successAttEntry(res)
                            } catch (e: Exception) {
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

    private fun successAttEntry(res: AttendanceResponse) {

        if (res.message.equals("Attendance Entered Successfully")) {

//TODO----------------------LOCATION TRACKING SERVICE START-----------------------
            val isTrackingEnabled = Utility.getUserInfo(requireActivity())!!.att_status
            if (isTrackingEnabled == "Yes") {
                storeKeyValue(requireActivity(), "Is_Location_Tracking_Running", "YES")
                startBackgroundLocationService()
            }

            loadingAnim.dismiss()
            Utility.commonToast(
                requireActivity(),
                "Check In",
                "Attendance Entered Successfully",
                1
            )
            binding.btnSubmitCheckout.visibility = View.GONE
            binding.txtInTime.text = getCurrentDate("EEE hh:mm a dd.MMM.yy")
            Utility.sendActivity(requireActivity(), "Act_Dashboard")

        } else {
            loadingAnim.dismiss()
            Utility.commonToast(requireActivity(), "Sorry", res.message.toString(), 2)
        }
    }

    private fun startBackgroundLocationService() {

        val username: String = getValueByKey(requireActivity(), "username")!!

        if (username.isNotEmpty() && !isMyServiceRunning()) {

            val intent = Intent(requireContext(), TrackingService::class.java)
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(intent)
            } else {
                requireActivity().startService(intent)
            }
        }
    }

    private fun isMyServiceRunning(): Boolean {
        var result = false
        val manager =
            requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TrackingService::class.java.name == service.service.className) {
                result = true
                break
            } else {
                result = true
            }
        }
        return result
    }

    private fun attendanceUpdate(
        id: String,
        attDate: String,
        attTime: String,
        shiftNo: String,
        shift: String,
        attImage: File,
        latitude: String,
        longitude: String
    ) {
        atteViewModel.attUpdate(id, attDate, attTime, shiftNo, shift, attImage, latitude, longitude)
            .observe(this) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res -> successAttUpdate(res) }

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

    private fun successAttUpdate(res: AttendanceResponse) {
        loadingAnim.dismiss()
        //   if (res.message.equals("Attendance Updated Successfully")) {
        if (res.code == "200") {
            Utility.removePreference(requireActivity(), "Is_Location_Tracking_Running")

            //CALL STOP SERVICE
            val intent = Intent(requireContext(), TrackingService::class.java)
            requireActivity().stopService(intent)

            //  Utility.commonToast(requireActivity(), "Successful ", "Check Out Completed", 1)
            binding.btnSubmitCheckout.visibility = View.GONE
            binding.txtOutTime.text = Utility.getCurrentDate("EEE hh:mm:ss a dd.MMM.yy")

            Utility.commonToast(
                requireActivity(),
                "Check Out Completed",
                res.message.toString(),
                1
            )
            Utility.sendActivity(requireActivity(), "Act_Dashboard")
        } else {
            Utility.commonToast(requireActivity(), "Sorry", res.message.toString(), 2)
        }
    }



    //todo for manual attendence
    fun isTimeAutomatic(c: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
        } else {
            Settings.System.getInt(c.contentResolver, Settings.System.AUTO_TIME, 0) == 1
        }
    }


    private fun timeWarningDialog(
    ) {

        val v: View = layoutInflater.inflate(R.layout.time_warning, null)
        val warningDialog = Dialog(v.context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        warningDialog.window!!.statusBarColor = ContextCompat.getColor(v.context, R.color.reject)
        warningDialog.setContentView(v)
        warningDialog.setCancelable(false)
        warningDialog.show()
        warningDialog.setOnKeyListener { dialogInterface, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                warningDialog.show()
                true
            } else false
        }
    }

//todo new camera
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



/*
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
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
                    "com.crowncement.tecnicalsupport.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            // Handle the case where no camera app is available
        }
    }

 */



    //todo works for every device except 15

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
                    "com.crowncement.TsSalesTracking.fileprovider",
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
                                "com.crowncement.TsSalesTracking.fileprovider",
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
            Utility.storeKeyValue(requireActivity(),"currentPhotoPath",currentPhotoPath)

        }
    }



   /*
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
            Utility.storeKeyValue(requireActivity(),"currentPhotoPath",currentPhotoPath)

        }
    }


    */


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
                currentPhotoPath = Utility.getValueByKey(requireActivity(),
                    "currentPhotoPath").toString()
            val imgFile = compressImage(currentPhotoPath)

            if (imgFile.exists()) {
                val imageSize = (imgFile.length() / 1024.0)
               // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
              //  finalFile.add(imgFile)
                finalFile = imgFile
            }

            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            binding.imgSetPic.setImageBitmap(bitmap)


            if (latitude == 0.0) {
                latitude = getLastKnownLocation()!!.latitude
                longitude = getLastKnownLocation()!!.longitude

                //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
            }

            if (!(att.intime!!.isEmpty()) && !(att.intime.equals(""))) {
                val emp = getValueByKey(requireActivity(), "username")
                val imName = emp + getCurrentDate("yyyyMMdd") + "out"
              //  val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
             //   finalFile = File(getRealPathFromURI(tempUri)!!)
              //  binding.imgSetPic.setImageBitmap(selectedImage)


                attendanceUpdate(
                    emp!!, getCurrentDate("yyyy-MM-dd").toString(),
                    getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                    att.shift_no.toString(),
                    att.shift.toString(),
                    File(finalFile.toString()),
                    latitude.toString(),
                    longitude.toString()
                )
            }


            //checkout
            if (!(att.intime!!.isEmpty()) && !(att.intime.equals(""))) {
                val emp = getValueByKey(requireActivity(), "username")
                val imName = emp + getCurrentDate("yyyyMMdd") + "out"
               // val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
               // finalFile = File(getRealPathFromURI(tempUri)!!)
               // binding.imgSetPic.setImageBitmap(selectedImage)


                attendanceUpdate(
                    emp!!, getCurrentDate("yyyy-MM-dd").toString(),
                    getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                    att.shift_no.toString(),
                    att.shift.toString(),
                    File(finalFile.toString()),
                    latitude.toString(),
                    longitude.toString()
                )

            }
            //check in
            else {
                val empI = getValueByKey(requireActivity(), "username")
                val imName = empI + getCurrentDate("yyyyMMdd") + "in"

              //  val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
             //   finalFile = File(getRealPathFromURI(tempUri)!!)
               // binding.imgSetPic.setImageBitmap(selectedImage)
//todo here will check the lat lon with the login lat lon
                val loginlat = Utility.getUserInfo(requireActivity())!!.attLat
                val loginlon = Utility.getUserInfo(requireActivity())!!.attLon

                if (loginlat != null) {
                    if (loginlon != null) {
                        exactLocationRestriction(loginlat,loginlon)
                    }
                }

/*
                attendanceEntry(
                    empI!!,
                    getCurrentDate("yyyy-MM-dd").toString(),
                    getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                    att.shift_no.toString(),
                    att.shift.toString(),
                    File(finalFile.toString()),
                    latitude.toString(),
                    longitude.toString()
                )

 */
            }

        }

            catch (e: Exception) {

            Utility.commonToast(requireActivity(),e.message.toString(), e.message.toString(), 0)

        }



        }

        else {
            Toast.makeText(requireActivity(), "ERROR in image processing", Toast.LENGTH_LONG).show()
        }


    }

//todo


/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {

            //   if (resultCode == RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
              //  if(isTimeAutomatic(requireActivity())==true) {
                if(isTimeAutomatic(requireContext())==true||getDeviceTimeZone(requireContext())==true) {

                    try {
                        loadingAnim.show()
                        //  val selectedImage = data.extras!!["data"] as Bitmap?
                   val path=   getValueByKey(requireActivity(),"currentPhotoPath")
                        val selectedImage = BitmapFactory.decodeFile(path)

                        if (latitude == 0.0) {
                            latitude = getLastKnownLocation()!!.latitude
                            longitude = getLastKnownLocation()!!.longitude

                          //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
                        }

                        //checkout
                        if (!(att.intime!!.isEmpty()) && !(att.intime.equals(""))) {
                            val emp = getValueByKey(requireActivity(), "username")
                            val imName = emp + getCurrentDate("yyyyMMdd") + "out"
                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            finalFile = File(getRealPathFromURI(tempUri)!!)
                            binding.imgSetPic.setImageBitmap(selectedImage)


                            attendanceUpdate(
                                emp!!, getCurrentDate("yyyy-MM-dd").toString(),
                                getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                                att.shift_no.toString(),
                                att.shift.toString(),
                                File(finalFile.toString()),
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                        //check in
                        else {
                            val empI = getValueByKey(requireActivity(), "username")
                            val imName = empI + getCurrentDate("yyyyMMdd") + "in"

                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            finalFile = File(getRealPathFromURI(tempUri)!!)
                            binding.imgSetPic.setImageBitmap(selectedImage)

                            attendanceEntry(
                                empI!!,
                                getCurrentDate("yyyy-MM-dd").toString(),
                                getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                                att.shift_no.toString(),
                                att.shift.toString(),
                                File(finalFile.toString()),
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                    } catch (e: Exception) {
                        Utility.commonToast(requireActivity(), "Failed", e.message.toString(), 0)
                    }
                }else{
                       timeWarningDialog()
                }

            }

        } else {
            Utility.commonToast(requireActivity(), "Failed", "failed to take picture", 0)
        }
    }




 */




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






    ///////////////////////////////////////////////////////////////////////////
    //todo for 150m check

    @SuppressLint("SuspiciousIndentation")
    fun exactLocationRestriction(destinationLat:Double, destinationLon:Double){
       // getLastLocations()

        if (latitude == 0.0) {
            latitude = getLastKnownLocation()!!.latitude
            longitude = getLastKnownLocation()!!.longitude

            //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
        }
        val distance=getDistance(destinationLat,destinationLon,latitude,longitude)
        val disInMeter= String.format("%.2f", distance)

        val empI = getValueByKey(requireActivity(), "username")
        val imName = empI + getCurrentDate("yyyyMMdd") + "in"
        val fence_distance = Utility.getUserInfo(requireActivity())!!.fence_distance

       // val loginlon = Utility.getUserInfo(requireActivity())!!.attLat
       // if(siteinfo.siteImage.toString()!=""){
            if(distance>=1000){
            //    binding.txtDestination.visibility=View.GONE

              //  QuickVisitDialog()

                attendanceEntry(
                    empI!!,
                    getCurrentDate("yyyy-MM-dd").toString(),
                    getCurrentDate("yyyy-MM-dd HH:mm:ss").toString() + ".000",
                    att.shift_no.toString(),
                    att.shift.toString(),
                    File(finalFile.toString()),
                    latitude.toString(),
                    longitude.toString()
                )


                //addQuickVisit(selectedCustomer)
            }

            else{
                val mess="Your are $disInMeter meter away from your residence .Now your current location is $latitude,$longitude  & your residence's location is" +
                        "$destinationLat,$destinationLon , please go to 1 kilometre  away from your residence & try again."
              //  binding.txtDefaultMessage1.text= mess
                val showDialog:Dialog= distanceWarning(requireActivity(),Dialog(requireContext()),mess)
                showDialog.show()
           // val showDialog:Dialog= distanceWarning(requireActivity(),Dialog(requireContext()),"mess")
                showDialog.show()

            }

        }

        /*
        else{

            Utility.getBaseMessage(requireActivity(),
                "Warning ",
                "Please add site image first then try to give quick visit. ",
                R.drawable.error_white,
                0
            )

        }

         */


    fun distanceWarning(activity: Activity, dialog: Dialog, mess: String): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate(R.layout.attdistance_warning_dialog, null, false)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val message = mView.findViewById<TextView>(R.id.txtDefaultMessage1)
        val try_again = mView.findViewById<Button>(R.id.try_again)

        message.text = mess
        dialog.setContentView(mView)
        dialog.setCanceledOnTouchOutside(true)
        val window = dialog.window
        Objects.requireNonNull(window)!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        try_again.setOnClickListener{
            captureImage()
            // dialog.dismiss()
        }
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window.setGravity(Gravity.CENTER)
        //dialog.show();
        return dialog
    }


    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // radius of Earth in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    }








