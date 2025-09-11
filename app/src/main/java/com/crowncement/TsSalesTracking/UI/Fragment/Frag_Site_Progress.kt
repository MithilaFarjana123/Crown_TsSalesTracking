package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.annotation.SuppressLint
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
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Adapter.SiteProgress
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.PickDocWPartResponseChild
import com.crowncement.TsSalesTracking.databinding.FragSiteProgressBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID


class Frag_Site_Progress : Fragment() {

    private var _binding: FragSiteProgressBinding? = null
    private val binding get() = _binding!!
    var currentPhotoPath = ""
    private val REQUEST_IMAGE_CAPTURE = 1


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    lateinit var finalFile: File

    private val permissionCode = 101

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private var selectedCustomer: String = ""

    private  val TAG = "Frag_Site_Progress"
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var siteinfo: GetTSSiteInfoChild

    lateinit var loadingAnim: Dialog
    lateinit var cardFile: ArrayList<File>
    var extension = ""
    lateinit var siteImgList: ArrayList<PickDocWPartResponseChild>
    lateinit var imageList: java.util.ArrayList<String>


    lateinit var siteProgress : SiteProgress


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragSiteProgressBinding.inflate(inflater, container, false)



        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        RetailerViewModelFactory()
        setupViewModel()
        finalFile = File("")
        cardFile = ArrayList()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        prepareLocation()
        getLastLocation()
        siteinfo = convertOtherSiteInfo(requireActivity())!!

        PickDocWPart("SalesSiteImg",
            "progressimg",
            siteinfo.siteNumber.toString())
        onClickLinener()

    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickLinener(){

        binding.fabAddSiteImg.setOnClickListener{
            captureImage()
        }

        /*
        binding.takeTsSiteimage.setOnClickListener {

            //   openCamera()
            captureImage()

        }


        binding.submitImg.setOnClickListener {

            if(finalFile.isFile){
                confirmationDialog()

            }else{

                Utility.commonToast(requireActivity(),
                    "Warning",
                    "Site Progress Image Required",
                0)
            }

        }

         */
    }



    //todo location
    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

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

//todo location end



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


    //todo worked for all except android 15
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
    @RequiresApi(Build.VERSION_CODES.O)
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
                cardFile.add(finalFile)

                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding.tsSiteImage.setImageBitmap(bitmap)


                if (latitude == 0.0) {
                    getLastLocation()

                    //   latitude = getLastKnownLocation()!!.latitude
                    //   longitude = getLastKnownLocation()!!.longitude
                }


                if(finalFile.isFile){

                    confirmationDialog()

                }else{

                    Utility.commonToast(requireActivity(),
                        "Warning",
                        "Site Progress Image Required",
                        0)
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

    private fun convertOtherSiteInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }



    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
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
     //   val animation: LottieAnimationView = v.findViewById(R.id.animation)
     //   val siteImg : ImageView = v.findViewById(R.id.siteImg)
      //  animation.setVisibility(View.GONE)
      //  siteImg.setImageBitmap(bitmap)


        //   var oth = retailerSetupInfo.split(",")[0]

        val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer")
        selectedCustomer = customertype.toString()


        txtCDialogTitle.text = " Site Progress"
        txtBody.text = "Are you sure , you want to add this photo of this site "

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = LocalDate.now()
        var currentdate = today.format(formatter)
        println(today.format(formatter))


        var  SiteCode = siteinfo.siteNumber.toString()

        var filename = SiteCode+"_"+currentdate

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

            if (finalFile.isFile) {

                uploadCustomerDoc(
                    empI,
                    filename, //siteid_date
                    "SalesSiteImg", //SalesSiteImg
                    "progressimg",//progressimg
                    "jpg",
                    cardFile,
                    loadingAnim,
                    "" + "nid"
                )
                dialog.dismiss()

            } else {

                dialog.dismiss()
                Toast.makeText(requireContext(), "File missing ", Toast.LENGTH_LONG)
                    .show()

            }


            }




        }


    ///todo for image upload

    private fun uploadCustomerDoc(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>,
        loading: Dialog,
        docType: String
    ) {
        loadingAnim.show()
        retailerViewModel.uploadCusFile(
            user_id,
            party_code,
            party_type,
            doc_type,
            doc_ext,
            all_images
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loadingAnim.dismiss()

                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Site progress image added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )

                                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                                    navController.navigate(R.id.nav_demo_Site_Home)

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
                                Log.e("Site progress img", "Error: " + e.message)
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




    private fun PickDocWPart(
        app_proj: String,
        doctype: String,
        partName: String

    ) {
        retailerViewModel.PickDocWPart(app_proj,doctype,partName)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            siteImgList = ArrayList()


                            siteImgList = res.data

                            imageList = java.util.ArrayList()

                            //todo for slider

                            for (image in res.data) {
                                imageList.add(IMAGE_BASE_URL + image.filePath.toString())
                            }


                            if (imageList.size > 0) {

                                setDocumentSliderUI(res.data)
                            }

                            if (siteImgList.size > 0) {
                                //   rootView.txtNoDealerInfo.visibility = View.GONE
                                siteProgress = SiteProgress(siteImgList)
                                if(isAdded) {
                                    val manager =
                                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.rdProgress.layoutManager = manager
                                    binding.rdProgress.adapter = siteProgress


                                }
                            }

                        }
                        loadingAnim.dismiss()
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        loadingAnim.dismiss()

                    }

                }

            }

    }






    fun setDocumentSliderUI(docs: List<PickDocWPartResponseChild>?) {
        val imageList = ArrayList<SlideModel>()
      //  val base = "http://34.142.207.144:8787/"

        docs?.forEach { doc ->
            doc.filePath?.takeIf { it.isNotBlank() }?.let { path ->
                val fullUrl = IMAGE_BASE_URL + path
                imageList.add(SlideModel(fullUrl, ScaleTypes.FIT))
            }
        }

        binding.siteProSlider.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }















}



