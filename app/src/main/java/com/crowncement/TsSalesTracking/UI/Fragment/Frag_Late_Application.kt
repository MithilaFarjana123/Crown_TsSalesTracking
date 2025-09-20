package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LateViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LateViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Adapter.AppliedLateApplicationAdapter
import com.crowncement.TsSalesTracking.data.Adapter.LateApplicationListAdapter
import com.crowncement.TsSalesTracking.data.Model.*
import com.crowncement.TsSalesTracking.databinding.AddLateApplicationBinding
import com.crowncement.TsSalesTracking.databinding.FragLateApplicationBinding
import com.crowncement.TsSalesTracking.databinding.LateapplicationWorningDialougeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.util.*

class Frag_Late_Application : Fragment() {

    //  private lateinit var rootView: View

    private var _binding: FragLateApplicationBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog

    private lateinit var lateViewModel: LateViewModel
    private lateinit var leaveViewModel: LeaveViewModel
    private lateinit var user: String
    lateinit var notificationSendTo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragLateApplicationBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//  rootView = inflater.inflate(R.layout.frag_late_application, container, false)
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireContext()),
            "P l e a s e    w a i t"
        )
        binding.shimmerLateContainer.visibility = View.VISIBLE
        binding.shimmerLateContainer.startShimmer()

        LateViewModelFactory()
        LeaveViewModelFactory()

        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        setProfileInformation()

        getLateList(user, getCurrentDate("yyyy-MM-dd HH:mm:ss")!!)
        //  getLateList("E00-005445",getCurrentDate("yyyy-MM-dd HH:mm:ss")!!)
        //getLateList("E11-001795", "2022-08-13 13:35:15")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        lateViewModel = ViewModelProviders.of(this.requireActivity(), LateViewModelFactory())
            .get(LateViewModel::class.java)
        leaveViewModel = ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
            .get(LeaveViewModel::class.java)
    }

    private fun prepareLateList(items: ArrayList<Late>) {
        val itemAdapter = LateApplicationListAdapter(items)
        if (isAdded) {
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

            binding.rvLateList.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvLateList.layoutManager = manager
            binding.rvLateList.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :
                LateApplicationListAdapter.OnAdapterItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun OnClickApply(v: View?, position: Int) {

                    if (items.get(position).lt_ent_stat == "Applied") {
                        lateAppliedList(items[position])
                    } else {
                        var lateDateInList = items.get(position).lt_date

                        val currentDate = LocalDate.now()
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        var todayDate = formatterDate.toString()
                        //todo

                     //   if (lateDateInList == todayDate) {

                        if (lateDateInList ==  getCurrentDate("yyyy-MM-dd").toString()) {

                                lateApplicationEntry(items[position])
                        } else {
                            loadLateArartDialouge()
                        }
                    }
                }
            })

        }
    }


    private fun loadLateArartDialouge() {
        // Inflate the layout using ViewBinding
        val binding = LateapplicationWorningDialougeBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Set content view using the binding root
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()


        binding.btnClose.setOnClickListener { dialog.dismiss() }

        binding.btnGotIt.setOnClickListener { dialog.dismiss() }


    }



    private fun lateApplicationEntry(late: Late) {
        // Inflate the layout using ViewBinding
        val binding = AddLateApplicationBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Set content view using the binding root
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()

        // Populate views with data
        binding.txtApproveBy.text = late.lt_hrcpath
        binding.txtLateApplicationDate.text =
            changeDateFormat(late.lt_date, "yyyy-MM-dd", "EEE, dd MMM yyyy")

        // Set onClickListeners
        binding.imgClose.setOnClickListener { dialog.dismiss() }

        binding.btnLateApply.setOnClickListener {
            if (lateApplyValidation(binding)) {
                binding.btnLateApply.isEnabled = false
                loadingAnim.show()
                lateEntry(
                    late.lt_emp.toString(),
                    late.lt_date.toString(),
                    getCurrentDate("yyyy-MM-dd").toString(),
                    binding.txtPurpose.text.toString(),
                    binding.txtRemarks.text.toString(),
                    notificationSendTo,
                    dialog
                )
            }
        }
    }


    private fun lateApplyValidation(binding: AddLateApplicationBinding): Boolean {
        var res = false
        binding.layoutPurpose.error = null
        //  binding.layoutRemarks.error = null
        binding.layoutPurpose.isErrorEnabled = false
        //  binding.layoutRemarks.isErrorEnabled = false

        when {
            binding.txtPurpose.text.toString().isBlank() -> {
                binding.layoutPurpose.error = getString(R.string.provide_purpose)
                binding.layoutPurpose.isErrorEnabled = true
                binding.layoutPurpose.errorIconDrawable =
                    resources.getDrawable(R.drawable.ic_baseline_sad)
                res = false
            }
//        binding.txtRemarks.text.toString().isBlank() -> {
//            binding.layoutRemarks.error = getString(R.string.provide_remarks)
//            binding.layoutRemarks.isErrorEnabled = true
//            binding.layoutRemarks.errorIconDrawable =
//                resources.getDrawable(R.drawable.ic_baseline_sad)
//            res = false
//
//        }
            else -> {
                res = true
            }
        }

        return res
    }

    /*
    private fun lateApplicationEntry(late: Late) {
        val v: View = layoutInflater.inflate(R.layout.add_late_application, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnLateApply = v.findViewById<Button>(R.id.btnLateApply)
        val imgClose = v.findViewById<ImageView>(R.id.imgClose)
        val txtLateApplicationDate = v.findViewById<TextView>(R.id.txtLateApplicationDate)
        val txtApproveBy = v.findViewById<TextView>(R.id.txtApproveBy)
        txtApproveBy.setText(late.lt_hrcpath)
        txtLateApplicationDate.text =
           changeDateFormat(late.lt_date, "yyyy-MM-dd", "EEE, dd MMM yyyy")

        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()

        imgClose.setOnClickListener { view1: View? -> dialog.hide() }

        btnLateApply.setOnClickListener { view1: View? ->


            if (lateApplyValidation(v)) {

                btnLateApply.isEnabled = false
                loadingAnim.show()
                lateEntry(
                    late.lt_emp.toString(),
                    late.lt_date.toString(),
                    getCurrentDate("yyyy-MM-dd").toString(),
                    v.txtPurpose.text.toString(),
                    v.txtRemarks.text.toString(),
                    notificationSendTo,
                    dialog
                )
            }
        }
    }

    private fun lateApplyValidation(v: View): Boolean {
        var res = false
        v.layoutPurpose.error = null
        //  v.layoutRemarks.error = null
        v.layoutPurpose.isErrorEnabled = false
        //  v.layoutRemarks.isErrorEnabled = false

        when {
            v.txtPurpose.text.toString().isBlank() -> {
                v.layoutPurpose.error = getString(R.string.provide_purpose)
                v.layoutPurpose.isErrorEnabled = true
                v.layoutPurpose.errorIconDrawable =
                    resources.getDrawable(R.drawable.ic_baseline_sad)
                res = false
            }
//            v.txtRemarks.text.toString().isBlank() -> {
//                v.layoutRemarks.error = getString(R.string.provide_remarks)
//                v.layoutRemarks.isErrorEnabled = true
//                v.layoutRemarks.errorIconDrawable =
//                    resources.getDrawable(R.drawable.ic_baseline_sad)
//                res = false
//
//            }
            else -> {
                res = true
            }
        }

        return res
    }
*/

    private fun getLateList(empId: String, lateDate: String) {

            lateViewModel.getLateList(empId, lateDate)
                .observe(requireActivity()) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.responseData?.let { res ->
                                successAttList(res)
                                //Get notification to ID
                                getUserHierarchy(
                                    empId,
                                    getCurrentDate("yyyy-MM-dd").toString(),
                                    "Late"
                                )
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

    private fun successAttList(res: LateResponse) {

        loadingAnim.dismiss()
        if (res.code.equals("200")) {

            if (res.data.isNotEmpty()) {
                _binding?.txtLateNoData?.visibility = View.GONE
                _binding?.rvLateList?.visibility = View.VISIBLE

             //   binding.txtLateNoData.visibility = View.GONE
             //   binding.rvLateList.visibility = View.VISIBLE
                prepareLateList(res.data)
            } else {
                _binding?.txtLateNoData?.visibility = View.VISIBLE
                _binding?.rvLateList?.visibility = View.GONE
                //binding.txtLateNoData.visibility = View.VISIBLE
                //binding.rvLateList.visibility = View.GONE
            }
        }
        _binding?.shimmerLateContainer?.visibility = View.GONE
        _binding?.shimmerLateContainer?.stopShimmer()
       // binding.shimmerLateContainer.visibility = View.GONE
       // binding.shimmerLateContainer.stopShimmer()
    }

    private fun lateAppliedList(late: Late) {
        val v: View = layoutInflater.inflate(R.layout.late_applied_list, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val txtListClose = v.findViewById<TextView>(R.id.txtListClose)
        val rvLateApplied = v.findViewById<RecyclerView>(R.id.rvLateApplied)
        val txtAppLateNoData = v.findViewById<LottieAnimationView>(R.id.txtAppLateNoData)


        // getAppliedLateList(late.lt_emp!!, "2022-08-08 13:35:15", rvLateApplied, txtAppLateNoData)
        getAppliedLateList(
            late.lt_emp!!,
            Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString(),
            rvLateApplied,
            txtAppLateNoData
        )

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtListClose.setOnClickListener { view1: View? -> dialog.dismiss() }
    }

    private fun getAppliedLateList(
        empId: String,
        lateDate: String,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {
        lateViewModel.getAppliedLateList(empId, lateDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            successAppliedList(res, rv, noData)
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

    private fun successAppliedList(
        res: LateAppliedResponse,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {

        if (res.code.equals("200")) {

            if (res.data.isNotEmpty()) {
                noData.visibility = View.GONE
                rv.visibility = View.VISIBLE
                prepareAppliedLateList(rv, res.data)
            } else {
                noData.visibility = View.VISIBLE
                rv.visibility = View.GONE
            }
        }
    }

    private fun prepareAppliedLateList(rv: RecyclerView, items: ArrayList<LateAppliedChild>) {

        val itemAdapter = AppliedLateApplicationAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object :
            AppliedLateApplicationAdapter.OnAdapterItemClickListener {
            override fun OnClickApply(v: View?, position: Int) {

            }
        })

    }

    private fun getUserHierarchy(userName: String, dateTime: String, type: String) {
        if (isAdded) {
            leaveViewModel.getUserHierarchy(userName, dateTime, type)
                .observe(requireActivity()) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.responseData?.let { res ->
                                successUserHierarchy(res)
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

    private fun successUserHierarchy(res: HierarchyResponse) {
        if (res.code.equals("200") && res.data.size>0 ) {
            notificationSendTo = res.data[0].repCode.toString()
        } else {
            Toast.makeText(requireContext(), "User hierarchy not found", Toast.LENGTH_LONG).show()
        }
    }


    private fun lateEntry(
        user_id: String,
        lt_date: String,
        lt_iss_date: String,
        lt_purpose: String,
        lt_rem: String,
        lv_not_to: String, dialog: Dialog
    ) {
        lateViewModel.lateEntry(
            user_id,
            lt_date,
            lt_iss_date,
            lt_purpose,
            lt_rem, lv_not_to
        )
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            dialog.dismiss()
                            if (res.code == "200") {
                                loadingAnim.dismiss()
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    //"Late application approved successfully",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            }

                            else {
                                loadingAnim.dismiss()
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    //"Sorry late application failed to approve.Please try again after some time",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    2
                                )
                            }
                        }

                        binding.shimmerLateContainer.visibility = View.VISIBLE
                        binding.shimmerLateContainer.startShimmer()
                        getLateList(user, getCurrentDate("yyyy-MM-dd HH:mm:ss")!!)

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



    private fun setProfileInformation() {
        try {
            val profile: Data = Utility.getUserInfo(requireActivity())!!

            binding.txtLateEMPName.text = "Name : "+ profile.userName
            var emp = Utility.getValueByKey(requireActivity(),"username")
            binding.txtLateEMPDEsignation.text = "Designation : "+ profile.userDesig + " | Emp ID : "+emp
            binding.txtLateDept.text = "Department : "+ profile.userDept
            //   rootView.txtPayEmpType.text = "Employee Type : " + profile.att_status



            //SET IMAGE
            if (!(profile.userProfileImagePath.toString() == "")) {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    // .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(binding.imgLateImage)
            }

        } catch (e: Exception) {
            Utility.commonToast(requireActivity(), "Error", e.message.toString(), 0)
        }
    }




}


