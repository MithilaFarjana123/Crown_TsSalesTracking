package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.common.Utility.loadImage
import com.crowncement.TsSalesTracking.databinding.FragAttendanceBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class Frag_Attendance : Fragment() {



    private val TAG = "Frag_Attendance"
 //   lateinit var rootView: View

    private var _binding: FragAttendanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var gMap: GoogleMap
    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionCode = 101
    var PERMISSION_ALL = 1

    var PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPermission()
        prepareLocation()
        getLastLocation()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun openCamera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra("android.intent.extras.CAMERA_FACING", 1)
        startActivityForResult(takePicture, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     //   rootView = inflater.inflate(R.layout.frag_attendance, container, false)
        _binding = FragAttendanceBinding.inflate(inflater, container, false)


        binding.txtSearch.setOnClickListener {

            getLastLocation()

        }

        binding.imgUser.setOnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePicture.putExtra("android.intent.extras.CAMERA_FACING", 1)
            startActivityForResult(takePicture, 0)
        }

        val isImageTaken = Utility.getValueByKey(requireActivity(), "isImageTaken")

        if (!isImageTaken.equals("YES")) {
            open_camera_Dialog()
        }


        return binding.root
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

    // Get current location
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

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
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude

//                        gMap.clear()
//                        gMap.addMarker(
//                            MarkerOptions().position(currentLocation)
//                                .title("You are here")
//                        )
//                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
//

                    }
                }
            }

        } else {
            requestPermissions()
        }
    }

    private fun appPermission() {
        if (!hasPermissions(requireContext(), *PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL)
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            permissionCode
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

//    override fun onMapReady(p0: GoogleMap) {
//        gMap = p0
//        getLastLocation()
//
//        gMap.setOnMapClickListener { latlng ->
//            gMap.clear()
//            gMap.addMarker(
//                MarkerOptions().position(latlng)
//                    .title("আপনার নির্বাচিত লোকেশন")
//                    .icon(
//                        BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)
//                    )
//            )
//            gMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
//
//            latitude = latlng.latitude
//            longitude = latlng.longitude
//        }
//
//
//
//    }

    override fun onResume() {
        super.onResume()
        requestDeviceLocationSettings()
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


    //---------------------Map Location------------------


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_CANCELED) {


            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val selectedImage = data.extras!!["data"] as Bitmap?
                val tempUri = getImageUri(requireContext(), selectedImage!!)
                val finalFile = File(getRealPathFromURI(tempUri)!!)
                loadImage(binding.imgSet, requireContext(), "selectedFarmImage")
                //  resizeImage(selectedImage)
                binding.imgSet.setImageBitmap(selectedImage)
            }

        } else {
            openCamera()
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "IMG-" + getCurrentDate("dd-MMM-yyyy-HHmma"),
            null
        )
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (requireActivity().contentResolver != null) {
            val cursor = requireActivity().contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    private fun resizeImage(yourBitmap: Bitmap) {
        val resized: Bitmap


        resized = Bitmap.createScaledBitmap(yourBitmap, 400, 200, false)
        binding.imgSet.setImageBitmap(resized)
    }

    private fun open_camera_Dialog() {
        val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.open_camera, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val openCamera = v.findViewById<Button>(com.crowncement.TsSalesTracking.R.id.openCamera)

        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()

        openCamera.setOnClickListener { view1: View? ->

            Utility.storeKeyValue(requireActivity(), "isImageTaken", "YES")
            dialog.dismiss()
            openCamera()
        }
    }







}