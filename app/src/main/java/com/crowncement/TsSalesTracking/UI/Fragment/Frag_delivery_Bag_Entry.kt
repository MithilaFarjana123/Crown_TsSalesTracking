package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit


class Frag_delivery_Bag_Entry : Fragment() {

    // ---------------------------------------------------------------------
    // View binding
    // ---------------------------------------------------------------------
    private var _binding: FragDeliveryBagEntryBinding? = null
    private val binding get() = _binding!!

    // ---------------------------------------------------------------------
    // State
    // ---------------------------------------------------------------------
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

    var division = ""
    var district = ""
    var thana = ""
    var sitenum = ""
    var firstTime = 0
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var otherCustomerLists: ArrayList<GetTSSiteInfoChild>
    lateinit var yesAlreadySortedLists: ArrayList<GetTSSiteInfoChild>
    lateinit var siteitemAdapter: SiteItemAdapter

    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList: ArrayList<String>
    lateinit var emprouteList: ArrayList<String>


    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragDeliveryBagEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        // todo added for individual employee location
        empDisDisthanasetUp()

        binding.layoutThana.visibility = View.VISIBLE
        otherCustomerLists = ArrayList()
        yesAlreadySortedLists = ArrayList()
        binding.searchSite.queryHint = "Search your site here"

        getPreviouslyStoredValue()
        loadClientType(binding.acClient)
        getDivisionList("Bangladesh")
        onClickEventListener()

        siteList = ArrayList()

        binding.searchSite.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) filter(newText)
                return false
            }
        })

        binding.searchFab.setOnClickListener {
            searchDate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference so no late callback can touch a dead view
        _binding = null
    }


    // ---------------------------------------------------------------------
    // Date search bottom sheet
    // ---------------------------------------------------------------------
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
        val heading = v.findViewById<TextView>(R.id.heading)

        heading.text = "Select Conversion Date"

        var fromDate = ""
        var toDate = ""
        btnSearchAtt.text = "Search Converted Site"

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
                { _: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    fromDate = selectedDate
                    txtFrSearchDate.setText(
                        Utility.changeDateFormat(selectedDate, "yyyy-MM-dd", "dd-MMM-yyyy")
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
                { _: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
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
                            Utility.changeDateFormat(selectedDate, "yyyy-MM-dd", "dd-MMM-yyyy")
                        )
                    }
                }, year, month, day
            )
            picker.show()
        }

        btnSearchAtt.setOnClickListener {
            if (fromDate != "" && toDate != "") {
                val result = yesAlreadySortedLists.filterByDateRange(fromDate, toDate)
                val xcode = _binding?.acClient?.text?.toString() ?: ""
                _binding?.let { binding ->
                    prepareOtherList(binding.rdDemoAllSite, result as ArrayList<GetTSSiteInfoChild>, xcode)
                }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun List<GetTSSiteInfoChild>.filterByDateRange(from: String, to: String): List<GetTSSiteInfoChild> {
        val parts = from.split("-")
        val formatted = "${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].padStart(2, '0')}"
        val fdate = LocalDate.parse(formatted).toString()

        val tparts = from.split("-")
        val tformatted = "${tparts[0]}-${tparts[1].padStart(2, '0')}-${tparts[2].padStart(2, '0')}"
        val tdate = LocalDate.parse(tformatted).toString()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fromDate = LocalDate.parse(fdate, formatter)
        val toDate = LocalDate.parse(tdate, formatter)

        return filter {
            it.firstDelDate?.let { date ->
                val d = LocalDate.parse(date, formatter)
                !d.isBefore(fromDate) && !d.isAfter(toDate)
            } ?: false
        }
    }

    private fun isGraterThan30Days(from: String, to: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fDate: Date = sdf.parse(from) ?: return false
        val tDate: Date = sdf.parse(to) ?: return false
        val diff: Long = tDate.time - fDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 30
    }


    // ---------------------------------------------------------------------
    // Employee division/district/thana/route restriction lists
    // ---------------------------------------------------------------------
    fun empDisDisthanasetUp() {
        empdivisionList = ArrayList()
        empdistrictList = ArrayList()
        empthanaList = ArrayList()
        emprouteList = ArrayList()

        val userInfo = Utility.getUserInfo(requireActivity()) ?: return

        val div = userInfo.empJobHrc2.toString()
        val dis = userInfo.empJobHrc3.toString()
        val than = userInfo.empJobHrc4.toString()
        val rou = userInfo.empJobHrc5.toString()

        empdivisionList = parseToArrayList(div)
        empdistrictList = parseToArrayList(dis)
        empthanaList = parseToArrayList(than)
        emprouteList = parseToArrayList(rou)
    }

    fun parseToArrayList(input: String): ArrayList<String> {
        return input
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toCollection(ArrayList())
    }


    // ---------------------------------------------------------------------
    // Click listeners
    // ---------------------------------------------------------------------
    private fun onClickEventListener() {

        binding.acClient.setOnItemClickListener { _, _, position, _ ->
            _binding?.let { binding ->
                binding.layoutThana.visibility = View.GONE
                binding.layoutRoute.visibility = View.GONE
            }
            getDivisionList("Bangladesh")

            _binding?.let { binding ->
                when {
                    clientTypes.getOrNull(position) == "Dealer" -> {
                        binding.layoutThana.visibility = View.GONE
                        binding.layoutRoute.visibility = View.GONE
                    }
                    clientTypes.getOrNull(position) == "Retailer" -> {
                        binding.layoutThana.visibility = View.VISIBLE
                        binding.layoutRoute.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.layoutRoute.visibility = View.GONE
                        binding.layoutThana.visibility = View.VISIBLE
                    }
                }
            }

            clientTypes.getOrNull(position)?.let {
                storeKeyValue(requireActivity(), "acCustomer", it)
            }
        }

        binding.acDivision.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedDivision = parent.getItemAtPosition(position).toString()
                getDistrictList("Bangladesh", selectedDivision)
            } else {
                selectedDivision = ""
            }
        }

        binding.acDistrict.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()

                getThanaList(
                    "Bangladesh",
                    _binding?.acDivision?.text?.toString() ?: "",
                    selectedDistrict
                )

                if (_binding?.acClient?.text?.toString() == "Dealer") {
                    storeKeyValue(requireActivity(), "selectedDivision", _binding?.acDivision?.text?.toString() ?: "")
                    storeKeyValue(requireActivity(), "selectedDistrict", _binding?.acDistrict?.text?.toString() ?: "")
                }
            } else {
                selectedDistrict = ""
            }
        }

        binding.acThana.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()

                // Clear everything before new search
                yesAlreadySortedLists.clear()
                otherCustomerLists.clear()
                _binding?.rdDemoAllSite?.adapter = null

                loadingAnim.show()

                getOtherCustomerList(
                    "Bangladesh",
                    _binding?.acDivision?.text?.toString() ?: "",
                    _binding?.acDistrict?.text?.toString() ?: "",
                    selectedThana,
                    _binding?.acClient?.text?.toString() ?: ""
                )
            }
        }
    }


    // ---------------------------------------------------------------------
    // Network calls — ALL observers now use viewLifecycleOwner
    // ---------------------------------------------------------------------
    private fun getDistrictList(country: String, division: String) {
        retailerViewModel.getDistrictInfo(country, division).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        _binding?.let { binding ->
                            binding.acDistrict.isEnabled = true
                            districtList = ArrayList()
                            districtList = res.data
                            loadDistrict(binding.acDistrict, res.data, empdistrictList)
                        }
                    }
                }
                Status.ERROR -> {
                    if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadDistrict(
        acDistrict: AutoCompleteTextView?,
        data: ArrayList<DistrictChild>,
        empdistrictList: ArrayList<String>
    ) {
        val filteredNames = data
            .map { it.disName.toString() }
            .filter { it in empdistrictList }
            .toMutableList()

        filteredNames.add(0, "Select District")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filteredNames
        )
        acDistrict?.setAdapter(adapter)
        acDistrict?.setText("Select District", false)
        adapter.notifyDataSetChanged()
    }

    private fun getThanaList(country: String, division: String, district: String) {
        retailerViewModel.getThanaInfo(country, division, district).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        _binding?.let { binding ->
                            binding.acThana.isEnabled = true
                            thanaList = ArrayList()
                            thanaList = res.data
                            loadThana(binding.acThana, res.data)
                        }
                    }
                }
                Status.ERROR -> {
                    if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadThana(acThana: AutoCompleteTextView?, data: ArrayList<ThanaChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Thana")
        for (item in data) items.add(item.thnName.toString())

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acThana?.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getRouteList(country: String, division: String, district: String, thana: String) {
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.getRouteInfo(country, division, district, thana)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            _binding?.let { binding ->
                                binding.acRoute.isEnabled = true
                                routeList = ArrayList()
                                routeList = res.data
                                loadRoute(binding.acRoute, res.data)
                            }
                        }
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                    }
                    Status.ERROR -> {
                        if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                    }
                }
            }
    }

    private fun loadRoute(acRoute: AutoCompleteTextView?, data: ArrayList<RouteChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Route")
        for (item in data) items.add(item.routeName.toString())

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        acRoute?.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        clientTypes.add("IHBs")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )

        if (firstTime == 0) {
            ac.setText(clientTypes[0])
        } else {
            val searchString = getValueByKey(requireActivity(), "acCustomer")
            val position = clientTypes.indexOf(searchString)
            val typepos = if (position != -1) position else 0
            ac.setText(clientTypes[typepos])
        }

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        firstTime = 1
    }

    private fun getDivisionList(value: String) {
        retailerViewModel.getDivisionInfo(value).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        divisionList = ArrayList()
                        divisionList = res.data
                        _binding?.let { binding ->
                            loadDivision(binding.acDivision, res.data, empdivisionList)
                        }
                    }
                }
                Status.ERROR -> {
                    if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadDivision(
        ac: AutoCompleteTextView,
        lists: ArrayList<DivisionChild>,
        empDivisionList: ArrayList<String>
    ) {
        val filtered = lists
            .map { it.divName.toString() }
            .filter { it in empDivisionList }
            .toMutableList()

        filtered.add(0, "Select Division")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            filtered
        )
        ac.setAdapter(adapter)
        ac.setText("Select Division", false)

        if (division != "") {
            ac.setText(division, false)
        }

        adapter.notifyDataSetChanged()
    }

    private fun filter(text: String) {
        if (text.isEmpty()) return

        if (yesAlreadySortedLists.isEmpty()) {
            Utility.getBaseMessage(
                requireActivity(), "Sorry", "No data found", R.drawable.error_white, 2
            )
            return
        }

        val filteredList = ArrayList<GetTSSiteInfoChild>()

        for (item in yesAlreadySortedLists) {
            if (
                item.siteNumber?.contains(text, true) == true ||
                item.siteAdd?.contains(text, true) == true ||
                item.partyMobile?.contains(text, true) == true ||
                item.partyName?.contains(text, true) == true ||
                item.contactPerson?.contains(text, true) == true ||
                item.contactPhone?.contains(text, true) == true
            ) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            _binding?.rdDemoAllSite?.adapter = null
            Utility.getBaseMessage(
                requireActivity(), "Sorry", "No site found", R.drawable.error_white, 0
            )
        } else {
            _binding?.let { binding ->
                prepareOtherList(binding.rdDemoAllSite, filteredList, selectedCustomer)
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
        if (!isAdded) return

        yesAlreadySortedLists.clear()
        otherCustomerLists.clear()
        _binding?.rdDemoAllSite?.adapter = null

        loadingAnim.show()

        retailerViewModel.getSavedSiteList(country, division, district, thana)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            otherCustomerLists = res.data

                            for (values in otherCustomerLists) {
                                if (
                                    values.conversionStatus == "Yes" ||
                                    values.conversionStatus == "Already converted site"
                                ) {
                                    yesAlreadySortedLists.add(values)
                                }
                            }

                            _binding?.let { binding ->
                                prepareOtherList(binding.rdDemoAllSite, yesAlreadySortedLists, xCode)
                            }
                        }
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                    }
                    Status.ERROR -> {
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                        if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
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
            rv.visibility = View.VISIBLE
            siteitemAdapter = SiteItemAdapter(items, type)
            if (isAdded) {
                val manager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv.layoutManager = manager
                rv.adapter = siteitemAdapter

                siteitemAdapter.setOnItemClickListener(object :
                    SiteItemAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {
                        if (!isAdded) return

                        storeKeyValue(requireActivity(), "selectedCustomer", _binding?.acClient?.text?.toString() ?: "")
                        storeKeyValue(requireActivity(), "selectedDivision", _binding?.acDivision?.text?.toString() ?: "")
                        storeKeyValue(requireActivity(), "selectedDistrict", _binding?.acDistrict?.text?.toString() ?: "")
                        storeKeyValue(requireActivity(), "selectedThana", _binding?.acThana?.text?.toString() ?: "")

                        items.getOrNull(position)?.let { site ->
                            prepareSelectedSiteObject(site, requireActivity())
                            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_deliveryBagEntryChild)
                        }
                    }
                })
            }
        } else {
            rv.visibility = View.GONE
        }
    }

    fun prepareSelectedSiteObject(info: GetTSSiteInfoChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedSiteInfo", json)
        mPrefs.apply()   // apply() instead of commit(): async, doesn't block the UI thread
    }

    private fun confirmationDialog(siteCode: String) {
        val v: View = layoutInflater.inflate(R.layout.site_tag_dialouge, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val tagCancel: TextView = v.findViewById(R.id.tag_cancel)
        val tagConfirm: TextView = v.findViewById(R.id.tag_confirm)
        val tagClose: ImageView = v.findViewById(R.id.tag_close)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        tagClose.setOnClickListener { dialog.hide() }
        tagCancel.setOnClickListener { dialog.hide() }

        tagConfirm.setOnClickListener {
            val empI = Utility.getValueByKey(requireActivity(), "username").toString()
            sitenum = siteCode
            val profileData = prepareListForTag()
            SaleBPSiteTag(empI, profileData, dialog)
        }
    }

    fun prepareListForTag(): String {
        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()
        val profileForSiteTag = ProfileForSiteTag(partyCode, sitenum, selectedCustomer)
        return Gson().toJson(profileForSiteTag)
    }

    private fun SaleBPSiteTag(user: String, value: String, dialog: Dialog) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.SaleBPSiteTag(user, value).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            Utility.getBaseMessage(
                                requireActivity(), "Successful", "Site Tag completed successfully",
                                R.drawable.ic_checked_green, 1
                            )
                            if (loadingAnim.isShowing) loadingAnim.dismiss()

                            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_dashboard)
                        } else {
                            if (loadingAnim.isShowing) loadingAnim.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(), "Error", res.message.toString(),
                                R.drawable.error_white, 0
                            )
                        }
                    }
                }
                Status.ERROR -> {
                    if (loadingAnim.isShowing) loadingAnim.dismiss()
                    if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // onResume — guarded against Fragment being detached
    // ---------------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        if (!isAdded) return

        view?.post {
            if (!isAdded) return@post

            val intent = requireActivity().intent
            if (intent == null) {
                val relaunch = Intent(requireActivity(), Act_Splash_Screen::class.java)
                relaunch.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
                )
                requireActivity().overridePendingTransition(0, 0)
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0)
                startActivity(relaunch)
            } else {
                _binding?.let { binding -> loadClientType(binding.acClient) }
                if (firstTime != 0) {
                    loadPreviouslySelectedCustomer()
                }
            }
        }
    }

    private fun loadPreviouslySelectedCustomer() {
        if (!isAdded) return

        when (getValueByKey(requireActivity(), "acCustomer").toString()) {

            "Dealer" -> {
                getDivisionList("Bangladesh")
                _binding?.acDivision?.setText(getValueByKey(requireActivity(), "selectedDivision").toString())
                getDistrictList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString()
                )
            }

            "Retailer" -> {
                _binding?.let { binding ->
                    binding.layoutThana.visibility = View.VISIBLE
                    binding.layoutRoute.visibility = View.VISIBLE
                }
            }

            else -> {
                _binding?.let { binding ->
                    binding.layoutThana.visibility = View.VISIBLE
                    binding.acDivision.setText(getValueByKey(requireActivity(), "selectedDivision").toString())
                    binding.acDistrict.setText(getValueByKey(requireActivity(), "selectedDistrict").toString())
                    binding.acThana.setText(getValueByKey(requireActivity(), "selectedThana").toString())
                }

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

    // ---------------------------------------------------------------------
    // Toolbar
    // ---------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                if (isAdded) {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_dashboard)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}