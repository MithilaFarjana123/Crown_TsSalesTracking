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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.DataForMasontag
import com.crowncement.TsSalesTracking.data.Model.OtherCustomer
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragMasonAddBinding
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
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Frag_Mason_Add : Fragment() {

    lateinit var  selectedImage:Bitmap
    lateinit var  nidselectedImage:Bitmap
    lateinit var  nomselectedImage:Bitmap



    lateinit var finalFile: File
    var currentPhotoPath = ""
    var currentPhotoIDPath = ""
    var fileType = ""
    var nomfileType = ""
    lateinit var attachFile: File
    lateinit var pdfFile: File
    lateinit var nompdfFile: File
    var extension = ""
    var nomextension = ""

    private val permissionCode = 101
    val REQUEST_CODE = 200
    var Nom_Attach_REQUEST_CODE = 201

    lateinit var cardFile: ArrayList<File>
    lateinit var nomCardFile: ArrayList<File>
    lateinit var rltnTypes: ArrayList<String>

    var contractor_code = ""
    var mason_code = ""
    var realPath = ""
    var prealPath = ""
    var nrealPath = ""


    private var _binding: FragMasonAddBinding? = null
    private val binding get() = _binding!!

    private var PRO_PIC_CAMERA_REQ_CODE = 0
    private var NID_CAMERA_REQ_CODE = 130
    private var NOM_NID_CAMERA_REQ_CODE = 131

    private var PRO_PIC_ATTACH_REQUEST_CODE = 1
    var profilePhotoPath = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var NID_ATTACH_REQUEST_CODE = 200

    lateinit var retailerViewModel: RetailerViewModel

    lateinit var loadingAnim: Dialog
    lateinit var other: OtherCustomerChild
    private lateinit var currentLocation: LatLng


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var retailerSetupInfo = ""


    lateinit var nidfinalFile: File
    lateinit var nomNidFinalFile: File


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



    var nidSelectionType = ""
    var nomSelectionType = ""
    var nomcurrentPhotoIDPath  = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //   return inflater.inflate(R.layout.frag_mason_add, container, false)

        _binding = FragMasonAddBinding.inflate(inflater, container, false)



        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        uri = Uri.EMPTY
        niduri = Uri.EMPTY
        nomuri = Uri.EMPTY
        // Use binding safely

        binding.layoutMEmail.visibility = View.GONE
        binding.layoutMPartyZIP.visibility = View.GONE


        finalFile = File("")
        nidfinalFile = File("")
        nomNidFinalFile = File("")
        cardFile = ArrayList()
        nomCardFile = ArrayList()

        RetailerViewModelFactory()
        setupViewModel()
        retailerSetupInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

        loadRltnType(binding.acMNRltn)
        other = convertOtherInfo(requireActivity())!!
        contractor_code = other.partyCode.toString()

        onClickListener()
      //  onClickForResize()

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


    override fun onDestroyView() {
        super.onDestroyView()

        //   val imageFile = File(currentPhotoPath)
        val imageFile = File(Utility.getValueByKey(requireActivity(), "masonImgLink").toString())
        if (imageFile.exists()) {
            val isDeleted = imageFile.delete()
            if (isDeleted) {
                println("Temporary file deleted successfully.")
                Log.d("TempFile", "DELETED: ")
            } else {
                Log.d("TempFile", " Not yet")
            }
        } else {
            println("NO temp file found.")
        }

        // Set the binding to null to avoid memory leaks
        _binding = null
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


    /*
    fun onClickForResize(){

        if (finalFileInKB >= 200) {
         //   binding.resizeImage.visibility = View.VISIBLE
        } else if (finalFileInKB < 200) {
           // binding.resizeImage.visibility = View.GONE
        }
        /*
        binding.resizeImage.setOnClickListener {
            //  openCamera()
         //   if(finalFileInKB>=200) {
                finalFile = File(getRealPathFromURI(uri)!!)
            binding.resizeImage.isEnabled = false

            resizer()
         //   }

        }

         */
///////////////////// Todo NID
        if (nidfinalFileInKB >= 200) {
           binding.resizeNID.visibility = View.VISIBLE
        } else if (nidfinalFileInKB < 200) {
            binding.resizeNID.visibility = View.GONE
        }
        binding.resizeNID.setOnClickListener {
                if(nidSelectionType == "attach"
                ){
                    //todo
                    Utility.getFileFromSharedPreferences(requireContext(),"nidAttach",nidfinalFile)

                }else if(nidSelectionType == "camera"){
                    nidfinalFile = File(getRealPathFromURI(niduri)!!)

                }
               // nidfinalFile = File(getRealPathFromURI(niduri)!!)
                binding.resizeNID.isEnabled = false

                nidresizer()


        }
///////////////////TODO Nom
        if (nomNidFileInKB >= 200) {
            binding.resizeNomNID.visibility = View.VISIBLE
        } else if (nomNidFileInKB < 200) {
            binding.resizeNomNID.visibility = View.GONE
        }
        binding.resizeNomNID.setOnClickListener {
            //  openCamera()
          //  if(nomNidFileInKB>=200) {
            if(nomSelectionType=="attach"){
                Utility.getFileFromSharedPreferences(requireContext(),
                    "nomNidAttach",nomNidFinalFile)
            }else if(nomSelectionType=="camera"){
                nomNidFinalFile = File(getRealPathFromURI(nomuri)!!)
            }
              //  nomNidFinalFile = File(getRealPathFromURI(nomuri)!!)
                binding.resizeNomNID.isEnabled = false

                nomnidresizer()
         //   }

        }
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
                    if(finalFileInKB>=200) {
                        selectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentPhotoPath"
                            )
                        )
                        selectedImage = getResizedBitmap(selectedImage, 600)!!

                        val emp =
                            Utility.getValueByKey(requireActivity(), "username").toString()
                        //  val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val imName = emp
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
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
                    if(finalFileInKB>=200) {
                        selectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentPhotoPath"
                            )
                        )
                        selectedImage = getResizedBitmap(selectedImage, 500)!!
                        val emp =
                            Utility.getValueByKey(requireActivity(), "username").toString()
                        //  val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val imName = emp
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        finalFile = File(getRealPathFromURI(tempUri)!!)
                        finalFileInKB = finalFile.length() / 1024.0
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
                        if(nidfinalFileInKB>=200) {
                            nidselectedImage = BitmapFactory.decodeFile(
                                Utility.getValueByKey(
                                    requireActivity(),
                                    "currentPhotoIDPath"
                                )
                            )

                            nidselectedImage = getResizedBitmap(nidselectedImage, 600)!!

                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val tempUri = getImageUri(requireContext(), nidselectedImage!!, emp)
                            uri = tempUri!!
                            realPath = getRealPathFromURI(tempUri)!!
                            nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                            cardFile.set( 0, nidfinalFile )
                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }else{
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
                                1
                            )
                        }
                    }

                    else -> {

                        if(nidfinalFileInKB>=200) {
                            nidselectedImage = BitmapFactory.decodeFile(
                                Utility.getValueByKey(
                                    requireActivity(),
                                    "currentPhotoIDPath"
                                )
                            )
                            nidselectedImage = getResizedBitmap(nidselectedImage, 500)!!
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val tempUri = getImageUri(requireContext(), nidselectedImage!!, emp)
                            uri = tempUri!!
                            realPath = getRealPathFromURI(tempUri)!!
                            nidfinalFile = File(getRealPathFromURI(tempUri)!!)
                            cardFile.set( 0, nidfinalFile )
                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                         }else{
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
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
            }
            else{

                Utility.commonToast(
                    requireActivity(),
                    "Crop selected NID Image",
                    "Current file size "+ String.format("%.2f", nidfinalFileInKB) + " KB. Please crop image to get your desired size ",
                    1
                )
            }


        } else {
            Log.e("Mason_Entry", "Error: ")
        }

        binding.resizeNID.isEnabled = true

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
                        if(nomNidFileInKB>=200) {
                            nomselectedImage = BitmapFactory.decodeFile(
                                Utility.getValueByKey(
                                    requireActivity(),
                                    "currentPhotoNomIDPath"
                                )
                            )
                            nomselectedImage = getResizedBitmap(nomselectedImage, 600)!!
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri =
                                getImageUri(requireContext(), nomselectedImage!!, imName)
                            nomuri = tempUri!!
                            nrealPath = getRealPathFromURI(tempUri)!!
                            nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                            nomCardFile.set( 0, nomNidFinalFile )
                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                        }
                        else{
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
                                1
                            )
                        }
                    }

                    else -> {

                        if(nomNidFileInKB>=200) {
                            nomselectedImage = BitmapFactory.decodeFile(
                                Utility.getValueByKey(
                                    requireActivity(),
                                    "currentPhotoNomIDPath"
                                )
                            )
                            nomselectedImage = getResizedBitmap(nomselectedImage, 600)!!
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri =
                                getImageUri(requireContext(), nomselectedImage!!, imName)
                            nomuri = tempUri!!
                            nrealPath = getRealPathFromURI(tempUri)!!
                            nomNidFinalFile = File(getRealPathFromURI(tempUri)!!)
                            nomCardFile.set( 0, nomNidFinalFile )
                            nomNidFileInKB = nomNidFinalFile.length() / 1024.0
                            }
                            else{
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format("%.2f", finalFileInKB) + " KB",
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
            Log.e("Mason_Entry", "Error: ")
        }

        binding.resizeNomNID.isEnabled = true

    }

     */




    fun onClickListener() {
        binding.takeMImage.setOnClickListener {
            //  chooseImage(requireContext())
          //  openCamera()
            captureImage(PRO_PIC_CAMERA_REQ_CODE)


        }

        /*
                binding.pdfAddMNID.setOnClickListener {
                    binding.imgMNidFile.setBackgroundResource(0)

                    openPDFFile()
                }

         */




        binding.imgAddNID.setOnClickListener {
            binding.imgMNidFile.setBackgroundResource(0)

            openGalleryForImages()

        }

        binding.addMNidCamera.setOnClickListener {
            //    binding.imgMNidFile.setBackgroundResource(0)

         //   openCameraForNIDCArd()

         //   openCameraProfilePic()
            fileType = "CAMERAID"
            captureImage(NID_CAMERA_REQ_CODE)

        }


        binding.addMNomNidCamera.setOnClickListener {
            //   binding.imgMNomNidFile.setBackgroundResource(0)
          //  openCameraNomNidCard()

            captureImage(NOM_NID_CAMERA_REQ_CODE)

        }

        /*
                binding.pdfAddMNomNID.setOnClickListener {
                    binding.imgMNomNidFile.setBackgroundResource(0)

                    openNomPDFFile()
                }

         */

        binding.imgAddNomNID.setOnClickListener {
            //  binding.imgMNomNidFile.setBackgroundResource(0)

            openGalleryForNOMNidImages()

        }





        binding.btnAddmaton.setOnClickListener {

            if (validationForMason()) {
                confirmationDialog()
            }

        }

    }


    //todo save data
    fun validationForMason(): Boolean {
        var result = false
        binding.layoutMasonname.isErrorEnabled = false
        binding.layoutMasonname.error = null
        binding.layoutMAdd.isErrorEnabled = false
        binding.layoutMAdd.error = null
        binding.layoutMMobile.isErrorEnabled = false
        binding.layoutMMobile.error = null

        binding.layoutMNIDNum.isErrorEnabled = false
        binding.layoutMNIDNum.error = null


        if (nidfinalFile.isFile) {
            nidfinalFileInKB = nidfinalFile.length() / 1024.0
          //  nidfinalFileInKB = 220.0
            if (nidfinalFileInKB >= 200) {
               // binding.resizeNID.visibility = View.VISIBLE

            } else {
              //  binding.resizeNID.visibility = View.GONE
            }
        }
        if (finalFile.isFile) {
            finalFileInKB = finalFile.length() / 1024.0
            if (finalFileInKB >= 200) {
              //  binding.resizeImage.visibility = View.VISIBLE

            } else {
              //  binding.resizeImage.visibility = View.GONE
            }
        }

        if (nomNidFinalFile.isFile) {
            nomNidFileInKB = nomNidFinalFile.length() / 1024.0

            if (nomNidFileInKB >= 200) {
              //  binding.resizeNomNID.visibility = View.VISIBLE

            } else {
              //  binding.resizeNomNID.visibility = View.GONE
            }

        }



        if (binding.txtMasonname.text.toString().isEmpty()) {
            result = false
            binding.layoutMasonname.isErrorEnabled = true
            binding.layoutMasonname.error = "Mason's name is required"
        } else if (binding.txtMAdd.text.toString().isEmpty()) {
            result = false
            binding.layoutMAdd.isErrorEnabled = true
            binding.layoutMAdd.error = "Mason's address is required"
        } else if (binding.txtMMobile.text.toString().isEmpty() ||
            binding.txtMMobile.text.toString().length < 11
        ) {
            result = false
            binding.layoutMMobile.isErrorEnabled = true
            binding.layoutMMobile.error = "Mason's 11 digit mobile number is required"
        } else if (binding.txtMNIDNum.text.toString().isEmpty()) {
            result = false
            binding.layoutMNIDNum.isErrorEnabled = true
            binding.layoutMNIDNum.error = "Mason's NID card number is required"
        } else if (finalFileInKB > 200) {
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
        } else if (nomNidFileInKB > 200) {
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
        } else if (cardFile.size == 0 || nidfinalFileInKB > 200) {
            result = false
            if (cardFile.size == 0){
                Utility.getBaseMessage(
                    requireActivity(),
                    "Nid card is required ",
                    "Please attach nid card",

                    R.drawable.error_white,
                    0
                )
            }else {
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
        } else {
            result = true
        }



        return result
    }


    private fun confirmationDialog() {
        val v: View = layoutInflater.inflate(R.layout.add_new_mason_dialouge, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCancelR: MaterialButton = v.findViewById(R.id.btnCancelR)
        val btnConfirmR: MaterialButton = v.findViewById(R.id.btnConfirmR)
        val cancel: TextView = v.findViewById(R.id.cancelR)
        val txtCDialogTitle: TextView = v.findViewById(R.id.txtCDialogTitle)
        val txtBody: TextView = v.findViewById(R.id.txtBody)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelR.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmR.setOnClickListener {

            val empI = Utility.getValueByKey(requireActivity(), "username").toString()

            if (latitude == 0.0) {
                latitude = getLastKnownLocation()!!.latitude
                longitude = getLastKnownLocation()!!.longitude
            }

            val masonData = prepareOtherCustomerData()
            saveNewCustomer(empI, "Mason", masonData, dialog)


        }


    }


    private fun saveNewCustomer(user: String, customerType: String, value: String, dialog: Dialog) {
        dialog.dismiss()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        loadingAnim.show()

        retailerViewModel.saveNewCustomer(user, customerType, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    it.responseData?.let { res ->

                        if (res.code == "200") {
                            var mason_code = res.bp_id.toString()

                            //    if (finalFile.isFile && other.partyImage.toString() == "") {

                            //TODO Mason Image
                            if (finalFile.isFile) {
                                uploadCoverPhoto(
                                    user,
                                    mason_code,
                                    "Mason",
                                    latitude.toString(),
                                    longitude.toString(),
                                    finalFile,
                                    loadingAnim
                                )
                            }


                            //TODO NID
                            if (cardFile.size > 0) {
                                extension = "jpg"
                                if (nidfinalFile.isFile) {
                                    uploadCustomerDoc(
                                        user,
                                        mason_code, //siteid_date
                                        "Mason", //SalesSiteImg
                                        "nid",//progressimg
                                        extension,
                                        cardFile,
                                        loadingAnim,
                                        "" + "nid"
                                    )
                                }

                            }


                        } else {
                            loadingAnim.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Warning",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }
                    }

                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    if(it.message=="500") {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Warning",
                            "Please search with the mobile number in the Dashboard is there any other party is enlisted with this number.",
                            R.drawable.error_white,
                            0
                        )
                       // Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }


    private fun deletePictureFile(path : String): Boolean {
        var deleted = false
        try {
          //  val res = File(realPath)!!
            val res = File(path)!!

            if (res.isFile) {
                deleted = res.delete()
            }

            when {
                deleted -> {
                    Log.d("Picture", "DELETED: ")
                }
                else -> {
                    Log.d("Picture DELETED", " Not yet")
                }
            }
        } catch (e: Exception) {
            deleted = false
        }

        return deleted

    }

    //nid upload
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

                                    deletePictureFile(realPath)

                                    // loading.dismiss()
                                    if (party_code != "") {
                                        mason_code = party_code
                                        val profileData = prepareListForTag()
                                        MasonTag(user_id, profileData, loading)
                                        //doc file delete after save

                                    } else {
                                        loading.dismiss()
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Warning",
                                            "Mason id not found",
                                            R.drawable.error_white,
                                            0
                                        )
                                    }

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

    fun deleteTempFile(tempFile: File) {
        if (tempFile.exists()) {
            val deleted = tempFile.delete()
            if (deleted) {
                println("File deleted: ${tempFile.absolutePath}")
            } else {
                println("Failed to delete file: ${tempFile.absolutePath}")
            }
        } else {
            println("File does not exist: ${tempFile.absolutePath}")
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

                                  //  deleteTempFiles(nomNidFinalFile)
                                    deletePictureFile(nrealPath)

                                    // loading.dismiss()
                                    if (party_code != "") {
                                        mason_code = party_code
                                        val navController =
                                            requireActivity().findNavController(R.id.nav_host_fragment)
                                        navController.navigate(R.id.nav_demo_masonList)
                                        loading.dismiss()
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Successful",
                                            "New Mason Created Successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                        //doc file delete after save

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

    //todo nom nid end


    fun prepareListForTag(): String {

        //  val customertype = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()

        val partyCode = Utility.getValueByKey(requireActivity(), "partyCode").toString()


        val dataForMasontag = DataForMasontag(

            mason_code,
            partyCode
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

                            //todo nom nid
                            if (nomCardFile.size > 0) {
                                if (nomNidFinalFile.isFile) {
                                    uploadCustomerNomDoc(
                                        user,
                                        mason_code,
                                        "mason",
                                        "nomnid",//nom nid folder name
                                        nomextension,
                                        nomCardFile,
                                        loadingAnim,
                                        "" + "nid"
                                    )
                                }

                            } else {

                                loading.dismiss()
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "New Mason Created Successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                val navController =
                                    requireActivity().findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.nav_demo_masonList)
                            }
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
                    //  loadingAnim.dismiss()
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
                                if (res.code != "200") {
                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Mason Image Warning",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                } else if (res.code =="200"){
                                    //delete image file after save
                                  //  deleteTempFiles(finalFile)
                                    deletePictureFile(prealPath)

                                }else{

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


    private fun prepareOtherCustomerData(): String {
        var division = Utility.getValueByKey(requireActivity(), "selectedDivision").toString()
        var district = Utility.getValueByKey(requireActivity(), "selectedDistrict").toString()
        var thana = Utility.getValueByKey(requireActivity(), "selectedThana").toString()


        val otherCustomer = OtherCustomer(
            binding.txtMasonname.text.toString(),
            binding.txtMAdd.text.toString(),
            binding.txtMCity.text.toString(),
            "",
            "Bangladesh",
            "",
            "",
            "Bangladesh",
            division,
            district,
            thana,
            binding.txtMMobile.text.toString(),
            "",
            "",
            0,
            0,
            "",
            "",
            "",

            binding.txtMNIDNum.text.toString(),
            binding.txtMNName.text.toString(),
            binding.txtMNnum.text.toString(),
            binding.acMNRltn.text.toString(),
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

        val gson = Gson()
        return gson.toJson(otherCustomer)
    }


    private fun getLastKnownLocation(): Location? {
        val mLocationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = mLocationManager!!.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return bestLocation
    }


    //TODO CAPTURE IMAGES-------------------------------------------------------------------------->


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "IMG-"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "$imageFileName.jpg")
    }


    private fun openCamera() {
        fileType = "CAMERA"
        if (latitude == 0.0) {
            //  location()
            getLastLocations()
        }
        val fileName = "Mason"
        val path = Environment.DIRECTORY_PICTURES
        val storageDirectory: File = requireActivity().getExternalFilesDir(path)!!


        var imageFile: File? = null
        try {

//            try {
//                val dir = File(path)
//                if (!dir.exists()) {
//                    dir.mkdirs()
//                }
//            }
//            catch (e:Exception)
//            {
//                Log.e("FragMasonAdd", e.message.toString() )
//            }

            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            currentPhotoPath = imageFile!!.absolutePath
            val imageUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.crowncement.TsSalesTracking.fileprovider",
                imageFile
            )
            Utility.storeKeyValue(requireActivity(), "masonImgLink", currentPhotoPath)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            startActivityForResult(intent, 0)
            //storage/emulated/0/Android/data/com.crowncement.salesforceapp/files/Pictures/IMG2399521268364836531.jpg
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e("ERROR", "openCamera: ${e.message}")
        }
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




    private fun openCameraForNIDCArd() {
        fileType = "CAMERAID"
        if (latitude == 0.0) {
            //   location()
            getLastLocations()
        }

        val fileName = "Mason"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentPhotoIDPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(), "com.crowncement.TsSalesTracking.fileprovider", imageFile
        )
        Utility.storeKeyValue(requireActivity(), "currentPhotoIDPath", currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 130)
    }
//todo new

    private fun openCameraProfilePic() {

        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        profilePhotoPath = imageFile!!.absolutePath
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.TsSalesTracking.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(), "currentPhotoIDPath", currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 130)

    }



    private fun openCameraNomNidCard() {
        nomfileType = "CAMERAID"
        if (latitude == 0.0) {
            //   location()
            getLastLocations()
        }

        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
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
        Utility.storeKeyValue(requireActivity(), "currentPhotoNomIDPath", currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, NOM_NID_CAMERA_REQ_CODE)
    }


    private fun openGalleryForImages() {
        fileType = "ATTACH"

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
            startActivityForResult(intent, NID_ATTACH_REQUEST_CODE)
        }
    }

    private fun openGalleryForNOMNidImages() {
        nomfileType = "ATTACH"

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), Nom_Attach_REQUEST_CODE)

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, Nom_Attach_REQUEST_CODE)
        }
    }


    private fun openPDFFile() {
        fileType = "PDF"
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    private fun openNomPDFFile() {
        nomfileType = "PDF"
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 102)
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





    suspend fun isImageIsExist(context: Context, fileName: String): String =
        suspendCoroutine { continuation ->
            CoroutineScope(IO).launch {

                var absolutePathOfImage = ""
                val path =
                    Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name)

                if (Build.VERSION.SDK_INT >= 29) {
                    val projection = arrayOf(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.MediaColumns.DATA,
                        MediaStore.MediaColumns.RELATIVE_PATH
                    )

                    val selection =
                        MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"

                    val selectionargs = arrayOf("%" + path + "%", "%" + fileName + "%")
                    val cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionargs,
                        null
                    );

                    val indexDisplayName =
                        cursor?.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)


                    async {
                        while (cursor?.moveToNext() == true) {
                            val displayName = indexDisplayName?.let { cursor.getString(it) }
                            absolutePathOfImage =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        }
                    }.await()
                    Log.d("TAG", "isImageIsExist: absolutePath -> $absolutePathOfImage")
                    if (cursor!!.count > 0) {
                        // file is exist
                        continuation.resume(absolutePathOfImage)
                    } else {
                        // file is not existed
                        continuation.resume(absolutePathOfImage)
                    }
                } else {
                    //For below api 29
                    try {

                        val downloadFolder = context.getExternalFilesDir(path)
                        val fileList = downloadFolder?.listFiles()

                        async {
                            if (fileList != null) {
                                for (file in fileList) {
                                    if (file.name == fileName) {
                                        absolutePathOfImage = file.absolutePath
                                    }
                                }
                            }
                        }.await()
                        Log.d("TAG", "isImageIsExist: absolutePath -> $absolutePathOfImage")

                        continuation.resume(absolutePathOfImage)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
        val storageDir =
            context.cacheDir // Use cache directory or external storage depending on your needs
        return File.createTempFile(tempFileName, null, storageDir)
    }

    private fun setImage(imgFile: File, v: ImageView) {
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    //todo index1,size 1 end


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


    //TODO CAPTURE IMAGES END---------------------------------------------------------------------->


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
                nomfileType = "CAMERAID"
                nomcurrentPhotoIDPath = absolutePath

            }else if (requestCode==NID_CAMERA_REQ_CODE){
                fileType = "CAMERAID"
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
                        binding.imgMason.setImageBitmap(bitmap)


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


                    //todo new one

                    NID_CAMERA_REQ_CODE -> {
                        try {
                            if (fileType == "CAMERAID") {


                                //  val selectedImage = BitmapFactory.decodeFile(currentPhotoIDPath)
                                val nidimgFile = compressImage(currentPhotoIDPath)

                                if (nidimgFile.exists()) {
                                    val nidimageSize = (nidimgFile.length() / 1024.0)
                                    // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                    //  finalFile.add(imgFile)
                                    nidfinalFile = nidimgFile
                                }

                                cardFile.add(nidfinalFile)

                                val nidbitmap = BitmapFactory.decodeFile(nidfinalFile.absolutePath)
                                binding.imgMNidFile.setImageBitmap(nidbitmap)
                                /*
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
                                    .into(binding.imgMNidFile)
                                // binding.imgMNidFile.setImageBitmap(selectedImage)
                                */
                                //  fileType = ""

                            }
                            else {
                              //  fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture nid card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        } catch (e: Error) {

                            /*
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
                                    .into(binding.imgMNidFile)
                                //   binding.imgMNidFile.setImageBitmap(selectedImage)
                                fileType = ""

                            }
                            else {
                                fileType = ""
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Failed to capture visiting card",
                                    R.drawable.error_white,
                                    2
                                )
                            }
                            */


                        }

                    }

                    //todo
/*
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
                                    .into(binding.imgMNidFile)
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
                                    .into(binding.imgMNidFile)
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

 */

                    //todo

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

                                   // var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
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
                                        .into(binding.imgMNidFile)
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

                        }
                        else if (data?.data != null) {
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
                               // val imgFile = compressImage(imguri.toString())
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
                                    .into(binding.imgMNidFile)
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
                                    .into(binding.imgMNomNidFile)

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
                                    .into(binding.imgMNomNidFile)
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

                                   // var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
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
                                        .into(binding.imgMNomNidFile)
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


                                val imgFile = compressImage(nomNidFinalFile.absolutePath)

                                nomNidFinalFile = imgFile


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
                                    .into(binding.imgMNomNidFile)
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

