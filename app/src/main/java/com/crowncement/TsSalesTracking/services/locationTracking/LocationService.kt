package com.crowncement.TsSalesTracking.services.locationTracking

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.common.StaticConfiguration.locationTrackingChannel
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.data.InternalDB.DatabaseSingleton
import com.crowncement.TsSalesTracking.data.Model.LocationTracking
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class LocationService : Service() {
    private val TAG = "LocationService"
    private lateinit var currentLocation: LatLng
    private lateinit var binder: LocationServiceBinder


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude: String? = null
    var longitude: String? = null

    private val permissionCode = 101
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!CheckLocation()) {
            Push_Notification("Enable GPS", "Please turn on your location", "Act_Splash_Screen")
        } else {
            startForegroundService()

            getLastLocation()
        }

        return START_NOT_STICKY
       // return START_STICKY
    }

//--------------------------------------------------------------------Get Location

    // Get current location
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()

                }
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        Log.e(
                            TAG,
                            "getLastLocation: Latitude" + latitude + "Longitude: " + longitude
                        )

                        val locationPoint = LocationTracking(
                            0,
                            latitude!!.toDouble(),
                            longitude!!.toDouble(),
                            "admin",
                            getCurrentDate("EEE, dd MMM yyyy hh:mm:ss a")
                        )
                        saveLocationIntoInternalDb(locationPoint)
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

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->

            getLastLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {

                try {
                    getLastLocation()

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(TAG, "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            applicationContext as Activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionCode
        )
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            latitude = mLastLocation.latitude.toString()
            longitude = mLastLocation.longitude.toString()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

//--------------------------------------------------------------------


    private fun Push_Notification(title: String, message: String, className: String) {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)
        lateinit var intent: Intent
        try {
            val cls = Class.forName("com.crowncement.tecnicalsupport.ui.Activity.$className")
            intent = Intent(applicationContext, cls)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val largeIcon = BitmapFactory.decodeResource(resources, com.crowncement.TsSalesTracking.R.mipmap.ic_launcher)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "LOCATION_CHANNEL")
                .setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setAutoCancel(false)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSound(notificationSoundUri)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent)
        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.ic_launcher)
            // notificationBuilder.setColor(getResources().getColor(R.color.colorAftab));
        } else {
            notificationBuilder.setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.ic_launcher)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }


    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to device notification"
        val adminChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            adminChannel = NotificationChannel(
                "LOCATION_CHANNEL",
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            adminChannel.description = adminChannelDescription
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            adminChannel.enableVibration(true)
            adminChannel.setShowBadge(true)
            notificationManager?.createNotificationChannel(adminChannel)
        } else {
            Log.e(TAG, "setupChannels:Toker else error")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveLocationIntoInternalDb(item: LocationTracking) {
        GlobalScope.launch {
            DatabaseSingleton.getAppDatabase(applicationContext).locationTrackingDao()
                .insert(item)
        }
    }

    private fun CheckLocation(): Boolean {
        val lm = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            Log.e(TAG, "CheckLocation: " + ex.message)
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Log.e(TAG, "CheckLocation: " + ex.message)
        }
        return gps_enabled || network_enabled
    }

    private fun startForegroundService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createNotificationChannel("123", "locationservice")
        } else {
            val intent = Intent()
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

//            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            val builder = NotificationCompat.Builder(this)
            val bigTextStyle = NotificationCompat.BigTextStyle()
            bigTextStyle.setBigContentTitle("Crown Cement")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bigTextStyle.bigText(getCurrentDate("dd MMM yyyy"))
            } else {
                bigTextStyle.bigText("All Right Reserved By MI Cement")
            }
            // Set big text style.
            builder.setStyle(bigTextStyle)
            builder.setWhen(System.currentTimeMillis())
            builder.setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.ic_launcher)
            val largeIconBitmap = BitmapFactory.decodeResource(resources, com.crowncement.TsSalesTracking.R.drawable.ic_launcher)
            builder.setLargeIcon(largeIconBitmap)
            builder.priority = Notification.PRIORITY_MAX
            builder.setFullScreenIntent(pendingIntent, true)

            // Build the notification.
            val notification = builder.build()
            startForeground(locationTrackingChannel, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val resultIntent = Intent(this, Act_Splash_Screen::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        //   PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
      //  val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val largeIcon = BitmapFactory.decodeResource(resources, com.crowncement.TsSalesTracking.R.drawable.ic_cement)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.crown_cement_icon)
            notificationBuilder.color = resources.getColor(com.crowncement.TsSalesTracking.R.color.crown_icon_color)
        } else {
            notificationBuilder.setSmallIcon(com.crowncement.TsSalesTracking.R.drawable.circle_yellow)
        }
        val notification = notificationBuilder
            .setOngoing(true)
            .setLargeIcon(largeIcon)
           // .setColor(R.color.crown_icon_color)

            //  .setContentTitle("Today is " + getCurrentDate("EEE, dd MMM yyyy"))
            .setContentTitle("ক্রাউন সিমেন্ট, রপ্তানিতে নাম্বার ওয়ান")
            //  .setContentText("All right reserved by Crown cement IT")
            .setContentText("অ্যাাপটি ক্রাউন সিমেন্ট আইটি দ্বারা সংরক্ষিত")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(resultPendingIntent) //intent
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
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
        notificationManager.notify(locationTrackingChannel, notificationBuilder.build())
        startForeground(locationTrackingChannel, notification)
    }

    class LocationServiceBinder : Binder() {
        val service: LocationServiceBinder
            get() = this@LocationServiceBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!CheckLocation()) {
            Push_Notification("Enable GPS", "Please turn on your location", "Act_Splash_Screen")
        } else {
            restartService()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!CheckLocation()) {
            Push_Notification("Enable GPS", "Please turn on your location", "Act_Splash_Screen")
        } else {
            restartService()
        }
    }

    private fun restartService() {
        if (!isMyServiceRunning()) {
            val intent = Intent(applicationContext, LocationServiceRepeater::class.java)
            val pIntent = PendingIntent.getBroadcast(
                applicationContext,
                12345,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarm = this.getSystemService(ALARM_SERVICE) as AlarmManager
            alarm.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                1000,
                pIntent
            )
        }
    }

    private fun isMyServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


}




