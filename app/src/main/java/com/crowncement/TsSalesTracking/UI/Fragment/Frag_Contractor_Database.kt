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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.crowncement.TsSalesTracking.databinding.FragContractorDatabaseBinding
import com.google.gson.Gson

import java.util.ArrayList
import java.util.Locale


class Frag_Contractor_Database : Fragment() {

  //  lateinit var rootView: View
  private var _binding: FragContractorDatabaseBinding? = null
    private val binding get() = _binding!!

    private  val TAG = "Frag_Con_Database"

    lateinit var retailerViewModel: RetailerViewModel
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

    lateinit var retaileritemAdapter: RetailerListAdapter
    lateinit var dealeritemAdapter: DealerListAdapter
    lateinit var otheritemAdapter: OtherCustomerListAdapter

    lateinit var retailerLists: ArrayList<RetailerChild>
    lateinit var dealerLists: ArrayList<DealerChild>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>
    var firstTime = 0

    lateinit var loadingAnim: Dialog

    lateinit var empdivisionList: ArrayList<String>
    lateinit var empdistrictList: ArrayList<String>
    lateinit var empthanaList : ArrayList<String>
    lateinit var emprouteList : ArrayList<String>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

  //      rootView = inflater.inflate(R.layout.frag_contractor_database, container, false)


        _binding = FragContractorDatabaseBinding.inflate(inflater, container, false)
/*
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )


        loadClientType(binding.acClient2)

        RetailerViewModelFactory()
        setupViewModel()
        getDivisionList("Bangladesh")



        onClickEventListener()
*/


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


        loadClientType(binding.acClient2)

        RetailerViewModelFactory()
        setupViewModel()
        getDivisionList("Bangladesh")

       //todo added for indivigual employee location
        empDisDisthanasetUp()

        onClickEventListener()
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
                loadClientType(binding.acClient2)
                if (firstTime != 0) {
                    loadPreviouslySelectedCustomer()
                }
            }


        }
        super.onResume()
    }


    private fun onClickEventListener() {

        binding.fabAddRetailer2.setOnClickListener {

            if (selectedDivision != "" && selectedDistrict != "" && selectedThana != "") {
                val cau = binding.acClient2.text.toString()
                val deDiTh = "$cau,$selectedDivision,$selectedDistrict,$selectedThana"
                Log.d(TAG, "deDiTh: $deDiTh")
                Utility.storeKeyValue(requireActivity(), "RetailerSetupInfo", deDiTh)

                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_contractor_add_update)
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

        binding.acClient2.setOnItemClickListener { parent, arg1, position, arg3 ->

            if (position > 0) {


                getDivisionList("Bangladesh")

                binding.layoutThana2.visibility = View.VISIBLE
                binding.fabAddRetailer2.visibility = View.VISIBLE
                binding.fabAddRetailer2.text = "Add New ${clientTypes[position]}"
                binding.txtRetailerType2.text = clientTypes[position] + " Information"

                Utility.storeKeyValue(requireActivity(), "acCustomer", clientTypes[position])
            }
        }

        binding.acDivision2.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {

                selectedDivision = parent.getItemAtPosition(position).toString()

                getDistrictList("Bangladesh", selectedDivision)
            } else {
                selectedDivision = ""
            }
        }

        binding.acDistrict2.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()


                getThanaList(
                    "Bangladesh",
                    binding.acDivision2.text.toString(),
                    selectedDistrict
                )

            } else {
                selectedDistrict = ""
            }
        }

        binding.acThana2.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()

                Utility.storeKeyValue(
                    requireActivity(),
                    "acCustomer",
                    binding.acClient2.text.toString()
                )
                Utility.storeKeyValue(
                    requireActivity(),
                    "selectedDivision",
                    binding.acDivision2.text.toString()
                )
                Utility.storeKeyValue(
                    requireActivity(),
                    "selectedDistrict",
                    binding.acDistrict2.text.toString()
                )
                Utility.storeKeyValue(
                    requireActivity(),
                    "selectedThana",
                    binding.acThana2.text.toString()
                )

                getOtherCustomerList(
                    "Bangladesh",
                    binding.acDivision2.text.toString(),
                    binding.acDistrict2.text.toString(),
                    binding.acThana2.text.toString(),
                    binding.acClient2.text.toString()
                )

            } else {
                selectedThana = ""
            }
        }


        binding.searchCustomer2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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

    private fun getOtherCustomerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        xCode: String
    ) {

        loadingAnim.show()
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            if(isAdded){
                                prepareOtherList(binding.rdRetailer2, otherCustomerList, xCode)

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


    private fun filter(text: String) {

        if (text.isNotEmpty()) {

            if (otherCustomerLists.size > 0)
            {
                val filteredlist = ArrayList<OtherCustomerChild>()
                for (item in otherCustomerLists) {
                    if (item.contactPerson!!.lowercase(Locale.getDefault())
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
                        "No data found",
                        R.drawable.error_white,
                        0
                    )
                }

                else {
                    prepareOtherList(
                        binding.rdRetailer2,
                        filteredlist,
                        binding.acClient2.text.toString()
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

    private fun prepareOtherList(
        rv: RecyclerView,
        items: ArrayList<OtherCustomerChild>,
        type: String
    ) {

        if (items.size > 0) {
            _binding?.txtNoDealerInfo2?.visibility = View.GONE
            rv.visibility = View.VISIBLE
            otheritemAdapter = OtherCustomerListAdapter(items, type)
            if(isAdded){


            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = otheritemAdapter

            otheritemAdapter.setOnItemClickListener(object :
                OtherCustomerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    Utility.storeKeyValue(
                        requireActivity(),
                        "contractor_update",
                        "YES"
                    )

                    Utility.storeKeyValue(
                        requireActivity(),
                        "selectedCustomer",
                        binding.acClient2.text.toString()
                    )
                    prepareOtherObject(items[position], requireActivity())

                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.nav_contractor_add_update)
                }
            })

            loadingAnim.dismiss()
            }
        } else {
            loadingAnim.dismiss()
            rv.visibility = View.GONE
            _binding?.txtNoDealerInfo2?.visibility = View.VISIBLE
        }
    }

    fun prepareOtherObject(info: OtherCustomerChild?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("selectedOther", json)
        mPrefs.commit()
    }

    private fun loadClientType(ac: AutoCompleteTextView) {
        clientTypes = ArrayList()
        clientTypes.add("Contractor")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientTypes
        )
        ac.setText(clientTypes[0])
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
                        loadDivision(binding.acDivision2, res.data,empdivisionList)
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
    private fun getDistrictList(country: String, division: String) {
        retailerViewModel.getDistrictInfo(country, division).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        binding.acDistrict2.isEnabled = true
                        districtList = ArrayList()
                        districtList = res.data
                        loadDistrict(binding.acDistrict2, res.data,empdistrictList)
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
    private fun getThanaList(country: String, division: String, district: String) {
        retailerViewModel.getThanaInfo(country, division, district).observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        binding.acThana2.isEnabled = true
                        thanaList = ArrayList()
                        thanaList = res.data

                        loadThana(binding.acThana2, res.data)

                       // loadThana(binding.acThana2, res.data,empthanaList)
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





    private fun loadPreviouslySelectedCustomer() {

        val selectedCustomerType = Utility.getValueByKey(requireActivity(), "acCustomer").toString()

        for (items in clientTypes) {

            if (items == selectedCustomerType) {
                binding.acClient2.setSelection(clientTypes.indexOf(items))
            }
        }

        binding.layoutThana2.visibility = View.VISIBLE



        getOtherCustomerList(
            "Bangladesh",
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            Utility.getValueByKey(requireActivity(), "acCustomer").toString()
        )
    }



    //todo new
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