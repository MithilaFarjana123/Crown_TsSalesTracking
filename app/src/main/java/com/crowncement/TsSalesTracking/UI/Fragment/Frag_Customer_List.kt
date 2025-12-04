package com.crowncement.TsSalesTracking.UI.Fragment


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.crowncement.TsSalesTracking.data.Adapter.DealerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.RetailerListAdapter
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.databinding.FragRetailerListBinding
import com.google.gson.Gson


import java.util.*
import kotlin.collections.ArrayList




class Frag_Customer_List : Fragment() {


    //itemList.sortDescending()


    var firstTime = 0
    //private lateinit var rootView: View
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


    lateinit var retaileritemAdapter: RetailerListAdapter
    lateinit var dealeritemAdapter: DealerListAdapter
    lateinit var otheritemAdapter: OtherCustomerListAdapter


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




        //    rootView = inflater.inflate(R.layout.frag_retailer_list, container, false)
        /*
               loadingAnim = Utility.baseLoadingAnimation(
                   requireActivity(),
                   Dialog(requireActivity()),
                   "P l e a s e    w a i t"
               )
               //     selectedDivision = getValueByKey(requireActivity(), "selectedDivision").toString()
               //    selectedDistrict = getValueByKey(requireActivity(), "selectedDistrict").toString()
               //    selectedThana = getValueByKey(requireActivity(), "selectedThana").toString()




               loadClientType(binding.acClient)


               RetailerViewModelFactory()
               setupViewModel()


               getDivisionList("Bangladesh")


               onClickEventListener()


        */


        return binding.root

    }


    //todo added
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Use binding safely
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        loadClientType(binding.acClient)

        RetailerViewModelFactory()
        setupViewModel()


        getDivisionList("Bangladesh")
        //todo added for indivigual employee location
        empDisDisthanasetUp()

        onClickEventListener()

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








    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
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
                                // navController.navigate(R.id.nav_retiler_home)
                                navController.navigate(R.id.nav_party_entry)
                            }else{
                                Utility.commonToast(requireActivity(), "Date expired", "Contractor Registration date expired", 2)
                            }
                        }else if(cau.equals("Engineer")){
                            if ("nav_engineer_database" in userInfo.user_access.toString())  {
                                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                                // navController.navigate(R.id.nav_retiler_home)
                                navController.navigate(R.id.nav_party_entry)
                            }else{
                                Utility.commonToast(requireActivity(), "Date expired", "Engineer Registration date expired", 2)
                            }
                        }else {

                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            // navController.navigate(R.id.nav_add_new_customer)
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


                getRouteList(
                    "Bangladesh",
                    binding.acDivision.text.toString(),
                    binding.acDistrict.text.toString(),
                    selectedThana,
                    loadingAnim
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
                }
                return false
            }
        })


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
                getDealerList(
                    "Bangladesh",
                    getValueByKey(requireActivity(), "selectedDivision").toString(),
                    getValueByKey(requireActivity(), "selectedDistrict").toString()
                )
            }


            "Retailer" -> {


                binding.layoutThana.visibility = View.VISIBLE
                binding.layoutRoute.visibility = View.VISIBLE
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



    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        clientTypes.add("Select Client Type")
        // clientTypes.add("Dealer")
        // clientTypes.add("Retailer")
        clientTypes.add("IHBs")
        clientTypes.add("Contractor")
        clientTypes.add("Engineer")
        clientTypes.add("Local Government Contractor")
        clientTypes.add("IP")

        // clientTypes.add("IP")




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


                            //  loadThana(binding.acThana, res.data,empthanaList)
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






    //.....................................All Type Of customer..............................




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
                        }


                    }
                    Status.ERROR -> {


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
                            //loadingAnim.dismiss()
                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            if (otherCustomerLists.size == 0) {
                                loadingAnim.dismiss()
                            }else{


                            }
                            _binding?.let { binding ->


                                prepareOtherList(binding.rdRetailer, otherCustomerList, xCode)
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


    private fun getDealerList(country: String, division: String, district: String) {
        retailerViewModel.getDealerList(country, division, district)
            .observe(requireActivity()) {
                when (it.status) {


                    Status.LOADING -> {


                    }


                    Status.SUCCESS -> {


                        it.responseData?.let { res ->


                            val dealerList: ArrayList<DealerChild> = res.data
                            dealerLists = dealerList


                            prepareDealerList(binding.rdRetailer, dealerList, "D")
                        }
                    }
                    Status.ERROR -> {


                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()


                    }
                }
            }
    }


    //TODO------------------------------------------------------------------prepareRecyclerView






    private fun prepareRetailerList(
        rv: RecyclerView,
        items: ArrayList<RetailerChild>,
        type: String
    ) {


        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            binding.txtNoDealerInfo.visibility = View.GONE
            retaileritemAdapter = RetailerListAdapter(items, type)
            //val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            //rv.layoutManager = rLayoutmanager


            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = retaileritemAdapter


            retaileritemAdapter.setOnItemClickListener(object :
                RetailerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    //items[position]
                    storeKeyValue(
                        requireActivity(),
                        "selectedCustomer",
                        binding.acClient.text.toString()
                    )
                    prepareRetailerObject(items[position], requireActivity())
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    // navController.navigate(R.id.nav_retiler_home)
                    navController.navigate(R.id.nav_demo_customerHome)
                }
            })
        } else {
            rv.visibility = View.GONE
            binding.txtNoDealerInfo.visibility = View.VISIBLE
        }




    }


    private fun prepareDealerList(rv: RecyclerView, items: ArrayList<DealerChild>, type: String) {


        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            binding.txtNoDealerInfo.visibility = View.GONE
            dealeritemAdapter = DealerListAdapter(items, type)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = dealeritemAdapter


            dealeritemAdapter.setOnItemClickListener(object :
                DealerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    //items[position]
                    storeKeyValue(
                        requireActivity(),
                        "selectedCustomer",
                        binding.acClient.text.toString()
                    )
                    prepareDealerObject(items[position], requireActivity())
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_demo_customerHome)
                }
            })
        } else {
            rv.visibility = View.GONE
            binding.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }


    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<OtherCustomerChild>,
        type: String
    ) {


        if (items.size > 0) {
            _binding?.txtNoDealerInfo?.visibility = View.GONE
            rv.visibility = View.VISIBLE
            otheritemAdapter = OtherCustomerListAdapter(items, type)
            if(isAdded){
                val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv.layoutManager = manager
                rv.adapter = otheritemAdapter
                //todo added
                loadingAnim.dismiss()
                otheritemAdapter.setOnItemClickListener(object :
                    OtherCustomerListAdapter.OnAdapterItemClickListener {
                    override fun OnClick(v: View?, position: Int) {


                        storeKeyValue(
                            requireActivity(),
                            "selectedCustomer",
                            binding.acClient.text.toString()
                        )


                        Utility.storeKeyValue(requireActivity(), "slected_party_type", binding.acClient.text.toString())
                        storeKeyValue(requireActivity(), "partyCode", items[position].partyCode)
                        //




                        prepareOtherObject(items[position], requireActivity())


                        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_demo_customerHome)
                    }
                })
            }


        } else {
            rv.visibility = View.GONE
            _binding?.txtNoDealerInfo?.visibility = View.VISIBLE
        }
    }


    fun prepareDealerObject(info: DealerChild?, activity: Activity) {
        val mPrefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedDealer", json)
        mPrefs.commit()
    }


    fun prepareRetailerObject(info: RetailerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedRetailer", json)
        mPrefs.commit()
    }


    fun prepareOtherObject(info: OtherCustomerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedOther", json)
        mPrefs.commit()
    }


    private fun filter(text: String) {


        if (text.isNotEmpty()) {
            when {
                binding.acClient.text.toString() == "Dealer" -> {


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
                            binding.rdRetailer.adapter = null
                        } else {


                            prepareDealerList(binding.rdRetailer, filteredlist, "D")
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
                binding.acClient.text.toString() == "Retailer" -> {
                    if (retailerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<RetailerChild>()
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
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry",
                                "No retailer found",
                                R.drawable.error_white,
                                0
                            )
                            binding.rdRetailer.adapter = null
                        } else {


                            prepareRetailerList(binding.rdRetailer, filteredlist, "R")
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
                }


                else -> {


                    if (otherCustomerLists.size > 0)
                    {
                        val filteredlist = java.util.ArrayList<OtherCustomerChild>()
                        for (item in otherCustomerLists) {
                            if (item.contactPerson!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||


                                item.partyAdd1!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))


                            /*||
                            item.partyName!!.lowercase(Locale.getDefault())
                                .contains(text.lowercase(Locale.getDefault())) ||


                            item.partyPhone!!.lowercase(Locale.getDefault())
                                .contains(text.lowercase(Locale.getDefault()))

                             */


                            ) {
                                filteredlist.add(item)
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
                        }


                        else {
                            prepareOtherList(
                                binding.rdRetailer,
                                filteredlist,
                                binding.acClient.text.toString()
                            )
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

                    /*
                    if (otherCustomerLists.size > 0) {
                        val filteredlist = java.util.ArrayList<OtherCustomerChild>()
                        for (item in otherCustomerLists) {
                            if (item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyMobile!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))||
                                item.partyName!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                item.partyPhone!!.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault()))
                            ) {
                                filteredlist.add(item)
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
                            binding.rdRetailer.adapter = null
                        } else {
                            prepareOtherList(
                                binding.rdRetailer,
                                filteredlist,
                                binding.acClient.text.toString()
                            )
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

