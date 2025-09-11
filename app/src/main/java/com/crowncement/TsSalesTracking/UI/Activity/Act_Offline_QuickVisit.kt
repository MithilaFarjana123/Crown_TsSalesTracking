package com.crowncement.TsSalesTracking.UI.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.LocationHelper
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.InternalDB.DatabaseSingleton
import com.crowncement.TsSalesTracking.data.Model.OfflineQuickVisit
import com.crowncement.TsSalesTracking.databinding.ActOfflineQuickVisitBinding


import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class Act_Offline_QuickVisit : AppCompatActivity() {

    lateinit var clientTypes: ArrayList<String>

    private lateinit var binding: ActOfflineQuickVisitBinding


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

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var selectedClientType = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.act_offline_quick_visit)


        binding = ActOfflineQuickVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPermission()


    }

    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        clientTypes.add("Select Client Type")
        clientTypes.add("Dealer")
        clientTypes.add("Retailer")
        clientTypes.add("Engineer")
        clientTypes.add("IHBs")
        clientTypes.add("Mason")
        clientTypes.add("Contractor")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    private fun confirmQuick() {
        val v: View = layoutInflater.inflate(R.layout.confirmation_offline_quickvisit, null)
        val dialog = BottomSheetDialog(this)
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnQCallCancel: MaterialButton = v.findViewById(R.id.btnQCallCancel)
        val btnQVisit: MaterialButton = v.findViewById(R.id.btnQVisit)
        val QCancel: TextView = v.findViewById(R.id.QCancel)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        QCancel.setOnClickListener {
            dialog.hide()
        }

        btnQCallCancel.setOnClickListener {
            dialog.hide()
        }

        btnQVisit.setOnClickListener {
            dialog.hide()
            val empI = Utility.getValueByKey(this, "username").toString()

            val prepareOfflineData = OfflineQuickVisit(
                0,
                selectedClientType,
                binding.txtCName.text.toString(),
                latitude,
                longitude,
                empI,
                Utility.getCurrentDate("yyyy-MM-dd"),
                Utility.getCurrentDate("HH:mm:ss"),
                binding.txtCRemarks.text.toString()
            )
            saveOfflineQuickVisit(prepareOfflineData)


            selectedClientType=""
            binding.txtCName.setText("")
            binding.txtCRemarks.setText("")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveOfflineQuickVisit(quickVisit: OfflineQuickVisit) {
        GlobalScope.launch {
            DatabaseSingleton.getAppDatabase(applicationContext).offlineQuickVisitDao()
                .insert(quickVisit)

            Utility.getBaseMessage(
                this@Act_Offline_QuickVisit,
                "Successful",
                "your offline quick visit has been saved",
                R.drawable.ic_checked_green,
                1
            )

        }
    }


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
                Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                regularTask()
            }
        } else {

            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                regularTask()
            }
        }
    }

    private fun getGeoLocation() {
        val locationHelper = LocationHelper(this)
        val location = locationHelper.getCurrentLocation()
        if (isLocationEnabled() && location != null) {
            latitude = location.latitude
            longitude = location.longitude

        } else {
            Utility.commonToast(this, "Location off", "Turn on your location ", 0)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun regularTask() {

        val dateMonth = Utility.getCurrentDate("EEE, MMM dd yyyy").toString()
        binding.txtQuickTime.text = dateMonth

        loadClientType(binding.acOfflineClient)

        val time = Utility.getCurrentDate("hh:mm a").toString()
        binding.txtQVisitingTime.text = time

        getGeoLocation()

        binding.acOfflineClient.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {
                selectedClientType = clientTypes[position]
            } else {
                selectedClientType = ""
            }
        }

        binding.btnOfflineQuickVisit.setOnClickListener {

            if (uiValidation()) {
                confirmQuick()
            }
        }
    }

    private fun uiValidation(): Boolean {
        var res = false
        binding.layoutCType.error = null
        binding.layoutCName.error = null

        binding.layoutCType.isErrorEnabled = false
        binding.layoutCName.isErrorEnabled = false

        if (selectedClientType == "") {
            binding.layoutCType.error = "Select client type"
            binding.layoutCType.isErrorEnabled = true
            res = false
        } else if (binding.txtCName.text.toString().isEmpty()) {
            binding.layoutCName.error = "Provide client name"
            binding.layoutCName.isErrorEnabled = true
            res = false
        } else if (latitude == 0.0) {

            Utility.commonToast(this, "Error", "Location not found", 0)
            res = false
        } else {
            res = true
        }

        return res
    }
}