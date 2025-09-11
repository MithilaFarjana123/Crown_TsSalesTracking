package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesCollectionViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesCollectionViewModelFactory
import com.crowncement.TsSalesTracking.common.ImagePathUtils
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.formatAmount
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.TypeResponseChild
import com.crowncement.TsSalesTracking.databinding.FragPaymenCollectionSaveBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Frag_Paymen_Collection_Save : Fragment() {


 //   lateinit var rootView: View
    private var _binding: FragPaymenCollectionSaveBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog
    lateinit var paymentTypes: ArrayList<String>
    lateinit var bankList: ArrayList<String>
    lateinit var branchList: ArrayList<String>
    lateinit var picker: DatePickerDialog
    lateinit var fromDate: String
    lateinit var selectedBank: String
    lateinit var selectedBranch: String
    lateinit var selectedPayType: String
    lateinit var companyName: String

    lateinit var accountNo: String
    lateinit var routeNo: String
    lateinit var dealerCode: String
    lateinit var payTo: String

    lateinit var dealer: DealerChild

    lateinit var salesCollectionViewModel: SalesCollectionViewModel
    var paymentTypeList: ArrayList<TypeResponseChild> = ArrayList()

    val REQUEST_CODE = 200
    lateinit var file: File
    lateinit var extension: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

     //   rootView = inflater.inflate(R.layout.frag_paymen_collection_save, container, false)

        _binding = FragPaymenCollectionSaveBinding.inflate(inflater, container, false)

/*

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        fromDate = ""
        selectedBank = ""
        selectedBranch = ""
        selectedPayType = ""
        companyName = ""
        accountNo = ""
        routeNo = ""
        dealerCode = ""
        payTo = ""
        extension = ""

//      TODO---> View model setup
        SalesCollectionViewModelFactory()
        setupViewModel()

//      TODO--->Get Selected dealer basic info
        dealer = convertDealerInfo(requireActivity())!!

        binding.txtPaymentDate.setText(Utility.getCurrentDate("dd MMM yyyy").toString())
        fromDate = Utility.getCurrentDate("yyyy-MM-dd").toString()

        getBankList()

        onClickEventListener()
*/
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        fromDate = ""
        selectedBank = ""
        selectedBranch = ""
        selectedPayType = ""
        companyName = ""
        accountNo = ""
        routeNo = ""
        dealerCode = ""
        payTo = ""
        extension = ""

//      TODO---> View model setup
        SalesCollectionViewModelFactory()
        setupViewModel()

//      TODO--->Get Selected dealer basic info
        dealer = convertDealerInfo(requireActivity())!!

        binding.txtPaymentDate.setText(Utility.getCurrentDate("dd MMM yyyy").toString())
        fromDate = Utility.getCurrentDate("yyyy-MM-dd").toString()

        getBankList()

        onClickEventListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    private fun onClickEventListener() {

        binding.txtPaymentDate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    fromDate = selectedDate
                    binding.txtPaymentDate.setText(
                        Utility.changeDateFormat(
                            selectedDate, "yyyy-MM-dd", "dd MMM yyyy"
                        )
                    )
                },
                year,
                month,
                day
            )
            picker.show()
        }

        binding.acBankName.setOnItemClickListener { parent, arg1, position, arg3 ->
            clearErrorSign()
            if (position > 0) {

                selectedBank = parent.getItemAtPosition(position).toString()
                setBankImage(selectedBank)

                getBranchList(selectedBank)
            } else {
                selectedBank = ""
                binding.txtCustomerName.setText("")
                binding.txtPaymentTo.setText("")
                binding.txtRoutingNo.setText("")
                binding.txtAccountNo.setText("")
            }
        }

        binding.acBranchName.setOnItemClickListener { parent, arg1, position, arg3 ->
            clearErrorSign()
            if (position > 0) {
                selectedBranch = parent.getItemAtPosition(position).toString()
                getBranchDetails(selectedBank, selectedBranch)
            } else {
                selectedBranch = ""
                binding.txtCustomerName.setText("")
                binding.txtPaymentTo.setText("")
                binding.txtRoutingNo.setText("")
                binding.txtAccountNo.setText("")
            }
        }

        binding.imgAddImage.setOnClickListener {
            openGalleryForImages()
        }

        binding.acPayType.setOnItemClickListener { parent, arg1, position, arg3 ->
            clearErrorSign()
            if (position > 0) {
                selectedPayType = parent.getItemAtPosition(position).toString()
            } else {
                selectedPayType = ""
            }
        }

        binding.btnCollectionSave.setOnClickListener {
            if (uiValidation()) {
                confirmBottomSheet(
                    "On ${binding.txtPaymentDate.text.toString()}, you are going to pay \n BDT ${
                        formatAmount(
                            binding.txtAmount.text.toString().toDouble()
                        )
                    } /- \n from  ${dealer.dealerName}  to " +
                            "$companyName ,through $selectedBank , $selectedBranch branch \n\n Are you sure you want to submit this sales collection?"
                )
            }
        }
    }


    private fun setBankImage(bankName: String) {

        val bankImgUrl = Utility.getBankImage(bankName)

        if (bankImgUrl.isNotEmpty()) {

            Glide
                .with(this)
                .load(bankImgUrl)
                .error(R.drawable.crown_cement_icon_gray)
                .centerInside()
                .placeholder(R.drawable.crown_cement_icon_gray)
                .into(binding.imgBankImage)
        }
    }

    private fun setupViewModel() {
        salesCollectionViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesCollectionViewModelFactory())
                .get(SalesCollectionViewModel::class.java)
    }

    private fun loadPaymentType(
        acAutocom: AutoCompleteTextView?,
        payType: ArrayList<TypeResponseChild>
    ) {
        paymentTypes = ArrayList()
        paymentTypes.add("Select Payment Type ")
        //  paymentTypes.add("Cash")
        //  paymentTypes.add("Online deposit")
        // paymentTypes.add("Cheque")
        //  paymentTypes.add("Pay Order")
        //  paymentTypes.add("Document Draft (DD)")

        for (item in payType) {
            paymentTypes.add(item.CodeNo.toString())
        }

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, paymentTypes
        )
        acAutocom!!.setText(paymentTypes[0])
        acAutocom.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getBankList() {
        salesCollectionViewModel.getBankList().observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        bankList = ArrayList()
                        bankList.add("Select Bank")
                        for (items in res.data) {
                            bankList.add(items.Bank.toString())
                        }

                        loadBank(binding.acBankName)

                        getPaymentType("Mode of Payment")

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

    private fun loadBank(acAutocom: AutoCompleteTextView) {
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, bankList
        )
        acAutocom.setText(bankList[0])
        acAutocom.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getBranchList(bankCode: String) {
        salesCollectionViewModel.getBranchList(bankCode).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        branchList = ArrayList()
                        branchList.add("Select Branch")
                        for (items in res.data) {
                            branchList.add(items.Branch.toString())
                        }

                        loadBranch(binding.acBranchName)
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

    private fun loadBranch(acAutocom: AutoCompleteTextView) {
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, branchList
        )
        acAutocom.setText(branchList[0])
        acAutocom.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun getBranchDetails(bankCode: String, branchCode: String) {
        salesCollectionViewModel.getBranchDetails(bankCode, branchCode).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        dealerCode = dealer.dealerCode.toString()
                        payTo = res.data[0].CompanyName.toString()
                        binding.txtCustomerName.setText(dealer.dealerName.toString())

                        companyName = res.data[0].CompanyName!!
                        binding.txtPaymentTo.setText(companyName)

                        routeNo = res.data[0].RoutingNo.toString()
                        binding.txtRoutingNo.setText(routeNo)

                        accountNo = res.data[0].AccountNo!!
                        binding.txtAccountNo.setText(accountNo)

                        if (paymentTypeList.size > 0) {
                            loadPaymentType(binding.acPayType, paymentTypeList)
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

    private fun convertDealerInfo(activity: Activity): DealerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedDealer", "")
        return gson.fromJson(json, DealerChild::class.java)
    }

// TODo-------------------------------------------------------------------------->Document selection

    private fun openGalleryForImages() {

        // For latest versions API LEVEL 19+
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_CODE)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && null != data) {

            if (data.data != null) {
                val imagePath: String = data.data!!.path!!
                Log.e("imagePath", imagePath)
                val imgUri: Uri = data.data!!
                Log.e("realPath", "onActivityResult: ${ImagePathUtils.getRealPath(requireContext(), imgUri)!!}")

                val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                try {

                    file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                        createFileFromImageUri(requireContext(),imgUri)!!
                    } else{
                        path.mkdirs()
                        File(ImagePathUtils.getRealPath(requireContext(), imgUri)!!)
                    }
                    //get file extension
                    extension = ImagePathUtils.getRealPath(requireContext(), imgUri).toString().split(".").last()
                   //Set image to view
                    val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(imgUri))
                    binding.imgCapturedImage.setImageBitmap(bitmap)

                }

                catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }

            }


            else {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Failed",
                    "Please select only one document",
                    R.drawable.error_white,
                    2
                )
            }

        }

        else {
            Utility.getBaseMessage(
                requireActivity(),
                "Failed",
                "Sorry, failed to selection document",
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


//    private fun setImage(imgFile: File, v: ImageView) {
//        v.setImageBitmap(null)
//        if (imgFile.exists()) {
//            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//            v.setImageBitmap(myBitmap)
//        }
//    }

// TODO---------------------------------------------------------------------->Document Selection End

    private fun uiValidation(): Boolean {
        val res: Boolean

        clearErrorSign()

        when {
            selectedBank == "" -> {
                res = false
                binding.layoutBank.isErrorEnabled = true
                binding.layoutBank.error = "Select any bank"
            }
            selectedBranch == "" -> {
                res = false
                binding.layoutBranch.isErrorEnabled = true
                binding.layoutBranch.error = "Select any branch"
            }
            selectedPayType == "" -> {
                res = false
                binding.layoutPayType.isErrorEnabled = true
                binding.layoutPayType.error = "Select any payment type"
            }

//            rootView.txtDocumentNo.text!!.isEmpty() -> {
//                res = false
//                rootView.layoutDocumentNo.isErrorEnabled = true
//                rootView.layoutDocumentNo.error = "Provide payment type document number"
//            }

            binding.txtAmount.text!!.isEmpty() -> {
                res = false
                binding.layoutCollectionAmount.isErrorEnabled = true
                binding.layoutCollectionAmount.error = "Provide amount"
            }

            extension == "" -> {
                res = false
                Utility.getBaseMessage(
                    requireActivity(),
                    "Warning",
                    "please provide valid document",
                    R.drawable.error_white,
                    0
                )
            }

            else -> {
                res = true
            }
        }

        return res
    }

    private fun clearErrorSign() {
        binding.layoutBank.isErrorEnabled = false
        binding.layoutBank.error = null

        binding.layoutBranch.isErrorEnabled = false
        binding.layoutBranch.error = null

        binding.layoutPayType.isErrorEnabled = false
        binding.layoutPayType.error = null

        binding.layoutCollectionAmount.isErrorEnabled = false
        binding.layoutCollectionAmount.error = null

        binding.layoutDocumentNo.isErrorEnabled = false
        binding.layoutDocumentNo.error = null
    }

    private fun confirmBottomSheet(message: String) {
        val v: View = layoutInflater.inflate(R.layout.confirm_payment, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnCollectionYes: MaterialButton = v.findViewById(R.id.btnCollectionYes)
        val btnCollectionNo: MaterialButton = v.findViewById(R.id.btnCollectionNo)
        val txtSalesCancel: TextView = v.findViewById(R.id.txtSalesCancel)
        val txtConfirmMessage: TextView = v.findViewById(R.id.txtConfirmMessage)

        txtConfirmMessage.text = message
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        btnCollectionNo.setOnClickListener {
            dialog.hide()
        }

        txtSalesCancel.setOnClickListener {
            dialog.hide()
        }

        btnCollectionYes.setOnClickListener {
            dialog.hide()
            val user = Utility.getValueByKey(requireActivity(), "username")
            loadingAnim.show()
            saveSalesCollection(
                user!!,
//                Utility.changeDateFormat(
//                    rootView.txtPaymentDate.text.toString(),
//                    "dd MMM yyyy",
//                    "yyyy-dd-MM"
//                ).toString(),
                fromDate,
                selectedBank,
                selectedBranch,
                accountNo,
                routeNo,
                Utility.getCurrentDate("yyyy-MM-dd").toString(),
                payTo,
                dealerCode,
                selectedPayType,
                binding.txtDocumentNo.text.toString(),
                user!!,
                binding.txtAmount.text.toString(),
                binding.txtCollectionRemarks.text.toString(),
                file,
                extension,
                dialog
            )
        }
    }

    private fun saveSalesCollection(
        user_id: String,
        ent_date: String,
        dp_bank: String,
        dp_br: String,
        dp_acc: String,
        dp_brrt: String,
        dp_date: String,
        dp_acc_nam: String,
        cus_code: String,
        pay_typ: String,
        doc_no: String,
        emp_code: String,
        dp_amt: String,
        dp_rem: String,
        all_images: File,
        extension: String,
        dialog: Dialog

    ) {
        salesCollectionViewModel.saveSalesCollection(
            user_id,
            ent_date,
            dp_bank,
            dp_br,
            dp_acc,
            dp_brrt,
            dp_date,
            dp_acc_nam,
            cus_code,
            pay_typ,
            doc_no,
            emp_code,
            dp_amt,
            dp_rem,
            extension,
            all_images

        ).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            dialog.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Your collection has been submitted successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Failed",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }
                        loadingAnim.dismiss()

                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)

                        navController.navigate(R.id.nav_retiler_home)
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Utility.getBaseMessage(
                        requireActivity(),
                        "Collection submission failed",
                        "Due to technical difficulties, we are unable to process your collection info",
                        R.drawable.error_white,
                        0
                    )
                    //Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun getPaymentType(
        code: String
    ) {
        salesCollectionViewModel.getPaymentType(code)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                paymentTypeList = ArrayList()
                                paymentTypeList.addAll(res.data)

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
}