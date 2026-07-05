package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.GetSalesMoveCallChild
import com.crowncement.TsSalesTracking.data.Model.GetSiteSumInfoChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerdirectcallinfo
import com.crowncement.TsSalesTracking.databinding.FragDeliveryBagEntryChildBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.util.Calendar
import java.util.Objects


class Frag_delivery_Bag_Entry_Child : Fragment() {

    // ---------------------------------------------------------------------
    // View binding
    // ---------------------------------------------------------------------
    private var _binding: FragDeliveryBagEntryChildBinding? = null
    private val binding get() = _binding!!

    // ---------------------------------------------------------------------
    // State
    // ---------------------------------------------------------------------
    private var other: GetTSSiteInfoChild? = null
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var loadingAnim: Dialog
    var selectedDate = ""
    lateinit var siteInfoLists: ArrayList<GetSiteSumInfoChild>
    var savedCalldata: ArrayList<GetSalesMoveCallChild> = ArrayList()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragDeliveryBagEntryChildBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        RetailerViewModelFactory()
        setupViewModel()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        savedCalldata = ArrayList()

        binding.txtRCallDeliveryDate.setOnClickListener {
            showConditionalDatePicker()
        }

        // Guarded: convertOtherInfo can legitimately return null (e.g. cleared
        // SharedPreferences after the app was killed in the background) — we
        // must not crash with !! here, we bail out gracefully instead.
        val fetchedSite = convertOtherInfo(requireActivity())
        if (fetchedSite == null) {
            Utility.commonToast(
                requireActivity(),
                "warning",
                "Site info not found, please go back and select the site again",
                0
            )
            if (isAdded) {
                requireActivity().findNavController(R.id.nav_host_fragment).navigateUp()
            }
            return
        }
        other = fetchedSite

        getSalesMoveCall(other!!.siteNumber.toString())
        getSiteSummeryInfo(other!!.siteNumber.toString(), "", "")

        binding.btnSavedemoRCallRecord.setOnClickListener {
            if (delValidation()) {
                if (savedCalldata.size > 0) {
                    confirmationDialog()
                } else {
                    Utility.commonToast(requireActivity(), "warning", "Necessary Data Not Found", 0)
                }
            }
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
    // Network calls — ALL observers now use viewLifecycleOwner
    // ---------------------------------------------------------------------
    private fun getSiteSummeryInfo(siteId: String, fDate: String, tDate: String) {
        retailerViewModel.getSiteSumInfo(siteId, fDate, tDate)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            _binding?.let { binding ->
                                if (res.code == "200" && res.data.isNotEmpty()) {
                                    siteInfoLists = res.data
                                    val sitedata = siteInfoLists[0]

                                    if (sitedata.lastDelDate.toString() == "2999-12-31") {
                                        binding.lDeldate.text = "Last delivery date :  "
                                    } else {
                                        val lastdate = Utility.changeDateFormat(
                                            sitedata.lastDelDate.toString(),
                                            "yyyy-MM-dd",
                                            "MMM, dd yyyy"
                                        )
                                        binding.lDeldate.text = "Last delivery date : $lastdate"
                                    }

                                    binding.lDelQty.text =
                                        "Last Delivery quantity : " + sitedata.noOfLastDel.toString()
                                } else {
                                    binding.lDeldate.text = "Last delivery date : "
                                    binding.lDelQty.text = "Last Delivery quantity : 0"
                                }
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        if (isAdded) Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun getSalesMoveCall(site_id: String) {
        retailerViewModel.getSalesMoveCall(site_id)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                savedCalldata = res.data

                                _binding?.let { binding ->
                                    if (savedCalldata.size > 0) {
                                        val first = savedCalldata[0]
                                        binding.basicSiteInfo.visibility = View.VISIBLE
                                        binding.siteCode.text = "Site Code : " + first.siteId.toString()
                                        binding.partyCode.text = "Party Code : " + first.partyId.toString()
                                        binding.ConversionStatus.text =
                                            "Conversion Status : " + first.siteConvStat.toString()
                                    } else {
                                        binding.basicSiteInfo.visibility = View.GONE
                                    }
                                }
                            }
                        }
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                    }
                    Status.ERROR -> {
                        if (isAdded) {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            Utility.commonToast(
                                requireActivity(),
                                "warning",
                                "Please check any delivery bag is pending to your reporting boss",
                                0
                            )
                        }
                        if (loadingAnim.isShowing) loadingAnim.dismiss()
                    }
                }
            }
    }

    private fun TSCallCompMoveDirectSave(user: String, value: String, dialog: Dialog) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.TSCallMoveDirectSave(user, value).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            Utility.getBaseMessage(
                                requireActivity(), "Successful", "Bag Submission completed",
                                R.drawable.ic_checked_green, 1
                            )
                            if (loadingAnim.isShowing) loadingAnim.dismiss()

                            if (isAdded) {
                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_deliveryBagEntry)
                            }
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
    // Date picker
    // ---------------------------------------------------------------------
    private fun showConditionalDatePicker() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(requireContext(), { _, selYear, selMonth, selDay ->
            selectedDate = "$selYear-${selMonth + 1}-$selDay"
            _binding?.txtRCallDeliveryDate?.setText(
                Utility.changeDateFormat(selectedDate, "yyyy-MM-dd", "dd MMM yyyy")
            )
        }, year, month, day)

        val minCal = Calendar.getInstance()
        val maxCal = Calendar.getInstance()

        if (day <= 10) {
            minCal.set(Calendar.DAY_OF_MONTH, 1)
            minCal.add(Calendar.MONTH, -1)
            maxCal.set(Calendar.DAY_OF_MONTH, day)
        } else {
            minCal.set(Calendar.DAY_OF_MONTH, 1)
            maxCal.set(Calendar.DAY_OF_MONTH, day)
        }

        dialog.datePicker.minDate = minCal.timeInMillis
        dialog.datePicker.maxDate = maxCal.timeInMillis

        dialog.show()
    }


    // ---------------------------------------------------------------------
    // Confirmation bottom sheet
    // ---------------------------------------------------------------------
    private fun confirmationDialog() {
        val v: View = layoutInflater.inflate(R.layout.add_ts_party_site_comfirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancelR: MaterialButton = v.findViewById(R.id.btnCancelR)
        val btnConfirmR: MaterialButton = v.findViewById(R.id.btnConfirmR)
        val cancel: TextView = v.findViewById(R.id.cancelR)
        val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val txtBody: TextView = v.findViewById(R.id.txtBody)

        txtCDialogTitle.text = "Delivery Quantity Submission"
        txtBody.text = "Do you want to Submit this Quantity?"

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener { dialog.hide() }
        btnCancelR.setOnClickListener { dialog.hide() }

        btnConfirmR.setOnClickListener {
            val empI = Utility.getValueByKey(requireActivity(), "username").toString()

            if (latitude == 0.0) {
                getLastKnownLocation()?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }

            val directComprecordData = prepareOtherCustomerSiteData()
            if (directComprecordData != null) {
                TSCallCompMoveDirectSave(empI, directComprecordData, dialog)
            } else {
                dialog.dismiss()
                Utility.commonToast(requireActivity(), "warning", "Missing data, please try again", 0)
            }
        }
    }


    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------
    private fun delValidation(): Boolean {
        val b = _binding ?: return false
        var result: Boolean
        b.layoutRdeliveryOrderQuantity.isErrorEnabled = false
        b.layoutRdeliveryOrderQuantity.error = null
        b.layoutRCallDeliveryDate.isErrorEnabled = false
        b.layoutRCallDeliveryDate.error = null

        val qtyText = b.txtRdeliveryOrderQuantity.text.toString()
        val qty = qtyText.toIntOrNull()

        if (qtyText.isEmpty() || qty == null || qty < 50 || qty > 5000) {
            result = false
            b.layoutRdeliveryOrderQuantity.isErrorEnabled = true
            b.layoutRdeliveryOrderQuantity.error = "Delivery order quantity must be in between 50 to 5000"
        } else if (b.txtRCallDeliveryDate.text.toString().isEmpty()) {
            result = false
            b.layoutRCallDeliveryDate.isErrorEnabled = true
            b.layoutRCallDeliveryDate.error = "Delivery date is required"
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

    private fun convertOtherInfo(activity: Activity): GetTSSiteInfoChild? {
        return try {
            val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
            val json = prefs.getString("selectedSiteInfo", null) ?: return null
            Gson().fromJson(json, GetTSSiteInfoChild::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun prepareOtherCustomerSiteData(): String? {
        if (savedCalldata.isEmpty()) return null
        val currentSite = other ?: convertOtherInfo(requireActivity()) ?: return null
        other = currentSite

        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
        val division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        val district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        val thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()
        val selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        val first = savedCalldata[0]
        var conversionStatus = first.siteConvStat.toString()

        if (latitude == 0.0) {
            getLastKnownLocation()?.let {
                latitude = it.latitude
                longitude = it.longitude
            }
        }

        conversionStatus = if (conversionStatus == "Yes") "Already converted site" else conversionStatus

        val qtyText = _binding?.txtRdeliveryOrderQuantity?.text?.toString()
        val qty = qtyText?.toIntOrNull() ?: return null

        val data = OtherCustomerdirectcallinfo(
            first.partyId.toString(),
            first.siteId.toString(),
            "Bangladesh",
            division,
            district,
            thana,
            "",
            selectedCustomer,
            "",
            "",
            first.partyContact,
            first.partyName,
            first.partyMobile.toString(),
            "",
            latitude,
            longitude,
            "Reference Call",
            emp,
            first.discussTop.toString(),
            first.discussSubTopic.toString(),
            first.siteRefBy.toString(),
            first.cemType.toString(),
            qty,
            first.respOff.toString(),
            "",
            "",
            conversionStatus,
            "",
            selectedDate,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "jpg",
            first.movStatus.toString(),
            first.dealerId.toString(),
            first.retailerId.toString(),
            first.siteCurrStatus.toString(),
        )

        return Gson().toJson(data)
    }

    private fun getLastKnownLocation(): Location? {
        return try {
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var bestLocation: Location? = null
            for (provider in locationManager.getProviders(true)) {
                val location = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }
            bestLocation
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
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