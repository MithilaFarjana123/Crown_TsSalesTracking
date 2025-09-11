package com.crowncement.TsSalesTracking.UI.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.airbnb.lottie.LottieAnimationView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.SalesCollectionViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.SalesCollectionViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.formatAmount
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Adapter.SalesCollectionListAdapter
import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.data.Model.SalesCollectionChild
import com.crowncement.TsSalesTracking.data.Model.SalesCollectionResponse
import com.crowncement.TsSalesTracking.databinding.FragPaymentCollectionListBinding

import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*


class Frag_Payment_Collection_List : Fragment() {

  //  lateinit var rootView: View
    private var _binding: FragPaymentCollectionListBinding? = null
    private val binding get() = _binding!!

    lateinit var paymentViewModel: SalesCollectionViewModel
    lateinit var paymentCollectionList: ArrayList<SalesCollectionChild>
    lateinit var itemAdapter: SalesCollectionListAdapter

    lateinit var picker: DatePickerDialog

    lateinit var dealer: DealerChild

    var selectedCustomer: String = ""

    var fromDate: String = ""
    var toDate: String = ""
    var user: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

     //   rootView = inflater.inflate(R.layout.frag_payment_collection_list, container, false)

        _binding = FragPaymentCollectionListBinding.inflate(inflater, container, false)

/*
        SalesCollectionViewModelFactory()
        setupViewModel()

        binding.shimmerSalesCollection.visibility = View.VISIBLE
        binding.shimmerSalesCollection.startShimmer()
        user = Utility.getValueByKey(requireActivity(), "username").toString()

        //  selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()


        dealer = convertDealerInfo(requireActivity())!!

        getPaymentList(user, dealer.dealerCode.toString(), "", "")


        binding.fabAddPayment.setOnClickListener {

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_payment_save)
        }

        binding.txtFDate.setOnClickListener {

            fromDatePicker()
        }




 */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        SalesCollectionViewModelFactory()
        setupViewModel()

        binding.shimmerSalesCollection.visibility = View.VISIBLE
        binding.shimmerSalesCollection.startShimmer()
        user = Utility.getValueByKey(requireActivity(), "username").toString()

        //  selectedCustomer = Utility.getValueByKey(requireActivity(), "selectedCustomer").toString()


        dealer = convertDealerInfo(requireActivity())!!

        getPaymentList(user, dealer.dealerCode.toString(), "", "")


        binding.fabAddPayment.setOnClickListener {

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_payment_save)
        }

        binding.txtFDate.setOnClickListener {

            fromDatePicker()
        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun fromDatePicker() {
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
                binding.txtFDate.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yy"
                    )
                )
                toDatePicker()
            }, year, month, day
        )
        picker.show()
    }

    private fun toDatePicker() {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        picker = DatePickerDialog(
            requireContext(),
            { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                val monthNumber = monthOfYear + 1
                val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                toDate = selectedDate
                binding.txtToDate.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yy"
                    )
                )

                binding.shimmerSalesCollection.visibility = View.VISIBLE
                binding.shimmerSalesCollection.startShimmer()
                getPaymentList(user, dealer.dealerCode.toString(), fromDate, toDate)

            }, year, month, day
        )
        picker.show()
    }

    private fun setupViewModel() {
        paymentViewModel =
            ViewModelProviders.of(this.requireActivity(), SalesCollectionViewModelFactory())
                .get(SalesCollectionViewModel::class.java)
    }

    private fun getPaymentList(
        user_id: String,
        customerCode: String,
        fDate: String,
        tDate: String
    ) {
        paymentViewModel.getSalesCollectionList(user_id, customerCode, fDate, tDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            paymentCollectionList = ArrayList()
                            paymentCollectionList = res.data

                            successPaymentList(
                                res,
                                binding.rvSalesCollection,
                                binding.txtSalesCollectionNoData
                            )
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

    private fun successPaymentList(
        res: SalesCollectionResponse,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            rv.visibility = View.VISIBLE
            prepareCollectionList(rv, res.data)

        } else {
            noData.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        binding.shimmerSalesCollection.visibility = View.GONE
        binding.shimmerSalesCollection.stopShimmer()
    }

    private fun prepareCollectionList(
        rv: RecyclerView,
        items: ArrayList<SalesCollectionChild>
    ) {

        itemAdapter = SalesCollectionListAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        calculateTotal(items)

        itemAdapter.setOnItemClickListener(object :
            SalesCollectionListAdapter.OnAdapterItemClickListener {

            override fun clickShowDoc(v: View?, position: Int) {

                showBottomSheet(items[position].dep_img.toString())
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun showBottomSheet(docUrl: String) {
        var presentAngle = 0f
        val v: View = layoutInflater.inflate(R.layout.sales_collection_doc_dialog, null)
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        val imgShowDoc: ImageView = v.findViewById(R.id.imgShowDoc)
        val imgLeft: ImageView = v.findViewById(R.id.imgLeft)
        val imgRight: ImageView = v.findViewById(R.id.imgRight)
        val txtDocCancel: TextView = v.findViewById(R.id.txtDocCancel)
        val btnOK: MaterialButton = v.findViewById(R.id.btnOK)
        val noDoc: LottieAnimationView = v.findViewById(R.id.noDoc)


        if (docUrl.isNotEmpty()) {

            imgShowDoc.visibility = View.VISIBLE
            noDoc.visibility = View.GONE

            imgShowDoc.rotation = presentAngle





            Picasso.get()
                .load(IMAGE_BASE_URL + docUrl)
                .placeholder(R.drawable.progress_animation)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)

                .into(imgShowDoc, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: java.lang.Exception?) {
                        Log.e("Error", e!!.message.toString())
                        loadWithCoil(imgShowDoc, docUrl)
                    }
                })


        } else {
            imgShowDoc.visibility = View.GONE
            noDoc.visibility = View.VISIBLE
        }


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        txtDocCancel.setOnClickListener {
            dialog.hide()
        }
        btnOK.setOnClickListener {
            dialog.hide()
        }

        imgLeft.setOnClickListener {
            presentAngle -= 90f
            imgShowDoc.rotation = presentAngle
            imgShowDoc.scaleType = ImageView.ScaleType.FIT_XY
        }

        imgRight.setOnClickListener {
            presentAngle += 90f
            imgShowDoc.rotation = presentAngle
            imgShowDoc.scaleType = ImageView.ScaleType.FIT_XY

        }

        imgShowDoc.setOnClickListener {
            val animZoomIn = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in)
            imgShowDoc.startAnimation(animZoomIn)
        }

    }


    fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (e: IOException) {
            null
        }
    }


    // extension function to save an image to internal storage
    fun Bitmap.saveToInternalStorage(context: Context): Uri? {
        // get the context wrapper instance
        val wrapper = ContextWrapper(context)
        // initializing a new file
        // bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        // create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")
        return try {
            // get the file output stream
            val stream: OutputStream = FileOutputStream(file)
            // compress bitmap
            compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // flush the stream
            stream.flush()
            // close stream
            stream.close()
            // return the saved image uri
            Uri.parse(file.absolutePath)
        } catch (e: IOException) { // catch the exception
            e.printStackTrace()
            null
        }

    }


    fun loadWithCoil(imageView: ImageView, imageUrl: String) {
        imageView.load(imageUrl) { error(R.drawable.ic_baseline_broken_image) }

    }

    private fun convertDealerInfo(activity: Activity): DealerChild? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("selectedDealer", "")
        return gson.fromJson(json, DealerChild::class.java)
    }

    private fun calculateTotal(items: ArrayList<SalesCollectionChild>) {
        if (items.size > 0) {
            var totalCollec = 0
            for (colllectiondata in items) {
                totalCollec += colllectiondata.depoAmt!!
            }
            binding.txtTotalCollection.text =
                "৳ " + formatAmount(totalCollec.toDouble()).toString() + " /-"
        } else {
            binding.txtTotalCollection.text = "৳ 0 "
        }

    }


}