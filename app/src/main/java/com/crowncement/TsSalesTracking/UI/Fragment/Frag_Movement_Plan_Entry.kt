package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.CommonViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.MovementViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.CommonViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MovementViewModelFactory
import com.crowncement.TsSalesTracking.common.CurrentDayDecorator
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.MovementListItemAdapter
import com.crowncement.TsSalesTracking.data.Adapter.MovementRouteAdapter
import com.crowncement.TsSalesTracking.data.Model.HolidayResponseChild
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild1
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild2
import com.crowncement.TsSalesTracking.databinding.FragMovementPlanBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.util.*


class Frag_Movement_Plan_Entry : Fragment() {

    private val TAG = "Movement_Plan_Entry"

 //   lateinit var rootView: View



    private var _binding: FragMovementPlanBinding? = null
    private val binding get() = _binding!!

    lateinit var empRouteList: ArrayList<String>
    var selectedRouteList: ArrayList<String> = ArrayList()
    var processedRouteList: ArrayList<String> = ArrayList()

    var planDateList: ArrayList<MovementPlanChild1> = ArrayList()
    var holidayList: ArrayList<HolidayResponseChild> = ArrayList()

    lateinit var movementViewModel: MovementViewModel
    lateinit var commonViewModel: CommonViewModel
    lateinit var routeAdapter: MovementRouteAdapter
    lateinit var loadingAnim: Dialog

    var selectedDate = ""
    var user = ""
    var da = ""
    var mo = ""
    var movementPlanStatus = ""
    var nextMonth = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_movement_plan, container, false)
        _binding = FragMovementPlanBinding.inflate(inflater, container, false)

/*

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )


        user = Utility.getValueByKey(requireActivity(), "username")!!

        MovementViewModelFactory()
        CommonViewModelFactory()
        setupViewModel()

        prepareCalendar()
        onDateSelectListener()

        //Get Holiday List
        val mo = Utility.getCurrentDate("MM")!!.toInt() + 1
        val year = Utility.getCurrentDate("yyyy")!!.toInt()
        getHolidayList(user, year.toString(), mo.toString())


        binding.fabAddMo.setOnClickListener {
            val coD = Utility.changeDateFormat(selectedDate, "yyyy-MM-dd", "EEE, dd MMM yyyy")
            addRouteBottomSheet(coD.toString())
        }
*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )


        user = Utility.getValueByKey(requireActivity(), "username")!!

        MovementViewModelFactory()
        CommonViewModelFactory()
        setupViewModel()

        prepareCalendar()
        onDateSelectListener()

        //Get Holiday List
        val mo = Utility.getCurrentDate("MM")!!.toInt() + 1
        val year = Utility.getCurrentDate("yyyy")!!.toInt()
        getHolidayList(user, year.toString(), mo.toString())


        binding.fabAddMo.setOnClickListener {
            val coD = Utility.changeDateFormat(selectedDate, "yyyy-MM-dd", "EEE, dd MMM yyyy")
            addRouteBottomSheet(coD.toString())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun prepareCalendar() {
        val month = Utility.getCurrentDate("MM")!!.toInt()
        val year = Utility.getCurrentDate("yyyy")!!.toInt()
        val date = Utility.getCurrentDate("dd")!!.toInt()
        nextMonth = month + 1
        mo = nextMonth.toString()
        movementPlanList(user, year.toString(), nextMonth.toString())


        //First Date
//        val mFirst = Calendar.getInstance()
//        mFirst[year, month, 1, 0, 0] = 0
//        rootView.calender.minDate = mFirst.timeInMillis

        //Last Date
//        val mLast = Calendar.getInstance()
        val lastDayOfMonth: Int = getDaysInMonth(year, nextMonth)
//        Log.d(TAG, "lastDayOfMonth: $lastDayOfMonth")
//        mLast[year, month, lastDayOfMonth, 0, 0] = 0
//        rootView.calender.maxDate = mLast.timeInMillis

        //Current Date
//        val mCurrent = Calendar.getInstance()
//        mCurrent[year, month, date, 0, 0] = 0
//        rootView.calender.date = mCurrent.timeInMillis

        // Customize the calendar view
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(year, month, 1))
            .setMaximumDate(CalendarDay.from(year, month, lastDayOfMonth))
            .setFirstDayOfWeek(Calendar.SATURDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()


    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // month is 0-based in Calendar class
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun onDateSelectListener() {

//        rootView.calender.setOnDateChangeListener { v, year, month, day ->
//
//            val currentMonth = month + 1
//            da = ""
//            mo = ""
//            da = day.toString()
//            mo = currentMonth.toString()
//
//            if (day < 10) {
//                da = ("0$day")
//            }
//            if (mo.toInt() < 10) {
//                mo = ("0$mo")
//            }
//
//            val date = "$da.$mo.$year"
//            selectedDate = "$year-$mo-$da"
//
//            rootView.txtMovDate.text = date
//            val convDate = Utility.changeDateFormat(rootView.txtMovDate.text.toString(), "dd.MM.yyyy", "EEE, dd MMM yyyy").toString()
//            rootView.txtMovDate.text = convDate
//
//            sortSelectedDateData(selectedDate)
//
//        }

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val sDate = Utility.changeDateFormat(
                date.date.toString(),
                "EEE MMM dd hh:mm:ss 'GMT'Z yyyy",
                "yyyy-MM-dd"
            )
            //Tue Jun 20 00:00:00 GMT+06:00 2023
            Log.d(TAG, "selectedDate: $sDate")
            selectedDate = sDate.toString()
            binding.txtMovDate.text =
                Utility.changeDateFormat(sDate, "yyyy-MM-dd", "EEE, dd MMM yyyy")
            var isHoliday = false
            if (holidayList.size > 0) {
                for (item in holidayList) {
                    if (selectedDate == item.HolidayDate.toString()) {
                        isHoliday = true
                        break
                    }
                }
            }
            if (!isHoliday) {
                sortSelectedDateData(selectedDate)
            } else {
                Utility.commonToast(
                    requireActivity(),
                    "Error",
                    "You can't add movement plan on a holiday",
                    2
                )
            }

        }

        binding.calendarView.setOnRangeSelectedListener { widget, dates ->
            // Handle range selection
        }

    }

    private fun addRouteBottomSheet(selDate: String) {

        var selectedRow = -1

        //    selectedRouteList = ArrayList()
        val v: View = layoutInflater.inflate(R.layout.movement_route_add, null)
        //val dialog = BottomSheetDialog(requireContext())
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val btnAdd: MaterialButton = v.findViewById(R.id.btnAdd)
        val routeChipGroup: ChipGroup = v.findViewById(R.id.routeChipGroup)
        val txtMovCancel: TextView = v.findViewById(R.id.txtMovCancel)
        val txtSelDate: TextView = v.findViewById(R.id.txtSelDate)
        val idGRV: GridView = v.findViewById(R.id.idGRV)

        txtSelDate.text = selDate

        // getEmpRoute("E11-001743", idGRV, v.context)
        getEmpRoute(user, idGRV, v.context)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        //TODO show chips if already save route previously
        for (item in selectedRouteList) {

            displayRouteChip(item, routeChipGroup)
        }

        //TODO HIDE DIALOG IF PLAN ALREADY APPROVED
        // if (movementPlanStatus != "") {
        if (movementPlanStatus == "Approved") {
            dialog.hide()
            Utility.getBaseMessage(
                v.context as Activity,
                "Movement Plan Already Approved",
                "Approved plan can not be modify",
                R.drawable.error_white,
                0
            )
        }


        txtMovCancel.setOnClickListener {

            selectedRouteList = ArrayList()

            dialog.hide()
        }

        btnAdd.setOnClickListener {

            if (selectedRouteList.size > 0) {
                confirmDialog(dialog)
            } else {
                Utility.commonToast(
                    v.context as Activity, "Error", "Please select minimum one route", 2
                )
            }

        }

        idGRV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, _ ->

            selectedRow = position

            if (!checkExistence(empRouteList[position])) {
                selectedRouteList.add(empRouteList[position])

                displayRouteChip(empRouteList[position], routeChipGroup)

                routeAdapter.notifyDataSetChanged() // call it here

                // idGRV.getChildAt(position).setBackgroundColor(124333)

            } else {
                Utility.commonToast(requireActivity(), "Error", "already added this route", 2)
            }

        }

    }

    private fun loadRoute(idGRV: GridView, context: Context) {

        if (empRouteList.size > 0) {
            routeAdapter = MovementRouteAdapter(empRouteList, context)
            idGRV.adapter = routeAdapter
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

    private fun displayRouteChip(values: String, chi: ChipGroup) {
        chi.clearCheck()
        //  for (item in values) {
        val chip = Chip(requireContext())
        // val regex = Regex("[^A-Za-z0-9]")
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

                    for (itemclear in selectedRouteList) {
                        if (itemclear.contains((chip.text))) {

                            selectedRouteList.remove(itemclear)
                            break
                        }
                    }
                }

                override fun onAnimationStart(animation: Animation?) {}
            })

            it.startAnimation(anim)
        }
        //   }
    }

    private fun setupViewModel() {
        movementViewModel = ViewModelProviders.of(
            this.requireActivity(),
            MovementViewModelFactory()
        )[MovementViewModel::class.java]
        commonViewModel = ViewModelProviders.of(
            this.requireActivity(),
            CommonViewModelFactory()
        )[CommonViewModel::class.java]
    }

    private fun getEmpRoute(
        empID: String, idGRV: GridView, context: Context
    ) {
        movementViewModel.getRoute(empID)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200" && res.data.size > 0) {

                            empRouteList = ArrayList()
                            for (item in res.data) {
                                empRouteList.add(item.routeNo + ":" + item.routeName + ":" + item.routeCode)
                            }

                            loadRoute(idGRV, context)

                        } else {

                            Utility.commonToast(
                                requireActivity(),
                                "No route plan found",
                                res.message.toString(),
                                2
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

        imgMClose.setOnClickListener { view1: View? -> dialog.hide() }

        btnMoveConfirm.setOnClickListener { view1: View? ->

            dialog.hide()
            parentDialog.hide()
            btnMoveConfirm.isEnabled = false

            val conRoute = prepareData()
            if (processedRouteList.size > 0) {
                loadingAnim.show()

                saveMovementPlan(user, selectedDate, conRoute, dialog, parentDialog)
            }
        }
    }

    private fun prepareData(): String {

        var citiesCommaSeparated = ""
        if (selectedRouteList.size > 0) {
            processedRouteList = ArrayList()
            for (item in selectedRouteList) {
                val res = item.trim().split(":")[2]
                processedRouteList.add(res)
            }
            citiesCommaSeparated = java.lang.String.join(",", processedRouteList)
            // Log.d(TAG, "prepareData:$citiesCommaSeparated")
            //.filter { !it.isWhitespace() }

        }
        return citiesCommaSeparated
    }


    private fun saveMovementPlan(
        empID: String, date: String, routes: String, dialog: Dialog, dialog1: Dialog
    ) {
        movementViewModel.saveMovementPlan(empID, date, routes)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            //Call date wise list
                            movementPlanList(user, Utility.getCurrentDate("yyyy").toString(), mo)

                            dialog.dismiss()
                            dialog1.dismiss()
                            selectedRouteList = ArrayList()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                res.message.toString(),
                                R.drawable.ic_checked_green,
                                1
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


    private fun movementPlanList(empID: String, year: String, month: String) {
        movementViewModel.movementPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            movementPlanStatus = res.plnStatus.toString()

                            planDateList = arrayListOf()
                            planDateList.addAll(res.data)

                            sortSelectedDateData(selectedDate)


                        }

//                        else {
//                            Utility.getBaseMessage(
//                                requireActivity(),
//                                "Error",
//                                res.message.toString(),
//                                R.drawable.error_white,
//                                0
//                            )
//                        }

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

    private fun sortSelectedDateData(date: String) {
        if (planDateList.size > 0) {

            val dateWisePlan = dateWiseData(planDateList, date)

            //LOAD ALREADY PLAN ROUTE
            if (dateWisePlan != null && dateWisePlan.size > 0) {
                binding.fabAddMo.visibility = View.VISIBLE
                prepareAlreadyAddedPlanData(dateWisePlan)
            }

            //ADD NEW ROUTE

            else {
                binding.fabAddMo.visibility = View.GONE
                binding.rvMovement.visibility = View.GONE
                binding.txtErMessage.visibility = View.VISIBLE
                binding.fabAddMo.visibility = View.GONE

                if (date.isNotEmpty()) {
                    val coD = Utility.changeDateFormat(date, "yyyy-MM-dd", "EEE, dd MMM yyyy")
                    addRouteBottomSheet(coD.toString())
                }
            }

        } else {
            binding.fabAddMo.visibility = View.GONE
            val coD = Utility.changeDateFormat(date, "yyyy-MM-dd", "EEE, dd MMM yyyy")
            addRouteBottomSheet(coD.toString())
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
        if (dateWiseDetails == null || dateWiseDetails.size == 0) {

            binding.rvMovement.visibility = View.GONE
            binding.txtErMessage.visibility = View.VISIBLE

        } else {
            binding.rvMovement.visibility = View.VISIBLE
            binding.txtErMessage.visibility = View.GONE

            loadAlreadyAddedPlan(dateWiseDetails, binding.rvMovement)
        }
    }

    private fun loadAlreadyAddedPlan(items: ArrayList<MovementPlanChild2>, rv: RecyclerView) {

        //TODO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Prepare data for previously saved route plan

        selectedRouteList = ArrayList()

        for (item in items) {
            val data = item.rtHrc5 + ":" + item.routeName + ":" + item.rtCode
            if (!checkExistence(data)) {
                selectedRouteList.add(data)
            }
        }

        val itemAdapter = MovementListItemAdapter(items)
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

//        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0 || dy < 0 && rootView.calender.isShown) rootView.calender.visibility=View.GONE
//            }
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) rootView.calender.visibility=View.VISIBLE
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//        })


        itemAdapter.setOnItemClickListener(object :
            MovementListItemAdapter.OnAdapterItemClickListener {

            override fun OnClickApply(v: View?, position: Int) {

            }
        })
    }

    private fun getHolidayList(emp: String, year: String, month: String) {
        commonViewModel.getHolidayList(emp, year, month)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200" && res.data.size > 0) {

                                holidayList = ArrayList()
                                holidayList = res.data

                                for (item in holidayList) {

                                    val mydate = CalendarDay.from(
                                        item.HolidayDate!!.split("-")[0].toInt(),
                                        item.HolidayDate!!.split("-")[1].toInt() - 1,
                                        item.HolidayDate!!.split("-")[2].toInt()
                                    ) // year, month, date
                                    binding.calendarView.invalidateDecorators()

                                    if(!holidayList.isNullOrEmpty()){
                                        binding.calendarView.addDecorators(
                                            CurrentDayDecorator(
                                                activity,
                                                mydate
                                            )
                                        )

                                    }




                                    //
                                }
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


}