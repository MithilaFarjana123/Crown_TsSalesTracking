package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.GetBPSiteInfoResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetSiteSumInfoChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.SiteVerificationData
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.databinding.FragTeamLeadCallRecordBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    lateinit var clientTypes: ArrayList<String>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>
    lateinit var ReferredTypeList: ArrayList<String>


    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""
    private var selectedRoute: String = ""
    private var selectedRouteName: String = ""

    var SELECTED_ENGORCon_ID = ""

    var division = ""
    var district = ""
    var thana = ""
    var selectedCustomer = ""
  //  lateinit var other: OtherCustomerChild

    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>
    lateinit var routeList: ArrayList<RouteChild>

    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>

    lateinit var otheritemAdapter: OtherCustomerListAdapter

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

        binding.txtRefferedPartiesID.visibility = View.GONE


        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]+1
        val year = cldr[Calendar.YEAR]
        nextVisitDate = "$year-$month-$day"
        otherCustomerLists = ArrayList()

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

        binding.etTagInfo.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                siteReferenceDialog("Engineer")
            }
        }

        prepareProjectRefferedByType(binding.etTagInfo)

        binding.etTagInfo.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                siteReferenceDialog(ReferredTypeList[position])
            }

        }

        onclickListener()

    }


    //todo
    private fun prepareProjectRefferedByType(ac: AutoCompleteTextView) {


        ReferredTypeList = ArrayList()


        ReferredTypeList.add("Tagged Engineer/Contractor")
        //1
//        ReferredTypeList.add("Self finding")
//        ReferredTypeList.add("Trade")
//        ReferredTypeList.add("Corporate")
//        ReferredTypeList.add("TS Team")
        //2
        ReferredTypeList.add("Engineer")
        ReferredTypeList.add("Contractor")
//        ReferredTypeList.add("Retailer")
//        ReferredTypeList.add("Dealer")
//        ReferredTypeList.add("Mason")
//        ReferredTypeList.add("IHBs")


        ac.setText(ReferredTypeList[0])


        for (item in ReferredTypeList) {
            val pos = ReferredTypeList.indexOf(item)
            ac.setText(ReferredTypeList[pos])
            // break


        }
        ac.setText(ReferredTypeList[0])
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ReferredTypeList
            )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        // Set up item click listener for the AutoCompleteTextView


        ac.setOnItemClickListener { parent, _, position, _ ->
            // Check if the selected item is the default "Visit Type"

            /*
            if (position == 0) {
                // Hide the layout
                selectedReferredType = ReferredTypeList[position].toString()
                binding.layoutTsPartSiteReferredEmp.visibility = View.GONE
            } else if ((position == 1) || (position == 2) || (position == 3) || (position == 4)) {
                selectedReferredType = ReferredTypeList[position].toString()


                binding.layoutTsPartSiteReferredEmp.visibility = View.VISIBLE
                //  rootView.siteRunning.visibility = View.GONE
            } else if ((position == 5) || (position == 6) || (position == 7) || (position == 8)
                || (position == 9) || (position == 10)
            ) {
                selectedReferredType = ReferredTypeList[position].toString()


                // Show the layout
                binding.layoutTsPartSiteReferredEmp.visibility = View.GONE

                // rootView.siteRunning.visibility = View.VISIBLE
                //todo pop up will come




            }

            */
        }




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
            retailerViewModel.GetSiteDetails(xparty)
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
                                        _binding?.tvContractor?.setText(index.partyName+" ( "+index.partyCode+" ) ")
                                        activity?.let {
                                            Utility.storeKeyValue(it, "conCode", index.partyCode)
                                        }
                                        //   Utility.storeKeyValue(requireActivity(),"conCode",index.partyCode)

                                        activity?.let {
                                            Utility.storeKeyValue(it, "conName", index.partyName)
                                        }


                                    }else if(index.partyType.toString()=="Engineer"){
                                        _binding?.tvEngineer?.setText(index.partyName+" ( "+index.partyCode+" ) ")

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
                                binding.tvtotalDelivery.setText(sitedata.noOfDel.toString())
                                if(sitedata.firstDelDate.toString().equals("2999-12-31")){
                                    //   binding.lDeldate.setText("Last delivery date :  ")
                                }else{
                                    var fastdeldate =  Utility.changeDateFormat(
                                        sitedata.firstDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.tvFirstDelivery.setText(fastdeldate+"(Qty: "+sitedata.noOfFirstDel.toString()+")")

                                }
                                if(sitedata.lastDelDate.toString().equals("2999-12-31")){
                                 //   binding.lDeldate.setText("Last delivery date :  ")
                                }else{
                                    var lastdate =  Utility.changeDateFormat(
                                        sitedata.lastDelDate.toString(),
                                        "yyyy-MM-dd",
                                        "MMM, dd yyyy"
                                    )
                                    binding.tvLastDelivery.setText(lastdate+"(Qty: "+sitedata.noOfLastDel.toString()+")")

                                }



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
            SELECTED_ENGORCon_ID,
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


    //todo

    private fun siteReferenceDialog(partyType:String) {
        val v: View = layoutInflater.inflate(R.layout.site_ref_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnRefSubmit = v.findViewById<Button>(R.id.btnRefSubmit)


        val acClient = v.findViewById<AutoCompleteTextView>(R.id.acClient)
        val acDivision = v.findViewById<AutoCompleteTextView>(R.id.acDivision)
        val acDistrict = v.findViewById<AutoCompleteTextView>(R.id.acDistrict)
        val acThana = v.findViewById<AutoCompleteTextView>(R.id.acThana)
        val acRoute = v.findViewById<AutoCompleteTextView>(R.id.acRoute)
        val search_customer = v.findViewById<SearchView>(R.id.search_customer)
        val tc_Cancle = v.findViewById<ImageView>(R.id.tc_Cancle)




        val layoutThana = v.findViewById<TextInputLayout>(R.id.layoutThana)
        val layoutRoute = v.findViewById<TextInputLayout>(R.id.layoutRoute)


        val txtRetailerType = v.findViewById<TextView>(R.id.txtRetailerType)
        val txtNoDealerInfo = v.findViewById<TextView>(R.id.txtNoDealerInfo)




        val rdRetailer = v.findViewById<RecyclerView>(R.id.rdRetailer)


        loadClientType(acClient,partyType)
        getDivisionList("Bangladesh",acDivision)
        //todo added for indivigual employee location
        empDisDisthanasetUp()
        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()




        layoutThana.visibility = View.GONE
        layoutRoute.visibility = View.GONE


        when {
            partyType == "Dealer" -> {
                layoutThana.visibility = View.GONE
                layoutRoute.visibility = View.GONE
            }


            partyType == "Retailer" -> {
                layoutThana.visibility = View.VISIBLE
                layoutRoute.visibility = View.VISIBLE


            }


            else -> {
                layoutRoute.visibility = View.GONE
                layoutThana.visibility = View.VISIBLE
            }
        }
        txtRetailerType.text = partyType+ " Information"


        btnRefSubmit.setOnClickListener { view1: View? ->
            dialog.dismiss()
            //   val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            //  navController.navigate(R.id.nav_Live_Attendance)
        }


        tc_Cancle.setOnClickListener { view1: View? ->
            dialog.dismiss()
        }




        acClient.setOnItemClickListener { parent, arg1, position, arg3 ->


            if (position > 0) {




            }
        }


        acDivision.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {


                selectedDivision = parent.getItemAtPosition(position).toString()


                getDistrictList("Bangladesh", selectedDivision,acDistrict)
            } else {
                selectedDivision = ""
            }
        }


        acDistrict.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()
                getThanaList(
                    "Bangladesh",
                    acDivision.text.toString(),
                    selectedDistrict,
                    acThana
                )


                if (acClient.text.toString() == "Dealer") {

/*
                    getDealerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        rdRetailer,
                        txtNoDealerInfo,
                        dialog
                    )

 */
                }
            } else {
                selectedDistrict = ""
            }
        }


        acThana.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()
                getRouteList(
                    "Bangladesh",
                    acDivision.text.toString(),
                    acDistrict.text.toString(),
                    selectedThana,
                    acRoute
                )


                if (acClient.text.toString() != "Dealer" && acClient.text.toString() != "Retailer") {




                    getOtherCustomerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        acThana.text.toString(),
                        acClient.text.toString(),
                        txtNoDealerInfo,
                        dialog,
                        rdRetailer
                    )
                }


            } else {
                selectedThana = ""
            }
        }


        acRoute.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0 && routeList.size > 0) {
                //selectedRoute = parent.getItemAtPosition(position).toString()
                selectedRoute = routeList[position - 1].routeNo.toString()
                selectedRouteName =
                    routeList[position - 1].routeNo.toString() + ">" + routeList[position - 1].routeName.toString()
                Log.e("T", "selectedRoute: $selectedRoute")
                Log.e("TAG", "selectedRouteName: $selectedRouteName")


                // Save this value for loading previously selected customer






                if (acClient.text.toString() == "Retailer") {

                    /*
                    getRetailerList(
                        "Bangladesh",
                        acDivision.text.toString(),
                        acDistrict.text.toString(),
                        acThana.text.toString(),
                        selectedRoute,
                        rdRetailer,
                        txtNoDealerInfo,
                        dialog


                    )

                     */
                }
            } else {
                selectedRoute = ""
            }
        }


        search_customer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {






            override fun onQueryTextSubmit(query: String): Boolean {


                if (query.isNotEmpty()) {
                    filter(query,acClient,txtNoDealerInfo,rdRetailer,dialog)
                }


                return false
            }


            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText,acClient,txtNoDealerInfo,rdRetailer,dialog)
                }
                return false
            }
        })




    }


    private fun loadClientType(ac: AutoCompleteTextView,selectedText:String) {
        clientTypes = ArrayList()
        // clientTypes.add("Select Client Type")
        //  clientTypes.add("Dealer")
        //  clientTypes.add("Retailer")
        clientTypes.add(selectedText)
        //  clientTypes.add("IHBs")
        //   clientTypes.add("Mason")
        //  clientTypes.add("Contractor")


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )
//        if (firstTime == 0) {
//            ac.setText(clientTypes[0])
//        }
        ac.setText(clientTypes[clientTypes.indexOf(selectedText)])


        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()


    }

    private fun getDivisionList(value: String,acDivision:AutoCompleteTextView) {
        retailerViewModel.getDivisionInfo(value).observe(requireActivity()) {
            when (it.status) {


                Status.LOADING -> {


                }


                Status.SUCCESS -> {


                    it.responseData?.let { res ->
                        divisionList = ArrayList()
                        divisionList = res.data
                        loadDivision(acDivision, res.data,empdivisionList)
                    }
                }
                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                }
            }
        }
    }

    private fun loadDivision(
        ac: AutoCompleteTextView,
        lists: ArrayList<DivisionChild>,
        empDivisionList: ArrayList<String>
    ) {
        // Filter DivisionChild objects whose divName matches any in empDivisionList
        val filtered = lists
            .map { it.divName.toString() }
            .filter { it in empDivisionList }
            .toMutableList()




        // Add the default hint at the top
        filtered.add(0, "Select Division")




        // Build and set the adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filtered
        )
        ac.setAdapter(adapter)




        // Optionally, show the hint as the initial text without filtering it out
        ac.setText("Select Division", false) // false prevents AutoComplete’s filter :contentReference[oaicite:1]{index=1}




        adapter.notifyDataSetChanged()
    }

    fun empDisDisthanasetUp(){
        empdivisionList = ArrayList()
        empdistrictList = ArrayList()
        empthanaList = ArrayList()
        emprouteList = ArrayList()




        val div = Utility.getUserInfo(requireActivity())!!.empJobHrc2.toString()
        val dis = Utility.getUserInfo(requireActivity())!!.empJobHrc3.toString()
        val than = Utility.getUserInfo(requireActivity())!!.empJobHrc4.toString()
        val rou = Utility.getUserInfo(requireActivity())!!.empJobHrc5.toString()




        empdivisionList = parseToArrayList(div)
        empdistrictList = parseToArrayList(dis)
        empthanaList = parseToArrayList(than)
        emprouteList = parseToArrayList(rou)

    }

    fun parseToArrayList(input: String): ArrayList<String> {
        //return ArrayList(parseItems(input))
        val items = input
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toCollection(ArrayList())








        return items




    }


    private fun getDistrictList(country: String, division: String,acDistrict:AutoCompleteTextView) {
        retailerViewModel.getDistrictInfo(country, division).observe(requireActivity()) {
            when (it.status) {


                Status.LOADING -> {


                }


                Status.SUCCESS -> {


                    it.responseData?.let { res ->
                        acDistrict.isEnabled = true
                        districtList = ArrayList()
                        districtList = res.data
                        loadDistrict(acDistrict, res.data,empdistrictList)
                    }
                }
                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                }
            }
        }
    }

    private fun loadDistrict(
        acDistrict: AutoCompleteTextView?,
        data: ArrayList<DistrictChild>,
        empdistrictList: ArrayList<String>
    ) {
        // Extract only those disName values present in empdistrictList
        val filteredNames = data
            .map { it.disName.toString() }
            .filter { it in empdistrictList }
            .toMutableList()




        // Ensure there's at least the placeholder option at the top
        filteredNames.add(0, "Select District")




        // Build and set the adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filteredNames
        )
        acDistrict?.setAdapter(adapter)




        // Optionally set the initial hint text so user sees "Select District" before typing
        acDistrict?.setText("Select District", false)




        adapter.notifyDataSetChanged()
    }

    private fun getThanaList(country: String, division: String, district: String,acThana:AutoCompleteTextView) {
        retailerViewModel.getThanaInfo(country, division, district).observe(requireActivity()) {
            when (it.status) {


                Status.LOADING -> {


                }


                Status.SUCCESS -> {


                    it.responseData?.let { res ->
                        acThana.isEnabled = true
                        thanaList = ArrayList()
                        thanaList = res.data


                        loadThana(acThana, res.data)


                        //  loadThana(acThana, res.data,empthanaList)
                    }
                }
                Status.ERROR -> {


                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                }
            }
        }
    }




    private fun loadThana(acThana: AutoCompleteTextView?, data: ArrayList<ThanaChild>) {


        val items: ArrayList<String> = ArrayList()
        items.add("Select Thana")
        for (item in data) {
            items.add(item.thnName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acThana!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getRouteList(country: String, division: String, district: String, thana: String,acRoute:AutoCompleteTextView) {
        retailerViewModel.getRouteInfo(country, division, district, thana)
            .observe(requireActivity()) {
                when (it.status) {


                    Status.LOADING -> {


                    }


                    Status.SUCCESS -> {


                        it.responseData?.let { res ->
                            acRoute.isEnabled = true
                            routeList = ArrayList()
                            routeList = res.data
                            loadRoute(acRoute, res.data)
                        }
                    }
                    Status.ERROR -> {


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                    }
                }
            }
    }

    private fun loadRoute(acRoute: AutoCompleteTextView?, data: ArrayList<RouteChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Route")
        for (item in data) {
            items.add(item.routeName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acRoute!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }



    private fun getOtherCustomerList(country: String,
                                     division: String,
                                     district: String,
                                     thana: String,
                                     xCode: String,
                                     txtNoDealerInfo:TextView ,
                                     dialog: Dialog,
                                     rdRetailer:RecyclerView
    ) {
        loadingAnim.show()
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {


                    Status.LOADING -> {
                        loadingAnim.dismiss()
                    }


                    Status.SUCCESS -> {


                        it.responseData?.let { res ->


                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            prepareOtherList(rdRetailer, otherCustomerList, xCode,txtNoDealerInfo, dialog)
                            loadingAnim.dismiss()
                        }
                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                    }
                }
            }
    }



    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<OtherCustomerChild>,
        type: String,
        txtNoDealerInfo:TextView,
        dialog: Dialog
    ) {


        if (items.size > 0) {
            txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            otheritemAdapter = OtherCustomerListAdapter(items, type)


            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = otheritemAdapter


            otheritemAdapter.setOnItemClickListener(object :
                OtherCustomerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    if(type=="Engineer"){
                        SELECTED_ENGORCon_ID = items[position].partyCode.toString()
                        binding.txtRefferedPartiesID.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")
                    }
                    else if(type=="Contractor"){
                        SELECTED_ENGORCon_ID = items[position].partyCode.toString()
                        binding.txtRefferedPartiesID.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")

                    }


                   //  SELECTED_PARTY_ID=items[position].partyCode.toString()
                    //binding.txtRefferedPartiesID.visibility = View.VISIBLE
                    //binding.txtRefferedPartiesID.text="Party Name: "+items[position].partyName + "("+ SELECTED_ENGORCon_ID +")\n"+ "Address: "+items[position].partyAdd1
                    dialog.dismiss()




                }


            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }


    private fun filter(text: String,acClient:AutoCompleteTextView, txtNoDealerInfo:TextView,rdRetailer:RecyclerView,dialog: Dialog) {


        if (text.isNotEmpty()) {
            when {
                acClient.text.toString() == "Dealer" -> {
/*

                    if (dealerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<DealerChild>()
                        for (item in dealerLists) {
                            if (item.dealerName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.dealerMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No dealer found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {


                            prepareDealerList(rdRetailer, filteredlist, "D",txtNoDealerInfo,dialog)
                        }


                    }
                    else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No data found",
                            R.drawable.error_white,
                            2
                        )
                    }

 */


                }
                acClient.text.toString() == "Retailer" -> {
                    /*
                    if (retailerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<RetailerChild>()
                        for (item in retailerLists) {
                            if (item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No retailer found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {


                            prepareRetailerList(rdRetailer, filteredlist, "R",dialog,txtNoDealerInfo)
                        }




                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No retailer found",
                            R.drawable.error_white,
                            2
                        )
                    }
                */
                }


                else -> {
                    if (otherCustomerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<OtherCustomerChild>()
                        for (item in otherCustomerLists) {
                            if (item.contactPerson!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                // if(filteredlist.size>0){
                                filteredlist.add(item)
                                // }


                            }
                        }
                        if (filteredlist.isEmpty()) {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No data found",
                                R.drawable.error_white,
                                0
                            )
                            rdRetailer.adapter = null
                        } else {
                            //   if(filteredlist.size>0) {
                            prepareOtherList(
                                rdRetailer,
                                filteredlist,
                                acClient.text.toString(),
                                txtNoDealerInfo,
                                dialog
                            )
                            //   }
                        }




                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Sorry",
                            "No data found",
                            R.drawable.error_white,
                            2
                        )
                    }
                }
            }
        } else {
            Utility.getBaseMessage(
                requireActivity(),
                "Sorry",
                "No data found",
                R.drawable.error_white,
                0
            )
        }




    }


}


