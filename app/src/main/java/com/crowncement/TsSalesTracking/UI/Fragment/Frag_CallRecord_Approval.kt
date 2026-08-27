package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.CallRecordApproveListAdapter
import com.crowncement.TsSalesTracking.data.Model.GetSalesCallAppResponce
import com.crowncement.TsSalesTracking.data.Model.GetSalesCallAppResponceChild
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.databinding.FragCallRecordApprovalBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import java.util.Objects

class Frag_CallRecord_Approval : Fragment() {

    private var _binding: FragCallRecordApprovalBinding? = null
    private val binding get() = _binding!!

    lateinit var user: String

    lateinit var loadingAnim: Dialog
    lateinit var itemAdapter: CallRecordApproveListAdapter
    lateinit var retailerViewModel: RetailerViewModel
    var approveList: ArrayList<GetSalesCallAppResponceChild> = ArrayList()
    var sortedapproveList :  ArrayList<GetSalesCallAppResponceChild> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragCallRecordApprovalBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        RetailerViewModelFactory()
        setupViewModel()
        user = Utility.getValueByKey(requireActivity(), "username").toString()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        getSalesCallApp(user)

      //  getSalesCallApp("E11-000452")
      //  getSalesCallApp("E11-001795")
        binding.searchFab.setOnClickListener {

            searchDate()
        }


    }

    private fun setupViewModel() {
        retailerViewModel = ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
            .get(RetailerViewModel::class.java)
    }


    private fun searchDate() {
        val v: View = layoutInflater.inflate(R.layout.search_date_dialog, null)
        val dialog = BottomSheetDialog(requireActivity())
        var picker: DatePickerDialog
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val imgClosesSearch = v.findViewById<ImageView>(R.id.imgClosesSearch)
        val txtFrSearchDate = v.findViewById<TextInputEditText>(R.id.txtFrSearchDate)
        val txtTSearchDate  = v.findViewById<TextInputEditText>(R.id.txtTSearchDate)
        val btnSearchAtt    = v.findViewById<Button>(R.id.btnSearchAtt)
        val layoutSearchF   = v.findViewById<TextInputLayout>(R.id.layoutSearchF)
        val layoutSearchT   = v.findViewById<TextInputLayout>(R.id.layoutSearchT)
        val txtErrorSearch  = v.findViewById<TextView>(R.id.txtErrorSearch)

        var fromDate = ""
        var toDate = ""

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgClosesSearch.setOnClickListener { dialog.hide() }



        txtFrSearchDate.setOnClickListener {
            layoutSearchF.isErrorEnabled = false
            layoutSearchT.isErrorEnabled = false
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
                    txtFrSearchDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MMM-yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }

        txtTSearchDate.setOnClickListener {

            layoutSearchF.isErrorEnabled = false
            layoutSearchT.isErrorEnabled = false

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

                    txtTSearchDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd-MMM-yyyy"
                        )
                    )


                    /*
                    if (isGraterThan30Days(fromDate, toDate)) {
                        txtErrorSearch.visibility = View.VISIBLE
                        txtFrSearchDate.setText("")
                        txtTSearchDate.setText("")
                    } else {
                        txtErrorSearch.visibility = View.GONE
                        txtTSearchDate.setText(
                            Utility.changeDateFormat(
                                selectedDate,
                                "yyyy-MM-dd",
                                "dd-MMM-yyyy"
                            )
                        )
                    }

                     */


                }, year, month, day
            )
            picker.show()
        }

        btnSearchAtt.setOnClickListener {

            if ((fromDate != "") && (toDate != "")) {
            //    getUserAttList(user, fromDate, toDate)

                // getUserAttList("E00-005445", fromDate, toDate)
                dialog.dismiss()
            } else {
                when {
                    txtFrSearchDate.text.isNullOrBlank() -> {
                        layoutSearchF.error = "Select from date"
                        layoutSearchF.isErrorEnabled = true
                        layoutSearchF.setErrorIconDrawable(R.drawable.ic_baseline_error_orange)
                    }
                    txtTSearchDate.text.isNullOrBlank() -> {
                        layoutSearchT.error = "Select to date"
                        layoutSearchT.isErrorEnabled = true
                        layoutSearchT.setErrorIconDrawable(R.drawable.ic_baseline_error_orange)
                    }
                }
            }
        }

    }


    private fun getSalesCallApp(
        id: String,

        ) {
        retailerViewModel.GetSalesCallApp(id)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200" && res.data.size > 0) {

                                approveList = ArrayList()
                                sortedapproveList = ArrayList()
                                approveList = res.data



                                successApprovalList(
                                    res,
                                    binding.rvcallapp,
                                    binding.txtAttAuthNoData
                                )
                            } else {
                                successApprovalList(
                                    res,
                                    binding.rvcallapp,
                                    binding.txtAttAuthNoData
                                )
                                if (isAdded && activity != null) {

                                    Utility.getBaseMessage(
                                        requireActivity(),
                                        "Sorry, No data for approve",
                                        res.message.toString(),
                                        R.drawable.error_white,
                                        0
                                    )
                                }
                            }

                            loadingAnim.dismiss()

                        }

                    }
                    Status.LOADING -> {


                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun successApprovalList(
        res: GetSalesCallAppResponce,
        rv: RecyclerView,
        noData: LottieAnimationView

    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            rv.visibility = View.VISIBLE
          //  prepareAttAuthList(rv, res.data)


            val otherCustomerList: ArrayList<GetSalesCallAppResponceChild> = res.data
            approveList = otherCustomerList


            for (values in approveList) {

                if (values.delQty != 0 )
                {
                    sortedapproveList.add(values)
                }
            }

            approveList = sortedapproveList

            prepareAttAuthList(rv, sortedapproveList)


        } else {
            noData.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        //    binding.shimmerAttApproveContainer.visibility = View.GONE
        //   binding.shimmerAttApproveContainer.stopShimmer()

    }



    private fun prepareAttAuthList(rv: RecyclerView, items: ArrayList<GetSalesCallAppResponceChild>) {

        itemAdapter = CallRecordApproveListAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter



        itemAdapter.setOnItemClickListener(object :
            CallRecordApproveListAdapter.OnAdapterItemClickListener {

            override fun OnAccept(v: View?, position: Int) {

                confirmBottomSheet(items[position])

            }

            override fun OnAbsent(v: View?, position: Int) {

                absentBottomSheet(items[position])

            }



        })



    }


    //todo for absent
    private fun absentBottomSheet(data: GetSalesCallAppResponceChild) {
        val v: View = layoutInflater.inflate(R.layout.callrecord_rejection, null)

        val dialog = BottomSheetDialog(requireContext())



        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnAttAbsent: MaterialButton = v.findViewById(R.id.btnAttAbsent)
        //   val btnAttReject: MaterialButton = v.findViewById(R.id.btnAttReject)
        val txtAttCancel: TextView = v.findViewById(R.id.txtAttCancel)
        val txtAttAbsentReason: TextInputEditText = v.findViewById(R.id.txtAttAbsentReason)
        val layoutAttAbsentReason : TextInputLayout = v.findViewById(R.id.layoutAttAbsentReason)


     //   val message = "You are going to absent " + data.empName + " attendance"

       // txtConAttMessage.text = message



        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtAttCancel.setOnClickListener {
            dialog.hide()
        }

        txtAttCancel.setOnClickListener {

            dialog.hide()
        }
        btnAttAbsent.setOnClickListener {


            //  dialog.dismiss()
            if(validationForAbsentReason(txtAttAbsentReason,layoutAttAbsentReason)){
                getCancelSalesCall(
                    user,
                    data.callTrno.toString(),
                    txtAttAbsentReason.text.toString())
                dialog.dismiss()
            }



        }
    }


    fun validationForAbsentReason(
        txtAttAbsentReason:TextInputEditText,
        layoutAttAbsentReason:TextInputLayout): Boolean{
        var result = false

        layoutAttAbsentReason.isErrorEnabled = false
        layoutAttAbsentReason.error = null
        if (txtAttAbsentReason.text.toString().isEmpty()) {
            result = false
            layoutAttAbsentReason.isErrorEnabled = true
            layoutAttAbsentReason.error = "Rejection reason is required"
        }else{
            result = true
        }
        return result
    }

    //todo approve
    private fun confirmBottomSheet(data: GetSalesCallAppResponceChild) {
        val v: View = layoutInflater.inflate(R.layout.callrecord_confarmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnAttApprove: MaterialButton = v.findViewById(R.id.btnApprove)
        val btnAttReject: MaterialButton = v.findViewById(R.id.btnReject)
        val txtAttCancel: TextView = v.findViewById(R.id.txtAttCancel)
        val txtConAttMessage: TextView = v.findViewById(R.id.txtConAttMessage)

    //    val message = "You are going to this bag number and call record"

      //  txtConAttMessage.text = message


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtAttCancel.setOnClickListener {
            dialog.hide()
        }

        btnAttReject.setOnClickListener {

            dialog.hide()
        }
        btnAttApprove.setOnClickListener {

            dialog.dismiss()


            getAppSalesCall(
                user,
                data.callTrno.toString()
            )

        }
    }

   //todo getAppSalesCall
   private fun getAppSalesCall(
       id: String,
       callNum: String

   ) {
       retailerViewModel.getAppSalesCall(id, callNum)
           .observe(requireActivity()) {
               when (it.status) {
                   Status.SUCCESS -> {

                       it.responseData?.let { res ->

                           if (res.code == "200") {

                               Utility.getBaseMessage(
                                   requireActivity(),
                                   "Approved",
                                   res.message.toString(),
                                   R.drawable.ic_checked_green,
                                   1
                               )

                               loadingAnim = Utility.baseLoadingAnimation(
                                   requireActivity(),
                                   Dialog(requireActivity()),
                                   "P l e a s e    w a i t"
                               )
                               loadingAnim.show()
                               getSalesCallApp(
                                   user
                               )

//                               getSalesCallApp(
//                                   "E11-001795"
//                               )

                           } else {
                               Utility.getBaseMessage(
                                   requireActivity(),
                                   "Sorry",
                                   res.message.toString(),
                                   R.drawable.error_white,
                                   0
                               )
                           }

                           loadingAnim.dismiss()

                       }

                   }
                   Status.LOADING -> {
                       loadingAnim.show()

                   }
                   Status.ERROR -> {
                       loadingAnim.dismiss()
                       Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                   }
               }
           }
   }



    //todo getAppSalesCall
    private fun getCancelSalesCall(
        id: String,
        callNum: String,
        reason : String

    ) {
        retailerViewModel.getCancelSalesCall(id, callNum,reason)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Rejected",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                loadingAnim = Utility.baseLoadingAnimation(
                                    requireActivity(),
                                    Dialog(requireActivity()),
                                    "P l e a s e    w a i t"
                                )
                                loadingAnim.show()
                                getSalesCallApp(
                                    user
                                )

//                               getSalesCallApp(
//                                   "E11-001795"
//                               )

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Sorry",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
                                )
                            }

                            loadingAnim.dismiss()

                        }

                    }
                    Status.LOADING -> {
                        loadingAnim.show()

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
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