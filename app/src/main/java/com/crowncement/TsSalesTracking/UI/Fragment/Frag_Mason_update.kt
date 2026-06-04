package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Adapter.OtherCustomerListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.RetailerListAdapter
import com.crowncement.TsSalesTracking.data.Model.DataForMasontag
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.DistrictChild
import com.crowncement.TsSalesTracking.data.Model.DivisionChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild3
import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.data.Model.RouteChild
import com.crowncement.TsSalesTracking.data.Model.ThanaChild
import com.crowncement.TsSalesTracking.databinding.FragMasonUpdateBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID
import javax.sql.DataSource

class Frag_Mason_update : Fragment() {

    private var _binding: FragMasonUpdateBinding? = null
    private val binding get() = _binding!!

    lateinit var cardFile: ArrayList<File>
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var other: OtherCustomerChild
    lateinit var loadingAnim: Dialog

    var extension = ""

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    lateinit var finalFile: File
    lateinit var nidfinalFile: File
    lateinit var nomNidFinalFile: File
    var currentPhotoPath = ""
    var currentPhotoIDPath = ""
    var fileType = ""
    lateinit var attachFile: File
    lateinit var pdfFile: File
    lateinit var otheritemAdapter: OtherCustomerListAdapter
    lateinit var retaileritemAdapter: RetailerListAdapter

    lateinit var rltnTypes: ArrayList<String>
    lateinit var clientTypes: ArrayList<String>

    lateinit var retailerLists: ArrayList<RetailerChild>
    lateinit var dealerLists: ArrayList<DealerChild>
    lateinit var otherCustomerLists: ArrayList<OtherCustomerChild>
    lateinit var routeList: ArrayList<RouteChild>
    lateinit var divisionList: ArrayList<DivisionChild>
    lateinit var districtList: ArrayList<DistrictChild>
    lateinit var thanaList: ArrayList<ThanaChild>

    private val permissionCode = 101
    val REQUEST_CODE = 200
    var party_code = ""
    var mason_code = ""
    var masonSetupInfo = ""
    var user = ""

    var selectedDivision = ""
    var selectedDistrict = ""
    var selectedThana = ""
    var selectedRoute = ""

    var SELECTED_CONTRACTOR_ID = ""
    var SELECTED_RETAILER_ID = ""
    var selectedRouteName = ""

    var nomfileType = ""
    var Nom_Attach_REQUEST_CODE = 201
    lateinit var nomCardFile: ArrayList<File>
    var nomextension = ""
    lateinit var nompdfFile: File

    var nidfinalFileInKB: Double = 0.0
    var finalFileInKB: Double = 0.0
    var nomNidFileInKB: Double = 0.0

    var fileSizeInMB: Double = 0.0
    var nidfinalFileInMB: Double = 0.0
    var nomNidFileInMB: Double = 0.0

    lateinit var uri: Uri
    lateinit var niduri: Uri
    lateinit var nomuri: Uri

    var realPath = ""
    var prealPath = ""
    var nrealPath = ""

    var nidSelectionType = ""
    var nomSelectionType = ""
    var nomcurrentPhotoIDPath = ""

    lateinit var selectedImage: Bitmap
    lateinit var nidselectedImage: Bitmap
    lateinit var nomselectedImage: Bitmap

    // FIX 1: Changed from 0 to 111 to avoid Android internal conflicts
    private var PRO_PIC_CAMERA_REQ_CODE = 111
    private var NID_CAMERA_REQ_CODE = 130
    private var NOM_NID_CAMERA_REQ_CODE = 131
    private var NID_ATTACH_REQUEST_CODE = 200

    // FIX 2: Track if user has taken a new photo so we don't overwrite with server image
    private var isNewProPicSelected = false
    private var isNewNidSelected = false
    private var isNewNomNidSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragMasonUpdateBinding.inflate(inflater, container, false)
        uri = Uri.EMPTY
        niduri = Uri.EMPTY
        nomuri = Uri.EMPTY
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        RetailerViewModelFactory()
        setupViewModel()

        binding.layoutUMEmail.visibility = View.GONE
        binding.layoutUMPartyZIP.visibility = View.GONE

        finalFile = File("")
        nidfinalFile = File("")
        nomNidFinalFile = File("")
        cardFile = ArrayList()
        nomCardFile = ArrayList()

        user = Utility.getValueByKey(requireActivity(), "username").toString()
        prepareLocation()
        getLastLocation()

        // FIX 3: Call this ONCE only here, never again after image capture
        setPreviouslySaveValueForUpdate()
        eventClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadRltnType(ac: AutoCompleteTextView) {
        rltnTypes = ArrayList()
        rltnTypes.add("Nominees relation with mason")
        rltnTypes.add("Father")
        rltnTypes.add("Mother")
        rltnTypes.add("Spouse")
        rltnTypes.add("Son")
        rltnTypes.add("Daughter")
        rltnTypes.add("Others")
        ac.setText(rltnTypes[0])
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, rltnTypes)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
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

    private fun setPreviouslySaveValueForUpdate() {
        val operationStatus = Utility.getValueByKey(requireActivity(), "mason_update")
        masonSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

        if (operationStatus != "") {
            other = convertOtherInfo(requireActivity())!!
        }

        if (operationStatus == "YES" && other.partyCode.toString() != "") {

            binding.btnUpdatemason.text = "Update Mason"

            party_code = other.partyCode.toString()
            mason_code = other.partyCode.toString()
            binding.txtUMAdd.setText(other.partyAdd1.toString())
            binding.txtUMCity.setText(other.partyCity.toString())

            val opendate = Utility.changeDateFormat(
                other.partyOpenDate.toString(), "yyyy-MM-dd", "dd MMM yyyy"
            )

            binding.txtUMasonRegDate.setText("Registration Date : $opendate")
            binding.masonUPartycode.setText("Party Code : ${other.partyCode}")
            binding.txtMUNIDNum.setText(other.masNid.toString())
            binding.txtUMasonname.setText(other.partyName.toString())
            binding.txtUMMobile.setText(other.partyMobile.toString())
            binding.txtMUNName.setText(other.masNomNam.toString())
            binding.txtMUNnum.setText(other.masNomMob.toString())
            binding.acMUNRltn.setText(other.masNomRel.toString())

            if (other.contId.toString().isNotEmpty()) {
                binding.txtTdeliveryQuantityContractorCode.isEnabled = false
                binding.txtTdeliveryQuantityContractorCode.setText(
                    other.contIdNam.toString() + "(" + other.contId.toString() + ")"
                )
                SELECTED_CONTRACTOR_ID = other.contId.toString()
            } else {
                binding.txtTdeliveryQuantityContractorCode.isEnabled = true
            }

            // FIX 4: Only load server images if user has NOT selected a new one
            if (!isNewProPicSelected) {

                if (other.partyImage != "") {


                    Glide.with(this)
                        //.load("http://34.142.207.144:8787/da/docs/x880022/salesbpimg/${other.partyImage}.jpg")

                        .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                        .error(R.drawable.default_user_image)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.imgUMason)


                }


            }

            if (!isNewNidSelected) {
                if (other.nidImg != "") {
                    val ext = other.nidImg.toString().substringAfterLast(".")
                    if (ext == "pdf") {
                        binding.imgUMNidFile.setImageDrawable(null)
                        binding.txtMNidFileName.text = other.nidImg.toString().split("/").last()
                        binding.imgUMNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                        binding.imgUMNidFile.scaleType = ImageView.ScaleType.FIT_CENTER
                    } else {
                        Glide.with(this)
                            .load(EndPoint.IMAGE_BASE_URL + other.nidImg)
                            .error(R.drawable.ic_baseline_broken_image)
                            .fitCenter()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.imgUMNidFile)
                    }
                } else {
                    Glide.with(this)
                        .load("http://34.142.207.144:8787/da/docs/x880022/Mason/nid/${other.partyCode}.jpg")
                        .error(R.drawable.ic_baseline_broken_image)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.imgUMNidFile)
                }
            }

            if (!isNewNomNidSelected) {
                if (other.nomNidImg != "") {
                    val ext = other.nomNidImg.toString().substringAfterLast(".")
                    if (ext == "pdf") {
                        binding.imgMUNomNidFile.setImageDrawable(null)
                        binding.txtMUNomNidFileName.text =
                            other.nomNidImg.toString().split("/").last()
                        binding.imgMUNomNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                        binding.imgMUNomNidFile.scaleType = ImageView.ScaleType.FIT_CENTER
                    } else {
                        Glide.with(this)
                            .load(EndPoint.IMAGE_BASE_URL + other.nomNidImg)
                            .error(R.drawable.ic_baseline_broken_image)
                            .fitCenter()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.imgMUNomNidFile)
                    }
                }
            }
        }
    }

    fun eventClickListener() {

        binding.acMUNRltn.setOnTouchListener { _, _ ->
            loadRltnType(binding.acMUNRltn)
            false
        }

        binding.takeUMImage.setOnClickListener {

            captureImage(PRO_PIC_CAMERA_REQ_CODE)
        }

        binding.addUMNidCamera.setOnClickListener {
            if (other.nidImg.toString().isEmpty()) {
                binding.imgUMNidFile.setBackgroundResource(0)
                captureImage(NID_CAMERA_REQ_CODE)
            }
        }

        binding.imgUAddNID.setOnClickListener {
            if (other.nidImg.toString().isEmpty()) {
                binding.imgUMNidFile.setBackgroundResource(0)
                openGalleryForImages()
            }
        }

        binding.addMUNomNidCamera.setOnClickListener {
            binding.imgMUNomNidFile.setBackgroundResource(0)
            captureImage(NOM_NID_CAMERA_REQ_CODE)
        }

        binding.imgAddUNomNID.setOnClickListener {
            binding.imgMUNomNidFile.setBackgroundResource(0)
            openGalleryForNOMNidImages()
        }

        binding.txtTdeliveryQuantityContractorCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) siteReferenceDialog("Contractor")
        }

        binding.btnUpdatemason.setOnClickListener {
            if (validationForMasonUpdate() && party_code != "") {
                confirmStart("Update Mason Information")
            }
        }
    }

    private fun openGalleryForNOMNidImages() {
        nomfileType = "ATTACH"
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures"), NID_ATTACH_REQUEST_CODE
            )
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, Nom_Attach_REQUEST_CODE)
        }
    }

    private fun siteReferenceDialog(partyType: String) {
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

        loadClientType(acClient, partyType)
        getDivisionList("Bangladesh", acDivision)

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

        txtRetailerType.text = "$partyType Information"
        tc_Cancle.setOnClickListener { dialog.dismiss() }
        btnRefSubmit.setOnClickListener { dialog.dismiss() }

        acDivision.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedDivision = parent.getItemAtPosition(position).toString()
                getDistrictList("Bangladesh", selectedDivision, acDistrict)
            } else selectedDivision = ""
        }

        acDistrict.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedDistrict = parent.getItemAtPosition(position).toString()
                getThanaList("Bangladesh", acDivision.text.toString(), selectedDistrict, acThana)
            } else selectedDistrict = ""
        }

        acThana.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0) {
                selectedThana = parent.getItemAtPosition(position).toString()
                getRouteList(
                    "Bangladesh", acDivision.text.toString(),
                    acDistrict.text.toString(), selectedThana, acRoute
                )
                if (acClient.text.toString() != "Dealer" && acClient.text.toString() != "Retailer") {
                    getOtherCustomerList(
                        "Bangladesh", acDivision.text.toString(),
                        acDistrict.text.toString(), acThana.text.toString(),
                        acClient.text.toString(), txtNoDealerInfo, dialog, rdRetailer
                    )
                }
            } else selectedThana = ""
        }

        acRoute.setOnItemClickListener { parent, _, position, _ ->
            if (position > 0 && routeList.size > 0) {
                selectedRoute = routeList[position - 1].routeNo.toString()
                selectedRouteName =
                    routeList[position - 1].routeNo.toString() + ">" + routeList[position - 1].routeName.toString()
                if (acClient.text.toString() == "Retailer") {
                    getRetailerList(
                        "Bangladesh", acDivision.text.toString(),
                        acDistrict.text.toString(), acThana.text.toString(),
                        selectedRoute, rdRetailer, txtNoDealerInfo, dialog
                    )
                }
            } else selectedRoute = ""
        }

        search_customer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) filter(query, acClient, txtNoDealerInfo, rdRetailer, dialog)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) filter(newText, acClient, txtNoDealerInfo, rdRetailer, dialog)
                return false
            }
        })
    }

    private fun loadClientType(ac: AutoCompleteTextView, selectedText: String) {
        clientTypes = ArrayList()
        clientTypes.add("Contractor")
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, clientTypes
        )
        ac.setText(clientTypes[clientTypes.indexOf(selectedText)])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getDivisionList(value: String, acDivision: AutoCompleteTextView) {
        retailerViewModel.getDivisionInfo(value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        divisionList = ArrayList()
                        divisionList = res.data
                        loadDivision(acDivision, res.data)
                    }
                }
                Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDivision(ac: AutoCompleteTextView, lists: ArrayList<DivisionChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Division")
        for (item in lists) items.add(item.divName.toString())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getDistrictList(country: String, division: String, acDistrict: AutoCompleteTextView) {
        retailerViewModel.getDistrictInfo(country, division).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        acDistrict.isEnabled = true
                        districtList = ArrayList()
                        districtList = res.data
                        loadDistrict(acDistrict, res.data)
                    }
                }
                Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDistrict(acDistrict: AutoCompleteTextView?, data: ArrayList<DistrictChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select District")
        for (item in data) items.add(item.disName.toString())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        acDistrict!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getThanaList(
        country: String, division: String, district: String, acThana: AutoCompleteTextView
    ) {
        retailerViewModel.getThanaInfo(country, division, district).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        acThana.isEnabled = true
                        thanaList = ArrayList()
                        thanaList = res.data
                        loadThana(acThana, res.data)
                    }
                }
                Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadThana(acThana: AutoCompleteTextView?, data: ArrayList<ThanaChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Thana")
        for (item in data) items.add(item.thnName.toString())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        acThana!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getRouteList(
        country: String, division: String, district: String,
        thana: String, acRoute: AutoCompleteTextView
    ) {
        retailerViewModel.getRouteInfo(country, division, district, thana).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        acRoute.isEnabled = true
                        routeList = ArrayList()
                        routeList = res.data
                        loadRoute(acRoute, res.data)
                    }
                }
                Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadRoute(acRoute: AutoCompleteTextView?, data: ArrayList<RouteChild>) {
        val items: ArrayList<String> = ArrayList()
        items.add("Select Route")
        for (item in data) items.add(item.routeName.toString())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        acRoute!!.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getOtherCustomerList(
        country: String, division: String, district: String, thana: String,
        xCode: String, txtNoDealerInfo: TextView, dialog: Dialog, rdRetailer: RecyclerView
    ) {
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            otherCustomerLists = res.data
                            prepareOtherList(rdRetailer, res.data, xCode, txtNoDealerInfo, dialog)
                        }
                    }
                    Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun prepareOtherList(
        rv: RecyclerView, items: ArrayList<OtherCustomerChild>,
        type: String, txtNoDealerInfo: TextView, dialog: Dialog
    ) {
        if (items.size > 0) {
            txtNoDealerInfo.visibility = View.GONE
            rv.visibility = View.VISIBLE
            otheritemAdapter = OtherCustomerListAdapter(items, type)
            rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.adapter = otheritemAdapter
            otheritemAdapter.setOnItemClickListener(object :
                OtherCustomerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    SELECTED_CONTRACTOR_ID = items[position].partyCode.toString()
                    binding.txtTdeliveryQuantityContractorCode.setText(
                        items[position].partyName + "(" + items[position].partyCode.toString() + ")"
                    )
                    dialog.dismiss()
                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    private fun filter(
        text: String, acClient: AutoCompleteTextView,
        txtNoDealerInfo: TextView, rdRetailer: RecyclerView, dialog: Dialog
    ) {
        if (text.isNotEmpty()) {
            when {
                acClient.text.toString() == "Retailer" -> {
                    if (retailerLists.size > 0) {
                        val filteredlist = ArrayList<RetailerChild>()
                        for (item in retailerLists) {
                            if (item.partyName!!.lowercase().contains(text.lowercase()) ||
                                item.partyMobile!!.lowercase().contains(text.lowercase())
                            ) filteredlist.add(item)
                        }
                        if (filteredlist.isEmpty()) {
                            rdRetailer.adapter = null
                        } else {
                            prepareRetailerList(rdRetailer, filteredlist, "R", dialog, txtNoDealerInfo)
                        }
                    }
                }
                else -> {
                    if (otherCustomerLists.size > 0) {
                        val filteredlist = ArrayList<OtherCustomerChild>()
                        for (item in otherCustomerLists) {
                            if (item.contactPerson!!.lowercase().contains(text.lowercase()) ||
                                item.partyMobile!!.lowercase().contains(text.lowercase())
                            ) filteredlist.add(item)
                        }
                        if (filteredlist.isEmpty()) {
                            rdRetailer.adapter = null
                        } else {
                            prepareOtherList(rdRetailer, filteredlist, acClient.text.toString(), txtNoDealerInfo, dialog)
                        }
                    }
                }
            }
        }
    }

    private fun prepareRetailerList(
        rv: RecyclerView, items: ArrayList<RetailerChild>,
        type: String, dialog: Dialog, txtNoDealerInfo: TextView
    ) {
        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            txtNoDealerInfo.visibility = View.GONE
            retaileritemAdapter = RetailerListAdapter(items, type)
            rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.adapter = retaileritemAdapter
            retaileritemAdapter.setOnItemClickListener(object :
                RetailerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {
                    SELECTED_RETAILER_ID = items[position].partyCode.toString()
                    dialog.dismiss()
                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }

    private fun getRetailerList(
        country: String, division: String, district: String, thana: String,
        route: String, rdRetailer: RecyclerView, txtNoDealerInfo: TextView, dialog: Dialog
    ) {
        retailerViewModel.getRetailerList(country, division, district, thana, route)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            retailerLists = ArrayList()
                            retailerLists = res.data
                            prepareRetailerList(rdRetailer, res.data, "R", dialog, txtNoDealerInfo)
                        }
                    }
                    Status.ERROR -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun confirmStart(title: String) {
        val v: View = layoutInflater.inflate(R.layout.engineer_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtETitle: TextView = v.findViewById(R.id.txtETitle)
        txtETitle.text = title

        val btnECancel: MaterialButton = v.findViewById(R.id.btnECancel)
        val btnEAdd: MaterialButton = v.findViewById(R.id.btnEAdd)
        if (title == "Update Mason Information") btnEAdd.text = "Update"
        val Ecancel: TextView = v.findViewById(R.id.Ecancel)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnECancel.setOnClickListener { dialog.dismiss() }
        Ecancel.setOnClickListener { dialog.dismiss() }

        btnEAdd.setOnClickListener {
            dialog.dismiss()
            if (title == "Update Mason Information") {
                val updateData = prepareMasonUpdateData()
                updateOtherParty(user, party_code, updateData)
            }
        }
    }

    private fun prepareMasonUpdateData(): String {
        val otherCustomer = OtherCustomerChild3(
            binding.txtUMasonname.text.toString(),
            binding.txtUMAdd.text.toString(),
            "Bangladesh", "",
            binding.txtUMEmail.text.toString(),
            "Bangladesh",
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            binding.txtUMMobile.text.toString(),
            "", "", 0, 0, "", "", "",
            binding.txtMUNIDNum.text.toString(),
            binding.txtMUNName.text.toString(),
            binding.txtMUNnum.text.toString(),
            extension,
            binding.txtUMCity.text.toString(),
            binding.txtUMPartyZIP.text.toString(),
            "Mason",
            nomextension,
            binding.acMUNRltn.text.toString(),
            "", "", "", "", "", ""
        )
        return Gson().toJson(otherCustomer)
    }

    private fun updateOtherParty(user: String, partyCode: String, value: String) {
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireActivity()), "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.updateOther(user, partyCode, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    loadingAnim.dismiss()
                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            if (other.partyImage.toString().isEmpty() && finalFile.isFile) {
                                uploadCoverPhoto(
                                    user, partyCode, "Mason",
                                    latitude.toString(), longitude.toString(),
                                    finalFile, loadingAnim
                                )
                            }
                            if (nomCardFile.size > 0 && nomNidFinalFile.isFile) {
                                uploadCustomerNomDoc(
                                    user, mason_code, "mason", "nomnid",
                                    nomextension, nomCardFile, loadingAnim, "nid"
                                )
                            }
                            if (other.nidImg.toString().isEmpty() && cardFile.size > 0 && nidfinalFile.isFile) {
                                uploadCustomerDoc(
                                    user, partyCode, "mason", "nid",
                                    extension, cardFile, loadingAnim, "nid"
                                )
                            }
                            if (other.contId.equals("")) {
                                mason_code = party_code
                                val profileData = prepareListForTag()
                                MasonTag(user, profileData, loadingAnim)
                            } else {
                                loadingAnim.dismiss()
                                Utility.getBaseMessage(
                                    requireActivity(), "Updated", res.message.toString(),
                                    R.drawable.ic_checked_green, 1
                                )
                                requireActivity().findNavController(R.id.nav_host_fragment)
                                    .navigate(R.id.nav_mason_database)
                            }
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

    private fun uploadCustomerNomDoc(
        user_id: String, party_code: String, party_type: String,
        doc_type: String, doc_ext: String, all_images: ArrayList<File>,
        loading: Dialog, docType: String
    ) {
        retailerViewModel.uploadCusFile(user_id, party_code, party_type, doc_type, doc_ext, all_images)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            try {
                                if (res.code != "200") {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(), "Warning",
                                        res.message.toString(), R.drawable.error_white, 0
                                    )
                                } else {

                                }
                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: ${e.message}")
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun uploadCoverPhoto(
        user_id: String, party_code: String, party_type: String,
        party_lat: String, party_lon: String, party_image: File, loading: Dialog
    ) {
        retailerViewModel.uploadCover(user_id, party_code, party_type, party_lat, party_lon, party_image)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(), "Successful",
                                        "Mason's image added successfully",
                                        R.drawable.ic_checked_green, 1
                                    )
                                } else {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(), "Warning",
                                        res.message.toString(), R.drawable.error_white, 0
                                    )
                                }
                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: ${e.message}")
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun uploadCustomerDoc(
        user_id: String, party_code: String, party_type: String,
        doc_type: String, doc_ext: String, all_images: ArrayList<File>,
        loading: Dialog, docType: String
    ) {
        retailerViewModel.uploadCusFile(user_id, party_code, party_type, doc_type, doc_ext, all_images)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()
                                    if (party_code != "") {
                                        mason_code = party_code
                                        if (other.contId.equals("")) {
                                            MasonTag(user_id, prepareListForTag(), loading)
                                        } else {
                                            loading.dismiss()
                                            Utility.getBaseMessage(
                                                requireActivity(), "Successful",
                                                "Mason updated successfully",
                                                R.drawable.ic_checked_green, 1
                                            )
                                            /*
                                            requireActivity().findNavController(R.id.nav_host_fragment)
                                                .navigate(R.id.nav_mason_database)

                                             */
                                        }
                                    } else loading.dismiss()
                                } else {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(), "Warning",
                                        res.message.toString(), R.drawable.error_white, 0
                                    )
                                }
                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: ${e.message}")
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    fun prepareListForTag(): String {
        val dataForMasontag = DataForMasontag(mason_code, SELECTED_CONTRACTOR_ID)
        return Gson().toJson(dataForMasontag)
    }

    private fun MasonTag(user: String, value: String, loading: Dialog) {
        retailerViewModel.ContMasonTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            loadingAnim.dismiss()
                            loading.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(), "Successful",
                                "Mason updated and tagged with the contractor successfully",
                                R.drawable.ic_checked_green, 1
                            )
                            /*
                            requireActivity().findNavController(R.id.nav_host_fragment)
                                .navigate(R.id.nav_mason_database)

                             */
                        } else {
                            loading.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(), "Error",
                                res.message.toString(), R.drawable.error_white, 0
                            )
                        }
                    }
                }
                Status.ERROR -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ===================== CAMERA =====================

    fun captureImage(requestCode: Int) {
        when (requestCode) {
            PRO_PIC_CAMERA_REQ_CODE -> currentPhotoPath = ""
            NID_CAMERA_REQ_CODE -> currentPhotoIDPath = ""
            NOM_NID_CAMERA_REQ_CODE -> nomcurrentPhotoIDPath = ""
        }
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> dispatchTakePictureIntent(requestCode)
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showCameraPermissionRationale()
            else -> requestPermissions()
        }
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            context?.let { ctx ->
                takePictureIntent.resolveActivity(ctx.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile(requestCode)
                    } catch (ex: IOException) {
                        Log.e("CAMERA", "Error creating file: ${ex.message}")
                        null
                    }
                    photoFile?.also { file ->
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.crowncement.TsSalesTracking.fileprovider",
                            file
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, requestCode)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(requestCode: Int): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getOutputDirectory()

        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

        when (requestCode) {
            PRO_PIC_CAMERA_REQ_CODE -> {
                currentPhotoPath = file.absolutePath
                Log.d("CAMERA_PATH", "Pro pic path: $currentPhotoPath")
            }
            NID_CAMERA_REQ_CODE -> {
                fileType = "CAMERAID"
                currentPhotoIDPath = file.absolutePath
                Log.d("CAMERA_PATH", "NID path: $currentPhotoIDPath")
            }
            NOM_NID_CAMERA_REQ_CODE -> {
                nomfileType = "CAMERAID"
                nomcurrentPhotoIDPath = file.absolutePath
                Log.d("CAMERA_PATH", "Nom NID path: $nomcurrentPhotoIDPath")
            }
        }
        return file
    }

    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, "CrownTitanAttendance").apply { mkdirs() }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
    }

    private fun openGalleryForImages() {
        fileType = "ATTACH"
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    // ===================== ACTIVITY RESULT =====================

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            Log.d("ACTIVITY_RESULT", "Cancelled or failed, requestCode=$requestCode")
            return
        }

        try {
            when (requestCode) {

                PRO_PIC_CAMERA_REQ_CODE -> {
                    var imgFile = File("")

                    Log.d("CAMERA_RESULT", "PRO_PIC path = $currentPhotoPath")

                    if (currentPhotoPath.isEmpty() || !File(currentPhotoPath).exists()) {
                        Toast.makeText(requireActivity(), "Image file not found", Toast.LENGTH_SHORT).show()
                        return
                    }else{
                        imgFile = compressImageToUnder200KB(currentPhotoPath)!!

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (bitmap != null) {
                            binding.imgUMason.post {
                                binding.imgUMason.setImageBitmap(bitmap)
                            }
                        }
                    }


                    // FIX 5: Compress FIRST, then display — never call setPreviouslySaveValueForUpdate again
                    if (imgFile != null && imgFile.exists()) {
                        finalFile = imgFile
                        finalFileInKB = finalFile.length() / 1024.0
                        Log.d("FILE_SIZE", "Pro pic size: $finalFileInKB KB")

                        // FIX 6: Mark new photo selected so server image is never loaded again
                        isNewProPicSelected = true


                    } else {
                        Toast.makeText(requireActivity(), "Compression failed", Toast.LENGTH_SHORT).show()
                    }

                    if (latitude == 0.0) getLastLocation()
                }

                NID_CAMERA_REQ_CODE -> {
                    Log.d("CAMERA_RESULT", "NID path = $currentPhotoIDPath")

                    if (currentPhotoIDPath.isEmpty() || !File(currentPhotoIDPath).exists()) {
                        Toast.makeText(requireActivity(), "NID file not found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val imgFile = compressImageToUnder200KB(currentPhotoIDPath)
                    if (imgFile != null && imgFile.exists()) {
                        nidfinalFile = imgFile
                        nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        cardFile.clear()
                        cardFile.add(nidfinalFile)
                        extension = "jpg"
                        isNewNidSelected = true
                        Log.d("FILE_SIZE", "NID size: $nidfinalFileInKB KB")

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (bitmap != null) {
                            binding.imgUMNidFile.post {
                                binding.imgUMNidFile.setImageBitmap(bitmap)
                            }
                        }
                    }
                }

                NID_ATTACH_REQUEST_CODE -> {
                    val imgUri: Uri? = if (data?.clipData != null) {
                        data.clipData!!.getItemAt(0).uri
                    } else data?.data

                    imgUri?.let { uri ->
                        try {
                            val sourceFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                createFileFromImageUri(requireContext(), uri)!!
                            } else {
                                File(ImagePathUtils.getRealPath(requireContext(), uri)!!)
                            }
                            val imgFile = compressImageToUnder200KB(sourceFile.absolutePath)
                            if (imgFile != null && imgFile.exists()) {
                                niduri = uri
                                nidfinalFile = imgFile
                                nidfinalFileInKB = nidfinalFile.length() / 1024.0
                                cardFile.clear()
                                cardFile.add(nidfinalFile)
                                extension = "jpg"
                                isNewNidSelected = true
                                Log.d("FILE_SIZE", "NID attach size: $nidfinalFileInKB KB")
                                Glide.with(this).load(nidfinalFile).fitCenter()
                                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgUMNidFile)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                NOM_NID_CAMERA_REQ_CODE -> {
                    Log.d("CAMERA_RESULT", "NOM NID path = $nomcurrentPhotoIDPath")

                    if (nomcurrentPhotoIDPath.isEmpty() || !File(nomcurrentPhotoIDPath).exists()) {
                        Toast.makeText(requireActivity(), "Nominee NID file not found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val imgFile = compressImageToUnder200KB(nomcurrentPhotoIDPath)
                    if (imgFile != null && imgFile.exists()) {
                        nomNidFinalFile = imgFile
                        nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        nomCardFile.clear()
                        nomCardFile.add(nomNidFinalFile)
                        nomextension = "jpg"
                        isNewNomNidSelected = true
                        Log.d("FILE_SIZE", "Nom NID size: $nomNidFileInKB KB")

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (bitmap != null) {
                            binding.imgMUNomNidFile.post {
                                binding.imgMUNomNidFile.setImageBitmap(bitmap)
                            }
                        }
                    }
                }

                Nom_Attach_REQUEST_CODE -> {
                    val imgUri: Uri? = if (data?.clipData != null) {
                        data.clipData!!.getItemAt(0).uri
                    } else data?.data

                    imgUri?.let { uri ->
                        try {
                            val sourceFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                createFileFromImageUri(requireContext(), uri)!!
                            } else {
                                File(ImagePathUtils.getRealPath(requireContext(), uri)!!)
                            }
                            val imgFile = compressImageToUnder200KB(sourceFile.absolutePath)
                            if (imgFile != null && imgFile.exists()) {
                                nomuri = uri
                                nomNidFinalFile = imgFile
                                nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                                nomCardFile.clear()
                                nomCardFile.add(nomNidFinalFile)
                                nomextension = "jpg"
                                isNewNomNidSelected = true
                                Log.d("FILE_SIZE", "Nom NID attach size: $nomNidFileInKB KB")
                                Glide.with(this).load(nomNidFinalFile).fitCenter()
                                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgMUNomNidFile)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ACTIVITY_RESULT", "Error: ${e.message}")
            Toast.makeText(requireActivity(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ===================== FIX 7: PROPER COMPRESSION UNDER 200KB =====================

    /**
     * Compresses image file to strictly under 200KB.
     * Keeps reducing quality and resolution until target is met.
     */
    private fun compressImageToUnder200KB(imagePath: String): File? {
        return try {
            val maxBytes = 200 * 1024 // 200 KB in bytes

            // Step 1: Decode with sample size to avoid OOM
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imagePath, options)
            options.inSampleSize = calculateInSampleSize(options, 1280, 1280)
            options.inJustDecodeBounds = false

            var bitmap = BitmapFactory.decodeFile(imagePath, options)
                ?: return null

            // Step 2: Try compressing with decreasing quality
            var quality = 85
            var outputStream = ByteArrayOutputStream()

            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 10
                Log.d("COMPRESS", "Quality: ${quality + 10}, Size: ${outputStream.size() / 1024} KB")
            } while (outputStream.size() > maxBytes && quality > 10)

            // Step 3: If still over 200KB, scale down resolution
            if (outputStream.size() > maxBytes) {
                val scaleFactor = Math.sqrt(maxBytes.toDouble() / outputStream.size())
                val newWidth = (bitmap.width * scaleFactor).toInt().coerceAtLeast(100)
                val newHeight = (bitmap.height * scaleFactor).toInt().coerceAtLeast(100)
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                Log.d("COMPRESS", "After scale, Size: ${outputStream.size() / 1024} KB")
            }

            // Step 4: Save to file
            val outputFile = File(
                requireContext().getDir("collection", Context.MODE_PRIVATE),
                "${UUID.randomUUID()}.jpg"
            )
            FileOutputStream(outputFile).use { fos ->
                fos.write(outputStream.toByteArray())
            }

            val finalSizeKB = outputFile.length() / 1024.0
            Log.d("COMPRESS", "Final file size: $finalSizeKB KB at path: ${outputFile.absolutePath}")

            outputFile
        } catch (e: Exception) {
            Log.e("COMPRESS", "Compression error: ${e.message}")
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun createFileFromImageUri(context: Context, imageUri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        return try {
            FileOutputStream(file).use { out -> inputStream?.copyTo(out) }
            inputStream?.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)!!
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path = cursor.getString(columnIndex)
        cursor.close()
        return path
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap, imageName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$imageName.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let {
            resolver.openOutputStream(it)?.use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
        }
        return imageUri
    }

    // ===================== VALIDATION =====================

    fun validationForMasonUpdate(): Boolean {
        binding.layoutUMasonname.isErrorEnabled = false
        binding.layoutUMasonname.error = null
        binding.layoutUMAdd.isErrorEnabled = false
        binding.layoutUMAdd.error = null
        binding.layoutUMMobile.isErrorEnabled = false
        binding.layoutUMMobile.error = null
        binding.layoutTdeliveryContractorCode.isErrorEnabled = false
        binding.layoutTdeliveryContractorCode.error = null
        binding.layoutMUNIDNum.isErrorEnabled = false
        binding.layoutMUNIDNum.error = null

        if (nidfinalFile.isFile) nidfinalFileInKB = nidfinalFile.length() / 1024.0
        if (finalFile.isFile) finalFileInKB = finalFile.length() / 1024.0
        if (nomNidFinalFile.isFile) nomNidFileInKB = nomNidFinalFile.length() / 1024.0

        return when {
            binding.txtUMasonname.text.toString().isEmpty() -> {
                binding.layoutUMasonname.isErrorEnabled = true
                binding.layoutUMasonname.error = "Mason's name is required"
                false
            }
            binding.txtUMAdd.text.toString().isEmpty() -> {
                binding.layoutUMAdd.isErrorEnabled = true
                binding.layoutUMAdd.error = "Mason's address is required"
                false
            }
            binding.txtUMMobile.text.toString().isEmpty() ||
                    binding.txtUMMobile.text.toString().length < 11 -> {
                binding.layoutUMMobile.isErrorEnabled = true
                binding.layoutUMMobile.error = "Mason's 11 digit mobile number is required"
                false
            }
            binding.txtMUNIDNum.text.toString().isEmpty() -> {
                binding.layoutMUNIDNum.isErrorEnabled = true
                binding.layoutMUNIDNum.error = "Mason's NID card number is required"
                false
            }
            SELECTED_CONTRACTOR_ID.isEmpty() -> {
                binding.layoutTdeliveryContractorCode.isErrorEnabled = true
                binding.layoutTdeliveryContractorCode.error =
                    "Contractor selection is required for this mason"
                false
            }
            nidfinalFileInKB > 200 -> {
                Utility.getBaseMessage(
                    requireActivity(),
                    "NID card size exceeds 200 KB",
                    "Current size: ${String.format("%.2f", nidfinalFileInKB)} KB",
                    R.drawable.error_white, 0
                )
                false
            }
            finalFileInKB > 200 -> {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Mason image size exceeds 200 KB",
                    "Current size: ${String.format("%.2f", finalFileInKB)} KB",
                    R.drawable.error_white, 2
                )
                false
            }
            nomNidFileInKB > 200 -> {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Nominee NID size exceeds 200 KB",
                    "Current size: ${String.format("%.2f", nomNidFileInKB)} KB",
                    R.drawable.error_white, 2
                )
                false
            }
            else -> true
        }
    }

    // ===================== LOCATION =====================

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location = task.result
                    if (location == null) requestNewLocationData()
                    else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            } else requestDeviceLocationSettings()
        } else requestPermissions()
    }

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { requestPermissions() }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 100)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("Location", "requestDeviceLocationSettings: ${sendEx.message}")
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
            return
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation!!
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
        }
    }

    private fun getLastLocations() {
        EasyLocation(requireActivity(), object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {
                Utility.commonToast(requireActivity(), "Location Permission Denied", "Permission denied", 0)
            }
            override fun locationSettingFailed() {
                Utility.commonToast(requireActivity(), "Location Settings Failed", "Location settings issue", 0)
            }
            override fun getLocation(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
            }
        })
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            permissionCode
        )
    }

    private fun showCameraPermissionRationale() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ -> }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    // ===================== TOOLBAR =====================

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.nav_dashboard)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}