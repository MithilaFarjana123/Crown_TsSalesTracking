package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.TourListAdapter
import com.crowncement.TsSalesTracking.data.Model.CommonSaveResponse
import com.crowncement.TsSalesTracking.data.Model.HierarchyChild
import com.crowncement.TsSalesTracking.data.Model.HierarchyResponse
import com.crowncement.TsSalesTracking.data.Model.TourResponseChild
import com.crowncement.TsSalesTracking.databinding.AddTravelBinding
import com.crowncement.TsSalesTracking.databinding.FragTravelListBinding
import com.crowncement.TsSalesTracking.services.locationTracking.TravelTrackingService

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

import java.util.*
import kotlin.collections.ArrayList


class Frag_Travel_List : Fragment() {

   // lateinit var rootView: View

    private var _binding: FragTravelListBinding? = null
    private val binding get() = _binding!!

    lateinit var tourViewModel: TourViewModel
    lateinit var leaveViewModel: LeaveViewModel
    lateinit var user: String
    private var notificationSendTo = ""
    private var userSubOrdinateList: java.util.ArrayList<HierarchyChild> = java.util.ArrayList()


    private lateinit var loadingAnim: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initiateUI(inflater, container)
/*
        onEventClickListener()

 */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        onEventClickListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun onEventClickListener() {

        binding.fabAddTour.setOnClickListener {
            travelEntry()
        }
    }

    private fun initiateUI(inflater: LayoutInflater, container: ViewGroup?) {
       // rootView = inflater.inflate(R.layout.frag_travel_list, container, false)


        _binding = FragTravelListBinding.inflate(inflater, container, false)


        binding.shimmerTravelLayout.visibility = View.VISIBLE
        binding.shimmerTravelLayout.startShimmer()

        TourViewModelFactory()
        LeaveViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        getTourList(user, Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())
        // getTourList("E11-002260", "2022-11-01 13:35:15")

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireContext()),
            "P l e a s e    w a i t"
        )
    }

    private fun setupViewModel() {
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)

        leaveViewModel = ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
            .get(LeaveViewModel::class.java)
    }

    private fun getTourList(empId: String, dateTime: String) {
        tourViewModel.getTourList(empId, dateTime)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                binding.noTravelData.visibility = View.GONE
                                binding.rvTravel.visibility = View.VISIBLE
                                prepareTourList(res.data)
                            } else {
                                binding.noTravelData.visibility = View.VISIBLE
                                binding.rvTravel.visibility = View.GONE
                            }
                        }
                        binding.shimmerTravelLayout.visibility = View.GONE
                        binding.shimmerTravelLayout.stopShimmer()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                        binding.shimmerTravelLayout.visibility = View.GONE
                        binding.shimmerTravelLayout.stopShimmer()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun prepareTourList(items: ArrayList<TourResponseChild>) {

        if (items.size > 0) {
            binding.noTravelData.visibility = View.GONE
            binding.rvTravel.visibility = View.VISIBLE

            val itemAdapter = TourListAdapter(items)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

            binding.rvTravel.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvTravel.layoutManager = manager
            binding.rvTravel.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                TourListAdapter.OnAdapterItemClickListener {


                override fun onStartTour(v: View?, position: Int) {

                    confirmStart(items[position])
                }

                override fun OnStopTour(v: View?, position: Int) {

                    confirmStop(items[position])
                }


                override fun OnFinishExpense(v: View?, position: Int) {

                    confirmExpenseClaim(items[position])
                }

                override fun OnExpenseClaim(v: View?, position: Int) {
                    try {
                        val gson = Gson()
                        val json = gson.toJson(items[position])
                        Utility.storeKeyValue(
                            requireActivity(),
                            "selectedExpenseInfo",
                            json.toString()
                        )
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_tour_expense_list)
                    } catch (e: Exception) {
                        Log.e("Travel_List", "OnExpenseClaim: ${e.message.toString()}")
                    }

                }

            })
        } else {
            binding.noTravelData.visibility = View.VISIBLE
            binding.rvTravel.visibility = View.GONE
        }

    }

    private fun travelEntry() {

        val v = AddTravelBinding.inflate(layoutInflater)

        // Create BottomSheetDialog


       // val v: View = layoutInflater.inflate(R.layout.add_travel, null)
        val dialog = BottomSheetDialog(requireContext())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        var from = ""
        var to = ""

        getUserHierarchy(user, Utility.getCurrentDate("yyyy-MM-dd").toString(), "Leave", v)

        dialog.setContentView(v.root)
        dialog.setCancelable(true)
        dialog.show()

        v.txtTrNo.setOnClickListener { view1: View? -> dialog.dismiss() }

        v.btnTrApply.setOnClickListener {

            if (saveUIValidation(v)) {
                dialog.dismiss()
                loadingAnim.show()
                applyTravelApplication(
                    user,
                    from,
                    to,
                    Utility.getCurrentDate("yyyy-MM-dd").toString(),
                    v.txtAgenda.text.toString(),
                    v.txtTrRemarks.text.toString(),
                    notificationSendTo,
                    v.txtCountry.text.toString(),
                    v.txtTAddress.text.toString()
                )
            }
        }

        v.txtTrFrom.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    from = selectedDate
                    v.txtTrFrom.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }

        v.txtTrTo.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    to = selectedDate
                    v.txtTrTo.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }
    }

    private fun getUserHierarchy(userName: String, dateTime: String, type: String, v: AddTravelBinding) {
        leaveViewModel.getUserHierarchy(userName, dateTime, type)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            successUserHierarchy(res, v)
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

    //todo
    private fun successUserHierarchy(res: HierarchyResponse, v: AddTravelBinding) {
        if (res.code.equals("200")) {
            v.btnTrApply.isEnabled = true
            if(res.data.size>0){
                notificationSendTo = res.data[0].repCode.toString()
                userSubOrdinateList = res.data

            }


            if (res.data.size > 1) {
                var keys = ""
                for (item in res.data) {
                    keys += item.repName + ">"
                }
                v.txtTrAuthority.setText(removeLastCharacter(keys))
            } else if(res.data.size>0){
                v.txtTrAuthority.setText(res.data[0].repName)
            }
        } else {
            v.btnTrApply.isEnabled = false
            Toast.makeText(requireContext(), "User hierarchy not found", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeLastCharacter(str: String?): String? {
        var str = str
        if (str != null && str.isNotEmpty() && str[str.length - 1] == '>') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }

    private fun saveUIValidation(
        v: AddTravelBinding

    ): Boolean {
        var res = false
        v.layoutTrFrom.isErrorEnabled = false
        v.layoutTrFrom.error = null
        v.layoutTrTo.isErrorEnabled = false
        v.layoutTrTo.error = null
        v.layoutAgenda.isErrorEnabled = false
        v.layoutAgenda.error = null

        v.layoutCountry.isErrorEnabled = false
        v.layoutCountry.error = null
        v.layoutTAddress.isErrorEnabled = false
        v.layoutTAddress.error = null

        when {

            v.txtCountry.text.toString().isEmpty() -> {
                v.layoutCountry.isErrorEnabled = true
                v.layoutCountry.error = "Country name is required"
                res = false
            }

            v.txtTAddress.text.toString().isEmpty() -> {
                v.layoutTAddress.isErrorEnabled = true
                v.layoutTAddress.error = "Tour address is required"
                res = false
            }


            v.txtTrFrom.text.toString().isEmpty() -> {
                v.layoutTrFrom.isErrorEnabled = true
                v.layoutTrFrom.error = "This date is required"
                res = false
            }
            v.txtTrTo.text.toString().isEmpty() -> {
                v.layoutTrTo.isErrorEnabled = true
                v.layoutTrTo.error = "This date is required"
                res = false
            }
            v.txtAgenda.text.toString().isEmpty() -> {
                v.layoutAgenda.isErrorEnabled = true
                v.layoutAgenda.error = "Provide your travel agenda"
                res = false
            }
            else -> {
                res = true
            }
        }
        return res
    }

    private fun applyTravelApplication(
        user_id: String,
        tr_fdate: String,
        tr_tdate: String,
        tr_iss_date: String,
        tr_purpose: String,
        tr_rem: String,
        tr_not_to: String,
        country: String,
        address: String

    ) {
        tourViewModel.saveTour(
            user_id,
            tr_fdate,
            tr_tdate,
            tr_iss_date,
            tr_purpose,
            tr_rem,
            tr_not_to,
            country,
            address
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {


                        it.responseData?.let { res ->

                            saveApplicationResponse(res)
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

    private fun saveApplicationResponse(response: CommonSaveResponse) {

        when (response.code) {
            "200" -> {
                loadingAnim.dismiss()
                Utility.getBaseMessage(
                    requireActivity(),
                    "Successful",
                    "Your application has been sent to your reporting boss.",
                    R.drawable.ic_checked_green,
                    1
                )
            }
            "500" -> {
                loadingAnim.dismiss()
                Utility.getBaseMessage(
                    requireActivity(),
                    "Failed",
                    response.message.toString(),
                    R.drawable.error_white,
                    0
                )
            }
            else -> {
                loadingAnim.dismiss()
                Utility.getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Sorry failed to process your travel application.",
                    R.drawable.error_white,
                    2
                )
            }
        }

        binding.shimmerTravelLayout.visibility = View.VISIBLE
        binding.shimmerTravelLayout.startShimmer()
        getTourList(user, Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())

    }

    private fun confirmStart(child: TourResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.start_travel_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancel: MaterialButton = v.findViewById(R.id.btnCancel)
        val btnStart: MaterialButton = v.findViewById(R.id.btnStart)

        val cancel: TextView = v.findViewById(R.id.cancel)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.hide()
        }

        btnCancel.setOnClickListener {
            dialog.hide()
        }

        btnStart.setOnClickListener {
            dialog.hide()
            startUserTour(user, child.trTrno.toString())
        }
    }

    private fun confirmStop(child: TourResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.stop_travel_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancel: MaterialButton = v.findViewById(R.id.btnCancelE)
        val btnStart: MaterialButton = v.findViewById(R.id.btnStartE)

        val cancel: TextView = v.findViewById(R.id.cancelE)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.hide()
        }

        btnCancel.setOnClickListener {
            dialog.hide()
        }

        btnStart.setOnClickListener {
            dialog.hide()
            stopUserTour(user, child.trTrno.toString())
        }

    }

    private fun startUserTour(empId: String, tourNo: String) {
        loadingAnim.show()
        tourViewModel.tourStart(empId, tourNo)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                Utility.storeKeyValue(
                                    requireActivity(),
                                    "Tour",
                                    tourNo + "|" + Utility.getCurrentDate("EEE, MMM.dd.yyyy")
                                )
                                Utility.storeKeyValue(
                                    requireActivity(),
                                    "Is_Travel_Tracking_Running",
                                    "YES"
                                )


                                val intent =
                                    Intent(requireContext(), TravelTrackingService::class.java)
                                intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    requireContext().startForegroundService(intent)
                                } else {
                                    requireContext().startService(intent)
                                }


                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Your tour has been started",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                getTourList(
                                    user,
                                    Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString()
                                )

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Error",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
                                )
                                loadingAnim.dismiss()

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

    private fun stopUserTour(empId: String, tourNo: String) {
        loadingAnim.show()
        tourViewModel.tourStop(empId, tourNo)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                Utility.removePreference(requireActivity(), "Tour")
                                Utility.removePreference(
                                    requireActivity(),
                                    "Is_Travel_Tracking_Running"
                                )

                                //CALL STOP SERVICE
                                val intent = Intent(requireContext(), TravelTrackingService::class.java)
                                requireActivity().stopService(intent)


                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Tour Completed",
                                    "Your tour has been marked as completed",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                getTourList(
                                    user,
                                    Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString()
                                )

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Error",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
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

    private fun confirmExpenseClaim(child: TourResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.stop_expense_claim, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancel: MaterialButton = v.findViewById(R.id.btnCancelC)
        val btnStart: MaterialButton = v.findViewById(R.id.btnStartC)

        val cancel: TextView = v.findViewById(R.id.cancelC)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.hide()
        }

        btnCancel.setOnClickListener {
            dialog.hide()
        }

        btnStart.setOnClickListener {
            dialog.hide()
            stopExpenseClaim(user, child.trTrno.toString())
        }

    }

    private fun stopExpenseClaim(empId: String, tourNo: String) {
        loadingAnim.show()
        tourViewModel.stopExpense(empId, tourNo)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )
                                getTourList(
                                    user,
                                    Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString()
                                )

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Error",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
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