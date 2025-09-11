package com.crowncement.TsSalesTracking.services.broadcast

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startForegroundService
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService


class DailyLocationStartReceiver : BroadcastReceiver() {

    private val TAG = "DailyLocationStartRecei"
    lateinit var mContext: Context
    override fun onReceive(p0: Context?, p1: Intent?) {

        mContext = p0!!

        val serviceIntent = Intent(mContext, TrackingService::class.java)

        if (!isMyServiceRunning() && (Utility.compareTimeFrame("07:00", "22:00"))) {
            try {

                val vibrate = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrate.vibrate(2000)


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   startForegroundService(mContext, serviceIntent)
                } else {
                    mContext.startService(serviceIntent)
                }

                Utility.displayNotification(
                    mContext,
                    "Location Service Started For Today",
                    "Daily Location Servic"
                )

            } catch (e: Exception) {
                Log.e(TAG, "ERROR Service restarted: " + e.message)
            }
        }

    }


    private fun isMyServiceRunning(): Boolean {
        val manager = mContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TrackingService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


}