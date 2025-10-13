package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.AttendanceViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.MovementViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.AttendanceViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MovementViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.data.Adapter.BreadCramAdapter
import com.crowncement.TsSalesTracking.data.Adapter.EmployeeWorkAdapter
import com.crowncement.TsSalesTracking.data.Model.AttApproveResponseChild
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.data.Model.GetEmpEntSumResponce
import com.crowncement.TsSalesTracking.data.Model.GetEmpEntSumResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetTSCallTagResponceChild
import com.crowncement.TsSalesTracking.databinding.FragEmployeesWorkSummeryBinding
import com.crowncement.TsSalesTracking.databinding.ItemEmpWorkSummeryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit


class Frag_Employees_WorkSummery : Fragment() {

    private var _binding: FragEmployeesWorkSummeryBinding? = null
    private val binding get() = _binding!!
    lateinit var atteViewModel: AttendanceViewModel

    private lateinit var picker: DatePickerDialog
    lateinit var fromDate: String
    lateinit var toDate: String
    lateinit var user: String
    val breadCramList: ArrayList<String> = ArrayList()
    lateinit var loadingAnim: Dialog
    var allvisitlist = ArrayList<GetTSCallTagResponceChild>()

    lateinit var empList: ArrayList<EmpInfo>

    var empWorkSum: ArrayList<GetEmpEntSumResponceChild> = ArrayList()


    lateinit var itemAdapter: EmployeeWorkAdapter
    lateinit var attApproveDate: String
    lateinit var movementViewModel: MovementViewModel
    lateinit var retailerViewModel: RetailerViewModel

    var idForBreadCram = ""
    var clickckeck = ""

    var siteVisitCount = 0

    var dateList = ArrayList<String>()


    var check = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragEmployeesWorkSummeryBinding.inflate(inflater, container, false)


        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AttendanceViewModelFactory()
        MovementViewModelFactory()
        RetailerViewModelFactory()

        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()
        attApproveDate = Utility.getCurrentDate("yyyy-MM-dd").toString()

        user = Utility.getValueByKey(requireActivity(), "username").toString()
        check = "monthlyData"
        idForBreadCram = user
       // var timePeriod = Utility.getAttendancePeriod().split(",")
      //  fromDate = timePeriod[0]
      //  toDate = timePeriod[1]

        allvisitlist =  ArrayList()

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        var lastdayofMonth =  getDaysInMonth(year.toInt(),month.toInt())
        fromDate = "$year-$month-01"
        toDate = "$year-$month-$lastdayofMonth"


        getSiteSummeryInfo(user, fromDate,toDate)


         //   getSiteSummeryInfo(user, timePeriod[0], timePeriod[1])
        //   attApproveList(user, Utility.getCurrentDate("yyyy-MM-dd").toString())

        zonalJuniorList(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())

        breadCramList.add(user)
        loadBreadCram()
        // fromDatePicker()

        binding.searchFab.setOnClickListener {

            searchDate()
        }

    }



    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        val mon:Int=month-1
        calendar.set(year, mon, 1) // month is 0-based in Calendar class
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }


    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory())
            .get(AttendanceViewModel::class.java)

        movementViewModel = ViewModelProviders.of(
            this.requireActivity(), MovementViewModelFactory()
        )[MovementViewModel::class.java]
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    private fun zonalJuniorList(empID: String, dateTime: String) {
        movementViewModel.getZonalJunior(empID, dateTime)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {
                            empList = ArrayList()


                            if (res.data.size > 0) {
                                empList.addAll(res.data)
                            }
                            loadEmpInfo(res.data)
                            //todo

                            loadingAnim.dismiss()

                        } else {

                            Utility.getBaseMessage(
                                requireActivity(),
                                "No subordinate set yet",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                            loadingAnim.dismiss()

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

    private fun loadEmpInfo(dataList: ArrayList<EmpInfo>) {

        if (dataList.size > 0) {

            binding.rvEmp.visibility = View.VISIBLE
            binding.txtAttAuthNoData.visibility = View.GONE
            val itemAdapter = EmployeeWorkAdapter(dataList)
            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvEmp.layoutManager = manager
            binding.rvEmp.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :
                EmployeeWorkAdapter.OnAdapterItemClickListener {


                @RequiresApi(Build.VERSION_CODES.O)
                override fun onClickItem(v: View?, position: Int) {
                    //


                    if (!checkExistence(dataList[position].empId.toString())) {
                        breadCramList.add(dataList[position].empId.toString())
                    }
                    idForBreadCram = dataList[position].empId.toString()
                    loadBreadCram()
                    loadingAnim.show()
                    //todo
                    if (clickckeck.equals("")) {

                        idForBreadCram = dataList[position].empId.toString()

                    }
                    zonalJuniorList(
                        dataList[position].empId.toString(),
                        getCurrentDate("yyyy-MM-dd HH:mm:ss").toString()
                    )
                  //  getDaysInMonth(2025,6)
                  //  fromDate = getDaysInMonth(2025,6).toString()
                 //   toDate = getDaysInMonth(2025,6).toString()
                    getSiteSummeryInfo(
                        dataList[position].empId.toString(),
                        fromDate, toDate
                    )

                }

            })


        } else {
            binding.rvEmp.visibility = View.GONE
            binding.txtAttAuthNoData.visibility = View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchDate() {
        val v: View = layoutInflater.inflate(R.layout.search_date_dialog, null)
        val dialog = BottomSheetDialog(requireActivity())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val imgClosesSearch = v.findViewById<ImageView>(R.id.imgClosesSearch)
        val txtFrSearchDate = v.findViewById<TextInputEditText>(R.id.txtFrSearchDate)
        val txtTSearchDate = v.findViewById<TextInputEditText>(R.id.txtTSearchDate)
        val btnSearchAtt = v.findViewById<Button>(R.id.btnSearchAtt)
        val layoutSearchF = v.findViewById<TextInputLayout>(R.id.layoutSearchF)
        val layoutSearchT = v.findViewById<TextInputLayout>(R.id.layoutSearchT)
        val txtErrorSearch = v.findViewById<TextView>(R.id.txtErrorSearch)

        btnSearchAtt.setText("Search")
        fromDate = ""
        toDate = ""

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
                    fromDate = String.format("%04d-%02d-%02d", year1, monthNumber, dayOfMonth)
                   // fromDate = selectedDate
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
                 //   toDate = selectedDate
                    toDate = String.format("%04d-%02d-%02d", year1, monthNumber, dayOfMonth)
                    txtTSearchDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MMM-yyyy"
                        ))

                    /*
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


                     */
                    check = "searchByDate"
                }, year, month, day

            )
            picker.show()
        }

        btnSearchAtt.setOnClickListener {

            if ((fromDate != "") && (toDate != "")) {
                // getUserAttList(user, fromDate, toDate)
                if (breadCramList.size > 1) {
                    idForBreadCram = breadCramList[breadCramList.size - 1]
                }
                else{
                    idForBreadCram=user
                }

                getSiteSummeryInfo(
                    idForBreadCram,
                    // breadCramList.get(breadCramList.size-1).toString(),
                    fromDate,
                    toDate
                )

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
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 31

    }


    private fun loadBreadCram() {
        if (breadCramList.size > 0) {
            val itemAdapter = BreadCramAdapter(breadCramList)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            binding.rbBreadcram.layoutManager = rLayoutmanager

            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rbBreadcram.layoutManager = manager
            binding.rbBreadcram.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                BreadCramAdapter.OnAdapterItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun OnClick(v: View?, position: Int) {


                    idForBreadCram = breadCramList[position]
                    zonalJuniorList(
                        breadCramList[position],
                        getCurrentDate("yyyy-MM-dd HH:mm:ss").toString()
                    )
                    getSiteSummeryInfo(breadCramList[position], fromDate, toDate)

                   // val itemsToRemove = listOf(breadCramList[position], breadCramList[(breadCramList.size - 1)])
                    for (i in breadCramList.size-1 downTo  breadCramList.indexOf(idForBreadCram)+1) {
                        // Do things
                        breadCramList.removeAt(i)
                    }


                    // clickckeck = "notremoveclicked"
//                    for (item in itemsToRemove) {
//                        val index = breadCramList.indexOf(item)
//
//                        if (index != -1) {
//                            breadCramList.removeAt(index)
//                        }
//                        clickckeck = "removeclicked"
//                    }


                    if (breadCramList.size == 0) {
                        breadCramList.add(user)
                    }



                    loadBreadCram()

                }

            })
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSiteSummeryInfo(
        id: String,
        fromDate: String,
        toDate: String

    ) {
        atteViewModel.getEmpEntSum(id, fromDate, toDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200" && res.data.size > 0) {

                                empWorkSum = ArrayList()
                                empWorkSum = res.data

                                successWorkList(
                                    res,
                                    binding.empWorkSumly,
                                    id,
                                    binding.txtAttAuthNoData
                                )
                            } else {
                                successWorkList(
                                    res,
                                    binding.empWorkSumly,
                                    id,
                                    binding.txtAttAuthNoData

                                )
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Sorry, No data found",
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun successWorkList(
        res: GetEmpEntSumResponce,
        layout: ItemEmpWorkSummeryBinding,
        id : String,
        noData: LottieAnimationView

    ) {
     //   loadingAnim.show()
        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            binding.empWorkSumly.root.visibility = View.VISIBLE

           // var id = breadCramList.get(breadCramList.size-1)

            layout.heading.setText("Employee's Work Summery ( "+id+" )")
            var f =  Utility.changeDateFormat(
                fromDate,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            )
            var t = Utility.changeDateFormat(
                toDate,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            )

            layout.dateofData.setText("From Date : "+f +"    |    To Date : "+t)


            layout.SiteNo.setText(res.data.get(0).noOfSite.toString())
            layout.ibhsNo.setText(res.data.get(0).noOfIhbEntry.toString())
            layout.engineerNo.setText(res.data.get(0).noOfEngineer.toString())
            layout.ContractorNo.setText(res.data.get(0).noOfContractor.toString())
            layout.MasonNo.setText(res.data.get(0).noOfMason.toString())
            layout.convSiteNo.setText(res.data.get(0).noOfSiteConv.toString())
            layout.delBagNo.setText(res.data.get(0).noOfBagsConsump.toString())
         //   layout.directSalesVol.setText(res.data.get(0).noOfBagsConsump.toString())

            layout.totalcompNo.setText(res.data.get(0).noOfCompHand.toString())

            layout.noEngineerVis.setText(res.data.get(0).noOfEngrVisit.toString())
            layout.noContractorVis.setText(res.data.get(0).noOfContVisit.toString())
            //todo old
          //  layout.noSiteVis.setText(res.data.get(0).noOfSitesVisit.toString())

            //todo new
        //    getSalesVisitSummary(user, getCurrentDate("yyyy-MM-dd").toString())

          //  getSalesVisitSummary(user, getCurrentDate("yyyy-MM-dd").toString(),layout.noSiteVis)
          //  var visitCountOfSite = main(fromDate,toDate)

       //    val visitCountOfSite = calculateTotalSize(fromDate, toDate,layout.noSiteVis)
            if(check=="monthlyData"){
                callsitesum(layout.noSiteVis)
            }else if(check=="searchByDate"){
                //todo for search date by date
                searchCallSiteSum(layout.noSiteVis)
            }
          //  layout.noSiteVis.setText(allvisitlist.size.toString())
            //will change by quick visit data

            layout.totalengRefcall.setText(res.data.get(0).noOfEngrRefCall.toString())
            layout.totalconRefcall.setText(res.data.get(0).noOfContRefCall.toString())
            layout.totalIhbsRefcall.setText(res.data.get(0).noOfIhbRefCall.toString())

            layout.engSiteVisit.visibility = View.GONE
            layout.conSiteVisit.visibility = View.GONE


            //   prepareWorkSumList(rv, res.data)


        } else {
            binding.empWorkSumly.root.visibility = View.GONE

            //  layout.visibility = View.GONE
            noData.visibility = View.VISIBLE

        }

        //   binding.shimmerAttApproveContainer.visibility = View.GONE
        //   binding.shimmerAttApproveContainer.stopShimmer()

    }


    private fun checkExistence(value: String): Boolean {
        var res = false
        for (item in breadCramList) {
            if (value == item) {
                res = true
                break
            } else {
                res = false
            }
        }

        return res
    }





    //from date to date




    private fun getSalesVisitSummary(userName: String, dateTime: String,siteVisNum :TextView) {
        loadingAnim.show()
        retailerViewModel.GetTSCallTag(userName, dateTime)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200" && res.data.size > 0) {
                              //  allvisitlist = ArrayList()
                                if (res.data.get(0).moveStat!!.isNotEmpty()){
                                    allvisitlist.addAll(res.data)

                                }
                              //  siteVisNum.setText(allvisitlist.size.toString())
                            }


                            else{
                                Log.e("Error", res.message.toString() )
                            }

                            if(dateList.size>0){
                                dateList.removeAt(0)
                                if(dateList.size>0){
                                    // CALL API
                                    getSalesVisitSummary(user,dateList[0],siteVisNum)
                                } else {
                                    siteVisNum.setText(allvisitlist.size.toString())
                                    loadingAnim.dismiss()
                                }
                            } else {

                                loadingAnim.dismiss()

                            }
                        }
                   //     loadingAnim.dismiss()

                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {
                     //   binding.layoutVisit.visibility = View.GONE
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }



    //todo new

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDatesInCurrentMonth(): ArrayList<String> {
        val currentDate = LocalDate.now()
        val yearMonth = YearMonth.from(currentDate)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Loop from the 1st to the last day of the current month
        for (day in 1..yearMonth.lengthOfMonth()) {
            val date = LocalDate.of(yearMonth.year, yearMonth.month, day)
            dateList.add(date.format(formatter))
        }

        return dateList
    }

     */

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDatesInCurrentMonth(): ArrayList<String> {
     //   val dateList = ArrayList<String>() // make sure it's initialized
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Loop from the 1st day of the month to today's date
        for (day in 1..currentDate.dayOfMonth) {
            val date = LocalDate.of(currentDate.year, currentDate.month, day)
            dateList.add(date.format(formatter))
        }

        return dateList

    }


    //todo customesearchdateList

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDatesBetween(fromDate: String, toDate: String): ArrayList<String> {
        //val dateList = ArrayList<String>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Convert string to LocalDate
        val start = LocalDate.parse(fromDate, formatter)
        val end = LocalDate.parse(toDate, formatter)

        // Loop from start to end date (inclusive)
        var date = start
        while (!date.isAfter(end)) {
            dateList.add(date.format(formatter))
            date = date.plusDays(1)
        }

        return dateList
    }





    @RequiresApi(Build.VERSION_CODES.O)
    fun callsitesum(visText : TextView){

        var Datelist = getAllDatesInCurrentMonth()

        getSalesVisitSummary(user,dateList[0],visText)



    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun searchCallSiteSum(visText : TextView){
        dateList = ArrayList()
        allvisitlist = ArrayList()
        var Datelist = getAllDatesBetween(fromDate,toDate)

        getSalesVisitSummary(user,dateList[0],visText)

    }


}