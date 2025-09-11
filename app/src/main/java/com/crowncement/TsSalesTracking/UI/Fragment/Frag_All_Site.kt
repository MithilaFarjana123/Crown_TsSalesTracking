package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.SiteItemAdapter
import com.crowncement.TsSalesTracking.data.Adapter.SiteTaggedItemAdapter
import com.crowncement.TsSalesTracking.data.Model.GetBPSiteInfoResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragAllSiteBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import java.util.Locale

class Frag_All_Site : Fragment() {


    private var _binding: FragAllSiteBinding? = null
    private val binding get() = _binding!!

    lateinit var siteList: ArrayList<String>

    var totalnumofsite = 0
    var user = ""
    private var selectedCustomer: String = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    private val permissionCode = 101
    lateinit var other: OtherCustomerChild


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    //  var retailerSetupInfo = ""
    var division = ""
    var district = ""
    var thana = ""
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var otherCustomerLists: ArrayList<GetTSSiteInfoChild>
    lateinit var otherCustomertaggedLists: ArrayList<GetBPSiteInfoResponceChild>

    lateinit var siteitemAdapter: SiteItemAdapter
    lateinit var siteitemtaggedAdapter : SiteTaggedItemAdapter
    lateinit var loadingAnim: Dialog



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragAllSiteBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        // rootView =  inflater.inflate(R.layout.frag_all_site, container, false)
        RetailerViewModelFactory()
        setupViewModel()
        binding.fabAddNewSite.visibility = View.GONE

        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()

        getPreviouslyStoredValue()

        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // prepareLocation()
        // getLastLocation()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
        //  other = convertOtherInfo(requireActivity())!!
        other = convertOtherInfo(requireActivity())!!
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        // loadingAnim.show()
        if(selectedCustomer=="IHBs"){
            binding.fabAddNewSite.visibility = View.VISIBLE
        }else{
            binding.fabAddNewSite.visibility = View.GONE
        }

        //todo
        getOtherCustomerListforTagged(
            other.partyCode.toString(),
            selectedCustomer
        )

        siteList =  ArrayList()
        binding.fabAddNewSite.setOnClickListener {

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            // navController.navigate(R.id.nav_retiler_home)
            navController.navigate(R.id.nav_demo_siteentry)
        }
        binding.searchAllsite.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.isNotEmpty()) {
                    filter(query)
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText)
                }
                return false
            }
        })

    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }



    private fun filter(text: String) {

        if (text.isNotEmpty()) {

           if (otherCustomertaggedLists.size > 0) {
                        val filteredlist = java.util.ArrayList<GetBPSiteInfoResponceChild>()
                        for (item in otherCustomertaggedLists) {
                            if (item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.siteNumber!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.siteAdd!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No site found",
                                R.drawable.error_white,
                                0
                            )
                            binding.rdDemoAllSite.adapter = null
                        } else {

                            prepareOtherListfortagged(binding.rdDemoAllSite, filteredlist, selectedCustomer)

                        }

                    }
           else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No data found",
                            R.drawable.error_white,
                            2
                        )
                    }


        }


    }


    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }


    //TODO LOCATION ----------------------------------------------------------------------------------->

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()

                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude

                        Log.e("Add Customer", "Latitude: $latitude | Longitude:$longitude")

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
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            requestPermissions()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(
                        "Frag Add Cus",
                        "requestDeviceLocationSettings: " + sendEx.message.toString()
                    )
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            return true
        }
        return false
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
            val currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude

            Log.e("New Customer", "latitude: $latitude longitude:  $longitude")

        }
    }

    private fun getLastKnownLocation(): Location? {
        val mLocationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = mLocationManager!!.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return bestLocation
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            permissionCode
        )
    }


    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun getPreviouslyStoredValue() {
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
      //  retailerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()
        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()

    }


    //for tagged site load
    private fun getOtherCustomerListforTagged(
        xparty: String,
        xCode: String
    ) {

            retailerViewModel.GetBPSiteInfoList(xparty)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val otherCustomerList: ArrayList<GetBPSiteInfoResponceChild> =
                                    res.data
                                otherCustomertaggedLists = otherCustomerList
                                totalnumofsite = otherCustomerList.size
                                _binding?.partyname?.setText(xCode+ " name : "+ other.partyName.toString())
                                _binding?.totalMason?.setText(totalnumofsite.toString())


                                _binding?.let { binding ->
                                    prepareOtherListfortagged(
                                        binding.rdDemoAllSite,
                                        otherCustomerList,
                                        xCode
                                    )
                                }
                            }
                            loadingAnim.dismiss()
                        }

                        Status.ERROR -> {

                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            loadingAnim.dismiss()
                        }
                    }
                }


    }


    private fun prepareOtherListfortagged(
        rv: RecyclerView,
        items: ArrayList<GetBPSiteInfoResponceChild>,
        type: String
    ) {

        if (items.size > 0) {
            //   rootView.txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            siteitemtaggedAdapter = SiteTaggedItemAdapter(items, type)
            if(isAdded) {
                val manager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv.layoutManager = manager
                rv.adapter = siteitemtaggedAdapter

                siteitemtaggedAdapter.setOnItemClickListener(object :
                    SiteTaggedItemAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {


//
                        prepareSelectedtaggedSiteObject(items[position], requireActivity())
                        if(selectedCustomer=="IHBs") {
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            // navController.navigate(R.id.nav_retiler_home)
                            navController.navigate(R.id.nav_demo_Site_Home)
                        }
                    }
                })
            }
        } else {
            rv.visibility = View.GONE
            //    rootView.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }


    fun prepareSelectedtaggedSiteObject(info: GetBPSiteInfoResponceChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedtaggedSite", json)
        mPrefs.commit()
    }

    //for allsite load

    private fun getOtherCustomerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xCode: String
    ) {
        retailerViewModel.getSavedSiteList(country, division, district, thana)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val otherCustomerList: ArrayList<GetTSSiteInfoChild> = res.data
                            otherCustomerLists = otherCustomerList
                            prepareOtherList(binding.rdDemoAllSite, otherCustomerList, xCode)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<GetTSSiteInfoChild>,
        type: String
    ) {

        if (items.size > 0) {
         //   rootView.txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            siteitemAdapter = SiteItemAdapter(items, type)

            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = siteitemAdapter

            siteitemAdapter.setOnItemClickListener(object :
                SiteItemAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {

                    prepareSelectedSiteObject(items[position], requireActivity())

                    if(selectedCustomer =="IHBs") {
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        // navController.navigate(R.id.nav_retiler_home)
                        navController.navigate(R.id.nav_demo_Site_Home)
                    }
                }
            })
        } else {
            rv.visibility = View.GONE
        //    rootView.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    fun prepareSelectedSiteObject(info: GetTSSiteInfoChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedSite", json)
        mPrefs.commit()
    }











    ////location





}