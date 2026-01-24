package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.UI.viewmodel.AttendanceViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.AttendanceViewModelFactory
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getAttendancePeriod
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Adapter.AttendanceReportAdapter
import com.crowncement.TsSalesTracking.data.Model.AttendanceReport
import com.crowncement.TsSalesTracking.data.Model.AttendanceReportChild
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.FragAttendenceReportBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Frag_AttendenceReport : Fragment() {

    //   lateinit var rootView: View

    private var _binding: FragAttendenceReportBinding? = null
    private val binding get() = _binding!!

    lateinit var atteViewModel: AttendanceViewModel
    lateinit var shimmer_att_container : ShimmerFrameLayout
    lateinit var txtAttNoData : LottieAnimationView
    lateinit var rvAttendance : RecyclerView

    lateinit var user: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //    rootView = inflater.inflate(R.layout.frag_attendence_report, container, false)


        _binding = FragAttendenceReportBinding.inflate(inflater, container, false)





        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        shimmer_att_container = binding.root.findViewById(R.id.shimmer_att_container)
        shimmer_att_container.visibility = View.VISIBLE
        shimmer_att_container.startShimmer()

        txtAttNoData = binding.root.findViewById(R.id.txtAttNoData)
        rvAttendance = binding.root.findViewById(R.id.rvAttendance)

        // rootView.shimmer_att_container.visibility = View.VISIBLE
        // rootView.shimmer_att_container.startShimmer()

        AttendanceViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        val timePeriod = getAttendancePeriod().split(",")




        //  getUserAttList(user, "2023-08-06", "2023-08-07")
        getUserAttList(user, timePeriod[0], timePeriod[1])
        setProfileInformation()


        binding.searchFab.setOnClickListener {

            searchDate()
        }


        binding.rvAttendance.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.searchFab.isShown) binding.searchFab.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) binding.searchFab.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    


    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory()
        ).get(AttendanceViewModel::class.java)
    }


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

                        shimmer_att_container.visibility = View.GONE
                        shimmer_att_container.stopShimmer()

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun successAttList(res: AttendanceReport) {

        if (res.code == "200") {
            if (res.data.isNotEmpty()) {

                txtAttNoData.visibility = View.GONE
                rvAttendance.visibility = View.VISIBLE

                prepareAttendanceRV(res.data)
            } else {
                txtAttNoData.visibility = View.VISIBLE
                rvAttendance.visibility = View.GONE
            }
        }

        shimmer_att_container.visibility = View.GONE
        shimmer_att_container.stopShimmer()
    }


    private fun prepareAttendanceRV(items: ArrayList<AttendanceReportChild>) {
        val itemAdapter = AttendanceReportAdapter(items)
        if (isAdded) {

            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())


            rvAttendance.layoutManager = rLayoutmanager
            val manager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            rvAttendance.layoutManager = manager
            rvAttendance.adapter = itemAdapter

            rvAttendance.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        itemAdapter.setOnItemClickListener(object :
            AttendanceReportAdapter.OnAdapterItemClickListener {
            override fun OnSelectSubMenu(v: View?, position: Int) {

            }
        })
    }

    private fun searchDate() {
        val v: View = layoutInflater.inflate(R.layout.search_date_dialog, null)
        val dialog = BottomSheetDialog(requireActivity())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val imgClosesSearch = v.findViewById<ImageView>(R.id.imgClosesSearch)
        val txtFrSearchDate = v.findViewById<TextInputEditText>(R.id.txtFrSearchDate)
        val txtTSearchDate  = v.findViewById<TextInputEditText>(R.id.txtTSearchDate)
        val btnSearchAtt    = v.findViewById<Button>(R.id.btnSearchAtt)
        val layoutSearchF   = v.findViewById<TextInputLayout>(R.id.layoutSearchF)
        val layoutSearchT   = v.findViewById<TextInputLayout>(R.id.layoutSearchT)
        val txtErrorSearch  = v.findViewById<TextView>(R.id.txtErrorSearch)

        var fromDate = ""
        var toDate = ""

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgClosesSearch.setOnClickListener { dialog.hide() }



        txtFrSearchDate.setOnClickListener {
            layoutSearchF.isErrorEnabled = false
            layoutSearchT.isErrorEnabled = false
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    fromDate = selectedDate
                    txtFrSearchDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MMM-yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }

        txtTSearchDate.setOnClickListener {

            layoutSearchF.isErrorEnabled = false
            layoutSearchT.isErrorEnabled = false

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    toDate = selectedDate
                    if (isGraterThan30Days(fromDate, toDate)) {
                        txtErrorSearch.visibility = View.VISIBLE
                        txtFrSearchDate.setText("")
                        txtTSearchDate.setText("")
                    } else {
                        txtErrorSearch.visibility = View.GONE
                        txtTSearchDate.setText(
                            Utility.changeDateFormat(
                                selectedDate,
                                "yyyy-MM-dd",
                                "dd-MMM-yyyy"
                            )
                        )
                    }


                }, year, month, day
            )
            picker.show()
        }

        btnSearchAtt.setOnClickListener {

            if ((fromDate != "") && (toDate != "")) {
                getUserAttList(user, fromDate, toDate)

                // getUserAttList("E00-005445", fromDate, toDate)
                dialog.dismiss()
            } else {
                when {
                    txtFrSearchDate.text.isNullOrBlank() -> {
                        layoutSearchF.error = "Select from date"
                        layoutSearchF.isErrorEnabled = true
                        layoutSearchF.setErrorIconDrawable(R.drawable.ic_baseline_error_orange)
                    }
                    txtTSearchDate.text.isNullOrBlank() -> {
                        layoutSearchT.error = "Select to date"
                        layoutSearchT.isErrorEnabled = true
                        layoutSearchT.setErrorIconDrawable(R.drawable.ic_baseline_error_orange)
                    }
                }
            }
        }

    }


    private fun isGraterThan30Days(from: String, to: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val fDate: Date = sdf.parse(from)!!
        val tDate: Date = sdf.parse(to)!!
        val diff: Long = tDate.time - fDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 30

    }




    private fun setProfileInformation() {
        try {
            val profile: Data = Utility.getUserInfo(requireActivity())!!
            //     var v = profile
            binding.txtEMPName.text = profile.userName
            binding.txtEMPDEsignation.text = profile.userDesig +" | "+ Utility.getValueByKey(requireActivity(),"username")
            binding.txtAttDept.text = "Department : "+ profile.userDept


            //SET IMAGE
            if (!(profile.userProfileImagePath.toString() == "")) {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    // .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(binding.attendenceUserImg)
            }


        } catch (e: Exception) {
            Utility.commonToast(requireActivity(), "Error", e.message.toString(), 0)
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







