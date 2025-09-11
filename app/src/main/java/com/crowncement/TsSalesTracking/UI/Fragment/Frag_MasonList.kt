package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.crowncement.TsSalesTracking.data.Adapter.MasonItemAdapter
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.data.Model.GetContractorMasonResponceChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragMasonListBinding
import com.google.gson.Gson
import java.util.Locale


class Frag_MasonList : Fragment() {

    private var _binding: FragMasonListBinding? = null
    private val binding get() = _binding!!
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var MasonLists: ArrayList<GetContractorMasonResponceChild>
    lateinit var loadingAnim: Dialog
    lateinit var masonItemAdapter : MasonItemAdapter
    lateinit var other: OtherCustomerChild
    lateinit var userInfo: Data


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragMasonListBinding.inflate(inflater, container, false)
/*
        RetailerViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()
        other = convertOtherInfo(requireActivity())!!
        binding.contractorForMason.setText(other.partyName.toString())
        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()
        getContractorMason(partyCode)

        binding.searchMason.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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


//getContractorMason("SBP-000124")

        binding.fabAddNewMason.setOnClickListener {

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            // navController.navigate(R.id.nav_retiler_home)
            navController.navigate(R.id.nav_demo_masonEntry)
        }
*/
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        RetailerViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()
        userInfo = Utility.getUserInfo(requireActivity())!!

        other = convertOtherInfo(requireActivity())!!
        binding.contractorForMason.setText(other.partyName.toString())
        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()
        getContractorMason(partyCode)

        binding.searchMason.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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


//getContractorMason("SBP-000124")
//nav_mason_database_add  check todo
        binding.fabAddNewMason.setOnClickListener {
                if ("nav_mason_database_add" in userInfo.user_access.toString())  {
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    // navController.navigate(R.id.nav_retiler_home)
                    navController.navigate(R.id.nav_demo_masonEntry)
                }else{
                    Utility.commonToast(requireActivity(), "Date expired", "Mason Registration date expired", 2)
                }
//            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
//            navController.navigate(R.id.nav_demo_masonEntry)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }


    private fun filter(text: String) {

        if (text.isNotEmpty()) {

            if (MasonLists.size > 0) {
                val filteredlist = java.util.ArrayList<GetContractorMasonResponceChild>()
                for (item in MasonLists) {
                    if (item.partyName!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.partyMobile!!.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault())) ||
                        item.partyAdd1!!.lowercase(Locale.getDefault())
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
                    binding.rdMasonList.adapter = null
                } else {

                    prepareMasonList(binding.rdMasonList, filteredlist)

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



    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }




    private fun getContractorMason(
        contractor_code: String
    ) {
        retailerViewModel.getContractorMason(contractor_code)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val masonList: ArrayList<GetContractorMasonResponceChild> = res.data

                            _binding?.totalMason?.setText(masonList.size.toString())

                            MasonLists = masonList
                            _binding?.let { binding ->
                                prepareMasonList(binding.rdMasonList, MasonLists)
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



    private fun prepareMasonList(
        rv: RecyclerView,
        items: ArrayList<GetContractorMasonResponceChild>

    ) {

        if (items.size > 0) {
            //   rootView.txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            masonItemAdapter = MasonItemAdapter(items)
            if(isAdded) {
                val manager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv.layoutManager = manager
                rv.adapter = masonItemAdapter


            }

        } else {
            rv.visibility = View.GONE
            //    rootView.txtNoDealerInfo.visibility = View.VISIBLE
        }
    }


}