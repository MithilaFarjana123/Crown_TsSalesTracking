package com.crowncement.TsSalesTracking.UI.Activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.LocationHelper
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getDeviceTimeZone
import com.crowncement.TsSalesTracking.data.InternalDB.AppRoomDatabase
import com.crowncement.TsSalesTracking.data.InternalDB.DatabaseSingleton
import com.crowncement.TsSalesTracking.data.Model.OfflineAttendance
import com.crowncement.TsSalesTracking.databinding.ActOfflineAttendanceBinding


import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalTime
import java.util.UUID


class Act_Offline_Attendance : AppCompatActivity() {


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val TAG = "Act_Offline_Attendance"

    private lateinit var binding: ActOfflineAttendanceBinding


    var currentPhotoPath = ""

    lateinit var loadingAnim: Dialog

    lateinit var finalFile: File

    private var PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE

    )


    private var SPECIAL_PERMISSIONS = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

    )

    var user = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(com.crowncement.tecnicalsupport.R.layout.act_offline_attendance)
        binding = ActOfflineAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun offlineAttendanceData(attInfo: OfflineAttendance) {
        val attPhoto = BitmapFactory.decodeFile(attInfo.AttImagePath)
        binding.imgOfflineAtt.setImageBitmap(attPhoto)

        val inTime = Utility.changeDateFormat(attInfo.AttTime, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
        binding.txtOfflineTime.text = inTime.toString()
        binding.txtAttDate.text = Utility.getCurrentDate("dd").toString()
        binding.txtAttMonthYear.text = Utility.getCurrentDate("MMM-yyyy").toString()
        binding.txtAttDay.text = Utility.getCurrentDate("EEE").toString()

        val time1 = LocalTime.parse(Utility.changeDateFormat(inTime, "hh:mm a", "HH:mm:ss"))
        val time2 = LocalTime.parse("09:16:00")
        val comparisonResult = time1.compareTo(time2)
        when {
            comparisonResult < 0 -> {
                println("$time1 is earlier than $time2")
                binding.txtOffAttStatus.setTextColor(Color.parseColor("#00914C"))
            }
            comparisonResult > 0 -> {
                println("$time1 is later than $time2")
                binding.txtOffAttStatus.setTextColor(Color.parseColor("#FC9E14"))
            }
            else -> {
                binding.txtOffAttStatus.setTextColor(Color.parseColor("#00914C"))
            }
        }
    }

    private fun getTodayAttendance(activity: Activity): OfflineAttendance {
        val attInfo: OfflineAttendance = AppRoomDatabase
            .getDatabase(activity.applicationContext)
            .offlineAttendanceDao()
            .getTodayAttendance(Utility.getCurrentDate("yyyy-MM-dd").toString())
        return attInfo
    }

    private fun openCamera() {
        val fileName = "photo.jpg"
        val storageDirectory: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            this.applicationContext,
            "com.crowncement.tecnicalsupport.fileprovider",
            imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }

    //TODO------------------------------------------------------------------------------------CAMERA

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {

            if (resultCode == RESULT_OK) {

                try {
                    loadingAnim.show()

                    val selectedImage = BitmapFactory.decodeFile(currentPhotoPath)

                    if (currentPhotoPath != "") {
                        val empI = Utility.getValueByKey(this, "username")
                        val imName = empI + Utility.getCurrentDate("yyyyMMdd") + "in"
                        val tempUri = getImageUri(this.applicationContext, selectedImage!!, imName)
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        val imgFile = compressImage(currentPhotoPath)
                        finalFile = imgFile


                        binding.imgOfflineAtt.setImageBitmap(selectedImage)


                        val currentTime = Utility.getCurrentDate("HH:mm:ss")

                        binding.txtOfflineTime.text = Utility.getCurrentDate("hh:mm a").toString()
                        binding.txtAttDate.text = Utility.getCurrentDate("dd").toString()
                        binding.txtAttMonthYear.text = Utility.getCurrentDate("MMM-yyyy").toString()
                        binding.txtAttDay.text = Utility.getCurrentDate("EEE").toString()

                        val time1 = LocalTime.parse(currentTime.toString())
                        val time2 = LocalTime.parse("09:16:00")

                        val comparisonResult = time1.compareTo(time2)
                        when {
                            comparisonResult < 0 -> {
                                println("$time1 is earlier than $time2")
                                binding.txtOffAttStatus.setTextColor(Color.parseColor("#00914C"))
                            }
                            comparisonResult > 0 -> {
                                println("$time1 is later than $time2")
                                binding.txtOffAttStatus.setTextColor(Color.parseColor("#FC9E14"))
                            }
                            else -> {
                                binding.txtOffAttStatus.setTextColor(Color.parseColor("#00914C"))

                            }
                        }
                    } else {
                        Utility.getBaseMessage(
                            this,
                            "Failed",
                            "Failed to capture image",
                            com.crowncement.TsSalesTracking.R.drawable.error_white,
                            2
                        )
                    }

                    val offlineData = OfflineAttendance(
                        0,
                        latitude,
                        longitude,
                        user,
                        Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString(),
                        currentPhotoPath,
                        Utility.getCurrentDate("yyyy-MM-dd").toString(),
                        false
                    )
                    if(!isTimeAutomatic(this)||!getDeviceTimeZone(this)){
                        timeWarningDialog()
                    }else{
                        saveAttIntoInternalDb(offlineData)
                        loadingAnim.dismiss()
                    }



                } catch (e: Exception) {
                    Utility.commonToast(this, "Failed", e.message.toString(), 0)
                    loadingAnim.dismiss()
                }
            }

        } else {
            Utility.commonToast(this, "Failed", "failed to take picture", 0)
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
        val wrapper = ContextWrapper(this)
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


    @OptIn(DelicateCoroutinesApi::class)
    private fun saveAttIntoInternalDb(offlineAttData: OfflineAttendance) {
        GlobalScope.launch {
            DatabaseSingleton.getAppDatabase(applicationContext).offlineAttendanceDao()
                .insert(offlineAttData)
            Utility.getBaseMessage(
                this@Act_Offline_Attendance,
                "Successful",
                "your attendance has been saved to offline",
                com.crowncement.TsSalesTracking.R.drawable.ic_checked_green,
                1
            )
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            getResizedBitmap(inImage, 800),
            imageName,
            null
        )
        return Uri.parse(path)
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

    private fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        try {
            if (this.contentResolver != null) {
                val cursor = this.contentResolver.query(uri!!, null, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cursor.getString(idx)
                    cursor.close()
                }
            }
        } catch (e: Exception) {
            Utility.commonToast(this, "Error", "Can not process image", 0)
        }

        return path
    }

    //TODO------------------------------------------------------------------------------------CAMERA END

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun getGeoLocation() {
        val locationHelper = LocationHelper(this)
        val location = locationHelper.getCurrentLocation()
        if (isLocationEnabled() && location != null) {
            latitude = location.latitude
            longitude = location.longitude
            openCamera()
            val address = locationHelper.getAddress(latitude, longitude)
            if (address != null) {
                val addressLine = address.getAddressLine(0)
                val city = address.locality
                val state = address.adminArea
                val country = address.countryName
                //Toast.makeText(this, "$city", Toast.LENGTH_LONG).show()
            }
        }
        else {
            Utility.commonToast(this, "Location off", "Turn on your location ", 0)
        }
    }

    //TODO------------------------------------------------------------------------------------Location Starts


    //TODO------------------------------------------------------------------------------------Location Ends


    //TODO------------------------------------------------------------------------------------PERMISSION

    @RequiresApi(Build.VERSION_CODES.O)
    private fun appPermission() {
        try {
            if (!hasPermissions(this, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)

            } else {
                regularTask()
            }
        } catch (e: Exception) {
            val error = e.message

        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (!hasPermissions(
                        this,
                        *SPECIAL_PERMISSIONS
                    ) && Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        SPECIAL_PERMISSIONS,
                        SPECIAL_PERMISSION_CODE
                    )
                } else {
                    regularTask()
                }

            } else {
                // Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                // appPermission()
            }
        } else {

            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                regularTask()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun regularTask() {
        finalFile = File("")
        loadingAnim = Utility.baseLoadingAnimation(this, Dialog(this), "P l e a s e    w a i t")
        user = Utility.getValueByKey(this, "username").toString()

        val attInfo: OfflineAttendance = getTodayAttendance(this)

        if (attInfo != null && attInfo.Latitude!!.toDouble() > 0) {
            offlineAttendanceData(attInfo)
        }
        else {
            getGeoLocation()
        }
    }

    //TODO----------------------------------------------------PERMISSION END

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



}


