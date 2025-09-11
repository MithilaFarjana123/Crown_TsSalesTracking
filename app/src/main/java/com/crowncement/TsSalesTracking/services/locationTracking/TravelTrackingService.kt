package com.crowncement.TsSalesTracking.services.locationTracking

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.InternalDB.DatabaseSingleton
import com.crowncement.TsSalesTracking.data.Model.Att
import com.crowncement.TsSalesTracking.data.Model.LocationTracking
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class TravelTrackingService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {


    private var isRunning = false

    private var currentLocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationManager: LocationManager? = null

    private var CHANNEL_ID = "Travel_Channel"
    private var wakelock: PowerManager.WakeLock? = null
    internal var requestID = 10001
    private val fusedLocationProviderApi = LocationServices.FusedLocationApi
    private var thread: ContinousThread? = null

    private val SERVICE_INTERVAL: Long = 20000
    private val SERVICE_FASTEST: Long = 14000

    override fun onCreate() {
        super.onCreate()

        startForeground()

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tour:wakelock")
        thread = ContinousThread()
        init()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY

    }


//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val isRestarted = intent == null // Intent is null if the service restarts
//        if (isRestarted) {
//            stopSelf() // Stop the service if restarted automatically
//            return START_NOT_STICKY
//        }
//        return START_STICKY // Service runs only when explicitly started
//    }


    private fun init() {
        wakelock!!.acquire()

        if (!isRunning) {
            isRunning = true
            thread!!.start()
        }

        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.interval = SERVICE_INTERVAL
        locationRequest!!.setFastestInterval(SERVICE_FASTEST)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        try {
            googleApiClient!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startForeground() {


        val tourInfo = applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
        ).getString("Tour", "")

        var title=""
        var info=""

        var message = "Your journey has been started on"
        if (tourInfo!="") {
             title= tourInfo!!.split("|")[0]
             info=tourInfo.split("|")[1]
            message+= " $info ."
        }
        //val attStatus: Att = Utility.getAttInfo(requireActivity())!!
        val attStatus = applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
        ).getString("AttInfo", "")
        val gson = Gson()
        val attInfo: Att =gson.fromJson(attStatus, Att::class.java)

        if(attInfo.tour_no!=""){
            title=attInfo.tour_no.toString()
        }



        val notificationIntent = Intent(this, Act_Splash_Screen::class.java)

        val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(applicationContext.packageName)) {
            notificationIntent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        } else {
            notificationIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            notificationIntent.data = Uri.parse("package:${applicationContext.packageName}")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            val channelName = "TourChannel"
            val chan = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_travel)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.crown_cement_icon)
            .setLargeIcon(largeIcon)
            .setContentTitle("Your tour is running: Tour No: $title")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            Log.v("MediumApp", location.accuracy.toString())
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) === PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderApi!!.requestLocationUpdates(
                googleApiClient!!,
                locationRequest!!,
                locationListener
            )
        } else {

            locationAlert("Enable Location", "Please turn on your location", "")

        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    internal inner class ContinousThread : Thread() {
        var i: Long = 0
        override fun run() {
            while (isRunning) {
                try {
                 //   sleep(15 * 60000)
                    sleep(30 * 60000)

                    sendLocationToServer()
                    i++

                } catch (e: InterruptedException) {
                    isRunning = false
                }
            }
        }

        @SuppressLint("MissingPermission")
        private fun getLastKnownLocation(): Location? {
            val providers = locationManager!!.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val location = locationManager!!.getLastKnownLocation(provider) ?: continue

                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }
            return bestLocation
        }

        private fun sendLocationToServer() {

            if (!isLocationEnabled()) {

                locationAlert(
                    "Enable Location",
                    "Tracking Service: You need to enable device location for working this app properly",
                    "Act_Splash_Screen"
                )
            } else {
                try {
                    val location = getLastKnownLocation()

                    Log.wtf(
                        "TrackingService",
                        "Latitude:" + location!!.latitude.toString() +
                                "\nLong" + location.longitude.toString()
                    )

                    Log.d("accuracy", currentLocation?.accuracy.toString())


                    val user = applicationContext.getSharedPreferences(
                        applicationContext.packageName,
                        Context.MODE_PRIVATE
                    ).getString("username", "")


                    val locationPoint = LocationTracking(
                        0,
                        location.latitude,
                        location.longitude,
                        user,
                        Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                    )

                    //TODO SAVE LOCATION DATA TO INTERNAL DATABASE IF TIME BETWEEN 7:00AM-10:00PM

                    // if (user!!.isNotEmpty() && Utility.compareTimeFrame("07:00", "22:00")) {
                    if (user!!.isNotEmpty()) {
                        saveLocationIntoInternalDb(locationPoint)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveLocationIntoInternalDb(item: LocationTracking) {
        GlobalScope.launch {
            DatabaseSingleton.getAppDatabase(applicationContext).locationTrackingDao().insert(item)
            Log.e("TravelTrackingService", "saveLocationIntoInternalDb:")
        }
    }

    private fun locationAlert(title: String, message: String, className: String) {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)
        val mPlayer: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.gps_alert)
        mPlayer.start()
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)
        var intent: Intent? = null
        try {
            val cls = Class.forName("com.crowncement.salesforceapp.ui.Activity.$className")
            intent = Intent(this, Act_Splash_Screen::class.java)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_near_me)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "Tour_LOCATION_CHANNEL")
            .setSmallIcon(R.drawable.crown_cement_icon)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(notificationSoundUri)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(pendingIntent)
        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
            // notificationBuilder.setColor(getResources().getColor(R.color.colorAftab));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "Tour notification"
        val adminChannelDescription = "Device to device notification"
        val adminChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            adminChannel = NotificationChannel(
                "TRAVEL_LOCATION_CHANNEL",
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
            Log.e("TrackingService", "setupChannels:Toker else error")
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {

        val restartServiceIntent =
            Intent(applicationContext, TravelServiceRepeater::class.java).also {
                it.setPackage(packageName)
            }
        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                1,
                restartServiceIntent,
                //  PendingIntent.FLAG_IMMUTABLE
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        applicationContext.getSystemService(Context.ALARM_SERVICE)

        val alarmService: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        )

//todo privious
        /*
        val isServiceRunning = applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
        ).getString("Is_Travel_Tracking_Running", "")

        if (isServiceRunning == "YES") {



            val restartServiceIntent =
                Intent(applicationContext, TravelServiceRepeater::class.java).also {
                    it.setPackage(packageName)
                }
            val restartServicePendingIntent: PendingIntent =
                PendingIntent.getService(
                    this,
                    1,
                    restartServiceIntent,
                    PendingIntent.FLAG_IMMUTABLE
                  //  PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            applicationContext.getSystemService(Context.ALARM_SERVICE)

            val alarmService: AlarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent
            )

        }

         */
    }

    override fun onDestroy() {

        val isServiceRunning = applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
        ).getString("Is_Travel_Tracking_Running", "")

        if (isServiceRunning == "YES") {
            val restartServiceIntent =
                Intent(applicationContext, TravelServiceRepeater::class.java).also {
                    it.setPackage(packageName)
                }
            val restartServicePendingIntent: PendingIntent =
                PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE)
            applicationContext.getSystemService(Context.ALARM_SERVICE)
            val alarmService: AlarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent
            )
        }
    }
}