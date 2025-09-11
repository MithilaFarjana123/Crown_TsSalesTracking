package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R

import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild1
import com.crowncement.TsSalesTracking.databinding.FragOtherPartnerUpdateBinding
import com.google.gson.Gson



class Frag_Other_Partner : Fragment() {


   // lateinit var rootView: View
   private var _binding: FragOtherPartnerUpdateBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog

    var selectedCustomer = ""
    lateinit var retailerViewModel: RetailerViewModel
    lateinit var other: OtherCustomerChild

    var isModify: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
    //    rootView = inflater.inflate(R.layout.frag_other_partner_update, container, false)

        _binding = FragOtherPartnerUpdateBinding.inflate(inflater, container, false)
/*
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t\n communicating with server"
        )

        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        other = convertOtherInfo(requireActivity())!!

        setOtherInfo()

        enableEditMode()

        RetailerViewModelFactory()

        setupViewModel()

        binding.btnUpdateOther.setOnClickListener {

            if (uiValidation()) {
                binding.btnUpdateOther.isEnabled = false
                updateOtherParty()
            }

        }
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t\n communicating with server"
        )

        selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        other = convertOtherInfo(requireActivity())!!

        setOtherInfo()

        enableEditMode()

        RetailerViewModelFactory()

        setupViewModel()

        binding.btnUpdateOther.setOnClickListener {

            if (uiValidation()) {
                binding.btnUpdateOther.isEnabled = false
                updateOtherParty()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun uiValidation(): Boolean {
        var result = false
        if (binding.txtOtherName.text.toString() == "") {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Party Name Required",
                "",
                R.drawable.error_white,
                2
            )
        } else if (binding.txtOtherAdd.text.toString() == "") {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Address Required",
                "",
                R.drawable.error_white,
                2
            )
        } else if (binding.updateOtherDetailsInfoChild.txtOtherContactPerson.text.toString() == "") {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Party Contact Person Required",
                "",
                R.drawable.error_white,
                2
            )
        } else {
            result = true
        }

        return result
    }

    private fun setupViewModel() {
        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    private fun setOtherInfo() {

        if (other.partyImage.toString() != "") {
            Glide
                .with(this)
                .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                .error(R.drawable.shop_banner)
                .fitCenter()
                .placeholder(R.drawable.shop_banner)
                .into(binding.imgOtherCover)
        }

        binding.txtOtherName.setText(other.partyName)
        binding.txtOtherAdd.setText(other.partyAdd1)
        binding.txtOtherMobile1.setText("Mobile: " + other.partyMobile)

        val duration = Utility.findDifference(
            Utility.changeDateFormat(other.partyOpenDate, "yyyy-MM-dd", "dd-MM-yyyy").toString(),
            Utility.getCurrentDate("dd-MM-yyyy")
        )
        binding.txtOtherDuration.text = duration

        binding.txtOtherCode.setText("Party Code: " + other.partyCode)

        binding.txtOtherAreaInfo.text =
            other.geoHrc1 + " > " + other.geoHrc2 + " > " + other.geoHrc3 + " > " + other.geoHrc4

//TODO Other party Details---------------------------------------------------------------------------------


        binding.updateOtherDetailsInfoChild.txtOtherCity.setText(other.partyCity)
        binding.updateOtherDetailsInfoChild.txtOtherZip.setText(other.partyZip)
        binding.updateOtherDetailsInfoChild.txtOtherCountry.setText(other.partyCountry)

        binding.updateOtherDetailsInfoChild.txtOtherGroup.setText(other.partyGroup)

        binding.updateOtherDetailsInfoChild.txtOtherContactPerson.setText(other.contactPerson)
        binding.updateOtherDetailsInfoChild.txtOtherEmail.setText(other.partyEmail)
    }

    private fun enableEditMode() {
        isModify = Utility.getValueByKey(requireActivity(), "update").toString()

        //If yes then set mode read only
        if (isModify == "YES") {
            binding.btnUpdateOther.visibility = View.VISIBLE

            //Basic info in cover
            binding.txtOtherName.isFocusable = true
            binding.txtOtherAdd.isFocusable = true
            binding.txtOtherMobile1.isFocusable = true

            //Other Details----------------------------

            binding.updateOtherDetailsInfoChild.txtOtherCity.isFocusableInTouchMode = true
            binding.updateOtherDetailsInfoChild.txtOtherZip.isFocusableInTouchMode = true
            binding.updateOtherDetailsInfoChild.txtOtherCountry.isFocusableInTouchMode = true
            //  rootView.txtOtherGroup.isFocusableInTouchMode = true
            binding.updateOtherDetailsInfoChild.txtOtherContactPerson.isFocusableInTouchMode = true
            binding.updateOtherDetailsInfoChild.txtOtherEmail.isFocusableInTouchMode = true

            Utility.removePreference(requireActivity(), "update")
        }
    }

    private fun updateOtherParty() {

        loadingAnim.show()

        val user = Utility.getValueByKey(requireActivity(), "username")

        val otherCustomerChild = OtherCustomerChild1(
            binding.txtOtherName.text.toString(),
            binding.txtOtherAdd.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherCity.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherZip.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherCountry.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherGroup.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherContactPerson.text.toString(),
            binding.updateOtherDetailsInfoChild.txtOtherEmail.text.toString(),
            other.partyMobile
        )

        val gson = Gson()
        val otherPartyModelJson = gson.toJson(otherCustomerChild)

        updateOtherParty(user.toString(), other.partyCode.toString(), otherPartyModelJson)

    }

    private fun updateOtherParty(user: String, otherParty: String, value: String) {
        retailerViewModel.updateOther(user, otherParty, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    loadingAnim.dismiss()

                    it.responseData?.let { res ->
                        binding.btnUpdateOther.isEnabled = true

                        if (res.code == "200") {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Updated",
                                "Party information has been updated successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_dashboard)
                        }
                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    binding.btnUpdateOther.isEnabled = true
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}