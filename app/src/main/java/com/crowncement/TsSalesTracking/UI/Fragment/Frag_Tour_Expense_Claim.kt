package com.crowncement.TsSalesTracking.UI.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.CommonViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.CommonViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.FileUtility
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.TourResponseChild
import com.crowncement.TsSalesTracking.data.Model.TypeResponseChild
import com.crowncement.TsSalesTracking.databinding.FragTourExpenseClaimBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

import java.io.*
import java.util.*


class Frag_Tour_Expense_Claim : Fragment() {

   // lateinit var rootView: View

    private var _binding: FragTourExpenseClaimBinding? = null
    private val binding get() = _binding!!

    private lateinit var pdfUri: Uri
    lateinit var expenseType: ArrayList<String>
    lateinit var accoType: ArrayList<String>
    lateinit var modeOfTransport: ArrayList<String>
    lateinit var mealType: ArrayList<String>
    lateinit var paymentMode: ArrayList<String>

    lateinit var subTypeList: ArrayList<String>

    var selectedExpenseType = ""
    var selectedAccoType = ""
    var selecteedFoodType = ""
    var selectedModeOfTransport = ""
    var selectedPaymentMode = ""
    var selectedSubType = ""

    private val IMAGE_DIRECTORY: String = "/demonuts_upload_gallery"
    val REQUEST_CODE = 200
    val BUFFER_SIZE = 1024 * 2

    // lateinit var file: File
    var images: ArrayList<File> = ArrayList()

    lateinit var tourViewModel: TourViewModel
    lateinit var commonViewModel: CommonViewModel
    lateinit var loadingAnim: Dialog

    var selectedDate = ""
    var extension = ""

    lateinit var travelInfo: TourResponseChild

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

     //   rootView = inflater.inflate(R.layout.frag_tour_expense_claim, container, false)
        _binding = FragTourExpenseClaimBinding.inflate(inflater, container, false)
/*
      //  checkFilerPermission()

        TourViewModelFactory()
        CommonViewModelFactory()
        setupViewModel()
        getPreviousFragmentValue()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireContext()),
            "P l e a s e    w a i t"
        )

        getDataByType("Travel Expense Type")
        getDataByType("Mode of Expense Payment")



        onClickEventListener()
*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        TourViewModelFactory()
        CommonViewModelFactory()
        setupViewModel()
        getPreviousFragmentValue()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireContext()),
            "P l e a s e    w a i t"
        )

        getDataByType("Travel Expense Type")
        getDataByType("Mode of Expense Payment")



        onClickEventListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun checkFilerPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", "com.crowncement.salesforceapp", null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }


    private fun getPreviousFragmentValue() {
        val selectedExpenseInfo = Utility.getValueByKey(requireActivity(), "selectedExpenseInfo")
        val gson = Gson()

        selectedDate = Utility.getValueByKey(requireActivity(), "selectedDate").toString()

        binding.txtDateExpen.setText(
            Utility.changeDateFormat(
                selectedDate,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ).toString()
        )

        if (selectedExpenseInfo.toString().isNotEmpty()) {
            travelInfo = gson.fromJson(selectedExpenseInfo, TourResponseChild::class.java)

        }
    }

    private fun onClickEventListener() {

        binding.acTExpense.setOnItemClickListener { parent, arg1, position, arg3 ->

            selectedExpenseType = ""
            selectedSubType = ""
            selectedAccoType = ""
            selecteedFoodType = ""
            selectedModeOfTransport = ""
            selectedPaymentMode = ""
            binding.txtFrom.setText("")
            binding.txtTo.setText("")
            binding.txtAmountT.setText("")
            binding.acPaymentMode.setText("")
            //loadPaymentMode(rootView.acPaymentMode,paymentMode)
            getSubDataByType("Travel Expense Sub-Type",parent.getItemAtPosition(position).toString())
            getDataByType("Mode of Transport")

            when {
                position > 0 && position == 1 -> {
                    //Accommodation
                    selectedExpenseType = parent.getItemAtPosition(position).toString()
                   // rootView.layoutAcoType.visibility = View.VISIBLE
                  //  loadAccommodationType(rootView.acACType)

                    binding.layoutFrom.visibility = View.GONE
                    binding.layoutTo.visibility = View.GONE
                    binding.layoutModeOfTransport.visibility = View.GONE
                    binding.layoutFood.visibility = View.GONE
                }

                position > 0 && position == 2 -> {
                    //Conveyance
                    selectedExpenseType = parent.getItemAtPosition(position).toString()
                    binding.layoutFrom.visibility = View.VISIBLE
                    binding.layoutTo.visibility = View.VISIBLE
                    binding.layoutModeOfTransport.visibility = View.VISIBLE
                   // loadTransportType(rootView.acModeOfTransport,modeOfTransport )

                    binding.layoutAcoType.visibility = View.GONE
                    binding.layoutFood.visibility = View.GONE

                }

                position > 0 && position == 3 -> {
                    //Daily Allowance
                    selectedExpenseType = parent.getItemAtPosition(position).toString()
                  //  rootView.layoutFood.visibility = View.VISIBLE
                  //  loadFoodType(rootView.acFoodCategory)

                    binding.layoutFrom.visibility = View.GONE
                    binding.layoutTo.visibility = View.GONE
                    binding.layoutModeOfTransport.visibility = View.GONE
                    binding.layoutAcoType.visibility = View.GONE
                    binding.layoutFood.visibility = View.GONE

                }
                position > 0 && position == 4 -> {
                    selectedExpenseType = parent.getItemAtPosition(position).toString()
                    binding.layoutAcoType.visibility = View.GONE
                    binding.layoutFrom.visibility = View.GONE
                    binding.layoutTo.visibility = View.GONE
                    binding.layoutModeOfTransport.visibility = View.GONE
                    binding.layoutFood.visibility = View.GONE
                }
            }
        }

        binding.acACType.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedAccoType = parent.getItemAtPosition(position).toString()
            } else {
                selectedAccoType = ""
            }
        }

        binding.acFoodCategory.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selecteedFoodType = parent.getItemAtPosition(position).toString()
            } else {
                selecteedFoodType = ""
            }
        }

        binding.acModeOfTransport.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedModeOfTransport = parent.getItemAtPosition(position).toString()
            } else {
                selectedModeOfTransport = ""
            }
        }

        binding.acPaymentMode.setOnItemClickListener { parent, arg1, position, arg3 ->
            if (position > 0) {
                selectedPaymentMode = parent.getItemAtPosition(position).toString()
            } else {
                selectedPaymentMode = ""
            }
        }


        binding.acSubType.setOnItemClickListener { parent, arg1, position, arg3 ->

            selectedSubType = ""
            selectedSubType = if (position > 0) {
                parent.getItemAtPosition(position).toString()
            } else {
                ""
            }
        }

        binding.imgAddExpenseImages.setOnClickListener {
            openGalleryForImages()
        }

        binding.adPDF.setOnClickListener {
            openPDFFile()
        }

        binding.btnExpenseSubmit.setOnClickListener {
            if (validateUI()) {

                val message =
                    "You are going to submit BDT ${binding.txtAmountT.text.toString()}/- Taka on purpose of ${binding.acTExpense.text}"
                confirmBottomSheet(message)
            }
        }
    }

    private fun getDataByType(
        code: String
    ) {
        commonViewModel.getDataByType(code)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            when {
                                res.code == "200" && code=="Travel Expense Type" && res.data.size>0 -> {
                                    expenseType = ArrayList()
                                    loadExpenseType(binding.acTExpense, res.data)
                                }

                                res.code=="200" && code=="Mode of Expense Payment" && res.data.size>0 -> {
                                    paymentMode = ArrayList()
                                    loadPaymentMode(binding.acPaymentMode,res.data)
                                }

                                res.code=="200" && code=="Mode of Transport" && res.data.size >0 -> {
                                    modeOfTransport = ArrayList()
                                    loadTransportType(binding.acModeOfTransport,res.data)
                                }

                            }

                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun getSubDataByType(code: String,subTypeName:String) {
        commonViewModel.getSubDataByType(code,subTypeName)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if(res.code=="200" && res.data.size>0){
                                subTypeList = ArrayList()
                                subTypeList.add("Select $subTypeName Type")

                                loadSubType(binding.acSubType,res.data)
                            }


                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun openPDFFile() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        //    pdfIntent.type = "*/*"
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)


        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, ".pdf")
        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        pdfIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)


        startActivityForResult(pdfIntent, 12)
    }

    private fun validateUI(): Boolean {

        var res: Boolean = false
        binding.layoutTExpenseType.isErrorEnabled = false
        binding.layoutTExpenseType.error = ""


        binding.layoutAcoType.isErrorEnabled = false
        binding.layoutAcoType.error = ""

        binding.layoutModeOfTransport.isErrorEnabled = false
        binding.layoutModeOfTransport.error = ""

        binding.layoutFood.isErrorEnabled = false
        binding.layoutFood.error = ""

        binding.layoutFrom.isErrorEnabled = false
        binding.layoutTo.isErrorEnabled = false
        binding.layoutFrom.error = ""
        binding.layoutTo.error = ""

        binding.layoutPaymentMode.error = ""

        binding.layoutAmount.isErrorEnabled = false
        binding.layoutAmount.error = ""

        binding.layoutAcSubType.isErrorEnabled = false
        binding.layoutAcSubType.error = ""

        val f = binding.txtFrom.text.toString()
        val t = binding.txtTo.text.toString()

        when {
            selectedExpenseType == ("") -> {
                res = false
                binding.layoutTExpenseType.isErrorEnabled = true
                binding.layoutTExpenseType.error = "Select any expense type"
            }
            selectedSubType == ("") -> {
                res = false
                binding.layoutAcSubType.isErrorEnabled = true
                binding.layoutAcSubType.error = "Select $selectedExpenseType type"
            }

//            selectedExpenseType == "Accommodation" && selectedAccoType == ("") -> {
//                res = false
//                rootView.layoutAcoType.isErrorEnabled = true
//                rootView.layoutAcoType.error = "Select any accommodation type"
//            }

            (selectedExpenseType == ("Conveyance")) && (selectedModeOfTransport == "") -> {
                res = false
                binding.layoutModeOfTransport.isErrorEnabled = true
                binding.layoutModeOfTransport.error = "Select any transport type"

            }

            (selectedExpenseType == ("Conveyance")) && (f == "") -> {
                res = false
                binding.layoutFrom.isErrorEnabled = true
                binding.layoutFrom.error = "From where you started?"
            }
            (selectedExpenseType == ("Conveyance")) && (t == "") -> {
                res = false
                binding.layoutTo.error = "Where you stopped?"
                binding.layoutTo.isErrorEnabled = true
            }

//            selectedExpenseType == ("Food") && selecteedFoodType == ("") -> {
//                res = false
//                rootView.layoutFood.isErrorEnabled = true
//                rootView.layoutFood.error = "Select any food type"
//            }

            selectedPaymentMode == ("") -> {
                res = false
                binding.layoutPaymentMode.isErrorEnabled = true
                binding.layoutPaymentMode.error = "Select any payment type"
            }

            binding.txtAmountT.text.toString().isEmpty() -> {
                res = false
                binding.layoutAmount.isErrorEnabled = true
                binding.layoutAmount.error = "Provide valid amount"
            }
            else -> {
                res = true
            }
        }

        return res
    }

    private fun loadExpenseType(
        ac: AutoCompleteTextView,
        expenseList: ArrayList<TypeResponseChild>
    ) {

        expenseType.add("Select Expense Type")

        for (item in expenseList) {
            expenseType.add(item.CodeNo.toString())
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            expenseType
        )
        ac.setText(expenseType[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun loadSubType(
        ac: AutoCompleteTextView,
        subList: ArrayList<TypeResponseChild>
    ) {

        for (item in subList) {
            subTypeList.add(item.CodeNo.toString())
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            subTypeList
        )
        ac.setText(subTypeList[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun loadAccommodationType(ac: AutoCompleteTextView) {
        accoType = ArrayList()
        accoType.add("Select Accommodation")
        accoType.add("Hotel")
        accoType.add("Paying Guest")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            accoType
        )
        ac.setText(accoType[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun loadTransportType(ac: AutoCompleteTextView,trasnportType:ArrayList<TypeResponseChild>) {

        modeOfTransport.add("Select Mode of Transport")
//        modeOfTransport.add("Ac Bus")
//        modeOfTransport.add("Non Ac Bus")
//        modeOfTransport.add("Airways")
//        modeOfTransport.add("CNG")
//        modeOfTransport.add("Motor Cycle")
//        modeOfTransport.add("Rickshaw")
//        modeOfTransport.add("Taxi")
//        modeOfTransport.add("Train")

        for(item in trasnportType){
            modeOfTransport.add(item.CodeNo.toString())
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            modeOfTransport
        )
        ac.setText(modeOfTransport[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun loadFoodType(ac: AutoCompleteTextView) {

        mealType = ArrayList()
        mealType.add("Select Food Type")
        mealType.add("Breakfast")
        mealType.add("Lunch")
        mealType.add("Dinner")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mealType
        )
        ac.setText(mealType[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun loadPaymentMode(ac: AutoCompleteTextView,paymentType:ArrayList<TypeResponseChild>) {


        paymentMode.add("Select Payment Mode")
//        paymentMode.add("Cash")
//        paymentMode.add("Cheque")
//        paymentMode.add("Online Transfer")
//        paymentMode.add("Pay-Order")
//        paymentMode.add("Credit Card")
//        paymentMode.add("Gift Voucher")
        for (item in paymentType){
            paymentMode.add(item.CodeNo.toString())
        }


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            paymentMode
        )
        ac.setText(paymentMode[0])
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    // TODo-------------------------------------------------------------------------->Document selection

    private fun openGalleryForImages() {

        if (SDK_INT < 19) {
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

    @SuppressLint("Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        images = ArrayList()

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && null != data) {

            when (requestCode) {
                200 -> {

                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount

                        for (i in 0 until count) {
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri

                            val path: File =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            try {
                                path.mkdirs()
                                val file =
                                    File(ImagePathUtils.getRealPath(requireContext(), imageUri)!!)
                                images.add(file)
                                extension = ImagePathUtils.getRealPath(requireContext(), imageUri)
                                    .toString().split(".")[1]
                                Log.e("extension", "onActivityResult: $extension")
                                // val selectedImage =   data.clipData!!.getItemAt(i).uri as Bitmap?
                                setImage(file, binding.attachedDoc)
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                    }

//                    else if (data.data != null) {
//                        val imagePath: String = data.data!!.path!!
//                        Log.e("imagePath", imagePath)
//                        val imgUri: Uri = data.data!!
//                        val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                        try {
//                            path.mkdirs()
//                            val file = File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
//                            images.add(file)
//                            setImage(file, rootView.attachedDoc)
//                            // For whats app size becomes 3 else size 2
//                            val splitSize:Int=ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").count()
//                            extension = if(splitSize>2){
//                                ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".")[2]
//                            } else{
//                                ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".")[1]
//                            }
//                            Log.e("extension", "onActivityResult: $extension")
//
//                        } catch (e: Exception) {
//                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//                        }
//                    }

                   else if (data.data != null) {
                        val imagePath: String = data.data!!.path!!
                        val imgUri: Uri = data.data!!


                        val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        try {

                          val  file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                createFileFromImageUri(requireContext(),imgUri)!!
                            } else{
                                path.mkdirs()
                                File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                            }
                            images.add(file)
//
                            //get file extension
                           // extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                            extension ="jpg"
                            //Set image to view
                            val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(imgUri))
                            binding.attachedDoc.setImageBitmap(bitmap)

                        }

                        catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    else {
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

        } else if (resultCode == RESULT_OK) {

            when (requestCode) {
                12 -> {

                    if (resultCode == RESULT_OK) {
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

                                    displayName =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                }
                            } finally {
                                cursor!!.close()
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile!!.name
                        }
                        images.add(myFile!!)
                        extension = "pdf"
                        binding.txtFileName.text = displayName
                        binding.attachedDoc.setBackgroundResource(R.drawable.ic_attached_pdf)
                        binding.attachedDoc.scaleType = ImageView.ScaleType.FIT_CENTER
                    } else {
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Selection Failed",
                            "Sorry, you didn't select any document",
                            R.drawable.error_white,
                            2
                        )
                    }
                }
            }
        } else {
            Utility.getBaseMessage(
                requireActivity(),
                "Failed",
                "Sorry, failed to select image/document",
                R.drawable.error_white,
                2
            )
        }
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
        val storageDir = context.cacheDir // Use cache directory or external storage depending on your needs
        return File.createTempFile(tempFileName, null, storageDir)
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            requireActivity().contentResolver.query(uri!!, projection, null, null, null)
                ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    private fun setImage(imgFile: File, v: ImageView) {
        v.setImageBitmap(null)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            v.setImageBitmap(myBitmap)
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

// TODO---------------------------------------------------------------------->Document Selection End

    private fun setupViewModel() {
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory()).get(
            TourViewModel::class.java
        )
        commonViewModel = ViewModelProviders.of(
            this.requireActivity(),
            CommonViewModelFactory()
        )[CommonViewModel::class.java]
    }

    private fun saveExpenseClaim(
        user_id: String,
        tr_trn_no: String,
        tr_date: String,
        tr_exp_type: String,
        tr_exp_sub: String,
        tr_exp_source: String,
        tr_frm_loc: String,
        tr_to_loc: String,
        tr_pay_mod: String,
        tr_amt: String,
        tr_rem: String,
        extension: String,
        all_images: ArrayList<File>
    ) {
        loadingAnim.show()
        tourViewModel.saveCommonExpenseClaim(
            user_id,
            tr_trn_no,
            tr_date,
            tr_exp_type,
            tr_exp_sub,
            tr_exp_source,
            tr_frm_loc,
            tr_to_loc,
            tr_pay_mod,
            tr_amt,
            tr_rem,
            extension,
            all_images
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successfully",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Error",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
                                )
                            }
                            loadingAnim.dismiss()
                            val navController =
                                requireActivity().findNavController(R.id.nav_host_fragment)
                            // navController.navigate(R.id.nav_travel_list)
                            navController.navigate(R.id.nav_tour_expense_list)
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        Log.e("ERROR", "saveExpenseClaim: ${it.message.toString()}")
                    }
                }
            }
    }

    private fun confirmBottomSheet(message: String) {
        val v: View = layoutInflater.inflate(R.layout.confirm_expense_claim, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnExpenseNo: MaterialButton = v.findViewById(R.id.btnExpenseNo)
        val btnExpenseYes: MaterialButton = v.findViewById(R.id.btnExpenseYes)
        val txtExpenseCancel: TextView = v.findViewById(R.id.txtExpenseCancel)

        val txtExpenseMessage: TextView = v.findViewById(R.id.txtExpenseMessage)

        txtExpenseMessage.text = message
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        btnExpenseNo.setOnClickListener {
            dialog.hide()
        }

        txtExpenseCancel.setOnClickListener {
            dialog.hide()
        }

        btnExpenseYes.setOnClickListener {
            dialog.hide()
            val user = Utility.getValueByKey(requireActivity(), "username").toString()
            loadingAnim.show()

            saveExpenseClaim(
                user,
                travelInfo.trTrno.toString(),
                selectedDate,
                selectedExpenseType,
                selectedModeOfTransport,
                selectedSubType,
                binding.txtFrom.text.toString(),
                binding.txtTo.text.toString(),
                selectedPaymentMode,
                binding.txtAmountT.text.toString(),
                binding.txtRemark.text.toString(),
                extension,
                images
            )
        }
    }

//-------------------------------------------------------------------------------------------------------------------

///sdcard/Download/Bluetooth/Remote Friendly Companies.pdf

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
