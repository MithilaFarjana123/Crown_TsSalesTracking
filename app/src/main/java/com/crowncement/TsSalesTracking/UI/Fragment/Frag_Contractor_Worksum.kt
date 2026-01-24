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
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.AttendanceViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.AttendanceViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragContractorWorksumBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.util.Calendar
import java.util.Objects


class Frag_Contractor_Worksum : Fragment() {


    private var _binding: FragContractorWorksumBinding? = null
    private val binding get() = _binding!!

    lateinit var atteViewModel: AttendanceViewModel
    lateinit var loadingAnim: Dialog
    lateinit var fromDate: String
    lateinit var toDate: String
    private lateinit var picker: DatePickerDialog


    lateinit var other: OtherCustomerChild
    private var customerCode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragContractorWorksumBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        AttendanceViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        other = convertOtherInfo(requireActivity())!!
        customerCode = other.partyCode.toString()
        //if(other.partyImage!!.isNotEmpty()){

        //    }

        fromDatePicker()

//        var timePeriod = Utility.getAttendancePeriod().split(",")
//        fromDate = timePeriod[0]
//        toDate = timePeriod[1]
//        getConSummeryInfo(customerCode, timePeriod[0], timePeriod[1])

        binding.searchFab.setOnClickListener {

            searchDate()
        }
    }


    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory())
            .get(AttendanceViewModel::class.java)


    }

    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
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

                getConSummeryInfo(customerCode, fromDate,toDate)

              //  getEngSummeryInfo(customerCode, fromDate, toDate)


            }, year, month, day
        )
        picker.show()
    }

    private fun getConSummeryInfo(
        id: String,
        fromDate: String,
        toDate: String
    ) {
        atteViewModel.getContEntSum(id, fromDate, toDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200" ) {

                                Glide.with(requireActivity())
                                    .load(EndPoint.IMAGE_BASE_URL + other.partyImage.toString())
                                    .error(R.drawable.shop_banner).fitCenter()
                                    // .placeholder(R.drawable.shop_banner)
                                    .into(_binding!!.imgUserName)

                                _binding!!.partyName.setText(other.partyName.toString())
                                var id = res.ConWorkSumChild.get(0).contId.toString()
                                _binding?.sbp?.setText("SBP : " +id)
                                var siteNum  = res.ConWorkSumChild.get(0).noOfSiteConv.toString()
                                _binding?.numOfSite?.setText("Converted Site : "+ siteNum)
                                var bagNum  = res.ConWorkSumChild.get(0).noOfBagsSell.toString()
                                _binding?.deliveredbag?.setText("Delivered bag : "+ bagNum)


                            } else {

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
        btnSearchAtt.setText("Search")

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
                //  getUserAttList(user, fromDate, toDate)

                getConSummeryInfo(customerCode, fromDate,toDate)


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