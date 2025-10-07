package com.crowncement.TsSalesTracking.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

import android.content.res.Configuration
import android.content.res.Resources

import android.location.Location
import android.location.LocationManager

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.crowncement.TsSalesTracking.UI.Receiver.ConnectivityReceiver
import com.crowncement.TsSalesTracking.UI.Receiver.GPSCheckingReceiver
import com.crowncement.TsSalesTracking.UI.viewmodel.SplashViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SplashViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.common.Utility.getDeviceTimeZone
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.common.Utility.storeKeyValue
import com.crowncement.TsSalesTracking.data.Model.AppVersionResponse
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.data.Model.OfferInfo
import com.crowncement.TsSalesTracking.databinding.ActSplashScreenBinding
import com.crowncement.TsSalesTracking.databinding.LayoutNoInternetBinding
import com.crowncement.TsSalesTracking.services.broadcast.UploadBroadCastReceiver
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat


import java.util.*

class Act_Splash_Screen : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
       // Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_MEDIA_IMAGES

    )




    private var SPECIAL_PERMISSIONS = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE_LOCATION
       // Manifest.permission.READ_MEDIA_IMAGES

        //    Manifest.permission.POST_NOTIFICATIONS
    )

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var PERMISSION_ALL = 1
    var SPECIAL_PERMISSION_CODE = 2

    private val permissionCode = 101

    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var device : String
    private lateinit var splashViewModel: SplashViewModel

    lateinit var mDatabase: DatabaseReference

    private val TAG = "Act_Splash_Screen"

    lateinit var alertD: AlertDialog

    private lateinit var binding: ActSplashScreenBinding
    private lateinit var noInternetBinding: LayoutNoInternetBinding


    var isUpdateDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActSplashScreenBinding.inflate(layoutInflater)






        noInternetBinding = LayoutNoInternetBinding.inflate(LayoutInflater.from(this))

       /* device = Utility.getValueByKey(this,"deviceId").toString()
        if(device.isEmpty()){
            device = getAndroidDeviceId() + " "+ getDeviceName() + " " + getCurrentDateTime()
            Utility.storeKeyValue(this, "deviceId", device)
        }else{
            device = ""
            Utility.storeKeyValue(this, "deviceId", device)
        }

        */

        device = Utility.getValueByKey(this, "deviceId").toString()

        if (device.isEmpty()) {
            device = getAndroidDeviceId() + " " + getDeviceName() + " " + getCurrentDateTime()
            Utility.storeKeyValue(this, "deviceId", device)
        }



        // setContentView(R.layout.act_splash_screen)
        registerReceiver(
            ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )


        alertD = AlertDialog.Builder(this).create()


        val locPermission: String = getValueByKey(this, "location_dialog")!!
        try {
            if (locPermission == "") {
                locationWarningDialog()


            } else {
                appPermission()
            }
        } catch (e: Exception) {
            appPermission()
        }




    }


    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {

            Utility.noInternetDialog(this)

        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

    override fun onResume() {

        ConnectivityReceiver.connectivityReceiverListener = this

        if (!isLocationEnabled()) {
            requestDeviceLocationSettings()
        }

        super.onResume()
    }

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            appPermission()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        if (getLocationMode(this) != 3) {
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            Toast.makeText(
                                this, "Set location mode to High accuracy", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val intent = Intent("android.location.GPS_ENABLED_CHANGE")
                            intent.putExtra("enabled", true)
                            sendBroadcast(intent)
                        }
                    } else {
                        exception.startResolutionForResult(this, 100)
                        Utility.commonToast(this, "Location Off", "Turn on your location", 0)

                    }

                } catch (sendEx: IntentSender.SendIntentException) {
                    Utility.commonToast(this, "Location Off", "Turn on your location", 0)
                    Log.e(TAG, "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }
    }

    private fun regularTask() {


        setContentView(binding.root)








        //TODO  SET DEFAULT LANGUAGE
        setDefaultLanguage()
        if (checkForInternet(this)) {


            if (isLocationEnabled()) {
                //  startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))
                //  ContextCompat.startForegroundService(this, Intent(this, ForegroundOnlyLocationService::class.java))
                // TODO  GET-----> FCM TOKEN FOR PUSH NOTIFICATION
                getFCMToke()
                SplashViewModelFactory()
                setupViewModel()


                // TODO----> AUTOMATIC LOCATION TRACKING DATA UPLOADING BROADCAST RECEIVER
                startScheduleForAutomaticUpload()


                //todo --- Set current app version
                val cV = getAppVersion()
                binding.txtAppVersionNo.text = cV


                //TODO UPDATE DIALOGUE CHECK
                getLatestAppVersion("CrownTitan", getCurrentDate("yyyy-MM-dd").toString())


            } else {
                requestDeviceLocationSettings()
            }


        } else {


            binding.noInternet.root.visibility = View.VISIBLE


            // img_CrownICon.visibility = View.GONE


        }


        noInternetBinding.btnRefresh.setOnClickListener {
            this.startActivity(Intent(this, Act_Splash_Screen::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun isMyServiceRunning(): Boolean {
        var result = false
        val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
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

    private fun transferActivity() {
        val handler = Handler()
        handler.postDelayed({
            try {

                val userINfo: Data = Utility.getUserInfo(this)!!
                val notificationClick = intent.getStringExtra("send_to")

                if (notificationClick != null && userINfo.userId.toString() != "") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("send_to", notificationClick)
                    startActivity(intent)
                } else {
                    Utility.sendActivity(this, "Act_Login")
                }

            } catch (e: Exception) {
                Log.e(TAG, "transferActivity: " + e.message)
                Utility.sendActivity(this, "Act_Login")
            }

        }, 2000)
    }

    @SuppressLint("ServiceCast")
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }
    }

    private fun appPermission() {
        try {
            if (!hasPermissions(this, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
                // regularTask()

            }
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                // Android 14 (API level 34) and above
//                if (!hasPermissions(this, *SPECIAL_PERMISSIONS)) {
//                    ActivityCompat.requestPermissions(this, SPECIAL_PERMISSIONS, SPECIAL_PERMISSION_CODE)
//                }
//            }

            else {
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



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

               // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (!hasPermissions(this, *SPECIAL_PERMISSIONS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ActivityCompat.requestPermissions(this, SPECIAL_PERMISSIONS, SPECIAL_PERMISSION_CODE)
                    }
            //    }
                /*
                if (!hasPermissions(this, *SPECIAL_PERMISSIONS) &&
                    Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    ActivityCompat.requestPermissions(
                        this, SPECIAL_PERMISSIONS, SPECIAL_PERMISSION_CODE
                    )
                }

                 */

                else {
                    if(grantResults.isNotEmpty() && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                        regularTask()
                    }
                    else{
                        requestPermissions()
                    }

                }

            }
            else {
                // Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                appPermission()
            }
        }



        else {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                regularTask()
            }

        }
    }






    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
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
        }
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES


            ), permissionCode
        )
    }

    @SuppressLint("MissingInflatedId")
    private fun updateDialog(currentV: String, updateV: String, url: String) {
        if (!isUpdateDialogShowing) {
            isUpdateDialogShowing = true
            val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.update_dialog, null)
            val updateDialog = BottomSheetDialog(this)
            Objects.requireNonNull(updateDialog.window)
                ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            // val btnUpdateApp = v.findViewById<Button>(R.id.btnUpdateApp)
         //   val btnUpdatePlayStore = v.findViewById<Button>(R.id.btnUpdatePlayStore)
            val btnUpdateApp = v.findViewById<Button>(com.crowncement.TsSalesTracking.R.id.btnUpdateApp)


            val currentVersion = v.findViewById<TextView>(com.crowncement.TsSalesTracking.R.id.txtCurrent)
            val updatedVersion = v.findViewById<TextView>(com.crowncement.TsSalesTracking.R.id.txtLatest)

            currentVersion.text =  currentV
            updatedVersion.text =  updateV

            updateDialog.setContentView(v)
            updateDialog.setCancelable(false)
            updateDialog.show()

            btnUpdateApp.setOnClickListener {

                if (url != "") {
                    updateDialog.dismiss()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                }
            }

/*
            btnUpdatePlayStore.setOnClickListener {

                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

 */

        }

    }

    private fun getAppVersion(): String {
        val pInfo: PackageInfo = this.packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }

    private fun setupViewModel() {
        splashViewModel =
            ViewModelProviders.of(this, SplashViewModelFactory()).get(SplashViewModel::class.java)
    }

    private fun getLatestAppVersion(appName: String, dateTime: String) {

        splashViewModel.getAppVersion(appName, dateTime)?.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        if (res.code.equals("200") && res.data.size > 0) {
                            getAppVersionInfo(res)
                        }
                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    Utility.getBaseMessage(
                        this,
                        "Error",
                        "Can not communicate with server, please try again after some time",
                        com.crowncement.TsSalesTracking.R.drawable.error_white,
                        0
                    )
                }
            }
        }
    }

    private fun getAppVersionInfo(res: AppVersionResponse) {

        storeKeyValue(this, "download_url", res.data[0].version_url.toString())
        val latestV = res.data[0].versionNo
        val currentV = getAppVersion()
        //Show version name
        //txtAppVersionNo.text = currentV
        if (latestV == currentV) {
            if(!isTimeAutomatic(this)||!getDeviceTimeZone(this)){
                timeWarningDialog()
            }else{
               // saveAttIntoInternalDb(offlineData)
               // loadingAnim.dismiss()
                transferActivity()

            }

        } else {
            updateDialog(currentV, latestV.toString(), res.data[0].version_url.toString())
        }

    }

    private fun getFCMToke() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            storeKeyValue(this, "fcm_token", token)
            Log.e(TAG, "token: $token")
        })
    }

    private fun setDefaultLanguage() {

        val lang = getValueByKey(this, "language")
        if (lang != null) {
            setAppLanguage(this, lang)
            ///crown_icon_bn
        } else {
            //Set default Language
            setAppLanguage(this, "en")
            storeKeyValue(this, "language", "en")
        }
    }

    private fun setAppLanguage(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode!!)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        storeKeyValue(activity, "language", languageCode)

    }

    private fun startScheduleForAutomaticUpload() {
        val intent = Intent(this, UploadBroadCastReceiver::class.java)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= 30) {
            PendingIntent.getBroadcast(
                this@Act_Splash_Screen,
                234324243,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(
                this@Act_Splash_Screen, 234324243, intent, PendingIntent.FLAG_IMMUTABLE
            )
        }
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        SystemClock.elapsedRealtime()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 60000, pendingIntent
        )
    }


    fun getLocationMode(context: Context?): Int {
        return Settings.Secure.getInt(
            this.contentResolver, Settings.Secure.LOCATION_MODE
        )
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGPSON = false

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                if (getLocationMode(this) != 3) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                } else {
                    isGPSON =
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                        )
                }
            } else {
                isGPSON =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER
                    )
            }
        } catch (e: Exception) {

        }

        return isGPSON
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED


        ) {
            return true
        }
        return false
    }

    private fun locationWarningDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val promptView: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.location_permission_dialog, null)
        val yes = promptView.findViewById<TextView>(com.crowncement.TsSalesTracking.R.id.txtYes)
        val no = promptView.findViewById<TextView>(com.crowncement.TsSalesTracking.R.id.txtNo)
        no.setOnClickListener { v: View? ->
            alertD.dismiss()
            alertD.setCancelable(false)
            finish()
        }
        yes.setOnClickListener { v: View? ->
            storeKeyValue(this, "location_dialog", "SHOWN")
            alertD.dismiss()
            // appPermission()
            Utility.sendActivity(this, "Act_Splash_Screen")
        }
        alertD.setView(promptView)
        alertD.setCancelable(false)
        if (!alertD.isShowing) {
            alertD.show()
        }
    }


    //--------------------------------------------------------------------------------------------------
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()

                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = com.google.android.gms.maps.model.LatLng(
                            location.latitude, location.longitude
                        )
                        latitude = location.latitude
                        longitude = location.longitude


                    }
                }
            }

        } else {
            requestPermissions()
        }
    }

    fun appInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }

    private fun noticeTableSetup() {

        val images: ArrayList<String> = ArrayList()
        images.add("value-1")
        images.add("value-2")
        images.add("value-3")

        mDatabase = FirebaseDatabase.getInstance().getReference("Db_OfferInfo")
        val offerDB = OfferInfo(
            "Offer Title",
            "This is details",
            getCurrentDate("EEE , dd.MMM.yyyy HH:mm a"),
            "Mohibul",
            images
        )

        mDatabase.child(("Offer-" + getCurrentDate("EEE , dd-MMM-yyyy (HH:mm a)")))
            .setValue(offerDB).addOnCompleteListener { task ->

                Toast.makeText(this, "Offer Table Created", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

//    fun startAlertAtParticularTime() {
//
//        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval
//        val intent = Intent(this, DailyLocationStartReceiver::class.java)
//        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= 30) {
//            PendingIntent.getBroadcast(
//                this.applicationContext,
//                280192,
//                intent,
//                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        } else {
//            PendingIntent.getBroadcast(
//                this.applicationContext,
//                280192,
//                intent,
//                0
//            )
//        }
//
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar[Calendar.HOUR_OF_DAY] = 7
//        calendar[Calendar.MINUTE] = 10
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmManager.setInexactRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            // AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//            pendingIntent
//        )
//        Toast.makeText(
//            this, "Checking Location Service",
//            Toast.LENGTH_SHORT
//        ).show()
//
//    }

    private fun prepareLocation() {
        val ai: ApplicationInfo = this.packageManager.getApplicationInfo(
            this.packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun startGPSChangeListener() {
        val intent = Intent(this, GPSCheckingReceiver::class.java)
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(Act_SplashScreen.this, 234324243, intent, 0);
        val pendingIntent = PendingIntent.getBroadcast(
            this@Act_Splash_Screen,
            234324243,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        SystemClock.elapsedRealtime()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 60000, pendingIntent
        )
    }

    private fun startBackgroundLocationService() {

        val username: String = getValueByKey(this, "username")!!

        if (username.isNotEmpty() && !isMyServiceRunning()) {


            val intent = Intent(this@Act_Splash_Screen, TrackingService::class.java)
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            //  ContextCompat.startForegroundService(this@Act_Splash_Screen, intent)

//            ContextCompat.startForegroundService(
//                this@Act_Splash_Screen, Intent(this@Act_Splash_Screen, TrackingService::class.java))
//            //Foreground Location service
            //  StartScheduleForFirebaseRateVersion();

//            try {
//                //Foreground Location service
//                val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
//                for (service in manager.getRunningServices(Int.MAX_VALUE)) {
//                    if (TrackingService::class.java.name != service.service.className) {
//
//                        //   startService(Intent(applicationContext, TrackingService::class.java))
//
////                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
////                            applicationContext.startForegroundService(
////                                Intent(
////                                    this@Act_Splash_Screen,
////                                    TrackingService::class.java
////                                )
////                            )
////                        } else {
////                            startService(
////                                Intent(
////                                    this@Act_Splash_Screen,
////                                    TrackingService::class.java
////                                )
////                            )
////                        }
//                    } else {
//                        Log.d(TAG, "startBackgroundLocationService: Service Running")
//                    }
//                }
//            } catch (e: Exception) {
            //   Log.e(TAG, "startBackgroundLocationService: " + e.message.toString())
            //  }

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

        val v: View = layoutInflater.inflate(com.crowncement.TsSalesTracking.R.layout.time_warning, null)
        val warningDialog = Dialog(v.context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        warningDialog.window!!.statusBarColor = ContextCompat.getColor(v.context, com.crowncement.TsSalesTracking.R.color.reject)
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


    private fun getAndroidDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }


    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.capitalize()
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }



    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

}