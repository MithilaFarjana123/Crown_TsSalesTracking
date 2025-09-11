package com.crowncement.TsSalesTracking.common

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.*

class LocationHelper (private val context: Context) {

    fun getCurrentLocation(): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if location services are enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // Location services are disabled
            return null
        }

        try {
            val providers: List<String> = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val location: Location? = locationManager.getLastKnownLocation(provider)
                if (location == null) {
                    continue
                }

                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }

            return bestLocation
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return null
    }

    fun getAddress(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                return addresses[0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}