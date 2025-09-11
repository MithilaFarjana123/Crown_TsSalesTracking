package com.crowncement.TsSalesTracking.services.locationTracking

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class TravelServiceRepeater : BroadcastReceiver() {

    val TAG = "LocationServiceRepeater"
    private lateinit var mContext: Context

    override fun onReceive(context: Context, intent: Intent?) {
        mContext = context

        val serviceIntent = Intent(context, TravelTrackingService::class.java)

        if (!isMyServiceRunning()) {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, serviceIntent)

                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "ERROR Service restarted: " + e.message)
            }
        }
    }

    private fun isMyServiceRunning(): Boolean {
        val manager = mContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TravelTrackingService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

}