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
    lateinit var nomNidFinalFile : File
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

    var selectedDivision =""
    var selectedDistrict=""
    var selectedThana=""
    var selectedRoute=""

    var SELECTED_CONTRACTOR_ID=""
    var SELECTED_RETAILER_ID=""


    var selectedRouteName=""




    var nomfileType = ""
    var Nom_Attach_REQUEST_CODE =201
    lateinit var nomCardFile : ArrayList<File>
    var nomextension = ""
    lateinit var nompdfFile:File


    var nidfinalFileInKB: Double = 0.0
    var finalFileInKB: Double = 0.0
    var nomNidFileInKB: Double = 0.0

    var fileSizeInMB: Double = 0.0
    var nidfinalFileInMB: Double = 0.0
    var nomNidFileInMB: Double = 0.0


    lateinit var uri: Uri
    lateinit var niduri: Uri
    lateinit var nomuri: Uri

    var resizeCount: Int = 0
    var nidesizeCount: Int = 0
    var nomesizeCount: Int = 0

    var realPath = ""
    var prealPath = ""
    var nrealPath = ""

    var nidSelectionType = ""
    var nomSelectionType = ""
    var nomcurrentPhotoIDPath = ""

    lateinit var  selectedImage:Bitmap
    lateinit var  nidselectedImage:Bitmap
    lateinit var  nomselectedImage:Bitmap



    private var PRO_PIC_CAMERA_REQ_CODE = 0
    private var NID_CAMERA_REQ_CODE = 130
    private var NOM_NID_CAMERA_REQ_CODE = 131
    private var NID_ATTACH_REQUEST_CODE = 200




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragMasonUpdateBinding.inflate(inflater, container, false)

        /*
        RetailerViewModelFactory()
        setupViewModel()

        binding.layoutUMEmail.visibility = View.GONE
        binding.layoutUMPartyZIP.visibility = View.GONE
        //  loadRltnType(binding.acMUNRltn)



        finalFile = File("")
        nidfinalFile = File("")
        nomNidFinalFile = File("")
        cardFile = ArrayList()
        nomCardFile = ArrayList()

        user = Utility.getValueByKey(requireActivity(), "username").toString()
        prepareLocation()
        getLastLocation()

        setPreviouslySaveValueForUpdate()

        eventClickListener()


         */

        uri = Uri.EMPTY
        niduri = Uri.EMPTY
        nomuri = Uri.EMPTY


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        RetailerViewModelFactory()
        setupViewModel()

        binding.layoutUMEmail.visibility = View.GONE
        binding.layoutUMPartyZIP.visibility = View.GONE
        //  loadRltnType(binding.acMUNRltn)


       // selectedImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)


        finalFile = File("")
        nidfinalFile = File("")
        nomNidFinalFile = File("")
        cardFile = ArrayList()
        nomCardFile = ArrayList()

        user = Utility.getValueByKey(requireActivity(), "username").toString()
        prepareLocation()
        getLastLocation()

        setPreviouslySaveValueForUpdate()

        eventClickListener()

      //  onClickForResize()
    }


    /*
    fun onClickForResize(){

        if (finalFileInKB >= 200) {
            binding.resizeImage.visibility = View.VISIBLE
        } else if (finalFileInKB < 200) {
            binding.resizeImage.visibility = View.GONE
        }
        binding.resizeImage.setOnClickListener {
            //  openCamera()
          //  if(finalFileInKB>=200) {
                finalFile = File(getRealPathFromURI(uri)!!)

            binding.resizeImage.isEnabled = false

            resizer()
          //  }

        }
///////////////////// Todo NID
        if (nidfinalFileInKB >= 200) {
            binding.resizeNID.visibility = View.VISIBLE
        } else if (nidfinalFileInKB < 200) {
            binding.resizeNID.visibility = View.GONE
        }
        binding.resizeNID.setOnClickListener {
            //  openCamera()
          //  if(nidfinalFileInKB>=200) {
                nidfinalFile = File(getRealPathFromURI(niduri)!!)
            binding.resizeNID.isEnabled = false

            nidresizer()
         //   }

        }
///////////////////TODO Nom
        if (nomNidFileInKB >= 200) {
            binding.resizeNomNID.visibility = View.VISIBLE
        } else if (nomNidFileInKB < 200) {
            binding.resizeNomNID.visibility = View.GONE
        }
        binding.resizeNomNID.setOnClickListener {
            //  openCamera()
           // if(nomNidFileInKB>=200) {
            nomNidFinalFile = File(getRealPathFromURI(nomuri)!!)
            binding.resizeNomNID.isEnabled = false

                nomnidresizer()
          //  }

        }
    }

  
    fun nomnidresizer() {

        if (nomNidFinalFile.isFile) {

            if(nomSelectionType=="camera"){
                when (nomesizeCount) {
                    0 -> {
                        GlobalScope.launch {
                            nomNidFinalFile = Compressor.compress(requireContext(), nomNidFinalFile) {
                                resolution(960, 540)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            nomCardFile.set( 0, nomNidFinalFile )
                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }
                    }

                    1 -> {
                        GlobalScope.launch {
                            nomNidFinalFile = Compressor.compress(requireContext(), nomNidFinalFile) {
                                resolution(768, 432)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            nomCardFile.set( 0, nomNidFinalFile )

                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }
                    }

                    2 -> {
                        GlobalScope.launch {
                            nomNidFinalFile = Compressor.compress(requireContext(), nomNidFinalFile) {
                                resolution(640, 360)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            nomCardFile.set( 0, nomNidFinalFile )

                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }
                    }
                    3->{
                        if(nomNidFileInKB>=200){
                            nomselectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "currentPhotoNomIDPath"))
                            nomselectedImage = getResizedBitmap(nomselectedImage,600)!!
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri = getImageUri(requireContext(), nomselectedImage!!, imName)
                            nomuri = tempUri!!
                            nrealPath = getRealPathFromURI(tempUri)!!
                            nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                            nomCardFile.set( 0, nomNidFinalFile )
                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }else{
                            Utility.commonToast(
                                requireActivity(),
                                "",
                                "Already Optimised",
                                1
                            )
                        }
                    }

                    else -> {

                        if(nomNidFileInKB>=200){
                            nomselectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "currentPhotoNomIDPath"))
                            nomselectedImage = getResizedBitmap(nomselectedImage,500)!!
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri = getImageUri(requireContext(), nomselectedImage!!, imName)
                            nomuri = tempUri!!
                            nrealPath = getRealPathFromURI(tempUri)!!
                            nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                            nomCardFile.set( 0, nomNidFinalFile )

                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }else{
                            Utility.commonToast(
                                requireActivity(),
                                "",
                                "Already Optimised",
                                1
                            )
                        }

                    }
                }

                Utility.commonToast(
                    requireActivity(),
                    "Nominee nid compression done",
                    "Current file size " + String.format("%.2f", nomNidFileInKB) + " KB",
                    1
                )

                nomesizeCount += 1
            }
            else{

                Utility.commonToast(
                    requireActivity(),
                    "Crop selected Nominee NID Image",
                    "Current file size "+ String.format("%.2f", nomNidFileInKB) + " KB. Please crop image to get your desired size ",
                    1
                )
            }

        } else {
            Log.e("Mason_Update", "Error: ")
        }

     //   binding.resizeNomNID.isEnabled = true

    }

    fun resizer() {


        if (finalFile.isFile) {

            when (resizeCount) {
                0 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(960, 540)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }

                1 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(768, 432)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }

                2 -> {
                    GlobalScope.launch {
                        finalFile = Compressor.compress(requireContext(), finalFile) {
                            resolution(640, 360)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                }
                3->{
                    if(finalFileInKB>=200){
                        selectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "masonUpdateImg"))
                        selectedImage= getResizedBitmap(selectedImage,600)!!
                        val emp =
                            Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                       // val tempUri = getImageUri(requireContext(), selectedImage!!, "Party")
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                    else{
                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }
                }

                else -> {

                    if(finalFileInKB>=200){
                        selectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "masonUpdateImg"))
                        selectedImage= getResizedBitmap(selectedImage,500)!!

                        val emp =
                            Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
                    }
                    else{
                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }


                }
            }

            Utility.commonToast(
                requireActivity(),
                "Image compression done",
                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
                1
            )

            resizeCount += 1
        } else {
            Log.e("Mason_Entry", "Error: ")
        }

        binding.resizeImage.isEnabled = true

    }

    fun nidresizer() {

        if (nidfinalFile.isFile) {

            if(nidSelectionType=="camera"){
                when (resizeCount) {
                    0 -> {
                        GlobalScope.launch {
                            nidfinalFile = Compressor.compress(requireContext(), nidfinalFile) {
                                resolution(960, 540)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            cardFile.set( 0, nidfinalFile )

                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }
                    }

                    1 -> {
                        GlobalScope.launch {
                            nidfinalFile = Compressor.compress(requireContext(), nidfinalFile) {
                                resolution(768, 432)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            cardFile.set( 0, nidfinalFile )

                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }
                    }

                    2 -> {
                        GlobalScope.launch {
                            nidfinalFile = Compressor.compress(requireContext(), nidfinalFile) {
                                resolution(640, 360)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            cardFile.set( 0, nidfinalFile )

                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }
                    }
                    3->{
                        if(nidfinalFileInKB>=200){
                            nidselectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "currentPhotoIDPathUpdate"))
                            nidselectedImage = getResizedBitmap(nidselectedImage,600)!!

                            val tempUri = getImageUri(requireContext(), nidselectedImage!!, "MasonNID")
                            uri = tempUri!!
                            realPath = getRealPathFromURI(tempUri)!!
                            nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                            cardFile.set( 0, nidfinalFile )

                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }else{
                            Utility.commonToast(
                                requireActivity(),
                                "",
                                "Already Optimised",
                                1
                            )
                        }
                    }

                    else -> {

                        if(nidfinalFileInKB>=200){
                            nidselectedImage = BitmapFactory.decodeFile(Utility.getValueByKey(requireActivity(), "currentPhotoIDPathUpdate"))
                            nidselectedImage = getResizedBitmap(nidselectedImage,500)!!

                            val tempUri = getImageUri(requireContext(), nidselectedImage!!, "MasonNID")
                            uri = tempUri!!
                            realPath = getRealPathFromURI(tempUri)!!
                            nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                            cardFile.set( 0, nidfinalFile )

                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }else{
                            Utility.commonToast(
                                requireActivity(),
                                "",
                                "Already Optimised",
                                1
                            )
                        }


                    }
                }

                Utility.commonToast(
                    requireActivity(),
                    "Image compression done",
                    "Current file size " + String.format("%.2f", nidfinalFileInKB) + " KB",
                    1
                )

                nidesizeCount += 1
            }else{

                Utility.commonToast(
                    requireActivity(),
                    "Crop selected NID Image",
                    "Current file size "+ String.format("%.2f", nidfinalFileInKB) + " KB. Please crop image to get your desired size ",
                    1
                )
            }


        } else {
            Log.e("Mason_Update", "Error: ")
        }

        binding.resizeNID.isEnabled = true

    }

     */






    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
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

        for (item in rltnTypes) {
            val pos = rltnTypes.indexOf(item)
            ac.setText(rltnTypes[pos])
            // break

        }
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
            //UPDATE
            //Get select item info
            binding.btnUpdatemason.text = "Update Mason"
            //   selectedOutlet = other.partyCat.toString()

            party_code = other.partyCode.toString()
            mason_code = other.partyCode.toString()
            binding.txtUMAdd.setText(other.partyAdd1.toString())
            binding.txtUMCity.setText(other.partyCity.toString())
            var opendate = Utility.changeDateFormat(
                other.partyOpenDate.toString(),
                "yyyy-MM-dd",
                "dd MMM yyyy"
            )

            binding.txtUMasonRegDate.setText("Registration Date : "+opendate)
            binding.masonUPartycode.setText("Party Code : "+ other.partyCode.toString())
            //  binding.txtUMPartyZIP.setText(other.partyZip.toString())
            binding.txtMUNIDNum.setText(other.masNid.toString())

            binding.txtUMasonname.setText(other.partyName.toString())
            //   binding.txtUMEmail.setText(other.partyEmail.toString())

            binding.txtUMMobile.setText(other.partyMobile.toString())
            //  binding.

            binding.txtMUNName.setText(other.masNomNam.toString())
            binding.txtMUNnum.setText(other.masNomMob.toString())

            binding.acMUNRltn.setText(other.masNomRel.toString())



            if(other.contId.toString().isNotEmpty()){
                binding.txtTdeliveryQuantityContractorCode.isEnabled =false
                binding.txtTdeliveryQuantityContractorCode.setText(other.contIdNam.toString()+"("+other.contId.toString()+")")

                // binding.txtTdeliveryQuantityContractorCode.setText(other.contId.toString())
                SELECTED_CONTRACTOR_ID = other.contId.toString()


            }else{
                binding.txtTdeliveryQuantityContractorCode.isEnabled =true

            }



            if (other.partyImage != "") {


                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgUMason)
            }

            var a = other.nidImg
            var b = a
            if (other.nidImg != "") {

                val input = other.nidImg.toString()
                val extension = input.substringAfterLast(".")
                if (extension == "pdf") {
                    binding.imgUMNidFile.setImageDrawable(null)
                    binding.txtMNidFileName.text = other.nidImg.toString().split("/").last()
                    binding.imgUMNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                    binding.imgUMNidFile.scaleType = ImageView.ScaleType.FIT_CENTER

                } else {


                    Glide
                        .with(this)
                        .load(EndPoint.IMAGE_BASE_URL + other.nidImg)
                        .error(R.drawable.ic_baseline_broken_image)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.imgUMNidFile)
                }

            }


            if (other.nomNidImg != "") {

                val input = other.nomNidImg.toString()
                val extension = input.substringAfterLast(".")
                if (extension == "pdf") {
                    binding.imgMUNomNidFile.setImageDrawable(null)
                    binding.txtMUNomNidFileName.text = other.nomNidImg.toString().split("/").last()
                    binding.imgMUNomNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                    binding.imgMUNomNidFile.scaleType = ImageView.ScaleType.FIT_CENTER

                } else {


                    Glide
                        .with(this)
                        .load(EndPoint.IMAGE_BASE_URL + other.nomNidImg)
                        .error(R.drawable.ic_baseline_broken_image)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.imgMUNomNidFile)
                }

            }



        } else {
            //SAVE
            //  binding.btnUpdatemason.text = "Submit"
        }
    }




    fun eventClickListener(){

        binding.acMUNRltn.setOnTouchListener { _, _ ->
            loadRltnType(binding.acMUNRltn)
            false
        }

        binding.takeUMImage.setOnClickListener {
           // openCamera()
            captureImage(PRO_PIC_CAMERA_REQ_CODE)

        }

        binding.addUMNidCamera.setOnClickListener {
            if(other.nidImg.toString().isEmpty()){
                binding.imgUMNidFile.setBackgroundResource(0)
             //   openCameraVisitingCard()
                captureImage(NID_CAMERA_REQ_CODE)

            }
        }
/*
        binding.pdfAddUMNID.setOnClickListener {
            if(other.nidImg.toString().isEmpty()) {
                binding.imgUMNidFile.setBackgroundResource(0)
                openPDFFile()
            }
        }

 */

        binding.imgUAddNID.setOnClickListener {
            if(other.nidImg.toString().isEmpty()) {
                binding.imgUMNidFile.setBackgroundResource(0)
                openGalleryForImages()
            }
        }


        //nominee
        binding.addMUNomNidCamera.setOnClickListener {
            binding.imgMUNomNidFile.setBackgroundResource(0)
         //   openCameraNomNidCard()
            captureImage(NOM_NID_CAMERA_REQ_CODE)

        }
/*
        binding.pdfAddMUNomNID.setOnClickListener {
            binding.imgMUNomNidFile.setBackgroundResource(0)

            openNomPDFFile()

        }

 */


        binding.imgAddUNomNID.setOnClickListener {
            binding.imgMUNomNidFile.setBackgroundResource(0)


            openGalleryForNOMNidImages()
        }

        binding.txtTdeliveryQuantityContractorCode.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                siteReferenceDialog("Contractor")
            }
        }

        binding.btnUpdatemason.setOnClickListener {
            if (validationForMasonUpdate() && party_code != "") {

                confirmStart("Update Mason Information")

            }
        }


    }

    private fun openCameraNomNidCard() {
        nomfileType = "CAMERAID"
        if (latitude == 0.0) {
            //   location()
            getLastLocations()
        }

        val fileName = "photo.jpg"
        val storageDirectory: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoIDPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.TsSalesTracking.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(),"currentPhotoNomIDPath",currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 131)
    }

    private fun openNomPDFFile() {
        nomfileType = "PDF"
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 102)
    }

    private fun openGalleryForNOMNidImages() {
        nomfileType = "ATTACH"

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), NID_ATTACH_REQUEST_CODE)

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, Nom_Attach_REQUEST_CODE)
        }
    }


    //todo for contractor code
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

        tc_Cancle.setOnClickListener { view1: View? ->
            dialog.dismiss()
        }


        btnRefSubmit.setOnClickListener { view1: View? ->
            dialog.dismiss()
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
                selectedRouteName = routeList[position - 1].routeNo.toString() + ">" + routeList[position - 1].routeName.toString()
                Log.e("T", "selectedRoute: $selectedRoute")
                Log.e("TAG", "selectedRouteName: $selectedRouteName")

                // Save this value for loading previously selected customer



                if (acClient.text.toString() == "Retailer") {
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
        //   clientTypes.add("Select Client Type")
        // clientTypes.add("Dealer")
        // clientTypes.add("Retailer")
        //  clientTypes.add("Engineer")
        //  clientTypes.add("IHBs")
        //  clientTypes.add("Mason")
        clientTypes.add("Contractor")

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
                        loadDivision(acDivision, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

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
                        loadDistrict(acDistrict, res.data)
                    }
                }
                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

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
                                     rdRetailer: RecyclerView
    ) {
        retailerViewModel.getOtherList(country, division, district, thana, xCode)
            .observe(requireActivity()) {
                when (it.status) {

                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            val otherCustomerList: ArrayList<OtherCustomerChild> = res.data
                            otherCustomerLists = otherCustomerList
                            prepareOtherList(rdRetailer, otherCustomerList, xCode,txtNoDealerInfo, dialog)
                        }
                    }
                    Status.ERROR -> {

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
                    SELECTED_CONTRACTOR_ID=items[position].partyCode.toString()
                    binding.txtTdeliveryQuantityContractorCode.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")

                    dialog.dismiss()


                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }
    }



    private fun filter(text: String, acClient:AutoCompleteTextView, txtNoDealerInfo:TextView, rdRetailer: RecyclerView, dialog: Dialog) {

        if (text.isNotEmpty()) {
            when {
                acClient.text.toString() == "Dealer" -> {

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

                            //  prepareDealerList(rdRetailer, filteredlist, "D",txtNoDealerInfo,dialog)
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
                acClient.text.toString() == "Retailer" -> {
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
                            rdRetailer.adapter = null
                        } else {
                            prepareOtherList(
                                rdRetailer,
                                filteredlist,
                                acClient.text.toString(),
                                txtNoDealerInfo,
                                dialog
                            )
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




    private fun prepareRetailerList(
        rv: RecyclerView,
        items: ArrayList<RetailerChild>,
        type: String,
        dialog: Dialog,
        txtNoDealerInfo:TextView

    ) {

        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            txtNoDealerInfo.visibility = View.GONE
            retaileritemAdapter = RetailerListAdapter(items, type)
            //val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            //rv.layoutManager = rLayoutmanager

            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = retaileritemAdapter

            retaileritemAdapter.setOnItemClickListener(object :
                RetailerListAdapter.OnAdapterItemClickListener {
                override fun OnClick(v: View?, position: Int) {

                    SELECTED_RETAILER_ID=items[position].partyCode.toString()
                    // binding.txtTdeliveryQuantityRetailerCode.setText(items[position].partyName+"("+items[position].partyCode.toString()+")")
                    dialog.dismiss()

                }
            })
        } else {
            rv.visibility = View.GONE
            txtNoDealerInfo.visibility = View.VISIBLE
        }


    }



    private fun getRetailerList(
        country: String,
        division: String,
        district: String,
        thana: String,
        route: String,
        rdRetailer: RecyclerView,
        txtNoDealerInfo:TextView,
        dialog: Dialog
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

                            prepareRetailerList(rdRetailer, retailerList, "R",dialog,txtNoDealerInfo)
                        }
                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
    }


//todo contractor code end

    private fun confirmStart(title: String) {
        val v: View = layoutInflater.inflate(R.layout.engineer_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtETitle: TextView = v.findViewById(R.id.txtETitle)
        txtETitle.text = title

        val btnECancel: MaterialButton = v.findViewById(R.id.btnECancel)
        val btnEAdd: MaterialButton = v.findViewById(R.id.btnEAdd)
        if (title == "Update Mason Information") {
            btnEAdd.text = "Update"
        }
        val Ecancel: TextView = v.findViewById(R.id.Ecancel)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnECancel.setOnClickListener {
            dialog.dismiss()
        }

        Ecancel.setOnClickListener {
            dialog.dismiss()
        }

        btnEAdd.setOnClickListener {
            dialog.dismiss()
            if (title == "Update Mason Information") {

                val updateData = prepareMasonUpdateData()
                updateOtherParty(user, party_code, updateData)

            } else {
                // val data = prepareEngineerData()
                // saveNewCustomer(user, "Mason", data)
            }
        }
    }


    private fun prepareMasonUpdateData(): String {

        var dob = ""
        var mar = ""


        val otherCustomer = OtherCustomerChild3(
            binding.txtUMasonname.text.toString(),
            binding.txtUMAdd.text.toString(),
            "Bangladesh",
            "",
            binding.txtUMEmail.text.toString(),
            "Bangladesh",
            //   engineerSetupInfo.split(",")[1],
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            //   engineerSetupInfo.split(",")[2],
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            //  engineerSetupInfo.split(",")[3],
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            binding.txtUMMobile.text.toString(),
            "",
            "",
            0,
            0,
            "",
            dob,
            mar,
            binding.txtMUNIDNum.text.toString(),
            binding.txtMUNName.text.toString(),
            binding.txtMUNnum.text.toString(),
            extension,
            binding.txtUMCity.text.toString(),
            binding.txtUMPartyZIP.text.toString(),
            "Mason",

            nomextension,
            binding.acMUNRltn.text.toString(),
            "",
            "",
            "",
            "",
            "",
            ""

        )


        /*
                val otherCustomer = OtherCustomer(
                    binding.txtUMasonname.text.toString(),
                    binding.txtUMAdd.text.toString(),
                    binding.txtUMCity.text.toString(),
                    "",
                    "Bangladesh",
                    "",
                    "",
                    "Bangladesh",
                    Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
                    Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
                    Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
                    binding.txtUMMobile.text.toString(),
                    "",
                    "",
                    0,
                    0,
                    "",
                    "",
                    "",

                    binding.txtMUNIDNum.text.toString(),
                    binding.txtMUNName.text.toString(),
                    binding.txtMUNnum.text.toString(),
                    binding.acMUNRltn.text.toString(),
                    "",

                    nomextension,
                    "",
                    0.0,
                    0,
                    "",
                    extension,
                    "",

                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )


         */

        val gson = Gson()
        return gson.toJson(otherCustomer)
    }


    private fun updateOtherParty(user: String, partyCode: String, value: String) {

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.updateOther(user, partyCode, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    loadingAnim.dismiss()

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            //    if (finalFile.isFile && other.partyImage.toString() == "") {
                            if(other.partyImage.toString().isEmpty()){

                                if (finalFile.isFile) {
                                    if(other.partyImage.toString().isEmpty()) {
                                        uploadCoverPhoto(
                                            user,
                                            partyCode,
                                            "Mason",
                                            latitude.toString(),
                                            longitude.toString(),
                                            finalFile,
                                            loadingAnim
                                        )
                                    }
                                }
                            }

                            //todo nom nid
                            if(nomCardFile.size>0){
                                if(nomNidFinalFile.isFile){
                                    uploadCustomerNomDoc(
                                        user,
                                        mason_code,
                                        "mason",
                                        "nomnid",//nom nid folder name
                                        nomextension,
                                        nomCardFile,
                                        loadingAnim,
                                        "" +"nid"
                                    )
                                }

                            }

                            if ( other.nidImg.toString().equals("")) {
                                if(cardFile.size > 0){
                                    if(nidfinalFile.isFile){
                                        uploadCustomerDoc(
                                            user,
                                            partyCode,
                                            "mason",
                                            "nid",
                                            extension,
                                            cardFile,
                                            loadingAnim,
                                            "" +"nid"
                                        )
                                    }

                                }

                            }

                            else if(other.contId.equals("")){
                                mason_code = party_code
                                val profileData = prepareListForTag()
                                MasonTag(user,profileData,loadingAnim)
                            }

                            else{
                                //
                                loadingAnim.dismiss()
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Mason updated successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_mason_database)



                            }


                        }

                        // }
                    }
                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    //todo was not commented
                    //   binding.btnUpdateOther.isEnabled = true
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    //todo nom nid
    //nid upload
    private fun uploadCustomerNomDoc(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>,
        loading: Dialog,
        docType: String
    ) {
        retailerViewModel.uploadCusFile(
            user_id,
            party_code,
            party_type,
            doc_type,
            doc_ext,
            all_images
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    // loading.dismiss()
                                    if (party_code != "") {
                                        mason_code = party_code



                                    } else {
                                        loading.dismiss()
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Warning",
                                            "Mason nom id not found",
                                            R.drawable.error_white,
                                            0
                                        )
                                    }

                                }
                                else {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: " + e.message)
                            }

                        }
                    }
                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun uploadCoverPhoto(
        user_id: String,
        party_code: String,
        party_type: String,
        party_lat: String,
        party_lon: String,
        party_image: File,
        loading: Dialog
    ) {
        retailerViewModel.uploadCover(
            user_id,
            party_code,
            party_type,
            party_lat,
            party_lon,
            party_image
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Successful",
                                        "Mason's image added successfully",
                                        R.drawable.ic_checked_green,
                                        1
                                    )
                                    val navController =
                                        requireActivity().findNavController(R.id.nav_host_fragment)
                                    navController.navigate(R.id.nav_mason_database)


                                } else {

                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: " + e.message)
                            }

                        }
                    }
                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }



    //nid upload (not work)
    private fun uploadCustomerDoc(
        user_id: String,
        party_code: String,
        party_type: String,
        doc_type: String,
        doc_ext: String,
        all_images: ArrayList<File>,
        loading: Dialog,
        docType: String
    ) {
        retailerViewModel.uploadCusFile(
            user_id,
            party_code,
            party_type,
            doc_type,
            doc_ext,
            all_images
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->
                            try {
                                if (res.code == "200") {
                                    loading.dismiss()

                                    if (party_code != "") {
                                        mason_code = party_code

                                        if(other.contId.equals("")){
                                            val profileData = prepareListForTag()
                                            MasonTag(user_id,profileData,loading)
                                        } else{
                                            //
                                            loading.dismiss()
                                            Utility.getBaseMessage(
                                                requireActivity(),
                                                "Successful",
                                                "Mason updated successfully",
                                                R.drawable.ic_checked_green,
                                                1
                                            )

                                            val navController =
                                                requireActivity().findNavController(R.id.nav_host_fragment)
                                            navController.navigate(R.id.nav_mason_database)



                                        }


                                    } else {
                                        loading.dismiss()

                                    }


                                }
                                else {
                                    loading.dismiss()
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }

                            } catch (e: Exception) {
                                loading.dismiss()
                                Log.e("Mason Add", "Error: " + e.message)
                            }

                        }
                    }
                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    fun prepareListForTag(): String {

        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        //  val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()
        var conPartyCode = SELECTED_CONTRACTOR_ID
        setPreviouslySaveValueForUpdate()

        val dataForMasontag = DataForMasontag(
            mason_code,
            conPartyCode
        )

        val gson = Gson()
        return gson.toJson(dataForMasontag)
    }



    private fun MasonTag(
        user: String,
        value: String,
        loading: Dialog
    ) {


        retailerViewModel.ContMasonTag(user, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            loadingAnim.dismiss()
                            loading.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Mason updated and tagged with the contractor successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_mason_database)


                            loading.dismiss()

                        } else {

                            loading.dismiss()
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
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//todo picture

    private fun openCamera() {
        fileType = "CAMERA"
        if (latitude == 0.0) {
            //  location()
            getLastLocations()
        }
        val fileName = "IMG"
        val storageDirectory: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            currentPhotoPath = imageFile!!.absolutePath
            val imageUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.crowncement.TsSalesTracking.fileprovider",
                imageFile
            )
            Utility.storeKeyValue(requireActivity(),"masonUpdateImg",currentPhotoPath)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            startActivityForResult(intent, 0)
            //storage/emulated/0/Android/data/com.crowncement.salesforceapp/files/Pictures/IMG2399521268364836531.jpg
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e("ERROR", "openCamera: ${e.message}")
        }
    }


    private fun openCameraVisitingCard() {
        fileType = "CAMERAID"
        if (latitude == 0.0) {
            //   location()
            getLastLocations()
        }

        val fileName = "photo.jpg"
        val storageDirectory: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoIDPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.TsSalesTracking.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(),"currentPhotoIDPathUpdate",currentPhotoIDPath)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 130)
    }



    private fun openGalleryForImages() {
        fileType = "ATTACH"

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }
    }
    /*
        private fun openPDFFile() {
            fileType = "PDF"
            val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
            pdfIntent.type = "application/pdf"
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
            pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, ".pdf")
            pdfIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(pdfIntent, 12)
        }

     */
    private fun openPDFFile() {
        fileType = "PDF"
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }




    //todo new
    /*
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // cardFile = ArrayList()
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                // Toast.makeText(requireContext(), fileType, Toast.LENGTH_LONG).show()
                when (requestCode) {
                    //camera profile picture
                    0 -> {

                        try {
                            if (fileType == "CAMERA") {

                                //  var currentPhotoPath = Utility.getValueByKey(requireActivity(),"masonUpdateImg")
                                selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                                uri = tempUri!!
                                prealPath = getRealPathFromURI(tempUri)!!

                                finalFile = File(getRealPathFromURI(tempUri)!!)

                                fileSizeInMB = currentPhotoPath.length.toDouble() / (1024 * 1024)


                                GlobalScope.launch {

                                    //  finalFile= Compressor.compress(requireContext(), finalFile)

                                    finalFile = Compressor.compress(requireContext(), finalFile) {
                                        resolution(1280, 720)
                                        quality(80)
                                        format(Bitmap.CompressFormat.JPEG)
                                        size(204_800) // 200 KB
                                    }
                                }
                                binding.imgUMason.setImageBitmap(selectedImage)
                                fileType = ""
                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture masons image",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {
                            if (fileType == "CAMERA") {

                                Log.e("e", e.message.toString())
                                currentPhotoPath =
                                    Utility.getValueByKey(requireActivity(), "masonUpdateImg")
                                        .toString()
                                selectedImage = BitmapFactory.decodeFile(currentPhotoPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                                uri = tempUri!!
                                prealPath = getRealPathFromURI(tempUri)!!

                                finalFile = File(getRealPathFromURI(tempUri)!!)

                                fileSizeInMB = currentPhotoPath.length.toDouble() / (1024 * 1024)

                                GlobalScope.launch {

                                    finalFile = Compressor.compress(requireContext(), finalFile) {
                                        resolution(1280, 720)
                                        quality(80)
                                        format(Bitmap.CompressFormat.JPEG)
                                        size(204_800) // 200 KB
                                    }


                                    // finalFile= Compressor.compress(requireContext(), finalFile)
                                }
                                binding.imgUMason.setImageBitmap(selectedImage)
                                fileType = ""
                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture masons image",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                    }
                    /*
                //attach

                 */
                    //todo index1,size 1 error solve


                    200 -> {

                        if (data?.clipData != null) {
                            val count = data.clipData!!.itemCount

                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                                val path: File =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                try {
                                    path.mkdirs()
                                    val file =
                                        File(
                                            ImagePathUtils.getRealPath(
                                                requireContext(),
                                                imageUri
                                            )!!
                                        )
                                    niduri = imageUri!!
                                    nidfinalFile = file

                                    nidfinalFileInMB =
                                        nidfinalFile.length().toDouble() / (1024 * 1024)
                                    nidSelectionType = "attach"

                                    GlobalScope.launch {
                                        nidfinalFile =
                                            Compressor.compress(requireContext(), nidfinalFile) {
                                                resolution(1280, 720)
                                                quality(80)
                                                format(Bitmap.CompressFormat.JPEG)
                                                size(204_800) // 200 KB
                                            }
                                        // nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                    }
                                    cardFile.add(nidfinalFile)
                                    extension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $extension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                    setImage(nidfinalFile, binding.imgUMNidFile)
                                    /*
                                Glide.with(requireActivity())
                                    .load(nidfinalFile)
                                    //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                    .into(binding.imgUMNidFile)

                                 */
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                        } else if (data?.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!


                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                                niduri = imgUri!!
                                nidfinalFile = file

                                nidfinalFileInMB = nidfinalFile.length().toDouble() / (1024 * 1024)
                                nidSelectionType = "attach"

                                GlobalScope.launch {

                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                    // nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                }
                                cardFile.add(nidfinalFile)
//
                                //get file extension
                                // extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                                extension = "jpg"
                                //Set image to view
                                val bitmap = BitmapFactory.decodeStream(
                                    requireContext().contentResolver.openInputStream(imgUri)
                                )
                                binding.imgUMNidFile.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Please select minimum one photo ",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //todo index1,size 1 error solve end

                    //pdf
                    12 -> {
                        if (fileType == "PDF") {
                            val uri: Uri = data!!.data!!
                            val uriString = uri.toString()
                            val myFile =
                                FileUtility().from(requireContext().applicationContext, uri)

                            //Get File name
                            var displayName: String? = null
                            if (uriString.startsWith("content://")) {
                                var cursor: Cursor? = null
                                try {
                                    cursor =
                                        requireActivity().contentResolver.query(
                                            uri,
                                            null,
                                            null,
                                            null,
                                            null
                                        )
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    }
                                } finally {
                                    cursor!!.close()
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile!!.name
                            }
                            pdfFile = myFile!!

                            binding.imgUMNidFile.setImageDrawable(null)
                            extension = "pdf"
                            binding.txtMNidFileName.text = displayName
//                        GlobalScope.launch {
//
//                            pdfFile= Compressor.compress(requireContext(), pdfFile)
//
//                        }
                            cardFile.add(pdfFile)
                            nidfinalFile = pdfFile

                            binding.imgUMNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                            binding.imgUMNidFile.scaleType = ImageView.ScaleType.FIT_CENTER
                            fileType = ""

                        } else {
                            fileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach pdf file",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }
                    //camera visiting card
                    130 -> {
                        try {
                            if (fileType == "CAMERAID") {
                              //  val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                nidselectedImage =  BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), nidselectedImage!!, imName)
                                niduri = tempUri!!
                                nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                                nidSelectionType = "camera"

                                nidfinalFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)


                                GlobalScope.launch {
                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                    //  nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                }
                                cardFile.add(nidfinalFile)
                                extension = "jpg"
                                binding.txtMNidFileName.text = ""
                                binding.imgUMNidFile.setImageDrawable(null)
                                binding.imgUMNidFile.setImageBitmap(nidselectedImage)
                                fileType = ""

                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture nid card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {
                            if (fileType == "CAMERAID") {
                                currentPhotoIDPath = Utility.getValueByKey(
                                    requireActivity(),
                                    "currentPhotoIDPathUpdate"
                                ).toString()
                               // val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                nidselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), nidselectedImage!!, imName)
                                niduri = tempUri!!
                                nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                                nidSelectionType = "camera"

                                nidfinalFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)


                                GlobalScope.launch {
                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                    //  nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                }
                                cardFile.add(nidfinalFile)
                                extension = "jpg"
                                binding.txtMNidFileName.text = ""
                                binding.imgUMNidFile.setImageDrawable(null)
                                binding.imgUMNidFile.setImageBitmap(nidselectedImage)
                                fileType = ""

                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture nid card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                    }
                    //nom nid attach
                    201 -> {

                        if (data?.clipData != null) {
                            val count = data.clipData!!.itemCount

                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                                val path: File =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                try {
                                    path.mkdirs()
                                    val file =
                                        File(
                                            ImagePathUtils.getRealPath(
                                                requireContext(),
                                                imageUri
                                            )!!
                                        )
                                    nomuri = imageUri!!
                                    nomNidFinalFile = file

                                    nomNidFileInMB =
                                        nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                    nomSelectionType = "attach"


                                    GlobalScope.launch {

                                        //     nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)

                                        nomNidFinalFile =
                                            Compressor.compress(requireContext(), nomNidFinalFile) {
                                                resolution(1280, 720)
                                                quality(80)
                                                format(Bitmap.CompressFormat.JPEG)
                                                size(204_800) // 200 KB
                                            }
                                    }
                                    nomCardFile.add(nomNidFinalFile)

                                    nomextension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $nomextension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                    setImage(file, binding.imgMUNomNidFile)
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                        } else if (data?.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!


                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                                nomuri = imgUri!!
                                nomNidFinalFile = file
                                nomNidFileInMB = nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                nomSelectionType = "attach"
                                GlobalScope.launch {

                                    nomNidFinalFile =
                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                    //  nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)
                                }
                                nomCardFile.add(nomNidFinalFile)


//
                                //get file extension
                                // extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                                //nomextension ="jpg"
                                nomextension = ImagePathUtils.getRealPath(requireContext(), imgUri)
                                    .toString().split(".")[1]
                                //Set image to view
                                val bitmap = BitmapFactory.decodeStream(
                                    requireContext().contentResolver.openInputStream(imgUri)
                                )
                                binding.imgMUNomNidFile.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Please select minimum one photo ",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //pdf nom
                    102 -> {
                        if (nomfileType == "PDF") {
                            val uri: Uri = data!!.data!!
                            val uriString = uri.toString()
                            val myFile =
                                FileUtility().from(requireContext().applicationContext, uri)

                            //Get File name
                            var displayName: String? = null
                            if (uriString.startsWith("content://")) {
                                var cursor: Cursor? = null
                                try {
                                    cursor =
                                        requireActivity().contentResolver.query(
                                            uri,
                                            null,
                                            null,
                                            null,
                                            null
                                        )
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    }
                                } finally {
                                    cursor!!.close()
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile!!.name
                            }
                            nompdfFile = myFile!!
                            binding.imgMUNomNidFile.setImageDrawable(null)
                            nomextension = "pdf"
                            binding.txtMUNomNidFileName.text = displayName
//                        GlobalScope.launch {
//
//                            nompdfFile= Compressor.compress(requireContext(), nompdfFile)
//                        }
                            nomCardFile.add(nompdfFile)

                            nomNidFinalFile = nompdfFile

                            binding.imgMUNomNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                            binding.imgMUNomNidFile.scaleType = ImageView.ScaleType.FIT_CENTER
                            nomfileType = ""

                        } else {
                            nomfileType = ""
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach nominees pdf file",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //camera nom nid card
                    131 -> {
                        try {
                            if (nomfileType == "CAMERAID") {
                               // val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                nomselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), nomselectedImage!!, imName)
                                nomuri = tempUri!!
                                nrealPath = getRealPathFromURI(tempUri)!!
                                nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                                nomNidFileInMB = nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                nomSelectionType = "camera"

                                GlobalScope.launch {
                                    // nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)
                                    nomNidFinalFile =
                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                }


                                nomCardFile.add(nomNidFinalFile)
                                nomextension = "jpg"
                                binding.txtMUNomNidFileName.text = ""
                                binding.imgMUNomNidFile.setImageDrawable(null)
                                binding.imgMUNomNidFile.setImageBitmap(nomselectedImage)
                                nomfileType = ""
                            } else {
                                nomfileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {
                            if (nomfileType == "CAMERAID") {
                                currentPhotoIDPath =
                                    Utility.getValueByKey(requireActivity(), "currentPhotoNomIDPath")
                                        .toString()
                                nomselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)

                                //val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val tempUri = getImageUri(requireContext(), nomselectedImage!!, imName)

                                nomuri = tempUri!!
                                nrealPath = getRealPathFromURI(tempUri)!!

                                nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)

                                nomNidFileInMB = nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                nomSelectionType = "camera"
                                GlobalScope.launch {

                                    // nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)
                                    nomNidFinalFile =
                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }

                                }
                                nomCardFile.add(nomNidFinalFile)
                                nomextension = "jpg"
                                binding.txtMUNomNidFileName.text = ""
                                binding.imgMUNomNidFile.setImageDrawable(null)
                                binding.imgMUNomNidFile.setImageBitmap(nomselectedImage)
                                nomfileType = ""

                            } else {
                                nomfileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                    }

                    else -> {
                        fileType = ""
                        Utility.commonToast(
                            requireActivity(),
                            "Failed",
                            "failed to process image or pdf file",
                            0
                        )
                    }
                }
            }

        }

        catch (e: Exception) {
            Utility.getBaseMessage(
                requireActivity(),
                "আপনার ফোনের স্টোরেজ ভর্তি হয়ে গেছে!",
                "ক্রাউন টাইটান অ্যাপস দিয়ে আজকের তোলা কিছু ছবি মুছে ফেলুন এবং ৮ জিবি জায়গা ফাঁকা করুন।",
                R.drawable.error_white,
                2
            )
        }

    }

     */

    private fun setImage(imgFile: File, v: ImageView) {
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)!!
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {

        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            // getResizedBitmap(inImage, 800),
            imageName,
            "Mason"
        )
        return Uri.parse(path)
    }



    private fun getLastLocations() {
        EasyLocation(requireActivity(), object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {

                Utility.commonToast(
                    requireActivity(),
                    "Location Permission Denied",
                    "Permission  denied",
                    0
                )
            }

            override fun locationSettingFailed() {

                Utility.commonToast(
                    requireActivity(),
                    "Location Settings Failed",
                    "Location settings issue",
                    0
                )

            }

            override fun getLocation(location: Location) {

                //   Log.i("Location_lat_lng"," latitude ${location?.latitude} longitude ${location?.longitude}")
                latitude = location.latitude
                longitude = location.longitude
                Log.d("latitude", "latitude: $latitude")
                Log.d("longitude", "longitude: $longitude")


            }
        })


    }





    //TODO LOCATION ------------------------------------------------------------------------------->

    private fun prepareLocation() {
        val ai: ApplicationInfo = requireContext().packageManager.getApplicationInfo(
            requireContext().packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()

                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.e("Add Customer", "Latitude: $latitude | Longitude:$longitude")

                    }
                }
            } else {
                requestDeviceLocationSettings()
            }

        } else {
            requestPermissions()
        }
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

        task.addOnSuccessListener {

            requestPermissions()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e(
                        "Frag Add Cus",
                        "requestDeviceLocationSettings: " + sendEx.message.toString()
                    )
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            return true
        }
        return false
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            val currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude

            Log.e("New Customer", "latitude: $latitude longitude:  $longitude")

        }
    }

//    private fun getLastKnownLocation(): Location? {
//        val mLocationManager: LocationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val providers: List<String> = mLocationManager.getProviders(true)
//        var bestLocation: Location? = null
//        for (provider in providers) {
//            val location = mLocationManager.getLastKnownLocation(provider) ?: continue
//            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
//                bestLocation = location
//            }
//        }
//        return bestLocation
//    }

    /*

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

     */

    //TODO LOCATION END---------------------------------------------------------------------------->



    //todo update data
    fun validationForMasonUpdate(): Boolean {
        var result = false
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

        if(nidfinalFile.isFile){
            nidfinalFileInKB = nidfinalFile.length() / 1024.0
        }
        if(finalFile.isFile){
            finalFileInKB = finalFile.length() / 1024.0
        }

        if(nomNidFinalFile.isFile){
            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
        }

       // nidfinalFileInKB = 220.0
        if (binding.txtUMasonname.text.toString().isEmpty()) {
            result = false
            binding.layoutUMasonname.isErrorEnabled = true
            binding.layoutUMasonname.error = "Mason's name is required"
        } else if (binding.txtUMAdd.text.toString().isEmpty()) {
            result = false
            binding.layoutUMAdd.isErrorEnabled = true
            binding.layoutUMAdd.error = "Mason's address is required"
        }else if (binding.txtUMMobile.text.toString().isEmpty()||
            binding.txtUMMobile.text.toString().length<11) {
            result = false
            binding.layoutUMMobile.isErrorEnabled = true
            binding.layoutUMMobile.error = "Mason's 11 digit mobile number is required"
        }
        else if (binding.txtMUNIDNum.text.toString().isEmpty()){
            result = false
            binding.layoutMUNIDNum.isErrorEnabled = true
            binding.layoutMUNIDNum.error = "Mason's NID card number is required"
        }
        else if(SELECTED_CONTRACTOR_ID.equals("")){
            result = false
            binding.layoutTdeliveryContractorCode.isErrorEnabled = true
            binding.layoutTdeliveryContractorCode.error = "Contractor selection is required for this mason"
        }

        else if ((cardFile.size==0 && other.nidImg.toString()=="")|| nidfinalFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Nid card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    nidfinalFileInKB
                ) + " KB",
                R.drawable.error_white,
                0
            )
        }
        else if (finalFileInKB > 200){
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Mason's image size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    finalFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        }

        else if ( nomNidFileInKB > 200){
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Nominee's Nid card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    nomNidFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        }


        else {
            result = true
        }



        return result
    }





    ////todo for galary index1,size1 error
    private fun handleImageUri(uri: Uri) {
        try {
            val attachFile = File(ImagePathUtils.getRealPath(requireContext(), uri)!!)
            extension = ImagePathUtils.getRealPath(requireContext(), uri).toString().split(".").last()
            cardFile.add(attachFile)
            setImage(attachFile, binding.imgUMNidFile) // Assuming setImage sets the image on ImageView
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }


    //todo for galary index1,size1 error


    fun createFileFromImageUri(context: Context, imageUri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = createTempImageFile(context)

        return try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    fun createTempImageFile(context: Context): File {
        val tempFileName = "temp_image.jpg" // Replace with your desired file name and extension
        val storageDir = context.cacheDir // Use cache directory or external storage depending on your needs
        return File.createTempFile(tempFileName, null, storageDir)
    }



    //todo index1,size 1 end




    //todo new capture img
    fun captureImage(requestCode: Int ) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent(requestCode)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationale()
            }

            else -> {
                requestPermissions()
            }
        }
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
        android.app.AlertDialog.Builder(requireContext()).setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to take pictures.")
            .setPositiveButton("OK") { _, _ ->

            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            context?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile(requestCode)
                    } catch (ex: IOException) {
                        null
                    }

                    photoFile?.also {

                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.crowncement.TsSalesTracking.fileprovider",
                            it
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
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getOutputDirectory()

        return File.createTempFile(
            "JPEG_${timeStamp}_bill_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if(requestCode==PRO_PIC_CAMERA_REQ_CODE){
                currentPhotoPath = absolutePath

            }else if(requestCode==NOM_NID_CAMERA_REQ_CODE){
                //   currentPhotoPath = absolutePath

                nomcurrentPhotoIDPath = absolutePath

            }else if (requestCode==NID_CAMERA_REQ_CODE){
                currentPhotoIDPath = absolutePath
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = File(requireContext().filesDir, "CrownTitanAttendance").apply {
            mkdirs()
        }
        return if (mediaDir.exists()) mediaDir else requireContext().filesDir
    }


    fun compressImage(imagePath: String, maxSizeKB: Int = 50): File {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        // Calculate inSampleSize (power of 2) to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, 960, 960)
        options.inJustDecodeBounds = false

        // Decode bitmap with inSampleSize set
        var bitmap = BitmapFactory.decodeFile(imagePath, options)

        // Compress with quality adjustment
        var outputStream = ByteArrayOutputStream()
        var quality = 50 // Start with 90% quality

        do {
            outputStream.reset() // Clear the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // Reduce quality by 5% each iteration
        } while (outputStream.toByteArray().size > maxSizeKB * 960 && quality > 10)

        // If still too large, resize further
        if (outputStream.toByteArray().size > maxSizeKB * 960) {
            val scaleFactor =
                Math.sqrt((maxSizeKB * 960).toDouble() / outputStream.toByteArray().size)
            val newWidth = (bitmap.width * scaleFactor).toInt()
            val newHeight = (bitmap.height * scaleFactor).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        }
        return convertBitmapToFile(
            BitmapFactory.decodeByteArray(
                outputStream.toByteArray(),
                0,
                outputStream.toByteArray().size
            )
        )
    }

    private fun convertBitmapToFile(imageBitmap: Bitmap): File {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("collection", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        stream.flush()
        stream.close()
        return file
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }



    //todo new img
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == RESULT_OK) {

            Log.e("IMAGE", "Image saved to $currentPhotoPath")

            try{
                when (requestCode) {

                    //camera profile picture
                    PRO_PIC_CAMERA_REQ_CODE -> {

                        val imgFile = compressImage(currentPhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            finalFile = imgFile
                        }

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.imgUMason.setImageBitmap(bitmap)


                        if (latitude == 0.0) {
                            //  latitude = getLastKnownLocation()!!.latitude
                            //  longitude = getLastKnownLocation()!!.longitude
                            getLastLocation()

                        }

                    }

                    /*
                                        VCARD_PIC_CAMERA_REQ_CODE -> {


                                            val imgFile = compressImage(visitingCardPhotoPath)
                                            vCardFile

                                            if (imgFile.exists()) {
                                                val imageSize = (imgFile.length() / 1024.0)
                                                // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                                //  finalFile.add(imgFile)
                                                vCardFile = imgFile
                                            }
                                            cardFile.add(vCardFile)

                                            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                                            binding.imgSetVisitingImage.setImageBitmap(bitmap)
                                        }



                                        VCARD_ATTACH_REQUEST_CODE -> {

                                            if (data?.clipData != null) {
                                                val count = data.clipData!!.itemCount

                                                for (i in 0 until count) {
                                                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                                                    val path: File =
                                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                                    try {
                                                        path.mkdirs()
                                                        val file =
                                                            File(
                                                                ImagePathUtils.getRealPath(
                                                                    requireContext(),
                                                                    imageUri
                                                                )!!
                                                            )
                                                        //  nuri = imageUri!!
                                                        //  nidSelectionType = "attach"
                                                        attachFile = file


                                                        var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                                        val imgFile = compressImage(imguri.toString())



                                                        if (imgFile.exists()) {
                                                            val imageSize = (imgFile.length() / 1024.0)
                                                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                                            //  finalFile.add(imgFile)
                                                            attachFile = imgFile
                                                        }
                                                        Utility.saveFileToSharedPreferences(
                                                            requireContext(),
                                                            attachFile, "engVisAttach"
                                                        )

                                                        cardFile.add(attachFile)

                                                        //   nidfinalFileInKB = nidFile.get(0).length() / 1024.0
                                                        // nidfinalFile.length() / 1024.0

                                                        extension =
                                                            ImagePathUtils.getRealPath(requireContext(), imageUri)
                                                                .toString().split(".")[1]
                                                        Log.e("extension", "onActivityResult: $extension")
                                                        // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                                                        // setImage(file, binding.imgNidFile)
                                                        Glide.with(requireActivity())
                                                            .load(attachFile)
                                                            .into(binding.imgSetVisitingImage)

                                                    } catch (e: Exception) {
                                                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                                            .show()
                                                    }
                                                }

                                            } else if (data?.data != null) {
                                                val imagePath: String = data.data!!.path!!
                                                val imgUri: Uri = data.data!!


                                                val path: File =
                                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                                try {

                                                    val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                                        createFileFromImageUri(requireContext(), imgUri)!!
                                                    } else {
                                                        path.mkdirs()
                                                        File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                                    }
                                                    //   nuri = imgUri!!
                                                    //  nidSelectionType == "attach"
                                                    attachFile = file
                                                    GlobalScope.launch {

                                                        //     nidfinalFile = Compressor.compress(requireContext(), nidfinalFile)

                                                        attachFile =
                                                            Compressor.compress(requireContext(), attachFile) {
                                                                resolution(1280, 720)
                                                                quality(80)
                                                                format(Bitmap.CompressFormat.JPEG)
                                                                size(204_800) // 200 KB
                                                            }
                                                    }
                                                    Utility.saveFileToSharedPreferences(
                                                        requireContext(),
                                                        attachFile, "engVisAttach"
                                                    )
                                                    cardFile.add(attachFile)
                                                    // nidfinalFileInKB = nidFile.get(0).length() / 1024.0


                                                    extension = "jpg"
                                                    //Set image to view

                                                    Glide.with(requireActivity())
                                                        .load(attachFile)
                                                        //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                                        .into(binding.imgSetVisitingImage)

                                                    //   binding.imgNidFile.setImageURI(Uri.fromFile(nidfinalFile))

                                                } catch (e: Exception) {
                                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                                        .show()
                                                }
                                            } else {
                                                Utility.getBaseMessage(
                                                    requireActivity(),
                                                    "Failed",
                                                    "Please select minimum one photo ",
                                                    R.drawable.error_white,
                                                    2
                                                )
                                            }
                                        }

                     */

                    //camera visiting card
                    NID_CAMERA_REQ_CODE -> {
                        try {
                            if (fileType == "CAMERAID") {
                                //  val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)

                                nidselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                // val imName = emp + Utility.getCurrentDate("yyyyMMdd").toString()
                                val resizedImage = resizeBitmap(nidselectedImage!!, 800, 800)
                                //  val tempUri = saveImageToGallery(requireContext(), resizedImage, emp)

                                val tempUri = getImageUri(requireContext(), nidselectedImage!!, emp)
                                niduri = tempUri!!
                                Log.e("realPath", getRealPathFromURI(tempUri)!!)
                                //     realPath = getRealPathFromURI(tempUri)!!
                                //     nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                                nidfinalFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)


                                nidSelectionType = "camera"

                                var imguri = ImagePathUtils.getRealPath(requireContext(), tempUri)
                                val imgFile = compressImage(imguri.toString())
                                nidfinalFile = imgFile




                                cardFile.add(nidfinalFile)
                                extension = "jpg"
                                // binding.txtMNidFileName.text = ""
                                // binding.imgMNidFile.setImageDrawable(null)
                                Glide
                                    .with(this)
                                    .load(nidfinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgUMNidFile)
                                // binding.imgMNidFile.setImageBitmap(selectedImage)
                                fileType = ""

                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {
                            if (fileType == "CAMERAID") {
                                currentPhotoIDPath =
                                    Utility.getValueByKey(requireActivity(), "currentPhotoIDPath")
                                        .toString()
                                //  val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                nidselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val resizedImage = resizeBitmap(selectedImage!!, 800, 800)
                                val tempUri =
                                    saveImageToGallery(requireContext(), resizedImage, imName)
                                niduri = tempUri!!

                                // val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                                nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                                nidfinalFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)
                                nidSelectionType = "camera"

                                var imguri = ImagePathUtils.getRealPath(requireContext(), tempUri)
                                val imgFile = compressImage(imguri.toString())
                                nidfinalFile = imgFile

                                /*
                                                                GlobalScope.launch {
                                                                    nidfinalFile =
                                                                        Compressor.compress(requireContext(), nidfinalFile) {
                                                                            resolution(1280, 720)
                                                                            quality(80)
                                                                            format(Bitmap.CompressFormat.JPEG)
                                                                            size(204_800) // 200 KB
                                                                        }
                                                                    // nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                                                }

                                 */

                                cardFile.add(nidfinalFile)

                                extension = "jpg"
                                //  _binding?.txtMNidFileName?.text = ""
                                // binding.imgMNidFile.setImageDrawable(null)
                                Glide
                                    .with(this)
                                    .load(nidfinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgUMNidFile)
                                //   binding.imgMNidFile.setImageBitmap(selectedImage)
                                fileType = ""

                            } else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                    }

                    //attach nid
                    NID_ATTACH_REQUEST_CODE -> {

                        if (data?.clipData != null) {
                            val count = data.clipData!!.itemCount

                            for (i in 0 until count) {
                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                                val path: File =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                try {
                                    path.mkdirs()
                                    val file =
                                        File(
                                            ImagePathUtils.getRealPath(
                                                requireContext(),
                                                imageUri
                                            )!!
                                        )
                                    niduri = imageUri!!

                                    nidfinalFile = file
                                    nidfinalFileInMB =
                                        nidfinalFile.length().toDouble() / (1024 * 1024)
                                    nidSelectionType = "attach"

                                //    var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                    val imgFile = compressImage(nidfinalFile.absolutePath)

                                    nidfinalFile = imgFile


                                    /*
                                     GlobalScope.launch {


                                         nidfinalFile =
                                             Compressor.compress(requireContext(), nidfinalFile) {
                                                 resolution(1280, 720)
                                                 quality(80)
                                                 format(Bitmap.CompressFormat.JPEG)
                                                 size(204_800) // 200 KB
                                             }
                                         // nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                     }

                                     */

                                    Utility.saveFileToSharedPreferences(requireContext(),
                                        nidfinalFile,"nidAttach")
                                    cardFile.add(nidfinalFile)

                                    Glide
                                        .with(this)
                                        .load(nidfinalFile)
                                        // .error(R.drawable.default_user_image)
                                        .fitCenter()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(binding.imgUMNidFile)
                                    extension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $extension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                    //   setImage(file, binding.imgMNidFile)


                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                        } else if (data?.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!


                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                                niduri = imgUri!!

                                nidfinalFile = file
                                nidfinalFileInMB = nidfinalFile.length().toDouble() / (1024 * 1024)
                                nidSelectionType = "attach"

                              //  var imguri = ImagePathUtils.getRealPath(requireContext(), imgUri)
                             //   val imgFile = compressImage(imguri.toString())
                                val imgFile = compressImage(nidfinalFile.absolutePath)

                                nidfinalFile = imgFile



                                /*
                                GlobalScope.launch {

                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                    //  nidfinalFile= Compressor.compress(requireContext(), nidfinalFile)
                                }

                                 */

                                Utility.saveFileToSharedPreferences(requireContext(),
                                    nidfinalFile,"nidAttach")

                                cardFile.add(nidfinalFile)
                                Glide
                                    .with(this)
                                    .load(nidfinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgUMNidFile)
//
                                //get file extension
                                // extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                                extension = "jpg"


                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Please select minimum one photo ",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    //camera nom nid card
                    NOM_NID_CAMERA_REQ_CODE -> {
                        try {
                            if (nomfileType == "CAMERAID") {
                                nomselectedImage = BitmapFactory.decodeFile(nomcurrentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                //  val resizedImage = resizeBitmap(selectedImage!!, 800, 800)
                                // val tempUri = saveImageToGallery(requireContext(), resizedImage, imName)
                                val tempUri =
                                    getImageUri(requireContext(), nomselectedImage!!, imName)
                                Log.e("nrealPath", getRealPathFromURI(tempUri)!!)
                                nomuri = tempUri!!
                                nrealPath = getRealPathFromURI(tempUri)!!
                                nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                                nomNidFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)
                                nomSelectionType = "camera"

                                var imguri = ImagePathUtils.getRealPath(requireContext(), tempUri)
                                val imgFile = compressImage(imguri.toString())
                                nomNidFinalFile = imgFile


                                /*
                                                                GlobalScope.launch {


                                                                    nomNidFinalFile =
                                                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                                                            resolution(1280, 720)
                                                                            quality(80)
                                                                            format(Bitmap.CompressFormat.JPEG)
                                                                            size(204_800) // 200 KB
                                                                        }

                                                                    //  nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)

                                                                }

                                 */

                                nomCardFile.add(nomNidFinalFile)
                                nomextension = "jpg"
                                //  binding.txtMNomNidFileName.text = ""

                                Glide
                                    .with(this)
                                    .load(nomNidFinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgMUNomNidFile)

                                // binding.imgMNomNidFile.setImageDrawable(null)
                                //binding.imgMNomNidFile.setImageBitmap(selectedImage)
                                nomfileType = ""

                            } else {
                                nomfileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {
                            if (nomfileType == "CAMERAID") {
                                currentPhotoIDPath =
                                    Utility.getValueByKey(
                                        requireActivity(),
                                        "currentPhotoNomIDPath"
                                    )
                                        .toString()
                                nomselectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val emp =
                                    Utility.getValueByKey(requireActivity(), "username").toString()
                                val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                                val resizedImage = resizeBitmap(nomselectedImage!!, 800, 800)
                                val tempUri =
                                    saveImageToGallery(requireContext(), resizedImage, imName)
                                nomuri = tempUri!!
                                nrealPath = getRealPathFromURI(tempUri)!!

                                nomNidFileInMB =
                                    currentPhotoIDPath.length.toDouble() / (1024 * 1024)
                                nomSelectionType = "camera"

                                //val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                                nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                                /*
                                GlobalScope.launch {

                                    nomNidFinalFile =
                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }

                                    // nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)
                                }

                                 */

                                var imguri = ImagePathUtils.getRealPath(requireContext(), tempUri)
                                val imgFile = compressImage(imguri.toString())
                                nomNidFinalFile = imgFile

                                nomCardFile.add(nomNidFinalFile)
                                nomextension = "jpg"
                                //  binding.txtMNomNidFileName.text = ""
                                Glide
                                    .with(this)
                                    .load(nomNidFinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgMUNomNidFile)
                                // binding.imgMNomNidFile.setImageDrawable(null)
                                //  binding.imgMNomNidFile.setImageBitmap(selectedImage)
                                nomfileType = ""

                            } else {
                                nomfileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                    }

                    //

//todo for nom attach nid
                    Nom_Attach_REQUEST_CODE -> {

                        if (data?.clipData != null) {
                            val count = data.clipData!!.itemCount

                            for (i in 0 until count) {

                                val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                                val path: File =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                try {
                                    path.mkdirs()
                                    val file =
                                        File(
                                            ImagePathUtils.getRealPath(
                                                requireContext(),
                                                imageUri
                                            )!!
                                        )

                                    nomuri = imageUri!!
                                    nomNidFinalFile = file
                                    nomNidFileInMB =
                                        nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                    nomSelectionType = "attach"

                                  //  var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                  //  val imgFile = compressImage(imguri.toString())
                                    val imgFile = compressImage(nomNidFinalFile.absolutePath)

                                    nomNidFinalFile = imgFile

                                    /*
                                    GlobalScope.launch {

                                        nomNidFinalFile =
                                            Compressor.compress(requireContext(), nomNidFinalFile) {
                                                resolution(1280, 720)
                                                quality(80)
                                                format(Bitmap.CompressFormat.JPEG)
                                                size(204_800) // 200 KB
                                            }

                                        // nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)
                                    }

                                     */

                                    Utility.saveFileToSharedPreferences(requireContext(),
                                        nomNidFinalFile,"nomNidAttach")
                                    nomCardFile.add(nomNidFinalFile)

                                    Glide
                                        .with(this)
                                        .load(nomNidFinalFile)
                                        // .error(R.drawable.default_user_image)
                                        .fitCenter()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(binding.imgMUNomNidFile)
                                    nomextension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $nomextension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                    // setImage(file, binding.imgMNomNidFile)
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                        } else if (data?.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!


                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                                nomuri = imgUri!!

                                nomCardFile.add(file)
                                nomNidFinalFile = file
                                nomNidFileInMB = nomNidFinalFile.length().toDouble() / (1024 * 1024)
                                nomSelectionType = "attach"

                          //      var imguri = ImagePathUtils.getRealPath(requireContext(), imgUri)
                             //   val imgFile = compressImage(imguri.toString())
                                val imgFile = compressImage(nomNidFinalFile.absolutePath)

                                nomNidFinalFile = imgFile

                                /*
                                GlobalScope.launch {

                                    nomNidFinalFile =
                                        Compressor.compress(requireContext(), nomNidFinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }

                                    //    nomNidFinalFile= Compressor.compress(requireContext(), nomNidFinalFile)

                                }
                                */
                                Utility.saveFileToSharedPreferences(requireContext(),
                                    nomNidFinalFile,"nomNidAttach")
                                nomCardFile.add(nomNidFinalFile)
                                nomextension = ImagePathUtils.getRealPath(requireContext(), imgUri)
                                    .toString().split(".")[1]


                                Glide
                                    .with(this)
                                    .load(nomNidFinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgMUNomNidFile)
//
                                //get file extension
                                // extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                                //extension ="jpg"
                                //Set image to view
                                //  val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(imgUri))
                                // binding.imgMNomNidFile.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Please select minimum one photo ",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }



                }





            }
            catch (e: Exception) {
                Utility.commonToast(requireActivity(), e.message.toString(), e.message.toString(), 0)

            }



        }

        else {
            Toast.makeText(requireActivity(), "ERROR in image processing", Toast.LENGTH_LONG).show()
        }


    }










    private fun resizeBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = image.width
        val height = image.height
        val aspectRatio = width.toFloat() / height.toFloat()

        val newWidth = if (aspectRatio > 1) maxWidth else (maxHeight * aspectRatio).toInt()
        val newHeight = if (aspectRatio > 1) (maxWidth / aspectRatio).toInt() else maxHeight

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
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
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        return imageUri
    }



}