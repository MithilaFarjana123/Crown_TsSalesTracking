package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.common.Utility.getDayCountBetweenDates
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.data.Adapter.LeaveApplicationListAdapter
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.databinding.AddLeaveBinding
import com.crowncement.TsSalesTracking.databinding.FragLeaveListBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.tapadoo.alerter.Alerter
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Frag_leave_list : Fragment() {

 //   lateinit var rootView: View
    private var _binding: FragLeaveListBinding? = null
    private val binding get() = _binding!!
    lateinit var pieChart: PieChart


    private val TAG = "Frag_Leave_List"
    private var lvType: String = ""

    var PICK_IMAGE_MULTIPLE = 1
    private lateinit var leaveViewModel: LeaveViewModel
    private lateinit var selectedLeaveInfo: LeaveTypeSummaryChild
    private var userLeaveAllocationList: ArrayList<LeaveTypeSummaryChild> = ArrayList()

    private lateinit var loadingAnim: Dialog




    private var notificationSendTo = ""
    private var from = ""
    private var to = ""
    private var user = ""

    val REQUEST_CODE = 200

    var imagePath = ""
    var imagesPathList: ArrayList<String> = arrayListOf()

    var countDays: Int = 0

    var images: ArrayList<File> = ArrayList()

    private var userSubOrdinateList: ArrayList<HierarchyChild> = ArrayList()

    var attachedView: View? = null
    var Img1 :ImageView? = null
    var Img2 :ImageView? = null
    var Img3 :ImageView? = null



    var isFirstTime = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // rootView = inflater.inflate(R.layout.frag_leave_list, container, false)

        _binding = FragLeaveListBinding.inflate(inflater, container, false)

/*
        loadingAnim = Utility.baseLoadingAnimation(requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t\n your leave application is applying")

        binding.shimmerLeaveContainer.visibility = View.VISIBLE

        binding.shimmerLeaveContainer.startShimmer()

        LeaveViewModelFactory()

        setupViewModel()

        user = getValueByKey(requireActivity(), "username").toString()
        getUserLeaveTypeInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())

        binding.fab.setOnClickListener {
            leaveApplyEntry(selectedLeaveInfo)
            //  openGalleryForImages()
        }

*/


        //   piechart()

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        loadingAnim = Utility.baseLoadingAnimation(requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t\n your leave application is applying")

        binding.shimmerLeaveContainer.visibility = View.VISIBLE

        binding.shimmerLeaveContainer.startShimmer()

        LeaveViewModelFactory()

        setupViewModel()

        user = getValueByKey(requireActivity(), "username").toString()
        getUserLeaveTypeInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())

        binding.fab.setOnClickListener {
            leaveApplyEntry(selectedLeaveInfo)
            //  openGalleryForImages()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }


    fun piechart(alocation : Float,rem :Float,enjoyed : Float ){

        // on below line we are initializing our
        // variable with their ids.

        pieChart = binding.root.findViewById(R.id.pieChart)

        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(enjoyed)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(alocation))
        entries.add(PieEntry(rem))
        entries.add(PieEntry(enjoyed))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.blue))
        colors.add(resources.getColor(R.color.light_green))
        colors.add(resources.getColor(R.color.light_orange))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

    }


    private fun setupViewModel() {
        leaveViewModel = ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
            .get(LeaveViewModel::class.java)
    }



    private fun leaveApplyEntry(leaveInfo: LeaveTypeSummaryChild) {
        val binding: AddLeaveBinding = AddLeaveBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnApply = binding.btnApply
        val btnNo = binding.txtNo
        val fromDate = binding.txtFromDate
        val txtAllocationStart = binding.txtAllocationStart
        val txtAllocationEnd = binding.txtAllocationEnd
        val txtDayCount = binding.txtDayCount
        var subOrdinate: String = ""

        setYearAllocationStartEnd(txtAllocationStart, txtAllocationEnd)
        // -----------------CHECK user hierarchy------------------------------------------------
        // val user = Utility.getValueByKey(requireActivity(), "username")
        getUserHierarchy(user, getCurrentDate("yyyy-MM-dd").toString(), "Leave", binding)
        checkSubordinate(user, getCurrentDate("yyyy-MM-dd").toString(), "Leave", binding)
        // --------------------------------------------------------------------------------------

        val toDate = binding.txtToDate
        val acLeaveType = binding.acLeaveType
        val acLVSubordinate = binding.acLVSubordinate
        val layoutLeaveType = binding.errorLayout

        fromDate.setText(getCurrentDate("dd MMM yyyy"))
        from = getCurrentDate("yyyy-MM-dd").toString()
        to = getCurrentDate("yyyy-MM-dd").toString()
        toDate.setText(getCurrentDate("dd MMM yyyy"))

        val count = getDayCountBetweenDates(
            getCurrentDate("yyyy-MM-dd").toString(),
            getCurrentDate("yyyy-MM-dd").toString()
        )
        txtDayCount.setText(if (count > 1) "${count + 1} Days" else "${count + 1} Day")

        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.show()

        //todo  added for set img
        attachedView = binding.root
        Img1 =  binding.layoutMedicalDocuments.img1
        Img2 =  binding.layoutMedicalDocuments.img2
        Img3 =  binding.layoutMedicalDocuments.img3


        btnNo.setOnClickListener { dialog.dismiss() }

        btnApply.setOnClickListener {
            if (saveUIValidation(binding, leaveInfo, dialog)) {
                btnApply.isEnabled = false
                subOrdinate = if (binding.layoutSubOrdinate.isVisible && userSubOrdinateList.isNotEmpty()) {
                    userSubOrdinateList[0].repCode.toString()
                } else {
                    ""
                }

                val noiTO = if (userSubOrdinateList.isNotEmpty()) {
                    userSubOrdinateList[0].repCode.toString()
                } else {
                    ""
                }

                loadingAnim.show()

                saveLeaveEntry(
                    user,
                    getCurrentDate("yyyy-MM-dd").toString(),
                    from,
                    to,
                    binding.acLeaveType.text.toString(),
                    binding.txtPurpose.text.toString(),
                    binding.txtAddress.text.toString(),
                    binding.txtMobile.text.toString(),
                    subOrdinate,
                    noiTO,
                    binding,
                    dialog,
                    images
                )
            }
        }

        binding.layoutMedicalDocuments.imgAddImages.setOnClickListener {
            openGalleryForImages()
        }

        fromDate.setOnClickListener {
            layoutLeaveType.visibility = View.GONE
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { _, year1, monthOfYear, dayOfMonth ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    from = selectedDate
                    fromDate.setText(changeDateFormat(selectedDate, "yyyy-MM-dd", "dd MMM yyyy"))
                    toDate.setText("")
                }, year, month, day
            )
            picker.show()
        }

        toDate.setOnClickListener {
            binding.layoutToDate.isErrorEnabled = false
            binding.layoutToDate.error = null

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { _, year1, monthOfYear, dayOfMonth ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    to = selectedDate

                    toDate.setText(changeDateFormat(selectedDate, "yyyy-MM-dd", "dd MMM yyyy"))

                    if (fromDate.text!!.isNotEmpty()) {
                        countDays = getDayCountBetweenDates(
                            changeDateFormat(fromDate.text.toString(), "dd MMM yyyy", "yyyy-MM-dd").toString(),
                            selectedDate
                        )
                        txtDayCount.setText(if (countDays + 1 > 1) "${countDays + 1} Days" else "${countDays + 1} Day")

                        val leaveRemain = (leaveInfo.lvDue!!.toInt()) - (leaveInfo.lvTkn!!.toInt())
                        if (countDays + 1 > leaveRemain) {
                            layoutLeaveType.visibility = View.VISIBLE
                            fromDate.setText("")
                            toDate.setText("")
                        } else {
                            layoutLeaveType.visibility = View.GONE
                        }

                        if ((countDays + 1) >= leaveInfo.lvMinlv!!.toInt() && leaveInfo.lvCode == "ML") {
                            binding.layoutMedicalDocuments.medicalDocuments.visibility = View.VISIBLE
                        } else {
                            binding.layoutMedicalDocuments.medicalDocuments.visibility = View.GONE
                        }
                    }
                }, year, month, day
            )
            picker.show()
        }

        val items = userLeaveAllocationList.map { it.lvCode.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        acLeaveType.setAdapter(adapter)

        userLeaveAllocationList.find { leaveInfo.lvCode == it.lvCode }?.let {
            acLeaveType.setText(leaveInfo.lvCode.toString())
        }

        adapter.notifyDataSetChanged()

        acLeaveType.setOnItemClickListener { _, _, _, _ ->
            fromDate.setText("")
            toDate.setText("")
        }

        acLVSubordinate.setOnItemClickListener { _, _, position, _ ->
            if (position > 0 && userSubOrdinateList.isNotEmpty()) {
                val subordinate = userSubOrdinateList[position]
                acLVSubordinate.setText("${subordinate.repCode} | ${subordinate.repName}")
                subOrdinate = subordinate.repCode.toString()
            }
        }
    }

    private fun checkSubordinate(userName: String, dateTime: String, type: String, binding: AddLeaveBinding) {
        leaveViewModel.checkSubordinate(userName, dateTime, type)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                binding.layoutSubOrdinate.visibility = View.VISIBLE
                                val items = userSubOrdinateList.map { it.repName.toString() }
                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    items
                                )
                                binding.acLVSubordinate.setAdapter(adapter)
                                if (userSubOrdinateList.isNotEmpty()) {
                                    binding.acLVSubordinate.setText(userSubOrdinateList[0].repName)
                                }
                                adapter.notifyDataSetChanged()
                            } else {
                                binding.layoutSubOrdinate.visibility = View.GONE
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


//todo 1
    /*
    private fun leaveApplyEntry(leaveInfo: LeaveTypeSummaryChild) {
        val v: View = layoutInflater.inflate(R.layout.add_leave, null)
        val dialog = BottomSheetDialog(requireContext())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnApply = v.findViewById<Button>(R.id.btnApply)
        val btnNo = v.findViewById<ImageView>(R.id.txtNo)
        val fromDate = v.findViewById<TextInputEditText>(R.id.txtFromDate)
        val txtAllocationStart = v.findViewById<TextInputEditText>(R.id.txtAllocationStart)
        val txtAllocationEnd = v.findViewById<TextInputEditText>(R.id.txtAllocationEnd)
        val txtDayCount = v.findViewById<TextInputEditText>(R.id.txtDayCount)
        var subOrdinate: String = ""
        setYearAllocationStartEnd(txtAllocationStart, txtAllocationEnd)
        //-----------------CHECK user hierarchy----------------------------------------------------------
        //val user = Utility.getValueByKey(requireActivity(), "username")
        getUserHierarchy(user, getCurrentDate("yyyy-MM-dd").toString(), "Leave", v)
        checkSubordinate(user, getCurrentDate("yyyy-MM-dd").toString(), "Leave", v)
        //-----------------------------------------------------------------------------------------------

        val toDate = v.findViewById<TextInputEditText>(R.id.txtToDate)
        val acLeaveType = v.findViewById<AutoCompleteTextView>(R.id.acLeaveType)
        val acLVSubordinate = v.findViewById<AutoCompleteTextView>(R.id.acLVSubordinate)
        val layoutLeaveType = v.findViewById<TextView>(R.id.errorLayout)

        fromDate.setText(getCurrentDate("dd MMM yyyy"))
        from = getCurrentDate("yyyy-MM-dd").toString()
        to = getCurrentDate("yyyy-MM-dd").toString()
        toDate.setText(getCurrentDate("dd MMM yyyy"))

        val count = getDayCountBetweenDates(
            getCurrentDate("yyyy-MM-dd").toString(),
            getCurrentDate("yyyy-MM-dd").toString()
        )
        if (count > 1) {
            txtDayCount.setText((count + 1).toString() + " Days")
        } else {
            txtDayCount.setText((count + 1).toString() + " Day")
        }

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        attachedView = v

        btnNo.setOnClickListener { view1: View? -> dialog.dismiss() }

        btnApply.setOnClickListener {

            if (saveUIValidation(v, leaveInfo, dialog)) {

                btnApply.isEnabled = false
                subOrdinate = ""
                if (v.layoutSubOrdinate.isVisible) {
                    if(userSubOrdinateList.size>0){
                        subOrdinate = userSubOrdinateList[0].repCode.toString()
                    }
                    else
                    {
                        subOrdinate=""
                    }

                }
                var noiTO=""
                noiTO = if(userSubOrdinateList.size>0){
                    userSubOrdinateList[0].repCode.toString()
                } else {
                    ""
                }

                loadingAnim.show()

                saveLeaveEntry(
                    user,
                    getCurrentDate("yyyy-MM-dd").toString(),
                    from,
                    to,
                    v.acLeaveType.text.toString(),
                    v.txtPurpose.text.toString(),
                    v.txtAddress.text.toString(),
                    v.txtMobile.text.toString(),
                    subOrdinate,
                    // userSubOrdinateList[0].repCode.toString(),
                    noiTO,
                    v,
                    dialog,
                    images
                )
            }
        }

        v.imgAddImages.setOnClickListener {

            openGalleryForImages()
        }

        fromDate.setOnClickListener {
            layoutLeaveType.visibility = View.GONE
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
                    fromDate.setText(
                        changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                    toDate.setText("")
                }, year, month, day
            )
            picker.show()

        }

        toDate.setOnClickListener {
            v.layoutToDate.isErrorEnabled = false
            v.layoutToDate.error = null

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

                    toDate.setText(
                        changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                    if (fromDate.text!!.isNotEmpty()) {
                        countDays = 0
                        countDays = getDayCountBetweenDates(
                            changeDateFormat(
                                fromDate.text.toString(),
                                "dd MMM yyyy",
                                "yyyy-MM-dd"
                            ).toString(), selectedDate
                        )
                        if (countDays + 1 > 1) {
                            txtDayCount.setText((countDays + 1).toString() + " Days")
                        } else {
                            txtDayCount.setText((countDays + 1).toString() + " Day")
                        }

                        val leaveRemain: Int =
                            (leaveInfo.lvDue!!.toInt()) - (leaveInfo.lvTkn!!.toInt())
                        if (countDays+1 > leaveRemain) {
                            layoutLeaveType.visibility = View.VISIBLE
                            fromDate.setText("")
                            toDate.setText("")
                        } else {
                            layoutLeaveType.visibility = View.GONE
                        }
                        //Document addition
                        val medicalMaxAllocation = leaveInfo.lvMinlv!!.toInt()

                        if ((countDays + 1) >= medicalMaxAllocation && leaveInfo.lvCode == "ML") {
                            v.layoutMedicalDocuments.visibility = View.VISIBLE
                        } else {
                            v.layoutMedicalDocuments.visibility = View.GONE
                        }
                    }
                }, year, month, day
            )
            picker.show()
        }

        // autocomplete spinner---------------------------------------------------------------

        val items: ArrayList<String> = ArrayList()
        for (item in userLeaveAllocationList) {
            items.add(item.lvCode.toString())
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        acLeaveType?.setAdapter(adapter)

        for (item in userLeaveAllocationList) {
            if (leaveInfo.lvCode == item.lvCode) {
                acLeaveType.setText(leaveInfo.lvCode.toString())
            }
        }

        adapter.notifyDataSetChanged()

        acLeaveType.setOnItemClickListener { parent, arg1, position, arg3 ->
            fromDate.setText("")
            toDate.setText("")
            //  val item = parent.getItemAtPosition(position)
            //   if (item in items) {
            //  val student: StudentInfo = item as StudentInfo
            // doSomethingWith(student)
            // }
        }

        acLVSubordinate.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                val item = parent.getItemAtPosition(position)
                if (item in userSubOrdinateList) {

                    val subordinate: HierarchyChild = item as HierarchyChild
                    acLVSubordinate.setText(subordinate.repCode.toString() + " | " + subordinate.repName.toString())
                    subOrdinate = subordinate.repCode.toString()
                }
            }


        }
    }


    private fun checkSubordinate(userName: String, dateTime: String, type: String, v: View) {
        leaveViewModel.checkSubordinate(userName, dateTime, type)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                v.layoutSubOrdinate.visibility = View.VISIBLE

                                val items: ArrayList<String> = ArrayList()
                                for (item in userSubOrdinateList) {
                                    items.add(item.repName.toString())
                                }
                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    items
                                )
                                v.acLVSubordinate?.setAdapter(adapter)
                                if (userSubOrdinateList.size > 0) {
                                    v.acLVSubordinate.setText(userSubOrdinateList[0].repName)
                                }

                                adapter.notifyDataSetChanged()

                            } else {
                                v.layoutSubOrdinate.visibility = View.GONE
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


     */
//todo finish1
//-------------Image.........................

    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

    }

    private fun setYearAllocationStartEnd(
        txtAllocationStart: TextInputEditText?,
        txtAllocationEnd: TextInputEditText?
    ) {
        val currentYear: Int = getCurrentDate("yy")!!.toInt()
        txtAllocationStart!!.setText("1 Jan $currentYear")
        txtAllocationEnd!!.setText("31 Dec $currentYear")
    }

    private fun getUserHierarchy(userName: String, dateTime: String, type: String, v: AddLeaveBinding) {
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
    private fun successUserHierarchy(res: HierarchyResponse, v: AddLeaveBinding) {
        if (res.code.equals("200") &&  res.data.size>0) {
            v.btnApply.isEnabled = true
            // set message to ID
            notificationSendTo = res.data[0].repCode.toString()
            // Utility.storeKeyValue(requireActivity(), "NotiSentTo", notificationSendTo)
            //set value for subordinate list
            userSubOrdinateList = res.data

            if (res.data.size > 1) {
                var keys = ""
                for (item in res.data) {
                    keys += item.repName + ">"
                }
                v.txtAuthority.setText(removeLastCharacter(keys))
            } else {
                v.txtAuthority.setText(res.data[0].repName)
            }
        } else {
            v.btnApply.isEnabled = false
            Toast.makeText(requireContext(), "Leave application approval authority not found", Toast.LENGTH_LONG).show()
        }
    }
//todo finish


    private fun removeLastCharacter(str: String?): String? {
        var str = str
        if (str != null && str.isNotEmpty() && str[str.length - 1] == '>') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }

//todo
    private fun saveUIValidation(
    v: AddLeaveBinding,
    leaveInfo: LeaveTypeSummaryChild,
    dialog: Dialog
    ): Boolean {
        var res = false

        v.layoutFromDate.isErrorEnabled = false
        v.layoutFromDate.error = null

        v.layoutToDate.isErrorEnabled = false
        v.layoutToDate.error = null

        v.layoutPurpose.isErrorEnabled = false
        v.layoutPurpose.error = null
        v.layoutAddress.isErrorEnabled = false
        v.layoutAddress.error = null
        v.layoutMobile.isErrorEnabled = false
        v.layoutMobile.error = null


        when {

            v.txtFromDate.text.toString().isEmpty() -> {
                v.layoutFromDate.isErrorEnabled = true
                v.layoutFromDate.error = "From date is required"
                res = false
            }
            v.txtToDate.text.toString().isEmpty() -> {
                v.layoutToDate.isErrorEnabled = true
                v.layoutToDate.error = "To date is required"
                res = false
            }

            v.txtPurpose.text.toString().isEmpty() -> {
                v.layoutPurpose.isErrorEnabled = true
                v.layoutPurpose.error = "Leave purpose is required"
                res = false
            }

            v.txtAddress.text.toString().isEmpty() -> {
                v.layoutAddress.isErrorEnabled = true
                v.layoutAddress.error = "Leave address is required"
                res = false
            }
            v.txtMobile.text.toString().isEmpty() -> {
                v.layoutMobile.isErrorEnabled = true
                v.layoutMobile.error = "Mobile number is required"
                res = false
            }
            v.layoutSubOrdinate.isVisible && v.acLVSubordinate.text.toString().isEmpty() -> {
                v.layoutSubOrdinate.isErrorEnabled = true
                v.layoutSubOrdinate.error = "Please select a person in your absence"
                res = false
            }

            (countDays + 1 >= leaveInfo.lvMinlv!!.toInt()) && (leaveInfo.lvCode == "ML") -> {
                if (images.size == 0) {
                    res = false
                    onSNACK(
                        dialog.window!!.decorView,
                        "Please attach valid documents for your medical leave application"
                    )
                } else if (images.size > 3) {
                    res = false
                    onSNACK(dialog.window!!.decorView, "Please attache maximum three images")
                } else {
                    res = true
                }
            }
            else -> {
                res = true
            }
        }

        return res
    }

//todo finish

    fun onSNACK(view: View, mess: String) {
        val snackbar = Snackbar.make(view, mess, Snackbar.LENGTH_LONG).setAction("Dismiss", null)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#FC9E14"))
        val textView: TextView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.BLACK)
        textView.textSize = 14f
        snackbar.show()
    }

    private fun saveLeaveEntry(
        user_id: String,
        lv_date: String,
        lv_fdate: String,
        lv_tdate: String,
        lv_code: String,
        lv_purpose: String,
        lv_add: String,
        lv_phone: String,
        lv_ap_emp: String,
        lv_not_to: String,
        v: AddLeaveBinding,
        dialog: Dialog,
        allImages: ArrayList<File>
    ) {
        leaveViewModel.saveLeaveApplication(
            user_id,
            lv_date,
            lv_fdate,
            lv_tdate,
            lv_code,
            lv_purpose,
            lv_add,
            lv_phone,
            lv_ap_emp,
            lv_not_to,
            allImages
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            saveApplicationResponse(res, v, dialog)
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        dialog.dismiss()

                        Log.e(TAG, "saveLeaveEntry: " + it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        loadingAnim.dismiss()
                    }
                }
            }
    }



    private fun getUserLeaveTypeInfo(userName: String, dateTime: String) {
        if(isAdded) {
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

                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    private fun successLeaveType(res: LeaveTypeSummaryResponse) {
        if (res.code.equals("200")) {
            userLeaveAllocationList = ArrayList()
            userLeaveAllocationList = res.data



        }

        if (isFirstTime == 0) {
            setLeaveSummary()
            isFirstTime = 1
        } else {
            refreshSummary()
        }
    }
//todo
    private fun refreshSummary() {

        val leave = getValueByKey(requireActivity(), "selectedLvInfo")
        val gson = Gson()

        if (leave.toString().isNotEmpty()) {
            val leaveInfo = gson.fromJson(leave, LeaveTypeSummaryChild::class.java)
            //  selectedLeaveInfo = leaveInfo
            var child: LeaveTypeSummaryChild? = null

            for (items in userLeaveAllocationList) {
                if (items.lvCode == leaveInfo.lvCode) {
                    selectedLeaveInfo = items
                    child = items
                }
            }
            binding.leaveDetailsPiechart.txtAllocation.text = child!!.lvDue.toString()
            binding.leaveDetailsPiechart.txtEnjoyed.text = child.lvTkn.toString()


            // rootView.txtLeaveTitle.text = leaveInfo.lvCode + " Leave Allocation Summary"

            when (child.lvCode) {
                "ML" -> {
                    binding.leaveDetailsPiechart.txtLeaveTitle.text = "Medical Leave Allocation Summary"
                }
                "CL" -> {
                    binding.leaveDetailsPiechart.txtLeaveTitle.text = "Casual Leave Allocation Summary"
                }
                "EL" -> {
                    binding.leaveDetailsPiechart.txtLeaveTitle.text = "Earn Leave Allocation Summary"
                }
                else -> {
                    binding.leaveDetailsPiechart.txtLeaveTitle.text = "Alternative Leave Allocation Summary"
                }
            }

            val remain = child.lvDue!!.toInt() - child.lvTkn!!.toInt()

            binding.leaveDetailsPiechart.txtRem.text = remain.toString()

            //todo need to call pie chart here
            var alocation : Float = leaveInfo.lvDue.toString().toFloat()
            var rem: Float = remain.toString().toFloat()
            var enjoyed: Float = leaveInfo.lvTkn.toString().toFloat()

            piechart(alocation,rem,enjoyed)

            lvType = child.lvCode.toString()
            getUserLeaveList(user, getCurrentDate("yyyy")!!, leaveInfo.lvCode.toString())
        } else {
            lvType = "CL"
            getUserLeaveList(user, getCurrentDate("yyyy")!!, "CL")
        }

    }


    private fun setLeaveSummary() {
        if(isAdded) {
            val leave = getValueByKey(requireActivity(), "selectedLvInfo")


            val gson = Gson()

            if (leave.toString().isNotEmpty()) {
                val leaveInfo = gson.fromJson(leave, LeaveTypeSummaryChild::class.java)
                selectedLeaveInfo = leaveInfo

                binding.leaveDetailsPiechart.txtAllocation.text = leaveInfo.lvDue.toString()

                binding.leaveDetailsPiechart.txtEnjoyed.text = leaveInfo.lvTkn.toString()

                // rootView.txtLeaveTitle.text = leaveInfo.lvCode + " Leave Allocation Summary"

                when (leaveInfo.lvCode) {
                    "ML" -> {
                        binding.leaveDetailsPiechart.txtLeaveTitle.text =
                            "Medical Leave Allocation Summary"
                    }

                    "CL" -> {
                        binding.leaveDetailsPiechart.txtLeaveTitle.text =
                            "Casual Leave Allocation Summary"
                    }

                    "EL" -> {
                        binding.leaveDetailsPiechart.txtLeaveTitle.text =
                            "Earn Leave Allocation Summary"
                    }

                    else -> {
                        binding.leaveDetailsPiechart.txtLeaveTitle.text =
                            "Alternative Leave Allocation Summary"
                    }
                }

                val remain = leaveInfo.lvDue!!.toInt() - leaveInfo.lvTkn!!.toInt()

                binding.leaveDetailsPiechart.txtRem.text = remain.toString()
                lvType = leaveInfo.lvCode.toString()

                //todo need to call pie chart here
                var alocation: Float = leaveInfo.lvDue.toString().toFloat()
                var rem: Float = remain.toString().toFloat()
                var enjoyed: Float = leaveInfo.lvTkn.toString().toFloat()

                piechart(alocation, rem, enjoyed)

                getUserLeaveList(user, getCurrentDate("yyyy")!!, leaveInfo.lvCode.toString())
            } else {
                lvType = "CL"
                getUserLeaveList(user, getCurrentDate("yyyy")!!, "CL")
            }

        }

    }

    //todo finish


    private fun getUserLeaveList(empId: String, leaveYear: String, leaveType: String) {
        leaveViewModel.getLeaveList(empId, leaveYear, leaveType).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        successLeaveList(res)
                    }
                }

                Status.ERROR -> {
                    binding.shimmerLeaveContainer.visibility = View.GONE
                    binding.shimmerLeaveContainer.stopShimmer()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun successLeaveList(res: LeaveApplicationResponse) {

        if (res.code.equals("200")) {

            if (res.data.size > 0) {
                _binding?.txtLeaveNoData?.visibility = View.GONE
                _binding?.rvLeaveList?.visibility = View.VISIBLE

             //   binding.txtLeaveNoData.visibility = View.GONE
             //   binding.rvLeaveList.visibility = View.VISIBLE
                prepareAttendanceRV(res.data)
            } else {
                _binding?.txtLeaveNoData?.visibility = View.VISIBLE
                _binding?.rvLeaveList?.visibility = View.GONE
                //binding.txtLeaveNoData.visibility = View.VISIBLE
               // binding.rvLeaveList.visibility = View.GONE
            }
        }
        _binding?.shimmerLeaveContainer?.visibility = View.GONE
        _binding?.shimmerLeaveContainer?.stopShimmer()


      //  binding.shimmerLeaveContainer.visibility = View.GONE
      //  binding.shimmerLeaveContainer.stopShimmer()

    }

    private fun prepareAttendanceRV(items: ArrayList<LeaveApplication>) {
        val itemAdapter = LeaveApplicationListAdapter(items)

        if (isAdded) {
            val rLayoutmanager: RecyclerView.LayoutManager =
                LinearLayoutManager(this.requireContext())

            binding.rvLeaveList.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvLeaveList.layoutManager = manager
            binding.rvLeaveList.adapter = itemAdapter

//        rootView.rvLeaveList.addItemDecoration(
//            DividerItemDecoration(
//                getContext(),
//                DividerItemDecoration.VERTICAL
//            )
//        )


            itemAdapter.setOnItemClickListener(object :
                LeaveApplicationListAdapter.OnAdapterItemClickListener {
                override fun OnSelect(v: View?, position: Int) {
                    itemDetailsDialog(items.get(position))
                }
            })
        }
    }

    private fun itemDetailsDialog(app: LeaveApplication) {
        val v: View = layoutInflater.inflate(R.layout.leave_list_item_details, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtLVApplicationNo = v.findViewById<TextView>(R.id.txtLVApplicationNo)
        val txtLVType = v.findViewById<TextView>(R.id.txtLVType)
        val txtLVFromDate = v.findViewById<TextView>(R.id.txtLVFromDate)
        val txtLVToDate = v.findViewById<TextView>(R.id.txtLVToDate)
        val txtLVTotalDays = v.findViewById<TextView>(R.id.txtLVTotalDays)
        val txtLVPurpose = v.findViewById<TextView>(R.id.txtLVPurpose)
        val txtLVAddress = v.findViewById<TextView>(R.id.txtLVAddress)
        val txtLVStatus = v.findViewById<TextView>(R.id.txtLVStatus)
        val txtLVDetails = v.findViewById<TextView>(R.id.txtLVDetails)
        //  val txtApproveAuth = v.findViewById<TextView>(R.id.txtApproveAuth)
        val txtApprovePending = v.findViewById<TextView>(R.id.txtApprovePending)
        val chipGroup = v.findViewById<ChipGroup>(R.id.chipGroup)
        txtLVApplicationNo.text = "Application No. :"+ app.lvTrno
        txtLVType.text = app.lvType
        txtLVFromDate.text = changeDateFormat(app.lvFdate, "yyyy-MM-dd", "EEE, MMM dd yyyy")
        txtLVToDate.text = changeDateFormat(app.lvTdate, "yyyy-MM-dd", "EEE, MMM dd yyyy")
        txtLVTotalDays.text = app.lvDays.toString()
        txtLVPurpose.text = app.lvPurpose
        txtLVAddress.text = app.lvAdd
        txtLVStatus.text = app.lvStat
        //   txtApproveAuth.text = app.lv_hrcpath
        txtApprovePending.text = app.lv_pending_to

        setChipsForAuthority(app.lv_hrcpath.toString(), chipGroup)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        txtLVDetails.setOnClickListener { dialog.hide() }
    }


    private fun setChipsForAuthority(authName: String, chi: ChipGroup) {

        val splitValue = authName.split(">")


        for (item in splitValue) {
            val chip = Chip(requireContext())
            // val regex = Regex("[^A-Za-z0-9]")
            //  val result = regex.replace(item, "")
            val result = item.replace("-", "")
            // chip.text = item+result
            chip.text = result
            // Set the chip icon
            chip.chipIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_account)
            chip.setChipIconTintResource(R.color.crown_icon_color)
            chi.addView(chip)
        }
    }

    private fun saveApplicationResponse(response: LeaveSaveResponse, v: AddLeaveBinding, dialog: Dialog) {
        dialog.dismiss()
        loadingAnim.dismiss()
        when {
            response.code == "200" && response.message == "Leave Applied Successfully" -> {
                // Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
                getBaseMessage(
                    requireActivity(),
                    "Successful",
                    "Your application has been sent to your reporting boss.",
                    R.drawable.ic_checked_green,
                    1
                )
                binding.shimmerLeaveContainer.visibility = View.VISIBLE
                binding.shimmerLeaveContainer.startShimmer()
                //REFRESH APPLIED LEAVE LIST
                getUserLeaveList(user, getCurrentDate("yyyy")!!, lvType)
                //REFRESH LEAVE ALLOCATION COUNT
                refreshLeaveAllocation()

            }
            response.code == "500" -> {
                getBaseMessage(
                    requireActivity(),
                    "Failed",
                    response.message.toString(),
                    R.drawable.error_white,
                    0
                )
            }
            else -> {
                // Toast.makeText(requireContext(), "Failed to", Toast.LENGTH_LONG).show()
                getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Sorry failed to process your leave application.",
                    R.drawable.error_white,
                    2
                )
            }
        }
        //Duplicate Record; Cannot Apply
        // dialog.dismiss()
    }

    fun getBaseMessage(activity: Activity, title: String, mess: String, icon: Int, status: Int) {

        when (status) {
            0 -> {

                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.orange)
                    // .setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize(38) // Optional - default is 38dp
                    .show()
            }
            1 -> {
                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.crown_icon_color)
                    // .setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize() // Optional - default is 38dp
                    .show()
            }
            else -> {
                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.reject)
                    //.setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize(38) // Optional - default is 38dp
                    .show()
            }
        }
    }

    private fun refreshLeaveAllocation() {
        getUserLeaveTypeInfo(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())
    }
/*
    private fun setImage(imgFile: File, v: ImageView) {
        // val imgFile = File(filePath)
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
        }
    }

 */

    private fun setImage(imgFile: File, v: ImageView) {
        // val imgFile = File(filePath)
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
        }
    }


    /*
        private fun setMedicalImageToDialog(v: View) {
            images = Utility.getFilesByKey(requireContext(),"imagesMedDoc")!!
            if (images != null && images.size >= 1) {

                when (images.size) {
                    1 -> {
                        setImage(images[0], v.img)
                    }
                    2 -> {
                        setImage(images[0], v2)
                        setImage(images[1], v3)
                    }
                    3 -> {
                        setImage(images[0], v1)
                        setImage(images[1], v2)
                        setImage(images[2], v3)
                    }
    //                else ->{
    //                    setImage(images[0],v.img1)
    //                }
                }
            }

        }

     */

    private fun setMedicalImageToDialog(v: View) {

        if (images != null && images.size >= 1) {

            when (images.size) {
                1 -> {
                    setImage(images[0], v.findViewById(R.id.img1))
                }
                2 -> {
                    setImage(images[0], v.findViewById(R.id.img1))
                    setImage(images[1], v.findViewById(R.id.img2))
                }
                3 -> {
                    setImage(images[0], v.findViewById(R.id.img1))
                    setImage(images[1], v.findViewById(R.id.img2))
                    setImage(images[2], v.findViewById(R.id.img3))
                }
//                else ->{
//                    setImage(images[0],v.img1)
//                }
            }
        }

    }



    //todo



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
//
//            // if multiple images are selected
//            if (data?.clipData != null) {
//                val count = data.clipData!!.itemCount
//                for (i in 0..count - 1) {
//                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
//                   // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
//                   // val tempUri = getImageUri(requireContext(), selectedImage!!)
//                   // images.add(File(getRealPathFromURI(tempUri)!!))
//                    images.add(File(getRealPathFromURI(imageUri)!!))
//
//                }
//
//
//            } else if (data?.data != null) {
//                // if single image is selected
//                var imageUri: Uri = data.data!!
//
//            }
//
//            uploadMultiple(images, "E11-001795", "LVT-080454")
//        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && null != data) {

            images = ArrayList()

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                    val path: File =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    try {
                        path.mkdirs()
                        var file = File(ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
                     //   val inputStream = requireContext().contentResolver.openInputStream(imgUri)
// Use inputStream as needed for reading the file
                        GlobalScope.launch {

                            file= Compressor.compress(requireContext(), file)
                        }
                        images.add(file)
                        // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }

            } else if (data.data != null) {
                val imagePath: String = data.data!!.path!!
                Log.e("imagePath", imagePath)
                val imgUri: Uri = data.data!!
                val path: File =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                try {
                    path.mkdirs()
                    var file = File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                    GlobalScope.launch {

                        file= Compressor.compress(requireContext(), file)
                    }
                    images.add(file)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }

            } else {
                getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Please do long press and select minimum one photo ",
                    R.drawable.error_white,
                    2
                )
            }

            try {
                setMedicalImageToDialog(attachedView!!)
            } catch (e: Exception) {
                getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Can't process image,please try again after some times",
                    R.drawable.error_white,
                    2
                )
            }


//            if(data.clipData!!.itemCount<=3){
//                uploadMultiple(images, "E11-001795", "LVT-080454")
//            }
//            else{
//                getBaseMessage(
//                        requireActivity(),
//                        "Warning",
//                        "You can upload maximum 3 pictures at a time",
//                        R.drawable.error_white,
//                        0
//                    )
//            }
            //------------------------------------------------------------
//            var count = 0
//            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && null != data) {
//                if (data.clipData != null) {
//                    count = data.clipData!!.itemCount
//                    for (i in 0 .. count-1) {
//                        val imageUri: Uri = data.clipData!!.getItemAt(i).uri
//                        val path: File =
//                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                        try {
//                            path.mkdirs()
//                            Log.e("images", ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
//                            val file = File(ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
//                            images.add(file)
//                        } catch (e: Exception) {
//                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//                        }
//                    }
//                } else if (data.data != null) {
//                    val imagePath: String = data.data!!.path!!
//                    val file = File("/"+imagePath.split(":")[1])
//                    images.add(file)
//                    Log.e("imagePath", imagePath)
//                }
//                if (count <= 3) {
//                    uploadMultiple(images, "E11-001795", "LVT-080454")
//                } else {
//                    getBaseMessage(
//                        requireActivity(),
//                        "Warning",
//                        "Please select at most 3 picture",
//                        R.drawable.error_white,
//                        0
//                    )
//                }
//
//            } else {
//                getBaseMessage(
//                    requireActivity(),
//                    "Failed",
//                    "Failed to select image",
//                    R.drawable.error_white,
//                    2
//                )
//            }

            //---------------------------
        } else {
            getBaseMessage(
                requireActivity(),
                "Failed",
                "Sorry, documents selection failed",
                R.drawable.error_white,
                2
            )
        }


    }

    /*
        @Deprecated("Deprecated in Java")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)


            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && null != data) {

                images = ArrayList()

                if (data.clipData != null) {
                    val count = data.clipData!!.itemCount

                    for (i in 0 until count) {
                        val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                        val path: File =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        try {
                            path.mkdirs()
                            val file = File(ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
                            images.add(file)
                            Utility.saveFilesByKey(requireContext(),"imagesMedDoc",images)

                            // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                } else if (data.data != null) {
                    val imagePath: String = data.data!!.path!!
                    Log.e("imagePath", imagePath)
                    val imgUri: Uri = data.data!!
                    val path: File =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    try {
                        path.mkdirs()
                        val file = File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                        images.add(file)
                        Utility.saveFilesByKey(requireContext(),"imagesMedDoc",images)


                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }

                } else {
                    getBaseMessage(
                        requireActivity(),
                        "Failed",
                        "Please do long press and select minimum one photo ",
                        R.drawable.error_white,
                        2
                    )
                }

                try {
                    setMedicalImageToDialog(Img1!!,Img2!!,Img3!!)

                  //  setMedicalImageToDialog(attachedView!!)
                } catch (e: Exception) {
                    getBaseMessage(
                        requireActivity(),
                        "Failed",
                        "Can't process image,please try again after some times",
                        R.drawable.error_white,
                        2
                    )
                }


    //            if(data.clipData!!.itemCount<=3){
    //                uploadMultiple(images, "E11-001795", "LVT-080454")
    //            }
    //            else{
    //                getBaseMessage(
    //                        requireActivity(),
    //                        "Warning",
    //                        "You can upload maximum 3 pictures at a time",
    //                        R.drawable.error_white,
    //                        0
    //                    )
    //            }
                //------------------------------------------------------------
    //            var count = 0
    //            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && null != data) {
    //                if (data.clipData != null) {
    //                    count = data.clipData!!.itemCount
    //                    for (i in 0 .. count-1) {
    //                        val imageUri: Uri = data.clipData!!.getItemAt(i).uri
    //                        val path: File =
    //                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    //                        try {
    //                            path.mkdirs()
    //                            Log.e("images", ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
    //                            val file = File(ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
    //                            images.add(file)
    //                        } catch (e: Exception) {
    //                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
    //                        }
    //                    }
    //                } else if (data.data != null) {
    //                    val imagePath: String = data.data!!.path!!
    //                    val file = File("/"+imagePath.split(":")[1])
    //                    images.add(file)
    //                    Log.e("imagePath", imagePath)
    //                }
    //                if (count <= 3) {
    //                    uploadMultiple(images, "E11-001795", "LVT-080454")
    //                } else {
    //                    getBaseMessage(
    //                        requireActivity(),
    //                        "Warning",
    //                        "Please select at most 3 picture",
    //                        R.drawable.error_white,
    //                        0
    //                    )
    //                }
    //
    //            } else {
    //                getBaseMessage(
    //                    requireActivity(),
    //                    "Failed",
    //                    "Failed to select image",
    //                    R.drawable.error_white,
    //                    2
    //                )
    //            }

                //---------------------------
            } else {
                getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Sorry, documents selection failed",
                    R.drawable.error_white,
                    2
                )
            }


        }


     */
//-------IMAGE END---------------------------------------------------------------------------------



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