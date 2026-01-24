package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.GetSiteSumInfoChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.databinding.FragSiteSummeryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects
import java.util.concurrent.TimeUnit


class Frag_Site_Summery : Fragment() {

    private var _binding: FragSiteSummeryBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var siteInfoLists: ArrayList<GetSiteSumInfoChild>
  //  lateinit var siteInfoLists: GetSiteSumInfoChild
    lateinit var siteinfo: GetTSSiteInfoChild

    private lateinit var picker: DatePickerDialog
    lateinit var fromDate: String
    lateinit var toDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragSiteSummeryBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        RetailerViewModelFactory()
        setupViewModel()

        siteinfo = convertOtherSiteInfo(requireActivity())!!

        getSiteSummeryInfo( siteinfo.siteNumber.toString(),"","")
     //   fromDatePicker()

        /*
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        var lastdayofMonth =  getDaysInMonth(year.toInt(),month.toInt())
        fromDate = "$year-$month-01"
        toDate = "$year-$month-$lastdayofMonth"



        getSiteSummeryInfo(
            siteinfo.siteNumber.toString(),
            fromDate,
            toDate
        )

         */


/*
        binding.searchFab.setOnClickListener {

            searchDate()
        }

 */

    }


    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        val mon:Int=month-1
        calendar.set(year, mon, 1) // month is 0-based in Calendar class
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun convertOtherSiteInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }

    private fun fromDatePicker() {
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
                binding.txtFrDate.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    )
                )
                toDatePicker()
            }, year, month, day
        )
        picker.show()
    }

    private fun toDatePicker() {
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
                binding.txtTDate.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    )
                )

                getSiteSummeryInfo(
                    siteinfo.siteNumber.toString(),
                    fromDate,
                    toDate
                    )
            }, year, month, day
        )
        picker.show()
    }

    private fun getSiteSummeryInfo(siteId: String, fDate: String, tDate: String) {
        retailerViewModel.getSiteSumInfo(siteId, fDate, tDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                siteInfoLists = res.data
                                var sitedata = siteInfoLists.get(0)
                                binding.siteID.setText(sitedata.siteId.toString())
                                if(sitedata.firstDelDate.toString().equals("2999-12-31")){
                                    binding.firstDelDate.setText(" ")
                                }else{
                                    var fdate =  Utility.changeDateFormat(
                                        sitedata.firstDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.firstDelDate.setText(fdate)

                                }

                                if(sitedata.lastDelDate.toString().equals("2999-12-31")){
                                    binding.lastDelDate.setText(" ")
                                }else{
                                    var lastdate =  Utility.changeDateFormat(
                                        sitedata.lastDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.lastDelDate.setText(lastdate)

                                }

                                binding.firstDelQty.setText(sitedata.noOfFirstDel.toString())

                                binding.lastDelQty.setText(sitedata.noOfLastDel.toString())

                                binding.totalNumDelivary.setText(sitedata.noOfDel.toString())
                                binding.refCall.setText(sitedata.refCallNo.toString())
                                binding.comNum.setText(sitedata.compCallNo.toString())

                            } else {
                              //  binding.siteID.setText("0")
                                binding.firstDelDate.setText(" ")
                                binding.lastDelDate.setText(" ")

                                binding.firstDelQty.setText("0")
                                binding.lastDelQty.setText("0")

                                binding.totalNumDelivary.setText("0")
                                binding.refCall.setText("0")
                                binding.comNum.setText("0")
                               // binding.rvMealList.visibility = View.GONE
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
                    txtTSearchDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MMM-yyyy"
                        )
                    )
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

                }, year, month, day
            )
            picker.show()
        }

        btnSearchAtt.setOnClickListener {

            if ((fromDate != "") && (toDate != "")) {
                // getUserAttList(user, fromDate, toDate)

                getSiteSummeryInfo(
                    siteinfo.siteNumber.toString(),
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