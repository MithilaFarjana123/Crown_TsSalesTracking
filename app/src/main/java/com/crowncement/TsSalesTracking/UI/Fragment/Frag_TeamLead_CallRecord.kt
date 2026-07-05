package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
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
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.GetBPSiteInfoResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetSiteSumInfoChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.SiteVerificationData
import com.crowncement.TsSalesTracking.databinding.FragTeamLeadCallRecordBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects


class Frag_TeamLead_CallRecord : Fragment() {

    private var _binding: FragTeamLeadCallRecordBinding? = null
    private val binding get() = _binding!!
    lateinit var retailerViewModel: RetailerViewModel
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var nextVisitDate =""
    lateinit var siteinfo: GetTSSiteInfoChild
    lateinit var loadingAnim: Dialog


    var division = ""
    var district = ""
    var thana = ""
    var selectedCustomer = ""
  //  lateinit var other: OtherCustomerChild

    lateinit var other: GetTSSiteInfoChild

    lateinit var siteInfoLists: ArrayList<GetSiteSumInfoChild>

    private lateinit var spinnerStatus: Spinner
    private lateinit var layoutMismatchFields: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragTeamLeadCallRecordBinding.inflate(inflater, container, false)


        

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        RetailerViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"

        layoutMismatchFields = view.findViewById(R.id.layoutMismatchFields)

        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedValue = parent?.getItemAtPosition(position).toString()

                // Show hidden fields only if "Information Mismatch" is selected
                if (selectedValue == "Information Mismatch") {
                    layoutMismatchFields.visibility = View.VISIBLE
                } else {
                    layoutMismatchFields.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

      //  val etActualDate = binding.findViewById<TextInputEditText>(R.id.etActualDate)

        other = convertOtherInfo(requireActivity())!!
        getSiteSummeryInfo( other.siteNumber.toString(),"","")
        binding.etActualDate.setOnClickListener {
            // 1. Build the date picker
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Delivery Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            // 2. Show the picker
            datePicker.show(parentFragmentManager, "DATE_PICKER")
            // 3. Handle the result
            datePicker.addOnPositiveButtonClickListener { selection ->
                // Convert the selection (Long) to a readable string
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val dateString = sdf.format(Date(selection))
                binding.etActualDate.setText(dateString)
            }
        }
        siteinfo = convertOtherSiteInfo(requireActivity())!!
     //   SiteCode = siteinfo.siteNumber.toString()

        getOtherCustomerListforTagged(siteinfo.siteNumber.toString(),"")


        onclickListener()

    }

    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun convertOtherInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }

     fun onclickListener(){
        binding.btnSubmit.setOnClickListener{
            if(binding.spinnerStatus.selectedItem.toString()=="OK"){
                confirmationDialog()
            }
            else if(validation()){
                confirmationDialog()
            }
        }
    }


    fun validation(): Boolean {
        var result = false

        //yes
        binding.layoutVerificationInfo.isErrorEnabled = false
        binding.layoutVerificationInfo.error = null

        if(binding.txtVerificationInfo.text.toString().isEmpty()) {
            result = false
            binding.layoutVerificationInfo.isErrorEnabled = true
            binding.layoutVerificationInfo.error = "Verification details is required"
        }else{
            result = true

        }

        return result

    }

    private fun convertOtherSiteInfo(activity: Activity): GetTSSiteInfoChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedtaggedSite", "")
        return gson.fromJson(json, GetTSSiteInfoChild::class.java)
    }


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
                                        _binding?.tvContractor?.setText("Contractor : "+index.partyName+" ( "+index.partyCode+" ) ")
                                        activity?.let {
                                            Utility.storeKeyValue(it, "conCode", index.partyCode)
                                        }
                                        //   Utility.storeKeyValue(requireActivity(),"conCode",index.partyCode)

                                        activity?.let {
                                            Utility.storeKeyValue(it, "conName", index.partyName)
                                        }


                                    }else if(index.partyType.toString()=="Engineer"){
                                        _binding?.tvEngineer?.setText("Engineer : "+index.partyName+" ( "+index.partyCode+" ) ")

                                        activity?.let {
                                            Utility.storeKeyValue(it, "engCode", index.partyCode)
                                        }


                                        activity?.let {
                                            Utility.storeKeyValue(it, "engName", index.partyName)
                                        }


                                    }else{


                                    }


                                }

                            }
                        //    loadingAnim.dismiss()
                        }

                        Status.ERROR -> {

                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                         //   loadingAnim.dismiss()

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


    private fun getSiteSummeryInfo(siteId: String, fDate: String, tDate: String) {
        retailerViewModel.getSiteSumInfo(siteId, fDate, tDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                siteInfoLists = res.data
                                var sitedata = siteInfoLists.get(0)
                                //  binding.siteID.setText(sitedata.siteId.toString())
                                if(sitedata.firstDelDate.toString().equals("2999-12-31")){
                                    //   binding.lDeldate.setText("Last delivery date :  ")
                                }else{
                                    var fastdeldate =  Utility.changeDateFormat(
                                        sitedata.firstDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.tvFirstDelivery.setText(fastdeldate)

                                }
                                if(sitedata.lastDelDate.toString().equals("2999-12-31")){
                                 //   binding.lDeldate.setText("Last delivery date :  ")
                                }else{
                                    var lastdate =  Utility.changeDateFormat(
                                        sitedata.lastDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.tvLastDelivery.setText(lastdate)

                                }


                             //   binding.lDelQty.setText("Last Delivery quantity : "+sitedata.noOfLastDel.toString())

                                //  binding.totalNumDelivary.setText(sitedata.noOfDel.toString())


                            } else {
                                //  binding.siteID.setText("0")
                            //    binding.lDeldate.setText("Last delivery date : ")

                            //    binding.lDelQty.setText("Last Delivery quantity : "+"0")


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



    private fun confirmationDialog() {
        val v: View = layoutInflater.inflate(R.layout.add_retailer_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancelR: MaterialButton = v.findViewById(R.id.btnCancelR)
        val btnConfirmR: MaterialButton = v.findViewById(R.id.btnConfirmR)
        val cancel: TextView = v.findViewById(R.id.cancelR)
        val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val txtBody: TextView = v.findViewById(R.id.txtBody)


            txtCDialogTitle.text = "Site Verification"
            txtBody.text = "Are you sure , you want to add this new site !"

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelR.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmR.setOnClickListener {

            val empI = Utility.getValueByKey(requireActivity(), "username").toString()



            val varificationData = preparevarificationData()
            saveVarificationData(empI,varificationData, dialog)


        }


    }


    private fun preparevarificationData(): String {

        val userId = Utility.getValueByKey(requireActivity(),"userId").toString()

        var qty = 0
        if(binding.etActualQty.text.toString().isNotEmpty()){
            qty = binding.etActualQty.text.toString().toInt()

        }
        val varificationData = SiteVerificationData(
            siteinfo.siteNumber.toString(),
            binding.spinnerStatus.selectedItem.toString(),
            userId,
            binding.etTagInfo.text.toString(),
            binding.etActualDate.text.toString(),
            qty,
            binding.txtVerificationInfo.text.toString()
        )

        val gson = Gson()
        return gson.toJson(varificationData)
    }


    private fun saveVarificationData(user: String, value: String, dialog: Dialog) {
        dialog.dismiss()

        loadingAnim.show()

        retailerViewModel.OpprtsiteVrfSave(user, value)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            loadingAnim.dismiss()

                            if (res.code == "200") {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_demo_Site_Home)

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



}


