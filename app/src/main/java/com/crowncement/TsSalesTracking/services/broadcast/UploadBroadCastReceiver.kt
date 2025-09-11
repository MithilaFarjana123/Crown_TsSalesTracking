package com.crowncement.TsSalesTracking.services.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.crowncement.TsSalesTracking.UI.viewmodel.CommonViewModel
import com.crowncement.TsSalesTracking.data.API.GetDataService
import com.crowncement.TsSalesTracking.data.API.RetrofitClientInstance
import com.crowncement.TsSalesTracking.data.InternalDB.AppRoomDatabase
import com.crowncement.TsSalesTracking.data.Model.LocationTracking
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

lateinit var commonViewModel: CommonViewModel
lateinit var mContext: Context
lateinit var mActivity: Activity

class UploadBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(contex: Context?, intent: Intent?) {
        mContext = contex!!

        if (checkForInternet(mContext!!)) {
            getLocation()
        } else {
            Log.e("BroadCastReceiver", "No Internet: ")
        }
    }

    private fun getLocation() {
        val locations = getLocationData()
        if (locations.size > 0) {
            // for (item in locations) {
            //  updateOnlineStatus(item)
            updateOnlineStatus(locations[0])

            //   }
        }
    }

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
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun getLocationData(): ArrayList<LocationTracking> {

        return AppRoomDatabase
            .getDatabase(mContext!!)
            .locationTrackingDao()
            .getAllLocationInfo() as ArrayList<LocationTracking>
    }

    private fun updateOnlineStatus(location: LocationTracking) {
        try {

            val username =
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    location.CreateBy.toString()
                )
            val time =
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    location.CreateDate.toString()
                )
            val lat = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                location.Latitude.toString()
            )
            val lon = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                location.Longitude.toString()
            )

            val service: GetDataService =
                RetrofitClientInstance.upload_location_retrofitInstance.create(
                    GetDataService::class.java
                )
            service.uploadOfflineLocationData(username, time, lat, lon)
                ?.toObservable()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response ->
                        if (response.code == "200") {
                            //Toast.makeText(mContext,"Location uploaded",Toast.LENGTH_LONG).show()
                            Log.d("UploadBroadcastReceiver", "onStatusSuccess: location Uploaded")
                            AppRoomDatabase
                                .getDatabase(mContext!!)
                                .locationTrackingDao()
                                .deleteLocationBySID(location.SID)
                            getLocation()
                        }
                    },
                    { error -> onStatusFailed(error) },
                )

        } catch (e: Exception) {
            Log.e("UploadBroadcast", "UploadLocation: " + e.message.toString())
        }
    }

    private fun onStatusFailed(error: Throwable) {
        Log.e("UploadBroadcast", "onStatusFailed: " + error.message.toString())
    }


}
