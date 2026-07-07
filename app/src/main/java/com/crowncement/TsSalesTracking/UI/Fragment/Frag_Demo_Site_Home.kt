package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.data.Model.GetBPSiteInfoResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetSiteVrfInfo
import com.crowncement.TsSalesTracking.data.Model.GetSiteVrfInfoChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.LatLngList
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.TSQuickVisitSaveInfo
import com.crowncement.TsSalesTracking.databinding.FragDemoSiteHomeBinding
import com.crowncement.TsSalesTracking.databinding.SiteProgressConfBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class Frag_Demo_Site_Home : Fragment() {

    //private lateinit var rootView: View

    private var _binding: FragDemoSiteHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "Frag_Demo_Site_Home"

    lateinit var siteinfo: GetTSSiteInfoChild
    lateinit var pointList: ArrayList<LatLngList>


    var type = ""
    private var SiteCode: String = ""
    var alreadyAddedImageURL = ""
    var user = ""
    private var selectedCustomer: String = ""
    var currentPhotoPath = ""
    lateinit var finalFile: File
    lateinit var sitefinalFile : File
    var retailerSetupInfo = ""
    var division = ""
    var district = ""
    var thana = ""
    lateinit var other: OtherCustomerChild

    // var sitenum = ""
    var siteNumber = ""

    val radius = 150.0 // in meters

    lateinit var retailerViewModel: RetailerViewModel

    lateinit var loadingAnim: Dialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    private var siteSavedlatitude: Double = 0.0
    private var siteSavedlongitude: Double = 0.0

    private val permissionCode = 101

    private var circle: Circle? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val Vis_PIC_CAMERA_REQ_CODE = 2
    lateinit var cardFile: ArrayList<File>

    var visitingsitePhotoPath = ""
//    private val REQUEST_CALL_PERMISSION = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragDemoSiteHomeBinding.inflate(inflater, container, false)


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        //  rootView = inflater.inflate(R.layout.frag_demo_site_home, container, false)
        RetailerViewModelFactory()

        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        prepareLocation()
        getLastLocation()

        getPreviouslyStoredValue()
        cardFile = ArrayList()


        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
      //  getOtherCustomerList("Bangladesh",division,district,"","")




        type = Utility.getValueByKey(requireActivity(), "slected_party_type").toString()
        siteinfo = convertOtherSiteInfo(requireActivity())!!
        SiteCode = siteinfo.siteNumber.toString()
        siteSavedlatitude = siteinfo.partyLat.toString().toDouble()
        siteSavedlongitude = siteinfo.partyLon.toString().toDouble()




        alreadyAddedImageURL = siteinfo.siteImage.toString()



        prepareBasicInfo()

        getOtherCustomerListforTagged(siteinfo.siteNumber.toString(),"")
        getSiteVrfInfo(siteinfo.siteNumber.toString())
     ///////   siteNumber


        onClickListener()

        MapsInitializer.initialize(requireContext())


        loadMap()

    }


    override fun onPause() {
        super.onPause()

        prepareLocation()
        getLastLocation()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    //todo for site progressimg

    private fun siteProgressImg() {
        val v: View = layoutInflater.inflate(R.layout.open_siteprogressimg, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btntakepicture = v.findViewById<Button>(R.id.btntakepicture)
        val btncancle = v.findViewById<Button>(R.id.btncancle)


        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()


        btncancle.setOnClickListener { view1: View? ->
            dialog.dismiss()

            //   val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            //  navController.navigate(R.id.nav_Live_Attendance)
        }

        btntakepicture.setOnClickListener { view1: View? ->
            dialog.dismiss()

        }

    }


    //todo end


     //todo 150m xheck for site varification
     fun exactLocationRestrictionSitevarification(destinationLat:Double,destinationLon:Double,profile: Data){
         getLastLocations()
         val distance=getDistance(destinationLat,destinationLon,latitude,longitude)
         val disInMeter= String.format("%.2f", distance)


         if(siteinfo.siteImage.toString()!=""){
             if(distance<=profile.fence_distance){
                 binding.txtDestination.visibility=View.GONE

                   val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                   navController.navigate(R.id.nav_teamlead_Visit)

          //       QuickVisitDialog()

                 //addQuickVisit(selectedCustomer)
             }
             else{
                 binding.txtDestination.visibility=View.VISIBLE
                 val mess="Your are $disInMeter meter away from the destination current location is $latitude,$longitude, please go to the $selectedCustomer ($SiteCode) closest location within ${profile.fence_distance} meter and try again"
                 binding.txtDestination.text= mess
                 val showDialog:Dialog= distanceWarning(requireActivity(),Dialog(requireContext()),mess)
                 showDialog.show()

             }
         }else{

             Utility.getBaseMessage(requireActivity(),
                 "Warning ",
                 "Please add site image first then try to verify. ",
                 R.drawable.error_white,
                 0
             )

         }

     }



    //todo for 150m check

    fun exactLocationRestriction(destinationLat:Double,destinationLon:Double,profile: Data){
        getLastLocations()
        val distance=getDistance(destinationLat,destinationLon,latitude,longitude)
        val disInMeter= String.format("%.2f", distance)


        if(siteinfo.siteImage.toString()!=""){
            if(distance<=profile.fence_distance){
                binding.txtDestination.visibility=View.GONE

                QuickVisitDialog()

                //addQuickVisit(selectedCustomer)
            }

            else{
                binding.txtDestination.visibility=View.VISIBLE
                val mess="Your are $disInMeter meter away from the destination current location is $latitude,$longitude, please go to the $selectedCustomer ($SiteCode) closest location within ${profile.fence_distance} meter and try again"
                binding.txtDestination.text= mess
                val showDialog:Dialog= distanceWarning(requireActivity(),Dialog(requireContext()),mess)
                showDialog.show()

            }

        }else{

            Utility.getBaseMessage(requireActivity(),
                "Warning ",
                "Please add site image first then try to give quick visit. ",
                R.drawable.error_white,
                0
            )

        }


    }


    fun distanceWarning(activity: Activity, dialog: Dialog, mess: String): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate(R.layout.distance_warning_dialog, null, false)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val message = mView.findViewById<TextView>(R.id.txtDefaultMessage1)
        message.text = mess
        dialog.setContentView(mView)
        dialog.setCanceledOnTouchOutside(true)
        val window = dialog.window
        Objects.requireNonNull(window)!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window.setGravity(Gravity.CENTER)
        //dialog.show();
        return dialog
    }


    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // radius of Earth in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }



    //end

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        if (siteinfo.partyLat!!.toDouble() != 0.0) {

            mapFragment?.getMapAsync { gmap ->
                gmap.mapType = GoogleMap.MAP_TYPE_NORMAL

                gmap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(LatLng(siteinfo.partyLat!!.toDouble(), siteinfo.partyLon!!.toDouble()))
                    .zoom(70f)
                    .tilt(45f)
                    .build()

                gmap.addMarker(
                    MarkerOptions()
                        .icon(BitmapFromVector(requireContext(), R.drawable.ic_location_flag))
                        .position(
                            LatLng(
                                siteinfo.partyLat!!.toDouble(),
                                siteinfo.partyLon!!.toDouble()
                            )
                        )
                        .title(siteinfo.siteAdd)
                )

                gmap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)
                gmap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            siteinfo.partyLat!!.toDouble(),
                            siteinfo.partyLon!!.toDouble()
                        ), 17F
                    )
                )
                drawCircle(
                    LatLng(siteinfo.partyLat!!.toDouble(), siteinfo.partyLon!!.toDouble()),
                    gmap
                )

            }
        }
        else{
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

            mapFragment.requireView().visibility = View.GONE
        }




    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun drawCircle(latlon: LatLng, googleMap: GoogleMap) {
        val circleOptions = CircleOptions()
            .center(latlon)
            .radius(30.0)
            .strokeWidth(1.0f)
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.titan_theme_color))
            .fillColor(ContextCompat.getColor(requireContext(), R.color.theme_light))
        circle?.remove() // Remove old circle.
        circle = googleMap.addCircle(circleOptions) // Draw new circle.
    }


    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    private fun prepareBasicInfo() {

        when (selectedCustomer) {

            "Retailer" -> {
                /*
                setBasicInfo(
                    retailer.partyName.toString(),
                    "Proprietor: " + retailer.ownerName.toString(),
                    retailer.partyAdd1.toString(),
                    retailer.partyMobile.toString(),
                    "Party Code: " + retailer.partyCode.toString()
                )
                Log.d(TAG, "------------------------------------------------------\n")
                Log.e(TAG, "Retailer Image: " + retailer.partyImage.toString())
                Log.d(TAG, "------------------------------------------------------\n")

                if (retailer.partyImage.toString() != "") {
                    rootView.imgVerified.visibility = View.VISIBLE
                    Glide.with(requireActivity())
                        .load(Endpoint.IMAGE_BASE_URL + retailer.partyImage)
                        .error(com.google.android.gms.location.R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .into(rootView.imgRetailerHomeCover)
                }

                 */
            }

            "Dealer" -> {

                /*
                    setBasicInfo(
                        dealer.dealerName.toString(),
                        "",
                        dealer.dealerAdd1.toString(),
                        dealer.dealerMobile.toString(),
                        "Dealer Code: " + dealer.dealerCode.toString()
                    )

                    if (dealer.dealerImage.toString() != "") {
                        rootView.imgVerified.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(Endpoint.IMAGE_BASE_URL + dealer.dealerImage)
                            .error(com.google.android.gms.location.R.drawable.shop_banner).fitCenter()
                            //.placeholder(R.drawable.shop_banner)
                            .into(rootView.imgRetailerHomeCover)
                    }

     */


            }

            else -> {

                setBasicInfo(
                    siteinfo.partyMobile.toString(),
                    siteinfo.siteNumber.toString(),
                    siteinfo.siteArea.toString(),
                    siteinfo.siteAdd.toString(),
                    siteinfo.siteType.toString(),
                    siteinfo.conversionStatus.toString()
                )

                if (siteinfo.siteImage.toString() != "") {
                    binding.imgSiteHomeCover.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(EndPoint.IMAGE_BASE_URL + siteinfo.siteImage)
                        .error(R.drawable.shop_banner).fitCenter()
                        // .placeholder(R.drawable.shop_banner)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.imgSiteHomeCover)
                }
            }
        }


    }

    private fun setBasicInfo(
        contact : String,name: String, area: String, address: String, type: String, status: String
    ) {

        siteNumber = name
        binding.txtPartyMobile.text = "IHBs number : " + contact

        binding.callIcon.setOnClickListener{

            openDialer(contact)

        // openDialer("01335094919")

        }

        binding.txtSiteName.text = "Site Code: " + name
      //  binding.txtSiteSize.text = "Total area: $area"
        binding.txtSiteAddress.text = "Address: $address"
        binding.txtsiteType.text = "Site Type: $type"+ " | "+"Total area: $area"
        if(status=="Yes"||status=="Already converted site"){
            binding.ConversionIcion.visibility = View.VISIBLE
        }else{
            binding.ConversionIcion.visibility = View.GONE

        }
        binding.txtconversionStatus.text = "Conversion Status: $status"
    }


    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }




    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun convertOtherSiteInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }


    @SuppressLint("SuspiciousIndentation")
    fun onClickListener() {

        binding.cardsiteMap.setOnClickListener {
            getSavedSiteList("Bangladesh",division,district,"","")

           // shoSiteLocation(pointList)
        }

        binding.carddemoSitequickVisit.setOnClickListener {
            val userInfo: Data = Utility.getUserInfo(requireActivity())!!

                exactLocationRestriction(siteSavedlatitude,siteSavedlongitude,userInfo)

         //   QuickVisitDialog()

        }




        binding.cardSiteUpdate.setOnClickListener {

            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_site_update)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }




        binding.cardSiteProgress.setOnClickListener {

            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_site_progress)
            //  navController.navigate(R.id.nav_demo_siteentry)
        }


        binding.cardsiteComplain.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_sitecomplain)

        }

        binding.cardRefCallrecord.setOnClickListener {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_ref_callrecord)
        }


        binding.cardSiteSummurey.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_Site_Summery)

        }


   //     binding.carddemoSiteVerfication.visibility = View.GONE

        binding.carddemoSiteVerfication.setOnClickListener {
            val userInfo: Data = Utility.getUserInfo(requireActivity())!!

            exactLocationRestrictionSitevarification(siteSavedlatitude,siteSavedlongitude,userInfo)
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_teamlead_Visit)

        }


        binding.imgSiteSetCover.setOnClickListener {
            //LOCATION Green LIGHT
            if (latitude != 0.0) {
                binding.locationStatus.visibility = View.VISIBLE
            } else {

                //  latitude = getLastKnownLocation()!!.latitude
                //  longitude = getLastKnownLocation()!!.longitude
                getLastLocations()
            }

           // openCamera()
            captureImage(REQUEST_IMAGE_CAPTURE)

        }

    }


    @SuppressLint("MissingInflatedId")
    private fun QuickVisitDialog() {
        val v: View = layoutInflater.inflate(R.layout.quick_visit_dialouge, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val qvisit_cancel: TextView = v.findViewById(R.id.qvisit_cancel)
        val qvisit_confirm: TextView = v.findViewById(R.id.qvisit_confirm)
        //    val cancel: TextView = v.findViewById(R.id.cancelR)
        //   val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val qVisit_close: ImageView = v.findViewById(R.id.qVisit_close)

        /*  val oth=retailerSetupInfo.split(",")[0]

        if( oth!= "Retailer"){
            txtCDialogTitle.text="New $oth Addition"
            txtBody.text="Are you sure , you want to add this new  $oth!"
        }


       */


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        qvisit_cancel.setOnClickListener {
            dialog.hide()
        }

        qVisit_close.setOnClickListener {
            dialog.hide()
        }

        qvisit_confirm.setOnClickListener {

            val empI = Utility.getValueByKey(requireActivity(), "username").toString()

            //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

            //  val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()

            //  sitenum = siteCode

            val profileData = prepareListForQuickVisit()

            QuickVisit(empI, profileData, dialog)

        }


    }


    private fun QuickVisit(
        user: String,
        value: String,
        dialog: Dialog
    ) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        loadingAnim.show()

        retailerViewModel.TSQuickVisitSave(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Quick visit completed successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            loadingAnim.dismiss()

                            showCustomDialog()


                        } else {
                            loadingAnim.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }
                    }
                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun prepareListForQuickVisit(): String {

        other = convertOtherInfo(requireActivity())!!
        var site = siteNumber
        //selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
        type = Utility.getValueByKey(requireActivity(), "slected_party_type").toString()

        selectedCustomer = type
        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()


        val quickVisitInfo = TSQuickVisitSaveInfo(
            other.partyCode.toString(),
            site,
            other.contactPerson.toString(),
            selectedCustomer,
            "",
            other.partyMobile.toString(),
            other.partyName.toString(),
            other.partyAdd1.toString(),
            "Bangladesh",
            division,
            district,
            thana,
            latitude,
            longitude
        )

        val gson = Gson()
        return gson.toJson(quickVisitInfo)
    }





    fun showCustomDialog() {
        val binding = SiteProgressConfBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.show()
       // binding.tvTitle.text = "Custom Title"
       // binding.tvMessage.text = "This is a custom message."
        binding.btnClose.setOnClickListener {
            // Handle button click
            dialog.dismiss()
        }

        binding.imgConfirm.setOnClickListener {
            // Handle button click
           dialog.dismiss()
            captureImage(Vis_PIC_CAMERA_REQ_CODE)

//            val navController =
//                requireActivity().findNavController(R.id.nav_host_fragment)
//            // navController.navigate(R.id.nav_retiler_home)
//            navController.navigate(R.id.nav_site_progress)

        }
        binding.imgCancel.setOnClickListener {
            // Handle button click
            dialog.dismiss()
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment)
            // navController.navigate(R.id.nav_retiler_home)
            navController.navigate(R.id.nav_dashboard)
        }


    }


    //TODO----------------------IMAGES---------------------------------------------------------->
    private fun openCamera() {
        //OLD Code
        //val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // startActivityForResult(takePicture, 0)
        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(), "com.crowncement.TsSalesTracking.fileprovider", imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 0)
    }



    ///todo for image upload

    private fun uploadTSSiteCover(
        user_id: String,
        site_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File,
        loading: Dialog
    ) {
        retailerViewModel.uploadTSSiteCover(
            user_id,
            party_image,
            site_code,
            party_lat,
            party_lon
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Site added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )

                                } else {

                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Customer Add", "Error: " + e.message)
                            }

                        }
                    }

                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {
        //val bytes = ByteArrayOutputStream()
        //inImage.compress(Bitmap.CompressFormat.PNG, 0, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            // inImage,
            getResizedBitmap(inImage, 800), imageName, null
        )
        return Uri.parse(path)
    }

    private fun getLastLocations() {
        EasyLocation(requireActivity(), object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {

                Utility.commonToast(
                    requireActivity(), "Location Permission Denied", "Permission  denied", 0
                )
            }

            override fun locationSettingFailed() {

                Utility.commonToast(
                    requireActivity(), "Location Settings Failed", "Location settings issue", 0
                )

            }

            override fun getLocation(location: Location) {

                latitude = location.latitude
                longitude = location.longitude
                Log.d("latitude", "latitude: $latitude")
                Log.d("longitude", "longitude: $longitude")
            }
        })


    }

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun getLastLocation() {

        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!checkPermissions()) {
                    requestDeviceLocationSettings()
                }

                return
            } else {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d(TAG, "onLocationResult: $latitude | $longitude")
                        if (latitude.toString().isNotEmpty()) {
                          //  binding.locationStatus.visibility = View.VISIBLE
                            _binding?.locationStatus?.visibility = View.VISIBLE
                        }
                    }
                }
            }

        } else {
            requestDeviceLocationSettings()
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
                        requireActivity(), 100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(TAG, "requestDeviceLocationSettings: " + sendEx.message.toString())
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
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

            if (latitude != 0.0) {
                binding.locationStatus.visibility = View.VISIBLE
            }
            Log.e(TAG, "onLocationResult: lat: $latitude")
            Log.e(TAG, "onLocationResult: long: $longitude")
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), permissionCode
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), permissionCode
            )
        }
    }


    private fun getPreviouslyStoredValue() {
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

    }



    //for tagged site load]
    /*
    private fun getOtherCustomerListforTagged(
        xparty: String,
        xCode: String
    ) {
        if(isAdded) {
            retailerViewModel.GetBPSiteInfoList(xparty)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val otherCustomerList: ArrayList<GetBPSiteInfoResponceChild> =
                                    res.data
                                    for (index in res.data) {
                                    if(index.partyType.toString()=="IHBs"){

                                    }else if(index.partyType.toString()=="Contractor"){
                                        _binding?.txtContractorCodeCode?.setText("Contractor : "+index.partyName+" ( "+index.partyCode+" ) ")
                                    }else if(index.partyType.toString()=="Engineer"){
                                        _binding?.txtEngineerCode?.setText("Engineer : "+index.partyName+" ( "+index.partyCode+" ) ")

                                    }else{

                                    }

                                    //println("Item at $index is ${items[index]}")
                                }


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

     */


    private fun getOtherCustomerListforTagged(
        xparty: String,
        xCode: String
    ) {
        if(isAdded) {
            retailerViewModel.GetBPSiteInfoList(xparty)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val otherCustomerList: ArrayList<GetBPSiteInfoResponceChild> =
                                    res.data

                                for (index in res.data) {
                                    if(index.partyType.toString()=="IHBs"){

                                    }else if(index.partyType.toString()=="Contractor"){
                                        _binding?.txtContractorCodeCode?.setText("Contractor : "+index.partyName+" ( "+index.partyCode+" ) ")
                                        activity?.let {
                                            Utility.storeKeyValue(it, "conCode", index.partyCode)
                                        }
                                         //   Utility.storeKeyValue(requireActivity(),"conCode",index.partyCode)

                                        activity?.let {
                                            Utility.storeKeyValue(it, "conName", index.partyName)
                                        }
                                        /*
                                        if(index.partyName!!.isNotEmpty()) {
                                            Utility.storeKeyValue(
                                                requireActivity(),
                                                "conName",
                                                index.partyName
                                            )
                                        }

                                         */

                                    }else if(index.partyType.toString()=="Engineer"){
                                        _binding?.txtEngineerCode?.setText("Engineer : "+index.partyName+" ( "+index.partyCode+" ) ")

                                        activity?.let {
                                            Utility.storeKeyValue(it, "engCode", index.partyCode)
                                        }
                                        /*
                                        if(index.partyCode!!.isNotEmpty()) {
                                            Utility.storeKeyValue(
                                                requireActivity(),
                                                "engCode",
                                                index.partyCode
                                            )
                                        }
                                         */

                                        activity?.let {
                                            Utility.storeKeyValue(it, "engName", index.partyName)
                                        }

                                        /*
                                        if(index.partyName!!.isNotEmpty()) {
                                            Utility.storeKeyValue(
                                                requireActivity(),
                                                "engName",
                                                index.partyName
                                            )
                                        }

                                         */
                                    }else{


                                    }

                                    //println("Item at $index is ${items[index]}")

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

    }



    override fun onResume() {
        super.onResume()
        //OnResume Fragment
        prepareLocation()
        getLastLocation()
    }


    //todo new camera method
    fun captureImage(requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent(requestCode)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }

            else -> {
                requestPermissions()
            }
        }
    }


    private fun showCameraPermissionRationale() {
        AlertDialog.Builder(requireContext()).setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ ->

            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            context?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile(requestCode)
                    } catch (ex: IOException) {
                        null
                    }

                    photoFile?.also {

                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.crowncement.TsSalesTracking.fileprovider",
                            it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, requestCode)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(requestCode: Int): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getOutputDirectory()

        return File.createTempFile(
            "JPEG_${timeStamp}_bill_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
         //   currentPhotoPath = absolutePath

            if(requestCode==REQUEST_IMAGE_CAPTURE){
                currentPhotoPath = absolutePath

            }else if(requestCode==Vis_PIC_CAMERA_REQ_CODE){
                //   currentPhotoPath = absolutePath
                visitingsitePhotoPath = absolutePath
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, "CrownTitanAttendance").apply {
            mkdirs()
        }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
    }


    fun compressImage(imagePath: String, maxSizeKB: Int = 50): File {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        // Calculate inSampleSize (power of 2) to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, 960, 960)
        options.inJustDecodeBounds = false

        // Decode bitmap with inSampleSize set
        var bitmap = BitmapFactory.decodeFile(imagePath, options)

        // Compress with quality adjustment
        var outputStream = ByteArrayOutputStream()
        var quality = 50 // Start with 90% quality

        do {
            outputStream.reset() // Clear the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // Reduce quality by 5% each iteration
        } while (outputStream.toByteArray().size > maxSizeKB * 960 && quality > 10)

        // If still too large, resize further
        if (outputStream.toByteArray().size > maxSizeKB * 960) {
            val scaleFactor =
                Math.sqrt((maxSizeKB * 960).toDouble() / outputStream.toByteArray().size)
            val newWidth = (bitmap.width * scaleFactor).toInt()
            val newHeight = (bitmap.height * scaleFactor).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        }
        return convertBitmapToFile(
            BitmapFactory.decodeByteArray(
                outputStream.toByteArray(),
                0,
                outputStream.toByteArray().size
            )
        )
    }

    private fun convertBitmapToFile(imageBitmap: Bitmap): File {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("collection", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        stream.flush()
        stream.close()
        return file
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    //todo new img

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
     //   if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.e("IMAGE", "Image saved to $currentPhotoPath")

        if (resultCode == RESULT_OK) {

            try {


                when (requestCode) {

                    REQUEST_IMAGE_CAPTURE -> {

                        val imgFile = compressImage(currentPhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            finalFile = imgFile
                        }

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        retailerSetupInfo =
                            Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

                        binding.imgSiteHomeCover.setImageBitmap(bitmap)

                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()

                        val type = Utility.getValueByKey(requireActivity(), "acCustomer").toString()
                        if (latitude > 0.0) {
                            uploadTSSiteCover(
                                user,
                                SiteCode,
                                retailerSetupInfo.split(",")[0],
                                latitude.toString(),
                                longitude.toString(),
                                finalFile,
                                loadingAnim
                            )
                        } else {
                            prepareLocation()
                            getLastLocation()
                            if (latitude != 0.0) {
                                uploadTSSiteCover(
                                    user,
                                    SiteCode,
                                    retailerSetupInfo.split(",")[0],
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    loadingAnim
                                )
                            }
                            //THIS SCOPE FOR BELOW ANDROID 7
                            else {
                                Utility.commonToast(
                                    requireActivity(),
                                    "Failed",
                                    "Location reset successfully\n try to take picture again",
                                    0
                                )

                                getLastLocations()
                                //LOCATION Green LIGHT
                                if (latitude != 0.0) {
                                    binding.locationStatus.visibility = View.VISIBLE
                                    uploadTSSiteCover(
                                        user,
                                        SiteCode,
                                        retailerSetupInfo.split(",")[0],
                                        latitude.toString(),
                                        longitude.toString(),
                                        finalFile,
                                        loadingAnim
                                    )
                                }
                            }
                        }


                    }

                    Vis_PIC_CAMERA_REQ_CODE ->{

                        val imgFile = compressImage(visitingsitePhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            sitefinalFile = imgFile
                        }
                        cardFile.add(sitefinalFile)

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                       // binding.tsSiteImage.setImageBitmap(bitmap)


                        if (latitude == 0.0) {
                            getLastLocation()

                            //   latitude = getLastKnownLocation()!!.latitude
                            //   longitude = getLastKnownLocation()!!.longitude
                        }
                        var  SiteCode = siteinfo.siteNumber.toString()
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                        val today = LocalDate.now()
                        var currentdate = today.format(formatter)
                        var filename = SiteCode+"_"+currentdate
                        val empI = Utility.getValueByKey(requireActivity(), "username").toString()

                        if(sitefinalFile.isFile) {
                            uploadCustomerDoc(
                                empI,
                                filename, //siteid_date
                                "SalesSiteImg", //SalesSiteImg
                                "progressimg",//progressimg
                                "jpg",
                                cardFile,
                                loadingAnim,
                                "" + "nid"
                            )
                        }
                    }

                }
            } catch (e: Exception) {
                Utility.commonToast(
                    requireActivity(),
                    e.message.toString(),
                    e.message.toString(),
                    0
                )

            }

        }
        else {
            Toast.makeText(requireActivity(), "ERROR in image processing", Toast.LENGTH_LONG).show()
        }


    }

//todo site progress img
    private fun uploadCustomerDoc(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>,
        loading: Dialog,
        docType: String
    ) {
        loadingAnim.show()
        retailerViewModel.uploadCusFile(
            user_id,
            party_code,
            party_type,
            doc_type,
            doc_ext,
            all_images
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loadingAnim.dismiss()

                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Site progress image added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )

                                } else {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Site progress img", "Error: " + e.message)
                            }

                        }
                    }

                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    //todo site verification list

    private fun getSiteVrfInfo(
        site_id: String
    ) {
        loadingAnim.show()
        if(isAdded) {
            retailerViewModel.GetSiteVrfInfo(site_id)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val vrfList: ArrayList<GetSiteVrfInfoChild> = res.data
                                if(res.code=="404"){
                                    binding.carddemoSiteVerfication.visibility = View.VISIBLE
                                    binding.siteVerifyIcon.visibility = View.GONE

                                }
                                else if (res.code=="200"){
                                    if(vrfList.isNotEmpty()&&
                                        ((vrfList.get(0).status=="OK")||(vrfList.get(0).status=="Information Mismatch"))){
                                        binding.carddemoSiteVerfication.visibility = View.GONE
                                        binding.siteVerifyIcon.visibility = View.VISIBLE

                                    }
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
    }


    //todo site lat lon list

    private fun getSavedSiteList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xCode: String
    ) {
        loadingAnim.show()
        if(isAdded) {
            retailerViewModel.getSavedSiteList(country, division, district, thana)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val otherCustomerList: ArrayList<GetTSSiteInfoChild> = res.data
                                pointList =  ArrayList()


                                for (values in otherCustomerList) {

                                       // latlonlist.add(values.partyLon)
                                //    pointList.add(values.partyLon?.toInt(),values.partyLat.toDouble())
                                    pointList.add(LatLngList(
                                        values.siteNumber!!.toString(),
                                        values.siteAdd!!.toString(),
                                        values.partyLat!!.toDouble(),
                                        values.partyLon!!.toDouble()))

                                }

                               // liveLocationBottomSheet(pointList)

                                showSiteLocation(pointList)
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
    }


//todo all site map
    @SuppressLint("ResourceType", "MissingInflatedId")
    private fun showSiteLocation(data: ArrayList<LatLngList>) {
    val v: View = layoutInflater.inflate(R.layout.emp_tracking, null)
//        val dialog = BottomSheetDialog(requireContext())

    val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    // Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.FILL_PARENT)

    dialog.setContentView(v)
    val heading: TextView = v.findViewById(R.id.location_heading)
    val txtLocationTrackingCancel: TextView = v.findViewById(R.id.txtLocationTrackingCancel)
    val txtTrackingLocationMessage: TextView = v.findViewById(R.id.txtTrackingLocationMessage)
    val tracking_map_view: MapView = v.findViewById(R.id.tracking_map_view)

    heading.text = "Nearby Site Location"


    if (latitude == 0.0) {
        getLastLocation()

    }


    if (data.size > 0 && data.size > 1) {


    } else {

        txtTrackingLocationMessage.text = "Site Location : Sorry, no data found"
    }

    dialog.setContentView(v)
    dialog.setCancelable(true)
    dialog.show()


    // val googleMap: GoogleMap
    MapsInitializer.initialize(requireActivity())

    tracking_map_view.onCreate(dialog.onSaveInstanceState())
    tracking_map_view.onResume() // needed to get the map to display immediately


    tracking_map_view.getMapAsync { googleMap ->

        googleMap.clear()

        val pointList: ArrayList<LatLng> = ArrayList()

        for (z in 0 until data.size) {

            val point = LatLng(data.get(z).partyLat!!, data.get(z).partyLon!!)

            pointList.add(point)
            //    val t=Utility.changeDateFormat(data.get(z).partyLat.toString(),"yyyy-MM-dd HH:mm:ss","hh:mm:ss a")
            //    Log.d("Tracking", "Time : $t")
            //   Log.d("Tracking", "Address :"+getAddress(point))

            //todo current
            googleMap.addMarker(
                MarkerOptions()
                    .icon(BitmapFromVector(requireContext(), R.drawable.ic_location_red))
                    .position(currentLocation!!)
                    .title(siteinfo.siteAdd + "(" + siteinfo.siteNumber + ")")
            )


            //todo current end

            googleMap.addMarker(
                MarkerOptions()
                    .icon(BitmapFromVector(requireContext(), R.drawable.baseline_home_24))
                    .position(point)
                    // .title(t.toString()+"("+getAddress(point)!!.split(",")[1]+")")
                    //  .title(t.toString())
                    .title(
                        data.get(z).siteAdd!! + "(" +
                                data.get(z).siteNumber!! + ")"
                    )

            )?.showInfoWindow()

        }

        if (pointList.size > 0 && pointList.size > 1) {


            if (currentLocation.latitude.toString() != "") {
                currentLocation?.let { loc ->
                    val update = CameraUpdateFactory.newLatLngZoom(loc, 15f)
                    googleMap.animateCamera(update)
                }
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList[0], 15f))
            }

            googleMap.setOnMarkerClickListener { true }

            googleMap.isTrafficEnabled = true


            googleMap.setOnMarkerClickListener { clickedMarker ->


                false
            }


        }


        txtLocationTrackingCancel.setOnClickListener {
            dialog.hide()

        }

    }


}


    //toolbar home
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                //  Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_dashboard)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}


