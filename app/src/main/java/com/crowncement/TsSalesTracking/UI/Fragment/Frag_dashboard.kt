package com.crowncement.TsSalesTracking.UI.Fragment




import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.UI.viewmodel.*
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.*
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.commonToast
import com.crowncement.TsSalesTracking.common.Utility.compareTimeFrame
import com.crowncement.TsSalesTracking.common.Utility.displayNotification
import com.crowncement.TsSalesTracking.common.Utility.getBaseMessage
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.common.Utility.saveAttInfo
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Adapter.DashboardLeaveTypeAdapterNew
import com.crowncement.TsSalesTracking.data.Adapter.MovementRouteAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OfflineQuickVisitAdapter
import com.crowncement.TsSalesTracking.data.Adapter.SalesVisitSummaryAdapter
import com.crowncement.TsSalesTracking.data.InternalDB.AppRoomDatabase
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.data.Repository.MovementRepository.movementPlanList
import com.crowncement.TsSalesTracking.databinding.FragDashboardBinding
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService
import com.crowncement.TsSalesTracking.services.locationTracking.TravelTrackingService
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.io.ByteArrayOutputStream


import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*




class Frag_dashboard : Fragment() {


    //  lateinit var rootView: View

    private var _binding: FragDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var atteViewModel: AttendanceViewModel
    private lateinit var leaveViewModel: LeaveViewModel
    private lateinit var lateViewModel: LateViewModel
    private lateinit var tourViewModel: TourViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    lateinit var retailerViewModel: RetailerViewModel


    private lateinit var userInfo: Data


    var user = ""
    lateinit var loadingAnim: Dialog
    lateinit var imageList: ArrayList<String>


    private var countLate = 0
    private var countLeave = 0
    private var countTravel = 0


    private lateinit var salesVisitViewModel: SalesVisitViewModel
    lateinit var movementViewModel: MovementViewModel
    private var notificationCount = 0


    var planActualDateList: ArrayList<MovementPlanChild1> = ArrayList()

    var selectedRouteList: ArrayList<String> = ArrayList()
    var processedRouteList: ArrayList<String> = ArrayList()


    var planedMovementList: ArrayList<MovementPlanChild2> = ArrayList()
    lateinit var empRouteList: ArrayList<String>
    var planDateList: ArrayList<MovementPlanChild1> = ArrayList()



    lateinit var attendanceResponse: Attendance
    lateinit var actualMovDialog: Dialog
    lateinit var routeAdapter: MovementRouteAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        _binding = FragDashboardBinding.inflate(inflater, container, false)


        Utility.removePreference(requireContext(),"contractor_update")
        Utility.removePreference(requireContext(),"RetailerSetupInfo")


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )


        user = Utility.getValueByKey(requireActivity(), "username").toString()


        BottomSheetBehavior.from(binding.layoutVisit).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        userInfo = Utility.getUserInfo(requireActivity())!!
        val location = getLocationData()
        setProfileGreetings()
        setProfileInformation()
        AttendanceViewModelFactory()
        RetailerViewModelFactory()
        DashboardViewModelFactory()
        LeaveViewModelFactory()
        LateViewModelFactory()
        TourViewModelFactory()
        SalesVisitViewModelFactory()
        MovementViewModelFactory()
        setupViewModel()
        // checkTodaysAttInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss") + ".000")

        checkTodaysAttInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss") + ".000")
        // currentDateSet()
        //  setBannerUI(getBannerData())
        getBannerImages(getString(R.string.base_project_name))


        //Get att summary
        val timePeriod = Utility.getAttendancePeriod().split(",")
        binding.attSummaryChild.txtFromMonth.text=Utility.changeDateFormat(timePeriod[0],"yyyy-MM-dd","EEE, dd,MMM yyyy")
        binding.attSummaryChild.txtToMonth.text=Utility.changeDateFormat(timePeriod[1],"yyyy-MM-dd","EEE, dd,MMM yyyy")
        getUserAttSummaryData(user, timePeriod[0], timePeriod[1])




        getUserLeaveTypeInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())



        //Notification
        getLeaveApplicationList(user)
//TODO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SALES Visit SUMMARY
        getSalesVisitSummary(user, getCurrentDate("yyyy-MM-dd").toString())




        menuPermission(userInfo.user_access.toString())


        onClickListener()


      //  reminderSection()

        offlineQuickVisitMenu()


        return binding.root




    }


    //TOdo new
    private fun offlineQuickVisitMenu() {
        val getOfflineValue = getTodayOfflineQuickVisit(requireActivity())
        if (getOfflineValue.size > 0) {
            if (user == getOfflineValue[0].CreateBy) {
                //  rootView.layout3.visibility = View.VISIBLE
            } else {
                // rootView.layout3.visibility = View.GONE
            }
        } else {
            //  rootView.layout3.visibility = View.GONE
        }
    }


    private fun getTodayOfflineQuickVisit(activity: Activity): ArrayList<OfflineQuickVisit> {
        val qVisit: ArrayList<OfflineQuickVisit> = AppRoomDatabase
            .getDatabase(activity.applicationContext)
            .offlineQuickVisitDao()
            .getTodayQuickVisit(
                getCurrentDate("yyyy-MM-dd").toString()
            ) as ArrayList<OfflineQuickVisit>
        return qVisit
    }


/*
    private fun convertOfflineVisit() {

        val v: View = layoutInflater.inflate(R.layout.convert_quick_visit, null)
        // val dialog = BottomSheetDialog(requireContext())
        val offlineDialog = Dialog(v.context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        Objects.requireNonNull(offlineDialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val QVCancel: TextView = v.findViewById(R.id.QVCancel)


        loadQuickVisit(getTodayOfflineQuickVisit(requireActivity()), v)

        offlineDialog.setContentView(v)
        offlineDialog.setCancelable(true)
        offlineDialog.show()

        QVCancel.setOnClickListener {

            offlineDialog.dismiss()
        }

    }

 */

    /*

    private fun loadQuickVisit(quickVisits: ArrayList<OfflineQuickVisit>, v: View) {

        val txtQNoData: TextView = v.findViewById(R.id.txtQNoData)
        val shimmer_container: ShimmerFrameLayout = v.findViewById(R.id.shimmer_container)
        val rvQList: RecyclerView = v.findViewById(R.id.rvQList)
        if (quickVisits.size > 0) {
            txtQNoData.visibility = View.GONE
            rvQList.visibility = View.VISIBLE
            prepareDataList(quickVisits, rvQList)

        } else {
            txtQNoData.visibility = View.VISIBLE
            rvQList.visibility = View.GONE
        }
        shimmer_container.visibility = View.GONE
        shimmer_container.stopShimmer()
    }


     */

    private fun prepareDataList(items: ArrayList<OfflineQuickVisit>, rv: RecyclerView) {
        val itemAdapter = OfflineQuickVisitAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(this.requireContext())

        rv.layoutManager = rLayoutmanager
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter


        itemAdapter.setOnItemClickListener(object :
            OfflineQuickVisitAdapter.OnAdapterItemClickListener {

            override fun OnSelect(v: View?, position: Int) {


            }

            override fun OnIconSelect(v: View?, position: Int) {

                liveLocationBottomSheet(items[position])
            }

        })
    }


    private fun liveLocationBottomSheet(data: OfflineQuickVisit) {
        val v: View = layoutInflater.inflate(R.layout.emp_live_att, null)
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val txtLocationCancel: TextView = v.findViewById(R.id.txtLocationCancel)
        val txtLocationMessage: TextView = v.findViewById(R.id.txtLocationMessage)
        val map: MapView = v.findViewById(R.id.map_view)

        if (data != null) {
            val conVoTime = Utility.changeDateFormat(
                data.CreateTime, "HH:mm:ss", "hh:mm:ss a"
            )
            txtLocationMessage.text = "Location updated on ${conVoTime}"

        } else {
            txtLocationMessage.text = "Location updated on : Sorry no data found"
        }


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        var currentLocation: LatLng? = null

        if (data.Latitude!! > 0) {
            currentLocation = com.google.android.gms.maps.model.LatLng(
                data.Latitude!!,
                data.Longitude!!
            )

            val googleMap: GoogleMap
            MapsInitializer.initialize(requireActivity())

            map.onCreate(dialog.onSaveInstanceState())
            map.onResume() // needed to get the map to display immediately


            map.getMapAsync { googleMap ->

                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentLocation)
                        .title("Visited Place")
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f)
                )

                googleMap.setOnMarkerClickListener { // Marker source is clicked

                    true
                }


                googleMap.addCircle(
                    CircleOptions()
                        .center(currentLocation)
                        .radius(60.0).strokeWidth(3f).strokeColor(Color.GREEN)
                        .fillColor(Color.argb(70, 50, 150, 50))
                )

                googleMap.isTrafficEnabled = true
            }
        }

        txtLocationCancel.setOnClickListener {
            dialog.dismiss()
        }


    }


    //TODO MOVEMENT PLAN Starts from here-----------------------------------

    private fun actualMovementEntryDialog(
        selDate: String,
        planList: ArrayList<MovementPlanChild2>
    ) {
        loadingAnim.dismiss()
        selectedRouteList = ArrayList()
        val v: View = layoutInflater.inflate(R.layout.movement_actualt_route, null)
        actualMovDialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val btnAdd: MaterialButton = v.findViewById(R.id.btnAcAdd)
        val routeChipGroup: ChipGroup = v.findViewById(R.id.routeAcChipGroup)
        val idGRV: GridView = v.findViewById(R.id.idAcGRV)

        //getEmpRoute("E11-001743", idGRV, v.context,planList,routeChipGroup)
        getEmpRoute(user, idGRV, v.context, planList, routeChipGroup)

        //already planed route set to chips

        actualMovDialog.setContentView(v)
        actualMovDialog.setCancelable(true)
        actualMovDialog.show()



        btnAdd.setOnClickListener {

            if (selectedRouteList.size > 0) {
                confirmDialog(actualMovDialog)
            } else {
                commonToast(
                    v.context as Activity, "Error", "Please select minimum one route", 2
                )
            }

        }

        idGRV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->

            if (!checkExistence(empRouteList[position])) {

                displayRouteChip(empRouteList[position], routeChipGroup)
                routeAdapter.notifyDataSetChanged() // call it here

            } else {
                commonToast(requireActivity(), "Error", "already added this route", 2)
            }

        }


        // TODO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Add back button listener
        actualMovDialog.setOnKeyListener { dialogInterface, keyCode, keyEvent ->

            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                actualMovDialog.show()
                true
            } else false

        }
        // TODO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Add back button listener end
    }

    private fun getEmpRoute(
        empID: String,
        idGRV: GridView,
        context: Context,
        dataList: ArrayList<MovementPlanChild2>,
        chips: ChipGroup
    ) {
        movementViewModel.getRoute(empID)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200" && res.data.size > 0) {

                            empRouteList = ArrayList()

                            for (item in res.data) {
                                val prePlanListItem =
                                    item.routeNo + ":" + item.routeName + ":" + item.routeCode
                                if (!checkEmpRouteExistence(prePlanListItem)) {
                                    empRouteList.add(prePlanListItem)
                                }
                            }
                            loadRoute(idGRV, context, chips, dataList)

                        } else {

                            commonToast(
                                requireActivity(),
                                "Route not set up yet",
                                "No route found for movement plan",
                                0
                            )
                        }
                    }
                }

                Status.LOADING -> {
                }

                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadRoute(
        idGRV: GridView,
        context: Context,
        routeChipGroup: ChipGroup,
        planList: ArrayList<MovementPlanChild2>
    ) {

        if (empRouteList.size > 0) {
            routeAdapter = MovementRouteAdapter(empRouteList, context)
            idGRV.adapter = routeAdapter

            loadAlreadyAddedPlan(planList, routeChipGroup)
        }
    }

    private fun checkExistence(value: String): Boolean {
        var res = false
        for (item in selectedRouteList) {
            if (value == item) {
                res = true
                break
            } else {
                res = false
            }
        }
        return res
    }


    private fun checkEmpRouteExistence(value: String): Boolean {
        var res = false
        for (item in empRouteList) {
            if (value == item) {
                res = true
                break
            } else {
                res = false
            }
        }
        return res
    }

    private fun displayRouteChip(values: String, chi: ChipGroup) {

        if (!checkExistence(values)) {
            selectedRouteList.add(values)
        }

        chi.clearCheck()
        val chip = Chip(requireContext())
        val chipText = values.split(":")[0]
        chip.text = chipText
        // Set the chip icon
        chip.textSize = 11f
        chip.chipIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_cancel_green)
        chip.setChipIconTintResource(R.color.reject)
        chi.addView(chip)
        chip.setOnClickListener {
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 250
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    chi.removeView(it)
                    val selectedChip = chip.text.toString()

                    for (value in selectedRouteList) {
                        if (value.contains(selectedChip)) {
                            val removePosition = selectedRouteList.indexOf(value)
                            selectedRouteList.removeAt(removePosition)
                            break
                        }
                    }

                    //  selectedRouteList.remove(chip.text.toString())

                }

                override fun onAnimationStart(animation: Animation?) {}
            })

            it.startAnimation(anim)
        }
        //   }
    }

    private fun confirmDialog(parentDialog: Dialog) {
        val v: View = layoutInflater.inflate(R.layout.confirm_movement_plan_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnMoveConfirm = v.findViewById<Button>(R.id.btnMoveConfirm)
        val imgMClose = v.findViewById<ImageView>(R.id.imgMClose)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgMClose.setOnClickListener { view1: View? -> dialog.dismiss() }

        btnMoveConfirm.setOnClickListener { view1: View? ->

            dialog.dismiss()
            parentDialog.dismiss()
            btnMoveConfirm.isEnabled = false

            val conRoute = prepareAllRouteCode()
            if (processedRouteList.size > 0) {
                loadingAnim.show()
                saveActualMovementPlan(
                    user,
                    getCurrentDate("yyyy-MM-dd").toString(),
                    conRoute,
                    dialog,
                    parentDialog,
                    v.context as Activity
                )
            }
        }
    }

    //Today's planed movement route list

    private fun monthlyMovementPlanList(empID: String, year: String, month: String) {
        movementViewModel.movementPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            planDateList = arrayListOf()
                            planDateList.addAll(res.data)
                            sortSelectedDateData(getCurrentDate("yyyy-MM-dd").toString())

                        } else {
                            loadingAnim.dismiss()
                            checkAppAttendanceEligibility(attendanceResponse)
                        }

//                        else {
//                            getBaseMessage(
//                                requireActivity(),
//                                "Error",
//                                res.message.toString(),
//                                R.drawable.error_white,
//                                0
//                            )
//                        }
                        // loadingAnim.dismiss()

                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    // loadingAnim.dismiss()
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sortSelectedDateData(date: String) {
        if (planDateList.size > 0) {

            val dateWisePlan = dateWiseData(planDateList, date)

            if (dateWisePlan != null && dateWisePlan.size > 0) {

                prepareAlreadyAddedPlanData(dateWisePlan)
            } else {
                //User doesn't have any plan from today
                loadingAnim.dismiss()

                if (attendanceResponse.data?.shift == "General") {
                    noPlanWarningDialog()
                }

            }
        } else {
            checkAppAttendanceEligibility(attendanceResponse)
        }
    }


    private fun noPlanWarningDialog(
    ) {

        val v: View = layoutInflater.inflate(R.layout.no_plan_warning, null)
        val warningDialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)


        val message = v.findViewById<TextView>(R.id.txtPlanMessage)

        message.text =
            "You do not submit any pre movement plan for ${getCurrentDate("EEE, dd MMM yyyy")}. As a result, you can not provide"

        warningDialog.window!!.statusBarColor = ContextCompat.getColor(v.context, R.color.reject)

        warningDialog.setContentView(v)
        warningDialog.setCancelable(false)
        warningDialog.show()
        warningDialog.setOnKeyListener { dialogInterface, keyCode, keyEvent ->

            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                warningDialog.show()
                true
            } else false
        }
    }


    private fun dateWiseData(
        listData: ArrayList<MovementPlanChild1>,
        date: String
    ): ArrayList<MovementPlanChild2> {

        var result: ArrayList<MovementPlanChild2> = ArrayList()

        for (item in listData) {

            if (item.plnDate.toString() == date) {
                result = item.routeDetails
                break
            }
        }

        return result
    }


    private fun prepareAlreadyAddedPlanData(dateWiseDetails: ArrayList<MovementPlanChild2>) {

        actualMovementEntryDialog(getCurrentDate("yyyy-MM-dd").toString(), dateWiseDetails)

    }

    private fun loadAlreadyAddedPlan(items: ArrayList<MovementPlanChild2>, chiP: ChipGroup) {

        for (item in items) {
            val planedValue = item.rtHrc5.toString() + ":" + item.routeName + ":" + item.rtCode
            //  selectedRouteList.add(planedValue)

            displayRouteChip(
                planedValue,
                chiP
            )
            routeAdapter.notifyDataSetChanged()
        }
    }

    private fun getActualMovementPlanList(empID: String, year: String, month: String) {
        movementViewModel.movementAcPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            planActualDateList = arrayListOf()
                            planActualDateList.addAll(res.data)

                            val acMovList: ArrayList<MovementPlanChild2> =
                                checkTodaysActualMovementList(res.data)

                            if (acMovList.size > 0) {
                                //NO DIALOG WILL SHOW
                                checkAppAttendanceEligibility(attendanceResponse)


                            } else {
                                monthlyMovementPlanList(
                                    user,
                                    getCurrentDate("yyyy").toString(),
                                    getCurrentDate("MM").toString()
                                )
                            }

                        }


                        if (res.code.toString() == "500") {
                            loadingAnim.dismiss()
                            checkAppAttendanceEligibility(attendanceResponse)
                        }

                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkTodaysActualMovementList(dataList: ArrayList<MovementPlanChild1>): ArrayList<MovementPlanChild2> {
        val actualMovList: ArrayList<MovementPlanChild2> = ArrayList()
        if (dataList != null && dataList.size > 0) {
            for (item in dataList) {
                if (item.plnDate == getCurrentDate("yyyy-MM-dd")) {
                    actualMovList.addAll(item.routeDetails)
                    break
                }
            }
        }
        return actualMovList
    }

    private fun prepareAllRouteCode(): String {

        var citiesCommaSeparated = ""
        if (selectedRouteList.size > 0) {
            processedRouteList = ArrayList()
            for (item in selectedRouteList) {
                val res = item.trim().split(":")[2]
                processedRouteList.add(res)

            }

            citiesCommaSeparated = java.lang.String.join(",", processedRouteList)

        }

        return citiesCommaSeparated

    }

    private fun saveActualMovementPlan(
        empID: String,
        date: String,
        routes: String,
        dialog: Dialog,
        dialog1: Dialog,
        activity: Activity
    ) {
        movementViewModel.saveActualMovementPlan(empID, date, routes)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            dialog.dismiss()
                            dialog1.dismiss()
                            getBaseMessage(
                                requireActivity(),
                                "Successful",
                                res.message.toString(),
                                R.drawable.ic_checked_green,
                                1
                            )
                            checkAppAttendanceEligibility(attendanceResponse)
                        } else {

                            dialog1.show()
                            commonToast(
                                activity,
                                "Error",
                                res.message.toString(),
                                2
                            )
                        }
                        loadingAnim.dismiss()
                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun reminderSection() {

        //Reminder Section
        binding.txtAnnouncement.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.txtAnnouncement.marqueeRepeatLimit = -1 // -1 for infinite
        binding.txtAnnouncement.isSingleLine = true
        binding.txtAnnouncement.isSelected = true

        val reminderDate = getCurrentDate("dd")!!.toInt() + 1
        if (reminderDate == 24) {
            binding.cardReminder.visibility = View.VISIBLE

            val timePeriod = Utility.getAttendancePeriod().split(",")
            getUserAttList(user, timePeriod[0], timePeriod[1])

            var upComingMonth: Int = (getCurrentDate("MM")?.toInt() ?: 0) + 1
            var currentYear: Int = (getCurrentDate("yyyy")?.toInt() ?: 0)

            if (upComingMonth > 12) {
                upComingMonth = 1
                currentYear += 1
            }

            getMovementPlanList(user, currentYear.toString(), upComingMonth.toString())
        } else {
            binding.cardReminder.visibility = View.GONE
        }
    }



    private fun getMovementPlanList(empID: String, year: String, month: String) {
        movementViewModel.movementPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            when {
                                res.plnStatus.toString() == "" -> {

                                    binding.txtMovPlanStatus.text = "Not Submitted"
                                }

                                res.plnStatus.toString() == "Submitted" -> {

                                    binding.txtMovPlanStatus.text = "Pending"

                                }

                                else -> {
                                    binding.txtMovPlanStatus.text = "Approved"
                                }
                            }

                        } else {
                            binding.txtMovPlanStatus.text = "N/A"
                            //getBaseMessage(requireActivity(),"Sorry",res.message.toString(),R.drawable.error_white,0)
                        }
                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    //attendencee

    private fun checkAppAttendanceEligibility(res: Attendance) {

        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ATTENDANCE POP UP
        if (res.code.equals("500")) {
            loadingAnim.dismiss()
            if (userInfo.att_status.equals("Yes") && res.data!!.tour_no.toString() == "") {
                //IF user already completed his offline attendance
                if (getOfflineAttendanceData() == null) {

                    attendanceWarning()

                } else {
                    val offlineAttData = getOfflineAttendanceData()
                    //val imageFile = File(offlineAttData.AttImagePath.toString())
                    val imgFile = compressImage(offlineAttData.AttImagePath.toString())
                    val imageFile = imgFile

                    loadingAnim.dismiss()
                    if (imageFile.isFile) {
                        attendanceEntry(
                            user,
                            offlineAttData.CreateDate.toString(),
                            offlineAttData.AttTime.toString() + ".000",
                            res.data!!.shift_no.toString(),
                            res.data!!.shift.toString(),
                            imageFile,
                            offlineAttData.Latitude.toString(),
                            offlineAttData.Longitude.toString()
                        )
                    } else {
                        displayNotification(
                            requireActivity(),
                            "Failed to Submit Offline Attendance",
                            "User image is corrupted/damage, offline attendance is failed",
                        )
                        attendanceWarning()
                    }
                }
            }
        }
        // SET ATTENDANCE IN TIME ,ALREADY CHECK IN COMPLETED
        else {
            if (res.data!!.intime != null && res.data!!.intime != "2999-12-31 00:00:00.0") {

                loadingAnim.dismiss()
                binding.basicInfo1.txtSumIn.text = Utility.changeDateFormat(
                    res.data!!.intime,
                    "yyyy-MM-dd HH:mm:ss",
                    "hh:mm:ss a"
                )

                if (!(res.data!!.outtime!!.contains("1900-01-01"))) {
                    binding.basicInfo1.txtSumOut.text = Utility.changeDateFormat(
                        res.data!!.outtime,
                        "yyyy-MM-dd HH:mm:ss",
                        "hh:mm:ss a"
                    )
                }
            }

            else {
                //For attendance reject case

              //  attendanceWarning()

            }


        }
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



    private fun attendanceEntry(
        id: String,
        attDate: String,
        attTime: String,
        shiftNo: String,
        shift: String,
        attImage: File,
        latitude: String,
        longitude: String
    ) {
        atteViewModel.attEntry(
            id,
            attDate,
            attTime,
            shiftNo,
            shift,
            attImage,
            latitude,
            longitude,
            "Offline"
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            try {
                                when (res.code) {
                                    "200" -> {
                                        val attD = getOfflineAttendanceData().CreateDate.toString()
                                        successAttEntry(res, attD)
                                    }

                                    else -> {
                                        commonToast(
                                            requireActivity(),
                                            "Offline Attendance Conversion Failed",
                                            res.message.toString(),
                                            0
                                        )
                                        activity?.finish()
                                    }
                                }
                                loadingAnim.dismiss()


                            } catch (e: Exception) {
                                loadingAnim.dismiss()
                                Log.e("Frag_Dashboard", "attendanceEntry: " + e.message)
                            }
                        }
                    }

                    Status.LOADING -> {
                        loadingAnim.show()
                    }

                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        activity?.finish()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun successAttEntry(res: AttendanceResponse, attDate: String) {

        if (res.message.equals("Attendance Entered Successfully")) {
            val isTrackingEnabled = Utility.getUserInfo(requireActivity())!!.att_status
            if (isTrackingEnabled == "Yes") {
                startBackgroundLocationService()
            }

            loadingAnim.dismiss()
            offlineConversionAlert(
                "Check In Completed",
                "Your offline attendance has been uploaded to online"
            )

            getBaseMessage(
                requireActivity(),
                "Attendance Entered Successfully",
                "Your offline attendance has been submitted to online",
                R.drawable.ic_checked_green,
                1
            )
            AppRoomDatabase
                .getDatabase(requireContext())
                .offlineAttendanceDao()
                .deleteTodayAttendance(attDate)

        } else {
            loadingAnim.dismiss()
            commonToast(
                requireActivity(),
                "Offline Attendance Upload Failed",
                res.message.toString(),
                2
            )
        }
    }


    private fun getOfflineAttendanceData(): OfflineAttendance {

        return AppRoomDatabase
            .getDatabase(requireContext())
            .offlineAttendanceDao()
            .getTodayAttendance(getCurrentDate("yyyy-MM-dd").toString())
    }

    private fun offlineConversionAlert(title: String, message: String) {
        val v = requireActivity().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)

        val notificationManager =
            requireActivity().getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)
        var intent: Intent? = null
        try {
            intent = Intent(context, Act_Splash_Screen::class.java)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.crown_cement_icon)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(requireContext(), "LOCATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_checked_green)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(notificationSoundUri)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(pendingIntent)


        notificationManager.notify(notificationID, notificationBuilder.build())
    }


    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "Offline Attendance"
        val adminChannelDescription = "CrownTitan"
        val adminChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            adminChannel = NotificationChannel(
                "OFFLINE_CHANNEL",
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
            Log.e("FragDashboard", "setupChannels: error")
        }
    }




    //todo new finish



    private fun getUserAttList(empId: String, startDate: String, endDate: String) {
        atteViewModel.getAttendanceReportList(empId, startDate, endDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            successAttList(res)
                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {


                        // rootView.shimmer_att_container.visibility = View.GONE
                        // rootView.shimmer_att_container.stopShimmer()


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun successAttList(res: AttendanceReport) {
        if (res.data != null && res.data.size > 0) {
            var lateCount = 0


            for (item in res.data) {


                val checkIn =
                    Utility.changeDateFormat(item.att_in, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a")
                        .toString()
                val lateParameter = checkIn.split(":")


                if (lateParameter[1].toInt() > 15) {
                    lateCount += 1
                }
            }


            //  rootView.txtLateCount.text = "$lateCount | 3"


            val mSpannableString = SpannableString("$lateCount| 3")
            val mRed = ForegroundColorSpan(Color.RED) //red color
            mSpannableString.setSpan(
                mRed,
                0,
                mSpannableString.count() - (lateCount.toString().length),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // rootView.txtLateCount.text = mSpannableString
        }
    }


    private fun getBannerImages(project: String) {
        dashboardViewModel.getDashboardBannerImage(project).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        imageList = ArrayList()


                        for (image in res.data) {
                            imageList.add(IMAGE_BASE_URL + image.imagePath.toString())
                        }


                        if (imageList.size > 0) {
                            // setBannerUI(OfferInfo("", "", "", "", imageList))
                        }
                    }
                }
                Status.LOADING -> {


                }
                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }




    private fun setBannerUI(offer: OfferInfo) {
        val imageList = ArrayList<SlideModel>()


        for (infoImages in offer.OfferImages!!) {
            imageList.add(SlideModel(infoImages, ""))
        }

        binding.dashboardSliderChild.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }




    private fun getLeaveApplicationList(empId: String) {
        leaveViewModel.leaveApprovalList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                notificationCount = 0
                                countLeave = res.data.size


                                getAppliedLateList(empId)


                            }


                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun getAppliedLateList(empId: String) {
        lateViewModel.lateApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code.equals("200")) {


                                val currentNoti: Int = res.data.size
                                countLate = 0
                                countLate = currentNoti


                                getTourApprovalList(empId)


                                // rootView.count.text = notificationCount.toString()
                            }
                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {


                        //Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun getTourApprovalList(empId: String) {
        tourViewModel.getTourApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->


                            if (res.code.equals("200")) {
                                countTravel = 0
                                countTravel = res.data.size
                                notificationCount = countTravel + countLate + countLeave
                                binding.notificationIcon.count.text = notificationCount.toString()

                            }
                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }




    private fun getSalesVisitSummary(userName: String, dateTime: String) {
        retailerViewModel.GetTSCallTag(userName, dateTime)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            if (res.code == "200" && res.data.size > 0) {
                                binding.layoutVisit.visibility = View.VISIBLE
                                prepareVisitSummary(res.data)

                            }
                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {
                        binding.layoutVisit.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }




    private fun prepareVisitSummary(dataList: ArrayList<GetTSCallTagResponceChild>) {
        if (dataList.size > 0) {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val itemAdapter = SalesVisitSummaryAdapter(dataList, fragmentManager)


            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.layoutSiteQuickVisit.rvQuickVisit.layoutManager = manager
            binding.layoutSiteQuickVisit.rvQuickVisit.adapter = itemAdapter


            itemAdapter.setOnItemClickListener(object :
                SalesVisitSummaryAdapter.OnAdapterItemClickListener {


                override fun OnClick(v: View?, position: Int) {


                    if (dataList[position].moveStat!!.isNotEmpty()) {
                        getBaseMessage(
                            requireActivity(),
                            "ERROR",
                            "You have already added this call record",
                            R.drawable.error_white,
                            0
                        )
                    } else {
                        prepareVisitInfo(dataList[position], requireActivity())
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_taged_Call_Record_Entry)
                    }


                }
            })
        }
    }


    fun prepareVisitInfo(info: GetTSCallTagResponceChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("quickVisitInfo", json)
        mPrefs.commit()
    }


    private fun menuPermission(featurePermission: String) {


        if ("nav_Leave_List" !in featurePermission)       {
            binding.dashbordNewVisitSummary.rvLeaveTypenew.visibility = View.INVISIBLE
            binding.dashbordNewVisitSummary.cardLeaveSummary.visibility = View.GONE
        }
        if ("nav_late_application" !in featurePermission) {
            binding.dashboardMenu.layLateApplication.visibility = View.INVISIBLE
        }
        if ("nav_meal_application" !in featurePermission) {
            binding.dashboardMenu.layoutApplyMeal.visibility = View.INVISIBLE
        }
        if ("nav_meal_cost_report" !in featurePermission) {
            binding.dashboardMenu.layoutMealCost.visibility = View.INVISIBLE
        }
        if ("nav_attendance_report" !in featurePermission){
            //   rootView.layoutAttendance.visibility = View.INVISIBLE
            binding.basicInfo1.cardAtt.visibility = View.GONE
        }
        if ("nav_Pay_slip" !in featurePermission)         {
            binding.dashboardMenu.laypayslip.visibility = View.INVISIBLE
        }
        if ("nav_travel_list" !in featurePermission)      {
            binding.dashboardMenu.layputTravelApplication.visibility = View.INVISIBLE
        }
    }




    private fun setProfileGreetings() {


        when {
            compareTimeFrame("05:00", "11:59") -> {
                // rootView.txtProfileGreetings.text = "Good morning"
                //  rootView.txtProfileGreetings.text = getString(R.string.good_morning)
            }


            compareTimeFrame("12:00", "16:59") -> {


                // rootView.txtProfileGreetings.text = getString(R.string.good_afternoon)
            }


            else -> {
                //   rootView.txtProfileGreetings.text = getString(R.string.good_evening)
            }
        }
    }


    private fun setProfileInformation() {
        try {
            val profile: Data = Utility.getUserInfo(requireActivity())!!


            binding.basicInfo1.txtCurrentAttDate.text= "  "+getCurrentDate("EEE, dd MMM yyyy")
            binding.txtProfileName.text = profile.userName
            binding.txtDesignation.text = profile.userDesig + " ( "+ user +" )"
            //  rootView.txtProfileDepartment.text =
            // "Department: " + profile.userDept + "\nJob Location: " + profile.userLocation


//                getString(R.string.department) +
//                        profile.userDept +n                                                                                                                                               nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
//                        getString(R.string.job_location) +
//                        profile.userLocation






            // rootView.txtProfileEmp.text = Utility.getValueByKey(requireActivity(),"username")


            //SET IMAGE
            if (!(profile.userProfileImagePath.toString() == "")) {
                Glide
                    .with(this)
                    .load(IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    // .centerCrop()
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.default_user_image)
                    .into(binding.imgUserHomeImage1)
            }


        } catch (e: Exception) {


            commonToast(requireActivity(), "Error", e.message.toString(), 0)


        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkTodaysAttInfo(userName: String, dateTime: String) {
        atteViewModel.getAttInfo(userName, dateTime).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {


                    it.responseData?.let { res ->
                        attendanceResponse = res
                        successAttInfo(res)

                        //  checkAppAttendanceEligibility(attendanceResponse)



                    }


                }
                Status.LOADING -> {



                }


                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                }
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun successAttInfo(res: Attendance) {


        saveAttInfo(res.data, requireActivity())


        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Start All Services
        startServices()




        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ACTUAL MOVEMENT POP UP


        if (res.data!!.tour_no.toString() == "") {
            getActualMovementPlanList(
                user,
                getCurrentDate("yyyy").toString(),
                getCurrentDate("MM").toString()
            )

            /*
                        movementActualPlanList(
                            user,
                            getCurrentDate("yyyy").toString(),
                            getCurrentDate("MM").toString()
                        )

             */


        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun currentDateSet(){
        val currentDate = LocalDate.now()
        val dayOfMonth = currentDate.dayOfMonth
        //  rootView.txtSumdate.text = dayOfMonth.toString()


        val currentDay = LocalDate.now().dayOfWeek
        val currentDayName = currentDay.getDisplayName(TextStyle.FULL, Locale.getDefault())
        //rootView.txtSumday.text = currentDayName.toString()
    }



    //todo before
    private fun movementActualPlanList(empID: String, year: String, month: String) {
        movementViewModel.movementAcPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {


                    it.responseData?.let { res ->


                        if (res.code.toString() == "200") {


                            planActualDateList = arrayListOf()
                            planActualDateList.addAll(res.data)


                            val acMovList: ArrayList<MovementPlanChild2> =
                                checkTodaysActualMovementList(res.data)


                            if (acMovList.size > 0) {
                                //NO DIALOG WILL SHOW
                            } else {
                                movementPlanList(
                                    user,
                                    getCurrentDate("yyyy").toString(),
                                    getCurrentDate("MM").toString()
                                )
                            }


                        }


//                        else {
//                            getBaseMessage(
//                                requireActivity(),
//                                "Error",
//                                res.message.toString(),
//                                R.drawable.error_white,
//                                0
//                            )
//                        }
                    }
                }
                Status.LOADING -> {


                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun attendanceWarning() {
        val v: View = layoutInflater.inflate(R.layout.open_attendance, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnAttendance = v.findViewById<Button>(R.id.btnAttendance)


        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()


        btnAttendance.setOnClickListener { view1: View? ->
            dialog.dismiss()
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_Live_Attendance)
        }
    }



/*
    @SuppressLint("SuspiciousIndentation")
    private fun startServices() {

        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>TOUR TRACKER SERVICE
        val isTourRunning = Utility.getValueByKey(requireActivity(), "Tour")
        val attStatus: Att = Utility.getAttInfo(requireActivity())!!
        if (isTourRunning!!.isNotBlank() || attStatus.tour_start_st.toString() == "1" || attStatus.tour_exec_st.toString() == "2-Running") {
            binding.tourStatus.visibility = View.VISIBLE
            //Stop location service if running and then start travel tracker
            //CALL STOP SERVICE
//            val intent = Intent(requireContext(), TrackingService::class.java)
//            requireActivity().stopService(intent)

            if (isMyServiceRunning()) {

                val intent = Intent(requireContext(), TrackingService::class.java)
                //  if(intent != null) {
                requireActivity().stopService(intent)
                //  }

            }

            if (!isTravelServiceRunning()) {
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                //
                //   if (intent != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent)
                } else {
                    requireContext().startService(intent)
                }
                //
                //   }
                //
            }
        }




        else {
            binding.tourStatus.visibility = View.GONE
            //CALL STOP SERVICE
            if (isTravelServiceRunning()) {
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                //   if(intent != null) {
                requireActivity().stopService(intent)
                //   }

            }

            checkingLocationTrackerStatus()
        }
//TODO->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Tour TRACKER SERVICE END

        Utility.removePreference(requireActivity(), "acCustomer")
        Utility.removePreference(requireActivity(), "selectedDivision")
        Utility.removePreference(requireActivity(), "selectedDistrict")
        Utility.removePreference(requireActivity(), "selectedThana")
        Utility.removePreference(requireActivity(), "selectedRoute")
        Utility.removePreference(requireActivity(), "selectedOther")


//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service
        checkingLocationTrackerStatus()
//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service END


    }

 */

    private fun startServices() {

        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>TOUR TRACKER SERVICE
        val isTourRunning = Utility.getValueByKey(requireActivity(), "Tour")
        val attStatus: Att = Utility.getAttInfo(requireActivity())!!
        if (isTourRunning!!.isNotBlank() || attStatus.tour_start_st.toString() == "1" || attStatus.tour_exec_st.toString() == "2-Running") {
            binding.tourStatus.visibility = View.VISIBLE
            //Stop location service if running and then start travel tracker
            //CALL STOP SERVICE
            if (isMyServiceRunning()) {
                val intent = Intent(requireContext(), TrackingService::class.java)
                requireActivity().stopService(intent)
            }


            if (!isTravelServiceRunning()) {
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent)
                } else {
                    requireContext().startService(intent)
                }
            }
        } else {
            binding.tourStatus.visibility = View.GONE
            //CALL STOP SERVICE
            if (isTravelServiceRunning()) {
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                requireActivity().stopService(intent)
            }

            checkingLocationTrackerStatus()
            
        }
//TODO->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Tour TRACKER SERVICE END
        Utility.removePreference(requireActivity(), "acCustomer")
        Utility.removePreference(requireActivity(), "selectedDivision")
        Utility.removePreference(requireActivity(), "selectedDistrict")
        Utility.removePreference(requireActivity(), "selectedThana")
        Utility.removePreference(requireActivity(), "selectedRoute")
        Utility.removePreference(requireActivity(), "selectedOther")
        Utility.removePreference(requireContext(), "contractor_update")
        Utility.removePreference(requireContext(), "RetailerSetupInfo")
//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service
        checkingLocationTrackerStatus()
//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service END

    }


    private fun isMyServiceRunning(): Boolean {
        var result = false
        val manager =
            requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TrackingService::class.java.name == service.service.className) {
                result = true
                break
            } else {
                result = false
            }
        }
        return result
    }


//todo privious
    /*
    private fun startServices() {

        //TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>TOUR TRACKER SERVICE
        val isTourRunning = Utility.getValueByKey(requireActivity(), "Tour")
        val attStatus: Att = Utility.getAttInfo(requireActivity())!!
        if (isTourRunning!!.isNotBlank() || attStatus.tour_start_st.toString() == "1" || attStatus.tour_exec_st.toString() == "2-Running") {
            binding.tourStatus.visibility = View.VISIBLE
            //Stop location service if running and then start travel tracker
            //CALL STOP SERVICE
            if(isLocationServiceRunning()){
                val intent = Intent(requireContext(), TrackingService::class.java)
                requireActivity().stopService(intent)
            }

            if (!isTravelServiceRunning()) {
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent)
                } else {
                    requireContext().startService(intent)
                }
            }
        }

        else {
            binding.tourStatus.visibility = View.GONE
            //CALL STOP SERVICE
            if(isTravelServiceRunning()){
                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                requireActivity().stopService(intent)
            }

            checkingLocationTrackerStatus()
        }
//TODO->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Tour TRACKER SERVICE END
        Utility.removePreference(requireActivity(), "acCustomer")
        Utility.removePreference(requireActivity(), "selectedDivision")
        Utility.removePreference(requireActivity(), "selectedDistrict")
        Utility.removePreference(requireActivity(), "selectedThana")
        Utility.removePreference(requireActivity(), "selectedRoute")
        Utility.removePreference(requireActivity(), "selectedOther")
//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service
        checkingLocationTrackerStatus()
//TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Location Tracking Service END

    }

     */


    private fun checkingLocationTrackerStatus() {
        val isLocationEnable = Utility.getValueByKey(requireActivity(), "Is_Location_Tracking_Running")
        val loc_track_status = Utility.getUserInfo(requireActivity())!!.loc_track_status
        val att_status = Utility.getAttInfo(requireActivity())
        if (loc_track_status == "Yes" || isLocationEnable == "YES") {
            //   if (att_status!!.intime.toString() != "2999-12-31 00:00:00.0" && att_status.outtime == "1900-01-01 00:00:00.0")
           // if (att_status!!.intime.toString() != "" && att_status!!.intime.toString() != "2999-12-31 00:00:00.0")
            if (att_status!!.intime.toString() != "2999-12-31 00:00:00.0" && att_status.outtime == "1900-01-01 00:00:00.0") {
                startBackgroundLocationService()
            }
        }
    }


    private fun startBackgroundLocationService() {


        val username: String = Utility.getValueByKey(requireActivity(), "username")!!
        if (username.isNotEmpty() && !isLocationServiceRunning()) {
            val isTrackingRun:Boolean=isLocationServiceRunning()
            // if (username.isNotEmpty() && !isLocationServiceRuning(requireContext(),TrackingService::class.java)) {
            val intent = Intent(requireContext(), TrackingService::class.java)
            //      if(intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(intent)
            } else {
                requireActivity().startService(intent)
            }
        }

    }



    private fun isLocationServiceRunning(): Boolean {
        var result = false
        val manager =
            requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TrackingService::class.java.name == service.service.className) {
                result = true
                break
            } else {
                result = false
            }
        }
        return result
    }


    private fun isTravelServiceRunning(): Boolean {
        val manager =
            requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TravelTrackingService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun getLocationData(): ArrayList<LocationTracking> {
        val offlineLocationData: ArrayList<LocationTracking> = AppRoomDatabase
            .getDatabase(requireContext())
            .locationTrackingDao()
            .getAllLocationInfo() as ArrayList<LocationTracking>


        return offlineLocationData
    }


    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory())
            .get(AttendanceViewModel::class.java)
        leaveViewModel = ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
            .get(LeaveViewModel::class.java)
        lateViewModel = ViewModelProviders.of(this.requireActivity(), LateViewModelFactory())
            .get(LateViewModel::class.java)


        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)


        dashboardViewModel =
            ViewModelProviders.of(this.requireActivity(), DashboardViewModelFactory())
                .get(DashboardViewModel::class.java)


        salesVisitViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesVisitViewModelFactory())
                .get(SalesVisitViewModel::class.java)




        movementViewModel =
            ViewModelProviders.of(this.requireActivity(), MovementViewModelFactory())
                .get(MovementViewModel::class.java)


        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }




    private fun onClickListener() {




//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    // Log.d(TAG, "Fragment back pressed invoked")
//                    // Do custom work here
//                    // if you want onBackPressed() to be called as normal afterwards
//                    if(actualMovementDialog){
//                        actualMovDialog.show()
//                    }
//                    else{
//                        if (isEnabled) {
//                            isEnabled = false
//                            requireActivity().onBackPressed()
//                        }
//                    }
//
//                }
//            }
//            )




        binding.tourStatus.setOnClickListener {
            tourStatusDialog()
        }


//todo notification

        binding.notificationIcon.counterValuePanel.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_notification_menu)
        }


        binding.dashboardMenu.laypayslip.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_paypin)

        }


        binding.dashboardMenu.layoutMealCost.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_meal_cost_report)
        }


        binding.attSummaryChild.layAttendenceReport.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_attendance_report)
        }

        binding.dashboardMenu.layoutCalender.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_calender)
        }



        /*
                rootView.cardAtt.setOnClickListener {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_attendance_report)
                }
         */


        binding.dashboardMenu.layLateApplication.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_Late_Application)
        }

        binding.dashboardMenu.layoutSearch.setOnClickListener{
            search()
        }

        binding.dashboardMenu.layoutApplyMeal.setOnClickListener {
            when {


                compareTimeFrame("09:00", "15:59") -> {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_meal_application)
                }
                else -> {
                    getBaseMessage(
                        requireActivity(),
                        "Sorry",
                        "Meal allocation time is over",
                        R.drawable.error_white,
                        0
                    )
                }
            }


        }






        /*
                rootView.counterValuePanel.setOnClickListener {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_notification_menu)
                }


                rootView.imgUserHomeImage.setOnClickListener {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_user_profile)
                }


        //        rootView.layoutLeaveApprove.setOnClickListener {
        //            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        //            navController.navigate(R.id.nav_leave_app_auth)
        //        }




                rootView.layoutLateApprove.setOnClickListener {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_late_approve)
                }
        */




        binding.dashboardMenu.layputTravelApplication.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_travel_list)
        }









        binding.dashboardSliderChild.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {


            }
        })


    }




    private fun tourStatusDialog() {
        val v: View = layoutInflater.inflate(R.layout.tour_status, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val tourInfo = Utility.getValueByKey(requireActivity(), "Tour")


        val cancelT: TextView = v.findViewById(R.id.cancelT)
        val txtTour: TextView = v.findViewById(R.id.txtTour)
        val txtInfo: TextView = v.findViewById(R.id.txtInfo)


        if (tourInfo!!.isNotEmpty()) {


            val title = tourInfo.split("|")[0]
            val info = tourInfo.split("|")[1]


            txtTour.text = "Tour No: $title"


            txtInfo.text = "on $info your journey has been started."
        }


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()


        cancelT.setOnClickListener {
            dialog.dismiss()
        }
    }




    private fun getUserLeaveTypeInfo(userName: String, dateTime: String) {
        loadingAnim.show()
        leaveViewModel.getLeaveTypeInfo(userName, dateTime)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            successLeaveType(res)
                        }
                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {

                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }




    private fun successLeaveType(res: LeaveTypeSummaryResponse) {


        if (res.code.equals("200")) {


            // older
            //    loadLeaveTypeInfo(res.data)
            loadLeaveTypeInfoNew(res.data)
            //  loadLeaveType(res.data)


        }else {
            loadingAnim.dismiss()
        }


    }




    private fun loadLeaveTypeInfoNew(dataList: ArrayList<LeaveTypeSummaryChild>) {


        if (dataList.size > 0) {
            val itemAdapter = DashboardLeaveTypeAdapterNew(dataList)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())


            binding.dashbordNewVisitSummary.rvLeaveTypenew.layoutManager = rLayoutmanager
            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.dashbordNewVisitSummary.rvLeaveTypenew.layoutManager = manager
            binding.dashbordNewVisitSummary.rvLeaveTypenew.adapter = itemAdapter
            ////

            itemAdapter.setOnItemClickListener(object :
                DashboardLeaveTypeAdapterNew.OnAdapterItemClickListener {


                override fun onSelectedLeaveType(v: View?, position: Int) {


                    val gson = Gson()
                    val json = gson.toJson(dataList.get(position))
                    Utility.storeKeyValue(requireActivity(), "selectedLvInfo", json)
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_Leave_List)


                }
            })
            loadingAnim.dismiss()


        }else{
            loadingAnim.dismiss()
        }
    }




    /*
        //older one
        private fun loadLeaveTypeInfo(dataList: ArrayList<LeaveTypeSummaryChild>) {


            if (dataList.size > 0) {
                val itemAdapter = DashboardLeaveTypeAdapter(dataList)
                val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())


                rootView.rvLeaveType.layoutManager = rLayoutmanager
                val manager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                rootView.rvLeaveType.layoutManager = manager
                rootView.rvLeaveType.adapter = itemAdapter


                itemAdapter.setOnItemClickListener(object :
                    DashboardLeaveTypeAdapter.OnAdapterItemClickListener {


                    override fun onSelectedLeaveType(v: View?, position: Int) {


                        val gson = Gson()
                        val json = gson.toJson(dataList.get(position))
                        Utility.storeKeyValue(requireActivity(), "selectedLvInfo", json)
                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_Leave_List)




                    }
                })
            }


        }


     */



    private fun getUserAttSummaryData(empId: String, startDate: String, endDate: String) {
        atteViewModel.getAttendanceReportList(empId, startDate, endDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            var presentCount=0
                            var lateCount=0
                            var leaveCount=0
                            var travelCount=0


                            if(res.code=="200"){
                                for( item in  res.data){


                                    when (item.att_stat) {
                                        "Present" -> {
                                            presentCount+=1
                                        }
                                        "Late" -> {
                                            lateCount+=1
                                        }
                                        "On Leave" -> {
                                            leaveCount+=1
                                        }
                                        "On Travel" -> {
                                            travelCount+=1
                                        }
                                    }
                                }
                                binding.attSummaryChild.txtPre.text=presentCount.toString()
                                binding.attSummaryChild.txtLat.text=lateCount.toString()
                                binding.attSummaryChild.txtLeav.text=leaveCount.toString()
                                binding.attSummaryChild.txtTrav.text=travelCount.toString()
                            }
                        }


                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    //added for back pressed
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                requireActivity().finish()

            }
        })
    }

//





//todo search
    private fun search() {

    val v: View = layoutInflater.inflate(R.layout.search_existingparty, null)
    val dialog = BottomSheetDialog(requireActivity())
    var picker: DatePickerDialog
    Objects.requireNonNull(dialog.window)
        ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    val SearchParty = v.findViewById<TextInputEditText>(R.id.search_Party)
    val exitingPartyDetails = v.findViewById<TextView>(R.id.exitingPartyDetails)
    val imgClosesSearch =  v.findViewById<ImageView>(R.id.imgClosesSearch)

   // SearchParty.queryHint = "Search your party here"

  //  getSearchByMobile(SearchParty.toString(),exitingPartyDetails)

    SearchParty.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


        }

        override fun afterTextChanged(p0: Editable?) {

            var given_num =SearchParty.text.toString()
            if (given_num.length == 11) {
                loadingAnim.show()

                getSearchByMobile(given_num,exitingPartyDetails)

            }
        }

    })



    dialog.setContentView(v)
    dialog.setCancelable(true)
    dialog.show()

    imgClosesSearch.setOnClickListener { dialog.dismiss() }






}


    //todo getSearchByMobile

    private fun getSearchByMobile(mob_no: String,
                                  exitingPartyDetails : TextView) {
        salesVisitViewModel.getSearchByMobile(mob_no).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {
                    loadingAnim.dismiss()
                   // dialog.dismiss()
                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        loadingAnim.dismiss()
                     //   dialog.dismiss()

                        if(res.data.isNotEmpty()) {
                            exitingPartyDetails.setText(
                                " Party Type : " + res.data.get(0).partyGroup +
                                        "\n Party Name: " + res.data.get(0).partyName+
                                        "\n Party Code: " + res.data.get(0).partyCode+
                                        "\n Contact Person  : " + res.data.get(0).contactPerson+

                                        "\n Party create date  : " + Utility.changeDateFormat(res.data.get(0).partyOpenDate.toString(), "yyyy-MM-dd", "dd MMM yyyy")+

                                        "\n Division : " + res.data.get(0).geoHrc2 +
                                        "\n District : " + res.data.get(0).geoHrc3 +
                                        "\n Thana : " + res.data.get(0).geoHrc4 +
                                        "\n Address : " + res.data.get(0).partyAdd1


                            )

                        }else{
                            exitingPartyDetails.setText("No data found")

                        }


                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                   // dialog.dismiss()

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

//toolbar home
//override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    setHasOptionsMenu(true)
//}
/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


 */

}

