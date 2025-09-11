package com.crowncement.TsSalesTracking.UI.Fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
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
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesVisitViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesVisitViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Model.CallRecordSpinnerData
import com.crowncement.TsSalesTracking.data.Model.OtherCustomer
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.FragContractorAddUpdateBinding
import com.crowncement.TsSalesTracking.databinding.PdfviewerBinding
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
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.UUID


class Frag_Contractor_Add_Update : Fragment() {

    // lateinit var rootView: View
    lateinit var nidselectedImage: Bitmap

    private var _binding: FragContractorAddUpdateBinding? = null
    private val binding get() = _binding!!

    lateinit var salesVisitViewModel: SalesVisitViewModel
    lateinit var retailerViewModel: RetailerViewModel

    lateinit var other: OtherCustomerChild
    var engineerGeoInfo = ""
    var party_code = ""
    var visitingCardPhotoPath = ""
    var nidCardpath = ""
    var profilePhotoPath = ""
    var proPicExtension = ""
    var DOB = ""
    var user = ""
    var selectedProgram = ""
    var fileType = ""
    var nidExtension = ""

    private var PRO_PIC_CAMERA_REQ_CODE = 0
    private var PRO_PIC_ATTACH_REQUEST_CODE = 1
    private var NID_ATTACH_REQUEST_CODE = 2
    private var VCARD_PIC_CAMERA_REQ_CODE = 3

    lateinit var programList: ArrayList<String>
    lateinit var brandList: ArrayList<CallRecordSpinnerData>
    lateinit var cementBrandList: ArrayList<String>

    lateinit var picFile: File
    lateinit var vCardFile: File
    //lateinit var nidFile: File

    var prealPath = ""
    var vrealPath = ""

    var realPath = ""


    lateinit var picker: DatePickerDialog

    lateinit var loadingAnim: Dialog
    lateinit var cardFile: java.util.ArrayList<File>
    lateinit var nidfinalFile: File
    lateinit var nidFile: java.util.ArrayList<File>


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private val permissionCode = 101

    //nom
    var currentPhotoIDPath = ""
    lateinit var nomCardFile: ArrayList<File>
    lateinit var nomNidFinalFile: File
    var nomextension = ""
    var nomfileType = ""
    var nidSelectionType = ""

    //for file size
    var nidfinalFileInKB: Double = 0.0
    var picFileInKB: Double = 0.0
    var vCardFileInKB: Double = 0.0

    lateinit var uri: Uri
    lateinit var vuri: Uri
    lateinit var nuri: Uri

    lateinit var selectedImage: Bitmap
    lateinit var vselectedImage: Bitmap
    lateinit var nselectedImage: Bitmap


    var resizeCount = 0
    var vresizeCount: Int = 0
    var nresizeCount: Int = 0

    private val PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url="
    private val REQUEST_IMAGE_CAPTURE = 1
    var currentPhotoPath = ""





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //   rootView = inflater.inflate(R.layout.frag_contractor_add_update, container, false)

        _binding = FragContractorAddUpdateBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uri = Uri.EMPTY
        vuri = Uri.EMPTY
        nuri = Uri.EMPTY

        // Use binding safely
        cardFile = ArrayList()
        nidfinalFile = File("")
        nidFile = ArrayList()
        nomCardFile = ArrayList()
        nomNidFinalFile = File("")

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        loadParticipatedProgram(binding.acConProgram)
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        picFile = File("")
        vCardFile = File("")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()
        SalesVisitViewModelFactory()
        RetailerViewModelFactory()
        setupViewModel()
        getAllCementBrandList()


        //   onClickLisener()

        binding.imgSelectProfile.setOnClickListener {

           // chooseImage(requireContext())
            captureImage(PRO_PIC_CAMERA_REQ_CODE)

        }

        binding.imgAddNID.setOnClickListener {
            //openNIDPDFFile()
            openGalleryForImages()
        }
        binding.imgAddVCard.setOnClickListener {
           // openCameraVisitingCard()
            captureImage(VCARD_PIC_CAMERA_REQ_CODE)

        }


        binding.txtConDOB.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    DOB = selectedDate
                    binding.txtConDOB.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                    val calculateAge = Utility.findDifference(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MM-yyyy"
                        ).toString(),
                        Utility.getCurrentDate("dd-MM-yyyy")
                    )
                    binding.txtConAge.setText(calculateAge)

                }, year, month, day
            )
            picker.show()
        }

        binding.btnAddContractor.setOnClickListener {
            if (party_code == "") {
                //SAVE
                if (saveUIValidation()) {
                    confirmStart("Add New Contractor")
                }

            } else {
                //UPDATE
                if (updateUIValidation() && party_code != "") {

                    confirmStart("Update Contractor Information")

                }
            }
        }

        binding.acConProgram.setOnItemClickListener { parent, arg1, position, arg3 ->

            selectedProgram = if (position > 0 && programList.size > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }

        onClickLisener()



        binding.imgNIDPreview.setOnClickListener {

            if (other.nidImg != "") {

                //http://34.142.207.144:8787/da/docs/x880022/Contractor/nid/SBP-050085.jpg

                if (other.nidImg.toString().contains("jpg")) {

                    showPDF(IMAGE_BASE_URL + other.nidImg, "")
                } else {

                    showPDF(PDF_VIEWER_URL + IMAGE_BASE_URL + other.nidImg, "")
                }


            }

        }


    }


    private fun showPDF(pdfURL: String, title: String) {

        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        val bind: PdfviewerBinding = PdfviewerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bind.getRoot())
        bind.txtTitle.text = title


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        bind.webView.webViewClient = WebViewClient()
        bind.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        bind.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        bind.webView.settings.useWideViewPort = true
        bind.webView.requestFocusFromTouch()
        CookieManager.getInstance().setAcceptCookie(true)

        dialog.setContentView(bind.root)
        dialog.setCancelable(true)
        dialog.show()


        val settings = bind.webView.settings

        // Enable java script in web view.
        settings.javaScriptEnabled = true

        // Enable DOM storage API.
        settings.domStorageEnabled = true

        // Enable zooming in web view.
        settings.setSupportZoom(true)

        // Allow pinch to zoom.
        settings.builtInZoomControls = true

        // Disable the default zoom controls on the page.
        settings.displayZoomControls = true

        // Enable responsive layout.
        settings.useWideViewPort = true

        // Zoom out if the content width is greater than the width of the viewport.
        settings.loadWithOverviewMode = false


        // Set web view client.
        bind.webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(webView: WebView?, newProgress: Int) {
                if (newProgress < 100) {

                    loadingAnim.show()
                }
                if (newProgress == 100) {

                    // There is a bug with Google Docs that sometimes you get blank screen
                    // instead of a PDF file. To avoid just reload when you get it.
                    if (webView?.contentHeight == 0) {
                        Toast.makeText(activity, "Reloading...", Toast.LENGTH_SHORT).show()
                        webView.reload()
                    }

                    loadingAnim.dismiss()
                }
            }
        }

        //     val urlString: String = PDF_VIEWER_URL + pdfURL
        val urlString: String = pdfURL

        bind.webView.loadUrl(urlString)

        bind.imageButton.setOnClickListener {

            dialog.dismiss()

        }

    }


    fun onClickLisener() {

        if (picFileInKB >= 200) {
           // binding.resizeImage.visibility = View.VISIBLE
        } else if (picFileInKB < 200) {
          // binding.resizeImage.visibility = View.GONE
        }

        /*
        binding.resizeImage.setOnClickListener {
            //  openCamera()
            //   if(picFileInKB>=200) {
            picFile = File(getRealPathFromURI(uri)!!)
            binding.resizeImage.isEnabled = false

            resizer()
            //  }

        }

        */
//todo visiting  card

        if (vCardFileInKB >= 200) {
          //  binding.resizeVisitingCard.visibility = View.VISIBLE
        } else if (vCardFileInKB < 200) {
          //  binding.resizeVisitingCard.visibility = View.GONE
        }
        /*
        binding.resizeVisitingCard.setOnClickListener {
            //  openCamera()
            //   if(vCardFileInKB>=200) {
            vCardFile = File(getRealPathFromURI(vuri)!!)
            binding.resizeVisitingCard.isEnabled = false

            visitingCardresizer()
            //   }

        }


         */

        //todo nidcard

        if (nidfinalFileInKB >= 200) {
           // binding.resizeNidCard.visibility = View.VISIBLE
        } else if (nidfinalFileInKB < 200) {
          //  binding.resizeNidCard.visibility = View.GONE
        }

        /*
        binding.resizeNidCard.setOnClickListener {
            //  openCamera()
            //  if(nidfinalFileInKB>=200) {
            if (nidSelectionType == "attach") {

                var a = Utility.getFileFromSharedPreferences(
                    requireContext(),
                    "conNidAttach",
                    nidfinalFile
                )
                var b = a


            } else if (nidSelectionType == "camera") {
                nidfinalFile = File(getRealPathFromURI(nuri)!!)
            }

            binding.resizeNidCard.isEnabled = false

            nidresizer()


        }

         */
    }

//old


    fun resizer() {


        if (picFile.isFile) {

            when (resizeCount) {
                0 -> {
                    GlobalScope.launch {
                        picFile = Compressor.compress(requireContext(), picFile) {
                            resolution(960, 540)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        picFileInKB = picFile.length() / 1024.0
                    }
                }

                1 -> {
                    GlobalScope.launch {
                        picFile = Compressor.compress(requireContext(), picFile) {
                            resolution(768, 432)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        picFileInKB = picFile.length() / 1024.0
                    }
                }

                2 -> {
                    GlobalScope.launch {
                        picFile = Compressor.compress(requireContext(), picFile) {
                            resolution(640, 360)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        picFileInKB = picFile.length() / 1024.0
                    }
                }

                3 -> {
                    if (picFileInKB >= 200) {
                        selectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentPhotoPath"
                            )
                        )
                        selectedImage = getResizedBitmap(selectedImage, 600)!!
                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        // val tempUri = getImageUri(requireContext(), selectedImage!!, "Party")
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        picFile = File(getRealPathFromURI(tempUri)!!)
                        picFileInKB = picFile.length() / 1024.0
                    } else {
                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }
                }

                else -> {

                    if (picFileInKB >= 200) {
                        selectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentPhotoPath"
                            )
                        )
                        selectedImage = getResizedBitmap(selectedImage, 500)!!
                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                        // val tempUri = getImageUri(requireContext(), selectedImage!!, "Party")
                        uri = tempUri!!
                        prealPath = getRealPathFromURI(tempUri)!!
                        picFile = File(getRealPathFromURI(tempUri)!!)
                        picFileInKB = picFile.length() / 1024.0
                    } else {
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
                "Current file size " + String.format("%.2f", picFileInKB) + " KB",
                1
            )

            resizeCount += 1
        } else {
            Log.e("Contractor_Entry", "Error: ")
        }

     //   binding.resizeImage.isEnabled = true

    }


    fun visitingCardresizer() {


        if (vCardFile.isFile) {

            when (vresizeCount) {
                0 -> {
                    GlobalScope.launch {
                        vCardFile = Compressor.compress(requireContext(), vCardFile) {
                            resolution(960, 540)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        cardFile.set(0, vCardFile)
                        vCardFileInKB = vCardFile.length() / 1024.0
                    }
                }

                1 -> {
                    GlobalScope.launch {
                        vCardFile = Compressor.compress(requireContext(), vCardFile) {
                            resolution(768, 432)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        cardFile.set(0, vCardFile)

                        vCardFileInKB = vCardFile.length() / 1024.0
                    }
                }

                2 -> {
                    GlobalScope.launch {
                        vCardFile = Compressor.compress(requireContext(), vCardFile) {
                            resolution(640, 360)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(153_600) // 200 KB
                        }
                        cardFile.set(0, vCardFile)

                        vCardFileInKB = vCardFile.length() / 1024.0
                    }
                }

                3 -> {
                    if (vCardFileInKB >= 200) {
                        vselectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentVisitingCardFilePath"
                            )
                        )
                        vselectedImage = getResizedBitmap(vselectedImage, 600)!!
                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), vselectedImage!!, imName)
                        // val tempUri = getImageUri(requireContext(), vselectedImage!!, "Party")
                        vuri = tempUri!!
                        vrealPath = getRealPathFromURI(tempUri)!!
                        vCardFile = File(getRealPathFromURI(tempUri)!!)
                        cardFile.set(0, vCardFile)

                        vCardFileInKB = vCardFile.length() / 1024.0

                    } else {
                        Utility.commonToast(
                            requireActivity(),
                            "",
                            "Already Optimised",
                            1
                        )
                    }
                }

                else -> {
                    if (vCardFileInKB >= 200) {
                        vselectedImage = BitmapFactory.decodeFile(
                            Utility.getValueByKey(
                                requireActivity(),
                                "currentVisitingCardFilePath"
                            )
                        )
                        vselectedImage = getResizedBitmap(vselectedImage, 500)!!
                        val emp = Utility.getValueByKey(requireActivity(), "username").toString()
                        val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                        val tempUri = getImageUri(requireContext(), vselectedImage!!, imName)
                        //   val tempUri = getImageUri(requireContext(), vselectedImage!!, "Party")
                        vuri = tempUri!!
                        vrealPath = getRealPathFromURI(tempUri)!!
                        vCardFile = File(getRealPathFromURI(tempUri)!!)
                        cardFile.set(0, vCardFile)

                        vCardFileInKB = vCardFile.length() / 1024.0

                    } else {
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
                "Visiting card compression done",
                "Current file size " + String.format("%.2f", vCardFileInKB) + " KB",
                1
            )

            vresizeCount += 1
        } else {
            Log.e("Contractor_Entry", "Error: ")
        }
      //  binding.resizeVisitingCard.isEnabled = true

    }


    fun nidresizer() {

        if (nidfinalFile.isFile) {

            if (nidSelectionType == "camera") {
                when (resizeCount) {
                    0 -> {
                        GlobalScope.launch {
                            nidfinalFile = Compressor.compress(requireContext(), nidfinalFile) {
                                resolution(960, 540)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                                size(153_600) // 200 KB
                            }
                            nidFile.set(0, nidfinalFile)

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
                            nidFile.set(0, nidfinalFile)
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
                            nidFile.set(0, nidfinalFile)
                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        }
                    }

                    3 -> {
                        if (nidfinalFileInKB >= 200) {
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
                            nidFile.set(0, nidfinalFile)
                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        } else {
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format(
                                    "%.2f",
                                    nidfinalFileInKB
                                ) + " KB",
                                1
                            )
                        }
                    }

                    else -> {

                        if (nidfinalFileInKB >= 200) {
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
                            nidFile.set(0, nidfinalFile)
                            nidfinalFileInKB = nidfinalFile.length() / 1024.0
                        } else {
                            Utility.commonToast(
                                requireActivity(),
                                "Image compression done",
                                "Current file size " + String.format(
                                    "%.2f",
                                    nidfinalFileInKB
                                ) + " KB",
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

                nresizeCount += 1
            } else {

                Utility.commonToast(
                    requireActivity(),
                    "Crop selected NID Image",
                    "Current file size " + String.format(
                        "%.2f",
                        nidfinalFileInKB
                    ) + " KB. Please crop image to get your desired size ",
                    1
                )
            }


        } else {
            Log.e("Con_Entry", "Error: ")
        }

     //   binding.resizeNidCard.isEnabled = true

    }


    private fun openGalleryForImages() {
        fileType = "ATTACH"

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures"),
                NID_ATTACH_REQUEST_CODE
            )

        } else { // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, NID_ATTACH_REQUEST_CODE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun saveUIValidation(): Boolean {

        var result = false


        //name
        removeUIErrorSign(binding.layoutConName)
        //present add
        removeUIErrorSign(binding.layoutConPresentAdd)
        //parmanent add
        removeUIErrorSign(binding.layoutPerAddress)
        //mobile 1
        removeUIErrorSign(binding.layoutCMo1)
        //mobile 2
        removeUIErrorSign(binding.layoutC2Mo)
        //dob
        removeUIErrorSign(binding.layoutCDOB)
        // total exp
        removeUIErrorSign(binding.layoutConExperience)
        //no sub labour
        removeUIErrorSign(binding.layoutNumLabo)
        //on going site
        removeUIErrorSign(binding.layoutCOngoing)
        //crown site
        removeUIErrorSign(binding.layoutCCrown)
        if (nidfinalFile.isFile) {
            nidfinalFileInKB = nidfinalFile.length() / 1024.0
            //  nidfinalFileInKB = 220.0
            if (nidfinalFileInKB >= 200) {

           //     binding.resizeNidCard.visibility = View.VISIBLE

            } else {

            //    binding.resizeNidCard.visibility = View.GONE

            }
        }
        if (picFile.isFile) {
            picFileInKB = picFile.length() / 1024.0
            // picFileInKB = 240.0
            if (picFileInKB >= 200) {

               // binding.resizeImage.visibility = View.VISIBLE

            } else {

           //     binding.resizeImage.visibility = View.GONE

            }
        }

        if (vCardFile.isFile) {
            vCardFileInKB = vCardFile.length() / 1024.0
            if (vCardFileInKB >= 200) {
             //   binding.resizeVisitingCard.visibility = View.VISIBLE

            } else {
             //   binding.resizeVisitingCard.visibility = View.GONE
            }
        }


        if (binding.txtConName.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConName, "Please provide contractor full name as per NID")

        } else if (binding.txtPreAddress.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConPresentAdd, "Please provide present address")
        } else if (binding.txtPerAddress.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutPerAddress, "Provide contractor permanent  address")
        } else if (binding.txtMobile1.text.toString()
                .isEmpty() || binding.txtMobile1.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutCMo1, "Invalid mobile number")
        } else if (DOB == "") {
            result = false
            setError(binding.layoutCDOB, "Date of birth is required")
        } else if (binding.txtConExperience.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConExperience, "Experience is required")
        } else if (binding.txtConLabour.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutNumLabo, "No of subordinate is required")
        } else if (binding.txtConOngoing.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutCOngoing, "Provide no of ongoing site")
        } else if (binding.txtConCrown.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutCCrown, "Provide no of crown site")
        } else if (!picFile.isFile || picFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Contractor Image size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    picFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else if (!nidfinalFile.isFile || nidfinalFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),

                "Contractor nid card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    nidfinalFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else if (vCardFile.isFile && vCardFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Contractor visiting card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    vCardFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else {
            result = true
        }

        return result

    }


    private fun updateUIValidation(): Boolean {

        var result = false


        //name
        removeUIErrorSign(binding.layoutConName)
        //present add
        removeUIErrorSign(binding.layoutConPresentAdd)
        //parmanent add
        removeUIErrorSign(binding.layoutPerAddress)
        //mobile 1
        removeUIErrorSign(binding.layoutCMo1)
        //mobile 2
        removeUIErrorSign(binding.layoutC2Mo)
        //dob
        removeUIErrorSign(binding.layoutCDOB)
        // total exp
        removeUIErrorSign(binding.layoutConExperience)
        //no sub labour
        removeUIErrorSign(binding.layoutNumLabo)
        //on going site
        removeUIErrorSign(binding.layoutCOngoing)
        //crown site
        removeUIErrorSign(binding.layoutCCrown)


        if (nidfinalFile.isFile) {
            nidfinalFileInKB = nidfinalFile.length() / 1024.0
            if (nidfinalFileInKB >= 200) {
              //  binding.resizeNidCard.visibility = View.VISIBLE

            } else {
             //   binding.resizeNidCard.visibility = View.GONE
            }
        }
        if (picFile.isFile) {
            picFileInKB = picFile.length() / 1024.0
            if (picFileInKB >= 200) {
                //  picFileInKB = 240.0

            //    binding.resizeImage.visibility = View.VISIBLE

            } else {
              //  binding.resizeImage.visibility = View.GONE
            }
        }

        if (vCardFile.isFile) {
            vCardFileInKB = vCardFile.length() / 1024.0
            if (vCardFileInKB >= 200) {

           //     binding.resizeVisitingCard.visibility = View.VISIBLE

            } else {
            //    binding.resizeVisitingCard.visibility = View.GONE
            }
        }


        if (binding.txtConName.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConName, "Please provide contractor full name as per NID")

        } else if (binding.txtPreAddress.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConPresentAdd, "Please provide present address")
        } else if (binding.txtPerAddress.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutPerAddress, "Provide contractor permanent  address")
        } else if (binding.txtMobile1.text.toString()
                .isEmpty() || binding.txtMobile1.text.toString().length != 11
        ) {
            result = false
            setError(binding.layoutCMo1, "Invalid mobile number")
        } else if (DOB == "") {
            result = false
            setError(binding.layoutCDOB, "Date of birth is required")
        } else if (binding.txtConExperience.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutConExperience, "Experience is required")
        } else if (binding.txtConLabour.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutNumLabo, "No of mason is required")
        } else if (binding.txtConOngoing.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutCOngoing, "Provide no of ongoing site")
        } else if (binding.txtConCrown.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutCCrown, "Provide no of crown site")
        } else if ((other.partyImage.toString() == "" && !picFile.isFile) || picFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Contractor Image size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    picFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else if ((!nidfinalFile.isFile && other.nidImg.toString() == "") || nidfinalFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Contractor nid card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    nidfinalFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else if (vCardFile.isFile && vCardFileInKB > 200) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Contractor visiting card size exceed expected max file size 200 KB ",
                "After compression your file size is   " + String.format(
                    "%.2f",
                    vCardFileInKB
                ) + " KB",
                R.drawable.error_white,
                2
            )
        } else {
            result = true
        }

        return result

    }


    private fun removeUIErrorSign(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = null
    }

    private fun setError(field: TextInputLayout, message: String) {

        field.isErrorEnabled = true
        field.error = message
    }


    private fun confirmStart(title: String) {
        val v: View = layoutInflater.inflate(R.layout.contractor_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtCTitle: TextView = v.findViewById(R.id.txtCTitle)
        txtCTitle.text = title

        val btnECancel: MaterialButton = v.findViewById(R.id.btnCCancel)
        val btnEAdd: MaterialButton = v.findViewById(R.id.btnCAdd)
        if (title == "Update Contractor Information") {
            btnEAdd.text = "Update"
        }
        val Ecancel: TextView = v.findViewById(R.id.Ccancel)


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
            if (title == "Update Contractor Information") {
                val updateData = prepareContractorUpdateData()
                updateOtherParty(user, party_code, updateData)
            } else {
                val data = prepareContractorData()
                saveNewCustomer(user, "Contractor", data)
            }
        }
    }


    private fun prepareContractorData(): String {

        val otherCustomer = OtherCustomer(
            engineerGeoInfo.split(",")[3] + " construction center",
            binding.txtPreAddress.text.toString(),
            "",
            "",
            "Bangladesh",
            binding.txtConName.text.toString(),
            "",
            "Bangladesh",
            engineerGeoInfo.split(",")[1],
            engineerGeoInfo.split(",")[2],
            engineerGeoInfo.split(",")[3],
            binding.txtMobile1.text.toString(),
            "",
            binding.txtMobile2.text.toString(),
            binding.txtConOngoing.text.toString().toInt(),
            binding.txtConCrown.text.toString().toInt(),
            "",
            DOB,
            "",
            binding.txtConBrand1.text.toString(),
            binding.txtConBrand2.text.toString(),
            binding.txtConBrand3.text.toString(),
            "",
            "",
            "jpg",
            binding.txtPerAddress.text.toString(),
            binding.txtConExperience.text.toString().toDouble(),
            binding.txtConLabour.text.toString().toInt(),
            selectedProgram,
            nidExtension,
            "Contractor",
            "",
            binding.txtNIDNUm.text.toString(),
            "",
            "",
            "",
            ""
        )

        val gson = Gson()
        return gson.toJson(otherCustomer)
    }


    private fun prepareContractorUpdateData(): String {

        var dob = ""

        if (binding.txtConDOB.text.toString()
                .isNotEmpty() && binding.txtConDOB.text.toString() != "2999-12-31"
        ) {
            dob = Utility.changeDateFormat(
                binding.txtConDOB.text.toString(),
                "dd MMM yyyy",
                "yyyy-MM-dd"
            ).toString()
        }


        val otherCustomerUpdate = OtherCustomer(
            //engineerGeoInfo.split(",")[2] + " construction center",
            other.partyName.toString(),
            binding.txtPreAddress.text.toString(),
            "",
            "",
            "Bangladesh",
            binding.txtConName.text.toString(),
            "",
            "Bangladesh",
            // engineerGeoInfo.split(",")[1],
            Utility.getValueByKey(requireActivity(), "selectedDivision").toString(),
            //  engineerGeoInfo.split(",")[2],
            Utility.getValueByKey(requireActivity(), "selectedDistrict").toString(),
            //engineerGeoInfo.split(",")[3],
            Utility.getValueByKey(requireActivity(), "selectedThana").toString(),
            binding.txtMobile1.text.toString(),
            "",
            binding.txtMobile2.text.toString(),
            binding.txtConOngoing.text.toString().toInt(),
            binding.txtConCrown.text.toString().toInt(),
            "",
            dob,
            "",
            binding.txtConBrand1.text.toString(),
            binding.txtConBrand2.text.toString(),
            binding.txtConBrand3.text.toString(),
            "",
            "",
            "jpg",
            binding.txtPerAddress.text.toString(),
            binding.txtConExperience.text.toString().toDouble(),
            binding.txtConLabour.text.toString().toInt(),
            binding.acConProgram.text.toString(),
            nidExtension,
            "Contractor",
            "",
            binding.txtNIDNUm.text.toString()
        )
        val gson = Gson()
        return gson.toJson(otherCustomerUpdate)
    }


    private fun saveNewCustomer(user: String, customerType: String, value: String) {

//        loadingAnim = Utility.baseLoadingAnimation(
//            requireActivity(),
//            Dialog(requireActivity()),
//            "P l e a s e    w a i t"
//        )
        loadingAnim.show()

        retailerViewModel.saveNewCustomer(user, customerType, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            //todo nom nid
                            if (nomCardFile.size > 0) {
                                if (nomNidFinalFile.isFile) {


                                }

                            }

                            //  nidfinalFileInKB = nidFile.get(0).length()/1024.0


                            uploadProfilePhoto(
                                user,
                                res.bp_id.toString(),
                                "Contractor",
                                latitude.toString(),
                                longitude.toString(),
                                picFile,
                                loadingAnim
                            )
                        } else {
                            loadingAnim.dismiss()

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
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun updateOtherParty(user: String, partyCode: String, value: String) {


        loadingAnim.show()

        retailerViewModel.updateOther(user, partyCode, value).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    loadingAnim.dismiss()

                    it.responseData?.let { res ->



                        if (res.code == "200") {



                            if ((picFile.isFile) && (other.partyImage=="")) {

                                uploadProfilePhoto(
                                    user,
                                    partyCode,
                                    "Contractor",
                                    latitude.toString(),
                                    longitude.toString(),
                                    picFile,
                                    loadingAnim
                                )
                            }
                            else if ((vCardFile.isFile) && (other.busCardImg =="")) {
                                //vcard
                                uploadCustomerDoc(
                                    user,
                                    party_code,
                                    "Contractor",
                                    "bid",
                                    "jpg",
                                    cardFile,
                                    loadingAnim,
                                    "vcard"
                                )
                            }
                            else if ((nidFile.size > 0) && (other.nidImg == "")) {
                                nidfinalFileInKB = nidFile.get(0).length() / 1024.0

                                //nid
                                uploadCustomerDoc(
                                    user,
                                    party_code,
                                    "Contractor",
                                    "nid",
                                    nidExtension,
                                    nidFile,
                                    loadingAnim,
                                    "" + "nid"
                                )

                            }
                            else {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Updated",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )



                            }
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_contractor_database)

                        }
                        else if(res.code=="500"){
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
                    loadingAnim.dismiss()
                    binding.btnAddContractor.isEnabled = true
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun uploadProfilePhoto(
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
                                    deletePictureFile(prealPath)

                                    if (nidFile.size > 0) {
                                        //nid
                                        uploadCustomerDoc(
                                            user_id,
                                            party_code,
                                            "Contractor",
                                            "nid",
                                            nidExtension,
                                            nidFile,
                                            loading, "" +
                                                    "nid"
                                        )
                                    }
                                    else if (cardFile.size > 0) {
                                        //vcard
                                        uploadCustomerDoc(
                                            user_id,
                                            party_code,
                                            "Contractor",
                                            "bid",
                                            "jpg",
                                            cardFile,
                                            loading,
                                            "vcard"
                                        )
                                    } else {
                                        loading.dismiss()
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Successful",
                                            "Contractor added successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                        val navController =
                                            requireActivity().findNavController(R.id.nav_host_fragment)
                                        navController.navigate(R.id.nav_contractor_database)
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
                                Log.e("Contractor Add", "Error: " + e.message)
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
                                    deletePictureFile(vrealPath)

                                    if (binding.btnAddContractor.text.toString() == "Update Contractor") {
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Updated",
                                            "Contractor information has been updated successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                    } else {
                                        Utility.getBaseMessage(
                                            requireActivity(),
                                            "Successful",
                                            "New contractor added successfully",
                                            R.drawable.ic_checked_green,
                                            1
                                        )
                                    }
                                    if (cardFile.size == 0) {
                                        val navController =
                                            requireActivity().findNavController(R.id.nav_host_fragment)
                                        navController.navigate(R.id.nav_contractor_database)
                                    } else {

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
                                Log.e("Contractor Add", "Error: " + e.message)
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


    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Camera",
            //   "Select from gallery",
            "Cancel"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            when {
                optionsMenu[i] == "Camera" -> {

//                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(takePicture, PRO_PIC_CAMERA_REQ_CODE)

                    openCameraProfilePic()

                }

                optionsMenu[i] == "Select from gallery" -> {

//                    val pickPhoto =
//                        // Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//                    startActivityForResult(pickPhoto, 1)


                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, PRO_PIC_ATTACH_REQUEST_CODE)
                }

                optionsMenu[i] == "Cancel" -> {
                    dialogInterface.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun setPreviouslySaveValueForUpdate() {


        val operationStatus = Utility.getValueByKey(requireActivity(), "contractor_update")
        engineerGeoInfo = Utility.getValueByKey(requireActivity(), "RetailerSetupInfo").toString()

        if (operationStatus != "") {
            other = convertOtherInfo(requireActivity())!!
        }

        if (operationStatus == "YES" && other.partyCode.toString() != "") {
            //UPDATE
            //Get select item info
            binding.btnAddContractor.text = "Update Contractor"

            party_code = other.partyCode.toString()
            binding.txtConName.setText(other.contactPerson.toString())
            binding.txtPreAddress.setText(other.partyAdd1.toString())
            binding.txtPerAddress.setText(other.permAdd.toString())
            binding.txtNIDNUm.setText(other.nidNo.toString())


            binding.txtMobile1.setText(other.partyMobile.toString())
            binding.txtMobile2.setText(other.partyPhone.toString())



            if (other.partyBdate.toString() != "" && other.partyBdate.toString() != "1900-01-01" && other.partyBdate.toString() != "2999-12-31") {
                binding.txtConDOB.setText(
                    Utility.changeDateFormat(
                        other.partyBdate.toString(),
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    ).toString()
                )
                DOB = other.partyBdate.toString()
            } else {
                binding.txtConDOB.setText("")
            }

            if (other.partyBdate.toString() != "" && other.partyBdate.toString() != "1900-01-01" && other.partyBdate.toString() != "2999-12-31") {
                val calculateAge = Utility.findDifference(
                    Utility.changeDateFormat(
                        other.partyBdate.toString(),
                        "yyyy-MM-dd",
                        "dd-MM-yyyy"
                    ).toString(),
                    Utility.getCurrentDate("dd-MM-yyyy")
                )
                binding.txtConAge.setText(calculateAge.toString())
            } else {
                binding.txtConAge.setText("")
            }

            binding.txtConExperience.setText(other.workExp.toString())
            binding.txtConLabour.setText(other.numberSubord.toString())


            binding.txtConOngoing.setText(other.numberSite.toString())
            binding.txtConCrown.setText(other.numberCrownSite!!.toString())

            //acConProgram

            if (other.partCamp.toString() != "") {

                binding.acConProgram.setText(
                    programList[programList.indexOf(other.partCamp.toString())],
                    false
                )
            }

            if (other.recomBrand1.toString() != "" && brandList.size > 0) {
                val cem1 = cementBrandList[cementBrandList.indexOf(other.recomBrand1.toString())]
                binding.txtConBrand1.setText(cem1)
            }

            if (other.recomBrand2.toString() != "" && brandList.size > 0) {
                val cem2 = cementBrandList[cementBrandList.indexOf(other.recomBrand2.toString())]
                binding.txtConBrand2.setText(cem2)
            }
            if (other.recomBrand3.toString() != "" && brandList.size > 0) {
                val cem3 = cementBrandList[cementBrandList.indexOf(other.recomBrand3.toString())]
                binding.txtConBrand3.setText(cem3)
            }

            binding.conSbp.setText("Party Code : " + other.partyCode)
            binding.createDate.setText("Registration Date : " + other.partyOpenDate)



            if (other.partyImage != "") {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + other.partyImage)
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgCoProfile)
            }
            if (other.busCardImg != "") {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + other.busCardImg)
                    .error(R.drawable.ic_baseline_broken_image)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgConVisitingCard)
            }

            if (other.nidImg != "") {
                binding.imgNidFile.setImageDrawable(null)
                binding.txtNidFileName.text = other.nidImg.toString().split("/").last()
                binding.imgNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                binding.imgNidFile.scaleType = ImageView.ScaleType.FIT_CENTER
            }


        } else {
            //SAVE
            binding.btnAddContractor.text = "Create New Contractor"
        }
    }

    private fun convertOtherInfo(activity: Activity): OtherCustomerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedOther", "")
        return gson.fromJson(json, OtherCustomerChild::class.java)
    }

    private fun loadParticipatedProgram(ac: AutoCompleteTextView) {

        programList = ArrayList()
        programList.add("Select program category")
        programList.add("Contractor Meet")
        programList.add("Safety Awareness")
        programList.add("Crash Program")
        programList.add("Not Attended Yet")

        ac.setText(programList[0])

//        for (item in programList) {
//            if (partyProgramCategory != "" && item == partyProgramCategory) {
//                val pos = programList.indexOf(item)
//                ac.setText(programList[pos])
//                // break
//            }
//        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, programList)
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    private fun setupViewModel() {
        salesVisitViewModel =
            ViewModelProviders.of(
                this.requireActivity(),
                SalesVisitViewModelFactory()
            )[SalesVisitViewModel::class.java]


        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)

    }

    private fun getAllCementBrandList() {
        cementBrandList = ArrayList()

        salesVisitViewModel.getAllCementList().observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {
                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        brandList = ArrayList()
                        brandList = res.data

                        loadAllCementSpinner(binding.txtConBrand1, res.data)
                        loadAllCementSpinner(binding.txtConBrand2, res.data)
                        loadAllCementSpinner(binding.txtConBrand3, res.data)

                        for (item in res.data) {
                            cementBrandList.add(item.Item.toString())
                        }

                        setPreviouslySaveValueForUpdate()

                    }
                }

                Status.ERROR -> {

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadAllCementSpinner(
        ac: AutoCompleteTextView,
        lists: ArrayList<CallRecordSpinnerData>
    ) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Brand")
        for (item in lists) {
            items.add(item.Item.toString())
        }
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    //TODO---------------------------------- IMAGE FILE PROCESSING ---------------------------------------------

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
            "com.crowncement.tecnicalsupport.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(), "currentPhotoPath", profilePhotoPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, PRO_PIC_CAMERA_REQ_CODE)

    }


    private fun openNIDPDFFile() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, ".pdf")
        pdfIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(pdfIntent, NID_ATTACH_REQUEST_CODE)
    }

    //todo for nominees nid
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
            "com.crowncement.tecnicalsupport.fileprovider",
            imageFile
        )
        Utility.storeKeyValue(requireActivity(), "currentPhotoIDPath", currentPhotoIDPath)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 131)
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


    private fun openCameraVisitingCard() {

        val fileName = "photo.jpg"
        val storageDirectory: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        visitingCardPhotoPath = imageFile!!.absolutePath
        Utility.storeKeyValue(
            requireActivity(),
            "currentVisitingCardFilePath",
            visitingCardPhotoPath
        )
        val imageUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.crowncement.tecnicalsupport.fileprovider",
            imageFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, VCARD_PIC_CAMERA_REQ_CODE)
    }

//todo old picture
   /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        try {

            if (resultCode == Activity.RESULT_OK) {
                cardFile = ArrayList()
                nidFile = ArrayList()
                when (requestCode) {

                    PRO_PIC_CAMERA_REQ_CODE -> {

                        if (profilePhotoPath != "") {
                            //val selectedImage = BitmapFactory.decodeFile(profilePhotoPath)
                            profilePhotoPath =
                                Utility.getValueByKey(requireActivity(), "currentPhotoPath")
                                    .toString()
                            selectedImage = BitmapFactory.decodeFile(profilePhotoPath)

                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri = getImageUri(requireContext(), selectedImage!!, imName)
                            uri = tempUri!!
                            Log.e("prealPath", getRealPathFromURI(tempUri)!!)
                            prealPath = getRealPathFromURI(tempUri)!!
                            picFile = File(getRealPathFromURI(tempUri)!!)

                            GlobalScope.launch {

                                // picFile = Compressor.compress(requireContext(), picFile)

                                picFile =
                                    Compressor.compress(requireContext(), picFile) {
                                        resolution(1280, 720)
                                        quality(80)
                                        format(Bitmap.CompressFormat.JPEG)
                                        size(204_800) // 200 KB
                                    }
                            }

                            Glide.with(requireActivity())
                                .load(picFile)
                                //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                .into(binding.imgCoProfile)
                            // binding.imgCoProfile.setImageBitmap(selectedImage)

                            proPicExtension =
                                ImagePathUtils.getRealPath(requireContext(), tempUri).toString()
                                    .split(".").last()
                        } else {

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to capture contractor image",
                                R.drawable.error_white,
                                2
                            )

                        }

                    }

                    PRO_PIC_ATTACH_REQUEST_CODE -> {

                        if (data!!.data != null) {
                            val imagePath: String = data.data!!.path!!
                            val imgUri: Uri = data.data!!

                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {

                                GlobalScope.launch {


                                    picFile =
                                        Compressor.compress(requireContext(), picFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }

                                }
                                picFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                    createFileFromImageUri(requireContext(), imgUri)!!
                                } else {
                                    path.mkdirs()
                                    File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                                }
                                //get file extension
                                proPicExtension =
                                    ImagePathUtils.getRealPath(requireContext(), imgUri).toString()
                                        .split(".").last()
                                //Set image to view
                                val bitmap = BitmapFactory.decodeStream(
                                    requireContext().contentResolver.openInputStream(imgUri)
                                )
                                //  binding.imgCoProfile.setImageBitmap(bitmap)


                                Glide.with(requireActivity())
                                    .load(picFile)
                                    //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                    .into(binding.imgCoProfile)

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }

                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach profile picture",
                                R.drawable.error_white,
                                2
                            )
                        }


                    }

//                todo for attach nid from geraly

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
                                    nuri = imageUri!!
                                    nidSelectionType = "attach"
                                    nidfinalFile = file
                                    GlobalScope.launch {


                                        nidfinalFile =
                                            Compressor.compress(requireContext(), nidfinalFile) {
                                                resolution(1280, 720)
                                                quality(80)
                                                format(Bitmap.CompressFormat.JPEG)
                                                size(204_800) // 200 KB
                                            }

                                    }
                                    Utility.saveFileToSharedPreferences(
                                        requireContext(),
                                        nidfinalFile, "conNidAttach"
                                    )

                                    nidFile.add(nidfinalFile)

                                    nidfinalFileInKB = nidFile.get(0).length() / 1024.0
                                    // nidfinalFile.length() / 1024.0

                                    nidExtension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $nidExtension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                                    // setImage(file, binding.imgNidFile)
                                    Glide.with(requireActivity())
                                        .load(nidfinalFile)
                                        //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                        .into(binding.imgNidFile)

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
                                nuri = imgUri!!
                                nidSelectionType == "attach"
                                nidfinalFile = file
                                GlobalScope.launch {

                                    //     nidfinalFile = Compressor.compress(requireContext(), nidfinalFile)

                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                }
                                Utility.saveFileToSharedPreferences(
                                    requireContext(),
                                    nidfinalFile, "conNidAttach"
                                )
                                nidFile.add(nidfinalFile)
                                nidfinalFileInKB = nidFile.get(0).length() / 1024.0


                                nidExtension = "jpg"
                                //Set image to view

                                Glide.with(requireActivity())
                                    .load(nidfinalFile)
                                    //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                    .into(binding.imgNidFile)

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

                    /*
                    NID_ATTACH_REQUEST_CODE -> {

                        if (data!!.data != null) {
                            val uri: Uri = data!!.data!!
                            val uriString = uri.toString()
                            val myFile = FileUtility().from(requireContext().applicationContext, uri)

                            //Get File name
                            var displayName: String? = null
                            if (uriString.startsWith("content://")) {
                                var cursor: Cursor? = null
                                try {
                                    cursor =
                                        requireActivity().contentResolver.query(uri, null, null, null, null)
                                    if (cursor != null && cursor.moveToFirst()) {

                                        // displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                        displayName = cursor.getString(with(cursor) {
                                            getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                        })
                                    }
                                } finally {
                                    cursor!!.close()
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile!!.name
                            }
                            //  nidFile = myFile!!
                            nidFile.add(myFile!!)
                            binding.imgNidFile.setImageDrawable(null)

                            binding.txtNidFileName.text = displayName

                            binding.imgNidFile.setBackgroundResource(R.drawable.ic_attached_pdf)
                            binding.imgNidFile.scaleType = ImageView.ScaleType.FIT_CENTER

                        } else {

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Failed to attach NID file",
                                R.drawable.error_white,
                                2
                            )
                        }

                    }

                     */

                    VCARD_PIC_CAMERA_REQ_CODE -> {

                        if (visitingCardPhotoPath != "") {
                            visitingCardPhotoPath =
                                Utility.getValueByKey(
                                    requireActivity(),
                                    "currentVisitingCardFilePath"
                                )
                                    .toString()
                            vselectedImage = BitmapFactory.decodeFile(visitingCardPhotoPath)
                            //val selectedImage = BitmapFactory.decodeFile(visitingCardPhotoPath)
                            val emp =
                                Utility.getValueByKey(requireActivity(), "username").toString()
                            val imName = emp + Utility.getCurrentDate("yyyyMMdd")
                            val tempUri = getImageUri(requireContext(), vselectedImage!!, imName)
                            Log.e("vrealPath", getRealPathFromURI(tempUri)!!)
                            vuri = tempUri!!
                            vrealPath = getRealPathFromURI(tempUri)!!

                            vCardFile = File(getRealPathFromURI(tempUri)!!)

                            GlobalScope.launch {

                                //  vCardFile = Compressor.compress(requireContext(), vCardFile)

                                vCardFile =
                                    Compressor.compress(requireContext(), vCardFile) {
                                        resolution(1280, 720)
                                        quality(80)
                                        format(Bitmap.CompressFormat.JPEG)
                                        size(204_800) // 200 KB
                                    }
                            }

                            cardFile.add(vCardFile)
                            Glide.with(requireActivity())
                                .load(vselectedImage)
                                //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                .into(binding.imgConVisitingCard)
                            //  binding.imgConVisitingCard.setImageBitmap(selectedImage)
                            proPicExtension =
                                ImagePathUtils.getRealPath(requireContext(), tempUri).toString()
                                    .split(".").last()
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                "Sorry, failed to process visiting card",
                                R.drawable.error_white,
                                2
                            )
                        }
                    }

                    else -> {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Failed",
                            "Sorry, Failed to process data",
                            R.drawable.error_white,
                            2
                        )
                    }
                }


            } else {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Sorry, failed to selection document",
                    R.drawable.error_white,
                    2
                )
            }

        } catch (e: Exception) {
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

    private fun getImageUri(inContext: Context, inImage: Bitmap, imageName: String): Uri? {

        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            // inImage,
            getResizedBitmap(inImage, 800),
            imageName,
            "Contractor"
        )
        return Uri.parse(path)
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


    //TODO-----------------------------------IMAGE FILE PROCESSING END------------------------------------------


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


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            permissionCode
        )
    }


    //todo img delete
    private fun deletePictureFile(path: String): Boolean {
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



    ///////////////////////////////////////////////////////////////////////////////

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
                            "com.crowncement.tecnicalsupport.fileprovider",
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

            }else if(requestCode==VCARD_PIC_CAMERA_REQ_CODE){
             //   currentPhotoPath = absolutePath
                visitingCardPhotoPath = absolutePath
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

                    PRO_PIC_CAMERA_REQ_CODE -> {


                        val imgFile = compressImage(currentPhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            picFile = imgFile
                        }

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.imgCoProfile.setImageBitmap(bitmap)


                        if (latitude == 0.0) {
                            //  latitude = getLastKnownLocation()!!.latitude
                            //  longitude = getLastKnownLocation()!!.longitude
                            getLastLocation()

                            //  getBaseMessage(requireActivity(),"","LOCATION",R.drawable.ic_baseline_error_orange,0)
                        }

                    }

/*
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
                                    nuri = imageUri!!
                                    nidSelectionType = "attach"
                                    nidfinalFile = file
                                    

                                    var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                    val imgFile = compressImage(imguri.toString())

/*
                                    GlobalScope.launch {


                                        nidfinalFile =
                                            Compressor.compress(requireContext(), nidfinalFile) {
                                                resolution(1280, 720)
                                                quality(80)
                                                format(Bitmap.CompressFormat.JPEG)
                                                size(204_800) // 200 KB
                                            }

                                    }

 */

                                    if (imgFile.exists()) {
                                        val imageSize = (imgFile.length() / 1024.0)
                                        // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                                        //  finalFile.add(imgFile)
                                        nidfinalFile = imgFile
                                    }
                                    Utility.saveFileToSharedPreferences(
                                        requireContext(),
                                        nidfinalFile,
                                        "conNidAttach"
                                    )

                                    nidFile.add(nidfinalFile)

                                    nidfinalFileInKB = nidFile.get(0).length() / 1024.0
                                    // nidfinalFile.length() / 1024.0

                                    nidExtension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $nidExtension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?

                                    // setImage(file, binding.imgNidFile)
                                    Glide.with(requireActivity())
                                        .load(nidfinalFile)
                                        //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                        .into(binding.imgNidFile)

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
                                nuri = imgUri!!
                                nidSelectionType == "attach"
                               // nidfinalFile = file
                                /*
                                GlobalScope.launch {

                                    //     nidfinalFile = Compressor.compress(requireContext(), nidfinalFile)

                                    nidfinalFile =
                                        Compressor.compress(requireContext(), nidfinalFile) {
                                            resolution(1280, 720)
                                            quality(80)
                                            format(Bitmap.CompressFormat.JPEG)
                                            size(204_800) // 200 KB
                                        }
                                }

                                 */

                                val imgFile = compressImage(imgUri.toString())
                                nidfinalFile = imgFile

                                Utility.saveFileToSharedPreferences(
                                    requireContext(),
                                    nidfinalFile, "conNidAttach"
                                )
                                nidFile.add(nidfinalFile)
                                nidfinalFileInKB = nidFile.get(0).length() / 1024.0


                                nidExtension = "jpg"
                                //Set image to view

                                Glide.with(requireActivity())
                                    .load(nidfinalFile)
                                    //  .override(reqWidth, reqHeight) // Resize to desired dimensions
                                    .into(binding.imgNidFile)

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

                                    nuri = imageUri!!
                                    nidfinalFile = file

                                    nidfinalFile = compressImage(nidfinalFile.absolutePath)
                                    nidfinalFileInKB =
                                        nidfinalFile.length().toDouble() / (1024 * 1024)
                                    Toast.makeText(requireContext(),
                                        nidfinalFileInKB.toString(), Toast.LENGTH_SHORT).show()
                                    nidSelectionType = "attach"

                                 //   val imgFile = compressImage(currentPhotoPath)

                                  //  var imguri = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                  //  val imgFile = compressImage(imguri.toString())


                                   // nidfinalFile = imgFile

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
                                        nidfinalFile,"NidAttach")
                                    nidFile.add(nidfinalFile)


                                    Glide
                                        .with(this)
                                        .load(nidfinalFile)
                                        // .error(R.drawable.default_user_image)
                                        .fitCenter()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(binding.imgNidFile)
                                    nidExtension =
                                        ImagePathUtils.getRealPath(requireContext(), imageUri)
                                            .toString().split(".")[1]
                                    Log.e("extension", "onActivityResult: $nidExtension")
                                    // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                    // setImage(file, binding.imgMNomNidFile)
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
                                nuri = imgUri!!

                                nidFile.add(file)
                                nidfinalFile = file
                              //  nidfinalFileInKB = nidfinalFile.length().toDouble() / (1024 * 1024)

                                nidfinalFile=  compressImage(nidfinalFile.absolutePath)
                                nidfinalFileInKB = nidfinalFile.length().toDouble() / (1024 * 1024)
                                nidSelectionType = "attach"
                                Toast.makeText(requireContext(),
                                    nidfinalFileInKB.toString(), Toast.LENGTH_SHORT).show()
                                Utility.saveFileToSharedPreferences(requireContext(),
                                    nidfinalFile,"nomNidAttach")
                                nidFile.add(nidfinalFile)
                                nidExtension = ImagePathUtils.getRealPath(requireContext(), imgUri)
                                    .toString().split(".")[1]


                                Glide
                                    .with(this)
                                    .load(nidfinalFile)
                                    // .error(R.drawable.default_user_image)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.imgNidFile)
//


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





                    VCARD_PIC_CAMERA_REQ_CODE -> {


                        val imgFile = compressImage(visitingCardPhotoPath)

                        if (imgFile.exists()) {
                            val imageSize = (imgFile.length() / 1024.0)
                            // Toast.makeText(requireActivity(), imageSize.toString(), Toast.LENGTH_LONG).show()
                            //  finalFile.add(imgFile)
                            vCardFile = imgFile
                        }
                        cardFile.add(vCardFile)

                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.imgConVisitingCard.setImageBitmap(bitmap)




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



}