package com.crowncement.TsSalesTracking.UI.Receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService

import java.util.*


class GPSCheckingReceiver : BroadcastReceiver() {

    lateinit var mContext: Context

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context

        if (!checkGPSStatus(mContext) &&
            //  (Utility.compareTimeFrame("07:00", "22:00")) &&
            isLocationServiceRunning()
        ) {
            locationAlert(
                "Enable Location",
                "GPS Checking: Please enable location for working this app properly"
            )
        }
    }

    fun checkGPSStatus(context: Context): Boolean {
        val manager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun locationAlert(title: String, message: String) {
        val v = mContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)

        val mPlayer: MediaPlayer = MediaPlayer.create(mContext, R.raw.gps_alert)
        mPlayer.start()

        val notificationManager =
            mContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)
        val intent = Intent(mContext, Act_Splash_Screen::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val largeIcon =
            BitmapFactory.decodeResource(mContext.resources, R.drawable.ic_baseline_near_me)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(mContext, "LOCATION_CHANNEL")
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)
        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.crown_cement_icon)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.crown_cement_icon)
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
            Log.e("TrackingService", "setupChannels:Toker else error")
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        var result = false
        val manager =
            mContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
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


}


