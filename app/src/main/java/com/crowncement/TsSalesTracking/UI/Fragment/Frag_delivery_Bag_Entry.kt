package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.common.Utility.storeKeyValue
import com.crowncement.TsSalesTracking.data.Adapter.SiteItemAdapter
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.ProfileForSiteTag
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.databinding.FragDeliveryBagEntryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit


class Frag_delivery_Bag_Entry : Fragment() {

    private var _binding: FragDeliveryBagEntryBinding? = null
    private val binding get() = _binding!!

    lateinit var siteList: ArrayList<String>

    lateinit var clientTypes: ArrayList<String>
    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>
    lateinit var routeList: ArrayList<RouteChild>

    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""
    private var selectedRoute: String = ""
    private var selectedRouteName: String = ""

    var user = ""
    private var selectedCustomer: String = ""
    lateinit var loadingAnim: Dialog

    //  var retailerSetupInfo = ""
    var division = ""
    var district = ""
    var thana = ""
    var sitenum = ""
    var firstTime = 0
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var otherCustomerLists: ArrayList<GetTSSiteInfoChild>
    lateinit var yesAlreadySortedLists : ArrayList<GetTSSiteInfoChild>
    lateinit var siteitemAdapter: SiteItemAdapter

    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragDeliveryBagEntryBinding.inflate(inflater, container, false)


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RetailerViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        //todo added for indivigual employee location
        empDisDisthanasetUp()

        binding.layoutThana.visibility = View.VISIBLE
        otherCustomerLists = ArrayList()
        yesAlreadySortedLists = ArrayList()
        binding.searchSite.queryHint = "Search your site here"


        getPreviouslyStoredValue()

        loadClientType(binding.acClient)

        getDivisionList("Bangladesh")

        onClickEventListener()

        // loadingAnim.show()
        /*
                getOtherCustomerList(
                    "Bangladesh",
                    division,
                    district,
                    thana,
                    selectedCustomer
                )

         */


        siteList = ArrayList()

        binding.searchSite.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.isNotEmpty()) {
                    filter(query)
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText)
                }
                return false
            }



        })


        binding.searchFab.setOnClickListener {

            searchDate()
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
        btnSearchAtt.setText("Search Converted Site")

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
             //   getUserAttList(user, fromDate, toDate)

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }



    private fun onClickEventListener() {

        binding.acClient.setOnItemClickListener { parent, arg1, position, arg3 ->

            //  if (position > 0) {

            binding.layoutThana.visibility = View.GONE
            binding.layoutRoute.visibility = View.GONE
            getDivisionList("Bangladesh")
            when {
                clientTypes[position] == "Dealer" -> {
                    binding.layoutThana.visibility = View.GONE
                    binding.layoutRoute.visibility = View.GONE
                }

                clientTypes[position] == "Retailer" -> {

                    binding.layoutThana.visibility = View.VISIBLE
                    binding.layoutRoute.visibility = View.VISIBLE

                }

                else -> {
                    binding.layoutRoute.visibility = View.GONE
                    binding.layoutThana.visibility = View.VISIBLE

                }


            }

            //  binding.txtPartyType.text = clientTypes[position] + "'s Site Information"
            storeKeyValue(requireActivity(), "acCustomer", clientTypes[position])

            // }


        }

        binding.acDivision.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {

                selectedDivision = parent.getItemAtPosition(position).toString()

                getDistrictList("Bangladesh", selectedDivision)
            } else {
                selectedDivision = ""
            }
        }

        binding.acDistrict.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()

                getThanaList(
                    "Bangladesh",
                    binding.acDivision.text.toString(),
                    selectedDistrict
                )

                if (binding.acClient.text.toString() == "Dealer") {

                    storeKeyValue(
                        requireActivity(),
                        "selectedDivision",
                        binding.acDivision.text.toString()
                    )

                    storeKeyValue(
                        requireActivity(),
                        "selectedDistrict",
                        binding.acDistrict.text.toString()
                    )

                }
            }

            else {
                selectedDistrict = ""
            }

        }

        binding.acThana.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()

                getRouteList(
                    "Bangladesh",
                    binding.acDivision.text.toString(),
                    binding.acDistrict.text.toString(),
                    selectedThana
                )

                if (binding.acClient.text.toString() != "Dealer" && binding.acClient.text.toString() != "Retailer") {


                    storeKeyValue(
                        requireActivity(),
                        "selectedDivision",
                        binding.acDivision.text.toString()
                    )
                    storeKeyValue(
                        requireActivity(),
                        "selectedDistrict",
                        binding.acDistrict.text.toString()
                    )
                    storeKeyValue(
                        requireActivity(),
                        "selectedThana",
                        binding.acThana.text.toString()
                    )
//                    storeKeyValue(
//                        requireActivity(),
//                        "selectedThana",
//                        selectedThana)

                    storeKeyValue(
                        requireActivity(),
                        "acCustomer",
                        binding.acClient.text.toString()
                    )
                    loadingAnim.show()
                    getOtherCustomerList(
                        "Bangladesh",
                        binding.acDivision.text.toString(),
                        binding.acDistrict.text.toString(),
                        binding.acThana.text.toString(),
                        binding.acClient.text.toString()
                    )
                }

            } else {
                selectedThana = ""
            }
        }


    }

    private fun getDistrictList(country: String, division: String) {
        retailerViewModel.getDistrictInfo(country, division).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        _binding?.let { binding ->

                            binding.acDistrict.isEnabled = true
                            districtList = ArrayList()
                            districtList = res.data
                            loadDistrict(binding.acDistrict, res.data,empdistrictList)
                        }

                    }
                }

                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    /*
    private fun loadDistrict(acDistrict: AutoCompleteTextView?, data: ArrayList<DistrictChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select District")
        for (item in data) {
            items.add(item.disName.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acDistrict!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


     */

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




    private fun getThanaList(country: String, division: String, district: String) {
        retailerViewModel.getThanaInfo(country, division, district).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        _binding?.acThana?.isEnabled = true
                        thanaList = ArrayList()
                        thanaList = res.data
                        _binding?.let { binding ->
                            loadThana(binding.acThana, res.data)
                            //    loadThana(binding.acThana, res.data,empthanaList)
                        }


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


    /*
    private fun loadThana(
        acThana: AutoCompleteTextView?,
        data: ArrayList<ThanaChild>,
        empthanaList: ArrayList<String>
    ) {
        // Filter data to only include thnName values present in empthanaList
        val filtered = data
            .map { it.thnName.toString() }
            .filter { it in empthanaList }
            .toMutableList()

        // Add a default prompt at the top
        filtered.add(0, "Select Thana")

        // Build and set the adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filtered
        )
        acThana?.setAdapter(adapter)

        // Optional: show the prompt initially without triggering filter
        acThana?.setText("Select Thana", false)

        adapter.notifyDataSetChanged()
    }

     */


    private fun getRouteList(country: String, division: String, district: String, thana: String) {
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()
        retailerViewModel.getRouteInfo(country, division, district, thana)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            _binding?.acRoute?.isEnabled = true
                            routeList = ArrayList()
                            routeList = res.data
                            _binding?.let { binding ->

                                loadRoute(binding.acRoute, res.data)
                            }
                            loadingAnim.dismiss()
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        loadingAnim.dismiss()
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



    /*
        private fun loadRoute(
            acRoute: AutoCompleteTextView?,
            data: ArrayList<RouteChild>,
            emprouteList: ArrayList<String>
        ) {
            // Extract routeName strings, filter to only those in emprouteList
            val filteredList = data
                .map { it.routeName.toString() }
                .filter { it in emprouteList }
                .toMutableList()

            // Ensure the prompt is always first
            filteredList.add(0, "Select Route")

            // Create adapter with filtered values
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                filteredList
            )
            acRoute?.setAdapter(adapter)

            // Optionally, set the prompt as default text (won’t filter)
            acRoute?.setText("Select Route", false)

            adapter.notifyDataSetChanged()
        }


     */


//////////////////////////////////

    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        //clientTypes.add(selectedCustomer)
        clientTypes.add("IHBs")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )

        if (firstTime == 0) {
            ac.setText(clientTypes[0])
        }

        else{
            //storeKeyValue(requireActivity(), "acCustomer", clientTypes[position])
            //   getValueByKey(requireActivity(), "acCustomer")
            val searchString = getValueByKey(requireActivity(), "acCustomer")


            // Get the index of the string in the ArrayList
            val position = clientTypes.indexOf(searchString)
            var typepos = 0
            // Check if the string is found
            if (position != -1) {
                typepos = position
                ac.setText(clientTypes[typepos])
                //  println("The string '$searchString' is found at position: $position")
            }


        }

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        firstTime = 1

//        ac.setText(clientTypes[0])
//        ac.setAdapter(adapter)
//        adapter.notifyDataSetChanged()
    }

    private fun getDivisionList(value: String) {
        retailerViewModel.getDivisionInfo(value).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        divisionList = ArrayList()
                        divisionList = res.data

                        _binding?.let { binding ->
                            //   loadDivision(binding.acDivision, res.data)
                            loadDivision(binding.acDivision, res.data,empdivisionList)

                        }
                        //   _binding?.acDivision?.let { it1 -> loadDivision(it1, res.data) }

                        // loadDivision(binding.acDivision, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }


    /*
    private fun loadDivision(ac: AutoCompleteTextView, lists: ArrayList<DivisionChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Division")
        for (item in lists) {
            items.add(item.divName.toString())
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }


     */

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




    private fun filter(text: String) {

        if (text.isNotEmpty()) {

            if (yesAlreadySortedLists.size > 0) {
                val filteredlist = java.util.ArrayList<GetTSSiteInfoChild>()
                for (item in yesAlreadySortedLists) {
                    if (
                        item.siteNumber!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.siteAdd!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.partyMobile!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.partyName!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.contactPerson!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.contactPhone!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault()))

                    ) {
                        filteredlist.add(item)
                    }

                }
                if (filteredlist.isEmpty()) {
                    Utility.getBaseMessage(
                        requireActivity(),
                        "Sorry",
                        "No site found",
                        R.drawable.error_white,
                        0
                    )
                    binding.rdDemoAllSite.adapter = null
                } else {

                    prepareOtherList(binding.rdDemoAllSite, filteredlist, selectedCustomer)


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


        }


    }



    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun getPreviouslyStoredValue() {
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()
        //  retailerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()
        division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()

    }

    private fun getOtherCustomerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xCode: String
    ) {
        if(isAdded) {
            retailerViewModel.getSavedSiteList(country, division, district, thana)
                .observe(requireActivity()) {
                    when (it.status) {

                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {

                            it.responseData?.let { res ->

                                val otherCustomerList: ArrayList<GetTSSiteInfoChild> = res.data
                                otherCustomerLists = otherCustomerList


                                for (values in otherCustomerLists) {

                                    if ((values.conversionStatus.equals("Yes"))||
                                        (values.conversionStatus.equals("Already converted site"))
                                    ){
                                        yesAlreadySortedLists.add(values)
                                    }
                                }


                                _binding?.let { binding ->
                                    prepareOtherList(
                                        binding.rdDemoAllSite,
                                        yesAlreadySortedLists,
                                        xCode
                                    )
                                }
                            }
                            loadingAnim.dismiss()
                        }

                        Status.ERROR -> {

                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            loadingAnim.dismiss()
                        }
                    }
                }
        }
    }


    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<GetTSSiteInfoChild>,
        type: String
    ) {

        if (items.size > 0) {
            //   rootView.txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            siteitemAdapter = SiteItemAdapter(items, type)
            if(isAdded) {
                val manager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv.layoutManager = manager
                rv.adapter = siteitemAdapter

                siteitemAdapter.setOnItemClickListener(object :
                    SiteItemAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {

                        storeKeyValue(
                            requireActivity(),
                            "selectedCustomer",
                            binding.acClient.text.toString()
                        )
                        storeKeyValue(
                            requireActivity(),
                            "selectedDivision",
                            binding.acDivision.text.toString()
                        )
                        storeKeyValue(
                            requireActivity(),
                            "selectedDistrict",
                            binding.acDistrict.text.toString()
                        )
                        storeKeyValue(
                            requireActivity(),
                            "selectedThana",
                            binding.acThana.text.toString()
                        )
                        prepareSelectedSiteObject(items[position], requireActivity())



                        //    confirmationDialog(items[position].siteNumber.toString())
                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_deliveryBagEntryChild)
                    }
                })
            }

        } else {
            rv.visibility = View.GONE
            //    rootView.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    fun prepareSelectedSiteObject(info: GetTSSiteInfoChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedSiteInfo", json)
        mPrefs.commit()
    }


    private fun confirmationDialog(siteCode : String) {

        val v: View = layoutInflater.inflate(R.layout.site_tag_dialouge, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val tag_cancel: TextView = v.findViewById(R.id.tag_cancel)
        val tag_confirm: TextView = v.findViewById(R.id.tag_confirm)
        //    val cancel: TextView = v.findViewById(R.id.cancelR)
        //   val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val tag_close: ImageView = v.findViewById(R.id.tag_close)

        /*  val oth=retailerSetupInfo.split(",")[0]

        if( oth!= "Retailer"){
            txtCDialogTitle.text="New $oth Addition"
            txtBody.text="Are you sure , you want to add this new  $oth!"
        }


       */
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        tag_close.setOnClickListener {
            dialog.hide()
        }

        tag_cancel.setOnClickListener {
            dialog.hide()
        }

        tag_confirm.setOnClickListener {

            val empI = Utility.getValueByKey(requireActivity(), "username").toString()

            val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

            val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()

            sitenum = siteCode

            val profileData = prepareListForTag()

            SaleBPSiteTag(empI,profileData,dialog)

        }

    }


    fun prepareListForTag(): String {

        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()


        val ProfileForSiteTag = ProfileForSiteTag(
            partyCode,
            sitenum,
            selectedCustomer
        )

        val gson = Gson()
        return gson.toJson(ProfileForSiteTag)
    }



    private fun SaleBPSiteTag(
        user: String,
        value: String,
        dialog: Dialog) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.SaleBPSiteTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Site Tag completed successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            loadingAnim.dismiss()

                            val navController = requireActivity().findNavController(R.id.nav_host_fragment)

                            navController.navigate(R.id.nav_dashboard)

                        }

//                        else if(res.code == "500"){
//                            Utility.getBaseMessage(
//                                requireActivity(),
//                                "Error",
//                                res.message.toString(),
//                                R.drawable.error_white,
//                                0
//                            )
//                        }

                        else {
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



    override fun onResume() {


        Handler().post {
            var intent = requireActivity().intent
            if (intent.equals(null)) {
                intent = Intent(requireActivity(), Act_Splash_Screen::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
                )
                requireActivity().overridePendingTransition(0, 0)
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0)
                startActivity(intent)
            }
            else {
                loadClientType(binding.acClient)
                if (firstTime != 0) {
                    loadPreviouslySelectedCustomer()
                }
            }


        }
        super.onResume()
    }


    private fun loadPreviouslySelectedCustomer() {
        val selectedCustomerType = getValueByKey(requireActivity(), "acCustomer").toString()
        //  loadClientType(rootView.acClient)
        for (items in clientTypes) {
            //if(clientTypes.contains(selectedCustomerType)){
            if (items == selectedCustomerType) {
                // binding.acClient.setSelection(clientTypes.indexOf(items))
                var position = clientTypes.indexOf(items)
                //  binding.acClient.setSelection(clientTypes.indexOf(items))
                //todo this was here
                //  binding.acClient.setSelection(clientTypes.indexOf(items))

            }

        }

        when (getValueByKey(requireActivity(), "acCustomer").toString()) {

            "Dealer" -> {
                getDivisionList("Bangladesh")
                binding.acDivision.setText(getValueByKey(requireActivity(), "selectedDivision").toString())
                getDistrictList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString()
                )


                /*
                getDealerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString()
                )

                 */


            }



            "Retailer" -> {


                binding.layoutThana.visibility = View.VISIBLE
                binding.layoutRoute.visibility = View.VISIBLE

                /*
                getRetailerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString(),
                    getValueByKey(requireActivity(), "selectedThana").toString(),
                    getValueByKey(requireActivity(), "selectedRoute").toString()
                )

                 */

            }


            else -> {


                binding.layoutThana.visibility = View.VISIBLE


//                rootView.acClient.setText(
//                    getValueByKey(requireActivity(), "acCustomer").toString()
//                )



                binding.acDivision.setText(
                    getValueByKey(requireActivity(), "selectedDivision").toString()
                )

                binding.acDistrict.setText(
                    getValueByKey(requireActivity(), "selectedDistrict").toString()
                )

                binding.acThana.setText(
                    getValueByKey(requireActivity(), "selectedThana").toString()
                )

                getOtherCustomerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString(),
                    getValueByKey(requireActivity(), "selectedThana").toString(),
                    getValueByKey(requireActivity(), "acCustomer").toString()
                )


            }

        }

    }



}

