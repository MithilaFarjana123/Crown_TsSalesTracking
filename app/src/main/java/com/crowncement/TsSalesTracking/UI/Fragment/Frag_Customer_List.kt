package com.crowncement.TsSalesTracking.UI.Fragment


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getValueByKey
import com.crowncement.TsSalesTracking.common.Utility.storeKeyValue
import com.crowncement.TsSalesTracking.data.Adapter.DealerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.RetailerListAdapter
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.databinding.FragRetailerListBinding
import com.google.gson.Gson
import java.util.Locale


class Frag_Customer_List : Fragment() {

    var firstTime = 0
    private var _binding: FragRetailerListBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Frag_Customer_List"
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var clientTypes: ArrayList<String>
    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>
    lateinit var routeList: ArrayList<RouteChild>

    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>

    private var selectedDivision: String = ""
    private var selectedDistrict: String = ""
    private var selectedThana: String = ""
    private var selectedRoute: String = ""
    private var selectedRouteName: String = ""

    // CRITICAL: Store adapter instances to reuse them
    private var retaileritemAdapter: RetailerListAdapter? = null
    private var dealeritemAdapter: DealerListAdapter? = null
   // private var otheritemAdapter: OtherCustomerListAdapter? = null

    lateinit var retailerLists: ArrayList<RetailerChild>
    lateinit var dealerLists: ArrayList<DealerChild>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>

    lateinit var loadingAnim:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragRetailerListBinding.inflate(inflater, container, false)

        otherCustomerLists= ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        // Setup RecyclerView with optimizations
        setupRecyclerView()

        loadClientType(binding.acClient)
        RetailerViewModelFactory()
        setupViewModel()
        getDivisionList("Bangladesh")
        empDisDisthanasetUp()
        onClickEventListener()
    }

    // One-time RecyclerView setup with maximum optimizations
    private fun setupRecyclerView() {
        binding.rdRetailer.apply {
            setHasFixedSize(true) // Fixed item size = faster layout
            setItemViewCacheSize(20) // Cache more views
            setDrawingCacheEnabled(true) // Enable drawing cache
            setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
            itemAnimator = null // No animations = instant updates
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycledViewPool.setMaxRecycledViews(0, 30) // Bigger pool
        }
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
        val items = input
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toCollection(ArrayList())
        return items
    }

    override fun onDestroyView() {
        super.onDestroyView()
        retaileritemAdapter = null
        dealeritemAdapter = null
        //otheritemAdapter = null
        _binding = null
    }

//    override fun onResume() {
//        Handler(Looper.getMainLooper()).post {
//            var intent = requireActivity().intent
//            if (intent.equals(null)) {
//                intent = Intent(requireActivity(), Act_Splash_Screen::class.java)
//                intent.addFlags(
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                )
//                requireActivity().overridePendingTransition(0, 0)
//                requireActivity().finish()
//                requireActivity().overridePendingTransition(0, 0)
//                startActivity(intent)
//            } else {
//                loadClientType(binding.acClient)
//                if (firstTime != 0) {
//                    loadPreviouslySelectedCustomer()
//                }
//            }
//        }
//        super.onResume()
//    }

    private fun onClickEventListener() {

        binding.fabAddRetailer.setOnClickListener {

            when {
                binding.acClient.text.toString() == "Dealer" -> {
                    Utility.getBaseMessage(
                        requireActivity(),
                        "Warning",
                        "Please contact with sales admin for adding new dealer ",
                        R.drawable.error_white,
                        0
                    )
                }

                binding.acClient.text.toString() == "Retailer" -> {

                    if (selectedDivision != "" && selectedDistrict != "" && selectedThana != "" && selectedRouteName != "") {

                        val deDiThRo =
                            "Retailer,$selectedDivision,$selectedDistrict,$selectedThana,$selectedRouteName"
                        Log.d(TAG, "deDiThRo: $deDiThRo")

                        storeKeyValue(requireActivity(), "RetailerSetupInfo", deDiThRo)
                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_party_entry)

                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Division,District,Thana or Route is missing",
                            "Please select required data",
                            R.drawable.error_white,
                            0
                        )
                    }
                }

                else -> {

                    if (binding.acClient.text.toString() == "Select Client Type") {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Customer type is required",
                            "Please select any client type",
                            R.drawable.error_white,
                            0
                        )
                    }

                    else if (selectedDivision != "" && selectedDistrict != "" && selectedThana != "") {
                        val cau = binding.acClient.text.toString()
                        val deDiTh = "$cau,$selectedDivision,$selectedDistrict,$selectedThana"
                        Log.d(TAG, "deDiTh: $deDiTh")
                        storeKeyValue(requireActivity(), "RetailerSetupInfo", deDiTh)
                        var userInfo = Utility.getUserInfo(requireActivity())!!

                        if(cau.equals("Contractor")){
                            if ("nav_contractor_database" in userInfo.user_access.toString())  {
                                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_party_entry)
                            }else{
                                Utility.commonToast(requireActivity(), "Date expired", "Contractor Registration date expired", 2)
                            }
                        }else if(cau.equals("Engineer")){
                            if ("nav_engineer_database" in userInfo.user_access.toString())  {
                                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_party_entry)
                            }else{
                                Utility.commonToast(requireActivity(), "Date expired", "Engineer Registration date expired", 2)
                            }
                        }else {
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_party_entry)
                        }
                    }

                    else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Division,District or Thana is missing",
                            "Please select required data",
                            R.drawable.error_white,
                            0
                        )
                    }
                }
            }
        }

        binding.acClient.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {
                binding.layoutThana.visibility = View.GONE
                binding.layoutRoute.visibility = View.GONE
                getDivisionList("Bangladesh")
                when {
                    clientTypes[position] == "Dealer" -> {
                        binding.layoutThana.visibility = View.GONE
                        binding.layoutRoute.visibility = View.GONE
                        binding.fabAddRetailer.visibility = View.GONE
                    }

                    clientTypes[position] == "Retailer" -> {
                        binding.layoutThana.visibility = View.VISIBLE
                        binding.layoutRoute.visibility = View.VISIBLE
                        binding.fabAddRetailer.visibility = View.VISIBLE
                        binding.fabAddRetailer.text = "Add Retailer"
                    }

                    clientTypes[position] == "Contractor" -> {
                        binding.layoutThana.visibility = View.VISIBLE
                        binding.fabAddRetailer.visibility = View.GONE
                    }
                    clientTypes[position] == "Engineer" -> {
                        binding.layoutThana.visibility = View.VISIBLE
                        binding.fabAddRetailer.visibility = View.GONE
                    }

                    else -> {
                        binding.layoutRoute.visibility = View.GONE
                        binding.layoutThana.visibility = View.VISIBLE
                        binding.fabAddRetailer.visibility = View.VISIBLE
                        binding.fabAddRetailer.text = "Add ${clientTypes[position]}"
                    }
                }
                binding.txtRetailerType.text = clientTypes[position] + " Information"
                storeKeyValue(requireActivity(), "acCustomer", clientTypes[position])

            }else{
                if(binding.acClient.text.equals("Contractor")||
                    binding.acClient.text.equals("Engineer")
                ){
                    binding.fabAddRetailer.visibility = View.GONE
                }else {
                    binding.fabAddRetailer.visibility = View.VISIBLE
                }
            }
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
                    // Show loading before fetching
                    loadingAnim.show()
                    getDealerList(
                        "Bangladesh",
                        binding.acDivision.text.toString(),
                        binding.acDistrict.text.toString()
                    )
                }
            } else {
                selectedDistrict = ""
            }
        }

        binding.acThana.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {

                selectedThana = parent.getItemAtPosition(position).toString()


//                getRouteList(
//                    "Bangladesh",
//                    binding.acDivision.text.toString(),
//                    binding.acDistrict.text.toString(),
//                    selectedThana,
//                    loadingAnim
//                )


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

                    storeKeyValue(
                        requireActivity(),
                        "acCustomer",
                        binding.acClient.text.toString()
                    )

                    // Show loading animation
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

        binding.searchCustomer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText)
                } else {
                    // Restore full list when search is cleared
                    when {
                        binding.acClient.text.toString() == "Dealer" && ::dealerLists.isInitialized -> {
                            prepareDealerList(binding.rdRetailer, dealerLists, "D")
                        }
                        binding.acClient.text.toString() == "Retailer" && ::retailerLists.isInitialized -> {
                            prepareRetailerList(binding.rdRetailer, retailerLists, "R")
                        }
                        ::otherCustomerLists.isInitialized -> {
                            prepareOtherList(binding.rdRetailer, otherCustomerLists, binding.acClient.text.toString())
                        }
                    }
                }
                return false
            }
        })
    }

    private fun loadPreviouslySelectedCustomer() {
        val selectedCustomerType = getValueByKey(requireActivity(), "acCustomer").toString()

        for (items in clientTypes) {
            if (items == selectedCustomerType) {
                var position = clientTypes.indexOf(items)
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
                loadingAnim.show()
                getDealerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString()
                )
            }

            "Retailer" -> {
                binding.layoutThana.visibility = View.VISIBLE
                binding.layoutRoute.visibility = View.VISIBLE
                loadingAnim.show()
                getRetailerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString(),
                    getValueByKey(requireActivity(), "selectedThana").toString(),
                    getValueByKey(requireActivity(), "selectedRoute").toString()
                )
            }

            else -> {
                binding.layoutThana.visibility = View.VISIBLE

                binding.acDivision.setText(
                    getValueByKey(requireActivity(), "selectedDivision").toString()
                )
                binding.acDistrict.setText(
                    getValueByKey(requireActivity(), "selectedDistrict").toString()
                )
                binding.acThana.setText(
                    getValueByKey(requireActivity(), "selectedThana").toString()
                )

                loadingAnim.show()
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

    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        clientTypes.add("Select Client Type")
        clientTypes.add("IHBs")
        clientTypes.add("Contractor")
        clientTypes.add("Engineer")
        clientTypes.add("Local Government Contractor")
        clientTypes.add("IP")

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
            var typepos = 0
            if (position != -1) {
                typepos = position
                ac.setText(clientTypes[typepos])
            }
        }

        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        firstTime = 1
    }

    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
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
                            loadDivision(binding.acDivision, res.data,empdivisionList)
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
        adapter.notifyDataSetChanged()
    }

 */

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

        // 🔥 Restore previous selection AFTER adapter set
        val savedDivision = getValueByKey(requireActivity(), "selectedDivision").toString()
        if (savedDivision.isNotEmpty() && filtered.contains(savedDivision)) {
            ac.setText(savedDivision, false)
            selectedDivision = savedDivision
        } else {
            ac.setText("Select Division", false)
        }

        adapter.notifyDataSetChanged()
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

    private fun getRouteList(country: String, division: String, district: String, thana: String,dialog: Dialog) {
        dialog.show()
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
                            dialog.dismiss()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
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

    private fun getRetailerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        route: String
    ) {
        retailerViewModel.getRetailerList(country, division, district, thana, route)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {
                        // Loading already shown
                    }

                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            retailerLists= ArrayList()
                            val retailerList: ArrayList<RetailerChild> = res.data
                            retailerLists = retailerList

                            storeKeyValue(
                                requireActivity(),
                                "selectedCustomer",
                                binding.acClient.text.toString()
                            )
                            prepareRetailerList(binding.rdRetailer, retailerList, "R")
                            // Dismiss loading AFTER adapter is updated
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

    private fun getOtherCustomerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xCode: String
    ) {
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {
                        // Loading already shown
                    }

                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            activity?.let {
                                Utility.storeKeyValue(
                                    requireActivity(),
                                    "selectedCustomer",
                                    xCode
                                )
                            }
                            otherCustomerLists= ArrayList()
                            if(res.data.size>0){
                                // var otherCustomerList: ArrayList<OtherCustomerChild> = ArrayList()

                                otherCustomerLists= res.data
                                // otherCustomerLists = otherCustomerList
//                            _binding?.let { binding ->
//                                if (otherCustomerLists.isEmpty()) {
//                                    binding.rdRetailer.visibility = View.GONE
//                                    binding.txtNoDealerInfo.visibility = View.VISIBLE
//                                } else {
//                                    prepareOtherList(binding.rdRetailer, otherCustomerLists, xCode)
//                                }
//
//                                loadingAnim.dismiss()
//                            }
                            }

                            if (otherCustomerLists.size==0) {
                                binding.rdRetailer.visibility = View.GONE
                                binding.txtNoDealerInfo.visibility = View.VISIBLE
                            } else {
                                prepareOtherList(binding.rdRetailer, otherCustomerLists, xCode)
                            }



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

    private fun getDealerList(country: String, division: String, district: String) {
        retailerViewModel.getDealerList(country, division, district)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {
                        // Loading already shown
                    }

                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            val dealerList: ArrayList<DealerChild> = res.data
                            dealerLists = dealerList
                            prepareDealerList(binding.rdRetailer, dealerList, "D")
                            // Dismiss loading AFTER adapter is updated
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

    // ULTRA-FAST: Create adapter once, reuse with updateList()
    private fun prepareRetailerList(
        rv: RecyclerView,
        items: ArrayList<RetailerChild>,
        type: String
    ) {
        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            binding.txtNoDealerInfo.visibility = View.GONE

            if (retaileritemAdapter == null) {
                retaileritemAdapter = RetailerListAdapter(items, type)
                rv.adapter = retaileritemAdapter

                retaileritemAdapter?.setOnItemClickListener(object :
                    RetailerListAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {
                        storeKeyValue(
                            requireActivity(),
                            "selectedCustomer",
                            binding.acClient.text.toString()
                        )
                        prepareRetailerObject(retailerLists[position], requireActivity())
                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_demo_customerHome)
                    }
                })
            } else {
              //  retaileritemAdapter?.updateList(items, type)
            }
        } else {
            rv.visibility = View.GONE
            binding.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    // ULTRA-FAST: Create adapter once, reuse with updateList()
    private fun prepareDealerList(rv: RecyclerView, items: ArrayList<DealerChild>, type: String) {
        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            binding.txtNoDealerInfo.visibility = View.GONE

            if (dealeritemAdapter == null) {
                dealeritemAdapter = DealerListAdapter(items, type)
                rv.adapter = dealeritemAdapter

                dealeritemAdapter?.setOnItemClickListener(object :
                    DealerListAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {
                        storeKeyValue(
                            requireActivity(),
                            "selectedCustomer",
                            binding.acClient.text.toString()
                        )
                        prepareDealerObject(dealerLists[position], requireActivity())
                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_demo_customerHome)
                    }
                })
            } else {
             //   dealeritemAdapter?.updateList(items, type)
            }
        } else {
            rv.visibility = View.GONE
            binding.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    // ULTRA-FAST: Create adapter once, reuse with updateList()
    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<OtherCustomerChild>,
        type: String
    ) {
        if (items.size > 0) {
            binding.txtNoDealerInfo?.visibility = View.GONE
            rv.visibility = View.VISIBLE

         //   if (otheritemAdapter == null && isAdded) {

            val  otheritemAdapter = OtherCustomerListAdapter(items, type)
                rv.adapter = otheritemAdapter

            otheritemAdapter.setOnItemClickListener(object :
                OtherCustomerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    storeKeyValue(
                        requireActivity(),
                        "selectedCustomer",
                        binding.acClient.text.toString()
                    )

                    Utility.storeKeyValue(requireActivity(), "slected_party_type", binding.acClient.text.toString())
                    storeKeyValue(requireActivity(), "partyCode", otherCustomerLists[position].partyCode)

                    prepareOtherObject(otherCustomerLists[position], requireActivity())

                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_demo_customerHome)
                }
            })
       //     }

//            else if (otheritemAdapter != null) {
//                otheritemAdapter?.updateList(items, type)
//            }

        } else {
            rv.visibility = View.GONE
           binding.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    fun prepareDealerObject(info: DealerChild?, activity: Activity) {
        val mPrefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedDealer", json)
        mPrefs.apply() // Use apply() instead of commit() for async
    }

    fun prepareRetailerObject(info: RetailerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedRetailer", json)
        mPrefs.apply()
    }

    fun prepareOtherObject(info: OtherCustomerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedOther", json)
        mPrefs.apply()
    }

    private fun filter(text: String) {
        if (text.isNotEmpty()) {

            when {
                binding.acClient.text.toString() == "Dealer" -> {
                    if (::dealerLists.isInitialized && dealerLists.size > 0) {
                        val filteredlist = ArrayList<DealerChild>()
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
                            binding.rdRetailer.visibility = View.GONE
                            binding.txtNoDealerInfo.visibility = View.VISIBLE
                            binding.txtNoDealerInfo.text = "No dealer found"
                        } else {
                            prepareDealerList(binding.rdRetailer, filteredlist, "D")
                        }
                    }
                }

                binding.acClient.text.toString() == "Retailer" -> {
                    if (::retailerLists.isInitialized && retailerLists.size > 0) {
                        val filteredlist = ArrayList<RetailerChild>()
                        for (item in retailerLists) {
                            if (item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyPhone!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyAdd1!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.contactPerson!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            binding.rdRetailer.visibility = View.GONE
                            binding.txtNoDealerInfo.visibility = View.VISIBLE
                            binding.txtNoDealerInfo.text = "No retailer found"
                        } else {
                            prepareRetailerList(binding.rdRetailer, filteredlist, "R")
                        }
                    }
                }

                else -> {
                    if (::otherCustomerLists.isInitialized && otherCustomerLists.size > 0) {
                        val filteredlist = ArrayList<OtherCustomerChild>()

                        for (item in otherCustomerLists) {
                            if (item.contactPerson!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyAdd1!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
                            }
                        }
                        if (filteredlist.isEmpty()) {
                            binding.rdRetailer.visibility = View.GONE
                            binding.txtNoDealerInfo.visibility = View.VISIBLE
                            binding.txtNoDealerInfo.text = "No data found"
                        } else {
                            prepareOtherList(
                                binding.rdRetailer,
                                filteredlist,
                                binding.acClient.text.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_dashboard)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()
        restoreClientUIState()
    }



    private fun restoreClientUIState() {
        val savedClient = getValueByKey(requireActivity(), "acCustomer").toString()

        when (savedClient) {

            "Retailer" -> {
                binding.layoutThana.visibility = View.VISIBLE
                binding.layoutRoute.visibility = View.VISIBLE
                binding.fabAddRetailer.visibility = View.VISIBLE
                binding.fabAddRetailer.text = "Add Retailer"
            }

            "Dealer" -> {
                binding.layoutThana.visibility = View.GONE
                binding.layoutRoute.visibility = View.GONE
                binding.fabAddRetailer.visibility = View.GONE
            }

            "Contractor", "Engineer", "IHBs", "IP", "Local Government Contractor" -> {
                binding.layoutThana.visibility = View.VISIBLE
                binding.layoutRoute.visibility = View.GONE
                binding.fabAddRetailer.visibility = View.GONE
            }

            else -> {
                binding.layoutThana.visibility = View.GONE
                binding.layoutRoute.visibility = View.GONE
                binding.fabAddRetailer.visibility = View.VISIBLE
            }
        }
    }

}