package com.crowncement.TsSalesTracking.UI.Activity

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Fragment.*
import com.crowncement.TsSalesTracking.UI.Receiver.ConnectivityReceiver
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.ActivityMainBinding
import com.crowncement.TsSalesTracking.databinding.AppBarMainBinding
import com.crowncement.TsSalesTracking.databinding.NavHeaderMainBinding
import com.crowncement.TsSalesTracking.services.locationTracking.TrackingService
import com.crowncement.TsSalesTracking.services.locationTracking.TravelTrackingService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


private val TAG = "MainActivity"
private lateinit var appBarConfiguration: AppBarConfiguration


private var PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
)

private var SPECIAL_PERMISSIONS = arrayOf(
    Manifest.permission.FOREGROUND_SERVICE,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.READ_MEDIA_IMAGES
)

var PERMISSION_ALL = 1
var SPECIAL_PERMISSION_CODE = 2

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    //  private lateinit var rootView :

    lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding
    private lateinit var appBarMainBinding: AppBarMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        headerBinding = NavHeaderMainBinding.inflate(LayoutInflater.from(this))
        appBarMainBinding = AppBarMainBinding.inflate(LayoutInflater.from(this))
        // appBarMainBinding = AppBarMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.appBarMain.toolbar)


        appPermission()


        //TODO SEND TO LOGIN IF USER ALREADY LOGOUT FROM SYSTEM

        val userInfo: Data = Utility.getUserInfo(this)!!

        if (userInfo.userId.toString().isEmpty()) {

            Utility.sendActivity(this, "Act_Login")

        } else {


            // setSupportActionBar(appBarMainBinding.toolbar)
            //   val navController = findNavController(R.id.nav_host_fragment)


            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_dashboard,
                    R.id.nav_Live_Attendance,
                    R.id.nav_attendance_report,
                    R.id.nav_Leave_List,
                    R.id.nav_Pay_slip,
                    R.id.nav_Late_Application,
                    R.id.nav_notification_menu,
                    R.id.nav_user_profile,
                    R.id.nav_leave_app_auth,
                    R.id.nav_settings,
                    R.id.nav_late_approve,
                    R.id.nav_leave_allocation,
                    R.id.nav_tour_bill_list,
                    R.id.nav_tour_approve,
                    R.id.nav_tour_expense,
                    R.id.nav_tour_expense_list,
                    R.id.nav_engineer_database,
                    R.id.nav_contractor_database,
                    R.id.nav_mason_database,
                    R.id.nav_deliveryBagEntry,
                    R.id.nav_runningOfferList,
                    R.id.nav_teamlead_Visit
//todo for mason menu permission
                    /*
                              //  R.id.nav_mason_update

                     */

                ), binding.drawerLayout

            )


            setupActionBarWithNavController(navController, appBarConfiguration)

            binding.navView.setupWithNavController(navController)
            binding.navView.itemIconTintList = null
            binding.navView.setBackgroundColor(resources.getColor(R.color.base_light_super))


//    binding.appBarMain.toolbar.navigationIcon!!.setColorFilter(
//        resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP
//    )


            //TODO user profile image click listener

            val headerView = binding.navView.getHeaderView(0) // Get the header view
            val imgUserHomeImage =
                headerView.findViewById<CircleImageView>(R.id.imgUserHomeImage) // Find the TextView inside the header
            imgUserHomeImage.setOnClickListener {
                val navController = this.findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_user_profile)
            }


            //TODO | drawer item click event

            binding.navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
                logoutBottomSheet()
                true
            }


            //  startLocationService()

            setUserImageToDrawer()

            val notificationClick = intent.getStringExtra("send_to")

            if (notificationClick != null && userInfo.userId.toString() != "") {

                when (notificationClick) {
                    "Leave" -> {
                        replaceFragment(R.id.nav_leave_app_auth)
                    }

                    "Late" -> {
                        replaceFragment(R.id.nav_late_approve)
                    }

                    "Travel" -> {
                        replaceFragment(R.id.nav_tour_approve)
                    }

                    "push_notification" -> {
                        //   replaceFragment(R.id.nav_message_list)
                    }

                    else -> {
                        replaceFragment(R.id.nav_dashboard)
                    }
                }
            }

//            else{
//                Utility.sendActivity(this, "Act_Login")
//            }

            //CHECK LOCATION IS ON OR OFF(Continuous process)
            // val myJob: Job = checkDeviceLocationStatus(10000,this)
            // myJob.start()

            hideDrawerItem()

            transferfragment()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }


    private fun setUserImageToDrawer() {
        try {
            val headerView = binding.navView.getHeaderView(0) // Get the header view
            val imgUserHomeImage =
                headerView.findViewById<CircleImageView>(R.id.imgUserHomeImage) // Find the TextView inside the header


            val profile: Data = Utility.getUserInfo(this)!!

//
//    if (profile.userProfileImagePath!!.isNotEmpty()) {
//        Glide.with(this).load(IMAGE_BASE_URL + profile.userProfileImagePath)
//            // .error("https://www.svgrepo.com/show/13656/user.svg")
//            .error(R.drawable.default_user_image)
//            .fitCenter()
//            .skipMemoryCache(true)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .placeholder(R.drawable.default_user_image)
//            .into(binding.navView.getHeaderView(0)
//                .imgUserHomeImage)
//    }

            // Get the header view using ViewBinding
            val headerBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

// Now you can access the ImageView through headerBinding
            if (!profile.userProfileImagePath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.default_user_image)
                    .into(headerBinding.imgUserHomeImage) // Use ViewBinding to access ImageView
            }


//
            val txtName =
                headerView.findViewById<TextView>(R.id.txtName) // Find the TextView inside the header
            txtName.text = profile.userName
            val txtDesi =
                headerView.findViewById<TextView>(R.id.txtDesi) // Find the TextView inside the header
            txtDesi.text = profile.userDesig
            val txtAppVersion =
                headerView.findViewById<TextView>(R.id.txtAppVersion) // Find the TextView inside the header
            txtAppVersion.text = "App Version: " + getAppVersion()

            //binding.navView.getHeaderView(0).txtName.text = profile.userName
            //  binding.navView.getHeaderView(0).txtDesi.text = profile.userDesig

            //   binding.navView.getHeaderView(0).txtAppVersion.text = "App Version: " + getAppVersion()

        } catch (e: Exception) {
            // Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getAppVersion(): String {
        val pInfo: PackageInfo = this.packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }


    private fun hideDrawerItem() {
        val profile: Data = Utility.getUserInfo(this)!!
        val navMenu: Menu = binding.navView.menu
//   if (profile.att_status!!.isEmpty()) {

        if (profile.att_status.toString() == "No") {


            navMenu.findItem(R.id.nav_Live_Attendance).isVisible = false

        }

//  userDrawerMenuPermission(navMenu)

        menuPermission(navMenu, Utility.getUserInfo(this)!!.user_access.toString())
    }

    private fun menuPermission(menu: Menu, featurePermission: String) {

//added

        if ("nav_Live_Attendance" !in featurePermission) {
            menu.findItem(R.id.nav_Live_Attendance).isVisible = false
        }


//SALES PORTAL
        if ("nav_retiler_list" !in featurePermission) {
            menu.findItem(R.id.nav_retiler_list).isVisible = false
        }
        if ("nav_call_approve" !in featurePermission) {
            menu.findItem(R.id.nav_call_approve).isVisible = false
        }
        if ("nav_att_approve" !in featurePermission) {
            menu.findItem(R.id.nav_att_approve).isVisible = false
        }
        if ("nav_movement_plan" !in featurePermission) {
            menu.findItem(R.id.nav_movement_plan).isVisible = false
        }
        if ("nav_movement_plan_list" !in featurePermission) {
            menu.findItem(R.id.nav_movement_plan_list).isVisible = false
        }
        if ("nav_engineer_database" !in featurePermission) {
            menu.findItem(R.id.nav_engineer_database).isVisible = false
        }



        if ("nav_message_list" !in featurePermission) {
            //  menu.findItem(R.id.nav_message_list).isVisible = false
        }
        if ("nav_push_message" !in featurePermission) {
            //   menu.findItem(R.id.nav_push_message).isVisible = false
        }

        if ("nav_app_movement_plan" !in featurePermission) {
            menu.findItem(R.id.nav_app_movement_plan).isVisible = false
        }
        if ("nav_change_ac_move_plan" !in featurePermission) {
            //need to add
            //  menu.findItem(R.id.nav_change_ac_move_plan).isVisible = false
        }
        if ("nav_collection_auth" !in featurePermission) {
            //   menu.findItem(R.id.nav_collection_auth).isVisible = false
        }


//REPORTS
        if ("nav_attendance_report" !in featurePermission) {
            menu.findItem(R.id.nav_attendance_report).isVisible = false
        }
        if ("nav_tour_bill_list" !in featurePermission) {
            menu.findItem(R.id.nav_tour_bill_list).isVisible = false
        }
//        if ("nav_leave_allocation" !in featurePermission) {
//            menu.findItem(R.id.nav_leave_allocation).isVisible = false
//        }
//Others
        if ("nav_settings" !in featurePermission) {
            menu.findItem(R.id.nav_settings).isVisible = false
        }

        if ("nav_contractor_database" !in featurePermission) {
            menu.findItem(R.id.nav_contractor_database).isVisible = false
        }
//todo for mason menu permission


        if ("nav_mason_database" !in featurePermission) {
            menu.findItem(R.id.nav_mason_database).isVisible = false
        }

        if ("nav_deliveryBagEntry" !in featurePermission) {
            menu.findItem(R.id.nav_deliveryBagEntry).isVisible = false
        }

        if ("nav_runningOfferList" !in featurePermission) {
            menu.findItem(R.id.nav_runningOfferList).isVisible = false
        }





    }


    private fun replaceFragment(fragment: Int) {

        val navController = this.findNavController(R.id.nav_host_fragment)
        navController.navigate(fragment)


// val transaction = supportFragmentManager.beginTransaction()
// transaction.replace(R.id.nav_host_fragment, fragment)
// transaction.commit()

    }


    private fun logoutBottomSheet() {
        val v: View = layoutInflater.inflate(R.layout.confirm_logout, null)
        val dialog = BottomSheetDialog(this@MainActivity)
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnYes: Button = v.findViewById(R.id.btnYes)
        val btnNo: Button = v.findViewById(R.id.btnNo)
        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()
        btnNo.setOnClickListener {

            dialog.hide()
        }
        btnYes.setOnClickListener {
            super.onBackPressed()

            if (isTrackingServiceRunning()) {
                val intent = Intent(this, TrackingService::class.java)
                this.stopService(intent)
            }

            if (isTravelServiceRunning()) {
                val intent = Intent(this, TravelTrackingService::class.java)
                this.stopService(intent)
            }

            btnYes.isEnabled = false
            Utility.removePreference(this, "username")
            Utility.removePreference(this, "pass")
            Utility.removePreference(this, "UserInfo")
            // Utility.clearCache(this)
            finish()
        }
    }

    private fun isTrackingServiceRunning(): Boolean {
        val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TrackingService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun isTravelServiceRunning(): Boolean {
        val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (TravelTrackingService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


//TODO PERMISSIONS_-------------------------------------------------------------
    /*
    private fun appPermission() {

    try {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)

        }

    } catch (e: Exception) {
        val error = e.message
        Log.e(TAG, "appPermission: " + error.toString())
    }
    }

     */


    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

//todo for android 14
    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (!hasPermissions(this, *SPECIAL_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(
                        this, SPECIAL_PERMISSIONS, SPECIAL_PERMISSION_CODE
                    )
                }

            } else {
                Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                appPermission()
            }
        } else {

            if (grantResults.isEmpty() && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                appPermission()
            }
        }
    }

     */


    private fun appPermission() {
        try {
            if (!hasPermissions(this, *PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14 (API level 34) and above
                if (!hasPermissions(this, *SPECIAL_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(
                        this,
                        SPECIAL_PERMISSIONS,
                        SPECIAL_PERMISSION_CODE
                    )
                }
            }
        } catch (e: Exception) {
            val error = e.message
            Log.e(TAG, "appPermission: " + error.toString())
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request SPECIAL_PERMISSIONS if on Android 14 or above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (!hasPermissions(this, *SPECIAL_PERMISSIONS)) {
                        ActivityCompat.requestPermissions(
                            this,
                            SPECIAL_PERMISSIONS,
                            SPECIAL_PERMISSION_CODE
                        )
                    }
                }
            } else {
                Toast.makeText(this, "All permissions not granted", Toast.LENGTH_LONG).show()
                appPermission()
            }
        } else if (requestCode == SPECIAL_PERMISSION_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Special permissions not granted", Toast.LENGTH_LONG).show()
                appPermission()
            }
        }
    }

    //todo end android 14

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {
            Utility.noInternetDialog(this)
        }
    }


    fun setBottomNavClick() {
        bottomNav = findViewById(R.id.bottomNavigationView) as BottomNavigationView

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(Frag_dashboard())
                    true
                }

                R.id.call_record -> {
                    loadFragment(Frag_Customer_List())
                    true
                }

                R.id.movementPlan -> {
                    loadFragment(Frag_Movement_Plan_Entry())
                    true
                }

                R.id.notifications -> {
                    loadFragment(Frag_Notification_Menu())
                    true
                }

                else -> {
                    false
                }
            }
        }

    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }


    private fun loadUI(destination: Int) {

        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(destination, null, getNavOptions())
    }

//    private fun transferFragment(destination: Int) {
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(destination, null, getNavOptions())
//    }


    protected fun getNavOptions(): NavOptions {
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .build()

        return navOptions
    }


    fun transferfragment(){
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    loadUI(R.id.nav_dashboard)
                }
                R.id.nav_Live_Attendance -> {
                    loadUI(R.id.nav_Live_Attendance)
                }
                R.id.nav_party_update -> {
                    loadUI(R.id.nav_party_update)
                }
                R.id.nav_att_approve -> {
                    loadUI(R.id.nav_att_approve)
                }
                R.id.nav_expense_claim -> {
                loadUI(R.id.nav_expense_claim)
                }
                R.id.nav_attendance_report -> {
                loadUI(R.id.nav_attendance_report)
                }
                R.id.nav_Leave_List -> {
                loadUI(R.id.nav_Leave_List)
                }
                R.id.nav_Pay_slip -> {
                loadUI(R.id.nav_Pay_slip)
                }
                R.id.nav_Late_Application -> {
                loadUI(R.id.nav_Late_Application)
                }
                R.id.nav_paypin -> {
                loadUI(R.id.nav_paypin)
                }
                R.id.nav_notification_menu -> {
                    loadUI(R.id.nav_notification_menu)
                }
                R.id.nav_travel_list -> {
                    loadUI(R.id.nav_travel_list)
                }
                R.id.nav_calender -> {
                loadUI(R.id.nav_calender)
            }
                R.id.nav_user_profile -> {
                loadUI(R.id.nav_user_profile)
            }
                R.id.nav_leave_app_auth -> {
                loadUI(R.id.nav_leave_app_auth)
            }
                R.id.nav_settings -> {
                loadUI(R.id.nav_settings)
            }

                R.id.nav_late_approve -> {
                    loadUI(R.id.nav_late_approve)
                }


                R.id.nav_leave_allocation -> {
                    loadUI(R.id.nav_leave_allocation)
                }


                R.id.nav_tour_bill_list -> {
                    loadUI(R.id.nav_tour_bill_list)
                }


                R.id.nav_tour_approve -> {
                    loadUI(R.id.nav_tour_approve)
                }


                R.id.nav_tour_expense -> {
                    loadUI(R.id.nav_tour_expense)
                }


                R.id.nav_tour_expense_list -> {
                    loadUI(R.id.nav_tour_expense_list)
                }


                R.id.nav_call_approve -> {
                    loadUI(R.id.nav_call_approve)
                }

                R.id.nav_retiler_list -> {
                    loadUI(R.id.nav_retiler_list)
                }

/*
                R.id.nav_retiler_home -> {
                    loadUI(R.id.nav_retiler_home)
                }

 */
                R.id.nav_other_details -> {
                    loadUI(R.id.nav_other_details)
                }
                /*
                R.id.nav_payment_save -> {
                    loadUI(R.id.nav_payment_save)
                }

                 */
                /*
                R.id.nav_payment_list -> {
                    loadUI(R.id.nav_payment_list)
                }

                 */
                R.id.nav_meal_application -> {
                    loadUI(R.id.nav_meal_application)
                }
                R.id.nav_meal_cost_report -> {
                    loadUI(R.id.nav_meal_cost_report)
                }
                R.id.nav_movement_plan -> {
                    loadUI(R.id.nav_movement_plan)
                }
                R.id.nav_app_movement_plan -> {
                    loadUI(R.id.nav_app_movement_plan)
                }
                R.id.nav_movement_plan_list -> {
                    loadUI(R.id.nav_movement_plan_list)
                }
//                R.id.nav_retailer_details -> {
//                    loadUI(R.id.nav_retailer_details)
//                }
                R.id.nav_party_entry -> {
                    loadUI(R.id.nav_party_entry)
                }
                /*
                R.id.nav_delivery_program -> {
                    loadUI(R.id.nav_delivery_program)
                }

                 */
                R.id.nav_demo_customerHome -> {
                    loadUI(R.id.nav_demo_customerHome)
                }
                R.id.nav_demo_siteentry -> {
                    loadUI(R.id.nav_demo_siteentry)
                }
                R.id.nav_direct_callrecord -> {
                    loadUI(R.id.nav_direct_callrecord)
                }
                R.id.nav_demo_allSiteList -> {
                    loadUI(R.id.nav_demo_allSiteList)
                }
                R.id.nav_Frag_SiteTag -> {
                    loadUI(R.id.nav_Frag_SiteTag)
                }
                R.id.nav_taged_Call_Record_Entry -> {
                    loadUI(R.id.nav_taged_Call_Record_Entry)
                }
                R.id.nav_demo_Site_Home -> {
                    loadUI(R.id.nav_demo_Site_Home)
                }
                R.id.nav_emp_work_sum -> {
                    loadUI(R.id.nav_emp_work_sum)
                }
                R.id.nav_Site_Summery -> {
                    loadUI(R.id.nav_Site_Summery)
                }
                R.id.nav_derictcomplain -> {
                    loadUI(R.id.nav_derictcomplain)
                }
                R.id.nav_sitecomplain -> {
                    loadUI(R.id.nav_sitecomplain)
                }
                R.id.nav_demo_masonEntry -> {
                    loadUI(R.id.nav_demo_masonEntry)
                }
                R.id.nav_ref_callrecord -> {
                    loadUI(R.id.nav_ref_callrecord)
                }
                R.id.nav_engineer_database -> {
                    loadUI(R.id.nav_engineer_database)
                }
                R.id.nav_engineer_add_update -> {
                    loadUI(R.id.nav_engineer_add_update)
                }
                R.id.nav_contractor_database -> {
                    loadUI(R.id.nav_contractor_database)
                }
                R.id.nav_contractor_add_update -> {
                    loadUI(R.id.nav_contractor_add_update)
                }
                R.id.nav_meeting_room_booking -> {
                    loadUI(R.id.nav_meeting_room_booking)
                }
                R.id.nav_meeting_room_booking_list -> {
                    loadUI(R.id.nav_meeting_room_booking_list)
                }
                R.id.nav_car_booking -> {
                    loadUI(R.id.nav_car_booking)
                }
                R.id.nav_car_booking_List -> {
                    loadUI(R.id.nav_car_booking_List)
                }
                R.id.nav_mason_database -> {
                    loadUI(R.id.nav_mason_database)
                }
                R.id.nav_mason_update -> {
                    loadUI(R.id.nav_mason_update)
                }
                R.id.nav_site_update -> {
                    loadUI(R.id.nav_site_update)
                }
                R.id.nav_site_progress -> {
                    loadUI(R.id.nav_site_progress)
                }
                R.id.nav_masonWorkSum -> {
                    loadUI(R.id.nav_masonWorkSum)
                }
                R.id.nav_engWorkSum -> {
                    loadUI(R.id.nav_engWorkSum)
                }
                R.id.nav_conWorkSum -> {
                    loadUI(R.id.nav_conWorkSum)
                }
                R.id.nav_deliveryBagEntry -> {
                    loadUI(R.id.nav_deliveryBagEntry)
                }
                R.id.nav_runningOfferList -> {
                    loadUI(R.id.nav_runningOfferList)
                }


                else -> false
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }




}