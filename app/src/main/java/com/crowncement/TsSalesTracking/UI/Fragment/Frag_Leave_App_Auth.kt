package com.crowncement.TsSalesTracking.UI.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide

import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.LeaveApplicationApproveAdapter
import com.crowncement.TsSalesTracking.data.Model.LeaveApproveChild
import com.crowncement.TsSalesTracking.data.Model.LeaveApproveResponse
import com.crowncement.TsSalesTracking.databinding.FragLeaveApplicationAuthorizationBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class Frag_Leave_App_Auth : Fragment() {

  //  lateinit var rootView: View

    private var _binding: FragLeaveApplicationAuthorizationBinding? = null
    private val binding get() = _binding!!

    lateinit var leaveApproveViewModel: LeaveViewModel
    lateinit var user: String

    lateinit var approvalList: ArrayList<LeaveApproveChild>

    lateinit var itemAdapter: LeaveApplicationApproveAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
       // rootView = inflater.inflate(R.layout.frag_leave_application_authorization, container, false)
        _binding = FragLeaveApplicationAuthorizationBinding.inflate(inflater, container, false)

/*

        //Enable search view menu item in toolbar
        setHasOptionsMenu(true)

        binding.shimmerApproveContainer.visibility = View.VISIBLE
        binding.shimmerApproveContainer.startShimmer()

        LeaveViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        getLeaveApplicationList(user)
        //getLeaveApplicationList("E00-001312")

        setLeaveTypeBg()

        binding.ml.setOnClickListener {
            selectedTypeBgChange(binding.ml)
            typeWiseSorting("ml")
        }
        binding.cl.setOnClickListener {
            selectedTypeBgChange(binding.cl)
            typeWiseSorting("cl")
        }
        binding.el.setOnClickListener {
            selectedTypeBgChange(binding.el)
            typeWiseSorting("el")
        }

        requireActivity().intent.putExtra("send_to","")
*/
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        //Enable search view menu item in toolbar
        setHasOptionsMenu(true)

        binding.shimmerApproveContainer.visibility = View.VISIBLE
        binding.shimmerApproveContainer.startShimmer()

        LeaveViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        getLeaveApplicationList(user)
        //getLeaveApplicationList("E00-001312")

        setLeaveTypeBg()

        binding.ml.setOnClickListener {
            selectedTypeBgChange(binding.ml)
            typeWiseSorting("ml")
        }
        binding.cl.setOnClickListener {
            selectedTypeBgChange(binding.cl)
            typeWiseSorting("cl")
        }
        binding.el.setOnClickListener {
            selectedTypeBgChange(binding.el)
            typeWiseSorting("el")
        }

        requireActivity().intent.putExtra("send_to","")

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }

    private fun setLeaveTypeBg() {
        binding.ml.setBackgroundResource(R.drawable.border_green)
        binding.cl.setBackgroundResource(R.drawable.border_green)
        binding.el.setBackgroundResource(R.drawable.border_green)

        binding.ml.setTextColor(Color.parseColor("#000000"))
        binding.cl.setTextColor(Color.parseColor("#000000"))
        binding.el.setTextColor(Color.parseColor("#000000"))
    }





    private fun selectedTypeBgChange(view: TextView) {

        when {
            view == binding.cl -> {
                binding.cl.setBackgroundResource(R.color.crown_icon_color)
                binding.cl.setTextColor(Color.parseColor("#FFFFFF"))
                binding.ml.setBackgroundResource(R.drawable.border_green)
                binding.ml.setTextColor(Color.parseColor("#000000"))
                binding.el.setBackgroundResource(R.drawable.border_green)
                binding.el.setTextColor(Color.parseColor("#000000"))
            }
            view == binding.ml -> {
                binding.ml.setBackgroundResource(R.color.crown_icon_color)
                binding.ml.setTextColor(Color.parseColor("#FFFFFF"))
                binding.cl.setBackgroundResource(R.drawable.border_green)
                binding.cl.setTextColor(Color.parseColor("#000000"))
                binding.el.setBackgroundResource(R.drawable.border_green)
                binding.el.setTextColor(Color.parseColor("#000000"))
            }
            else -> {
                binding.el.setBackgroundResource(R.color.crown_icon_color)
                binding.el.setTextColor(Color.parseColor("#FFFFFF"))
                binding.ml.setBackgroundResource(R.drawable.border_green)
                binding.ml.setTextColor(Color.parseColor("#000000"))
                binding.cl.setBackgroundResource(R.drawable.border_green)
                binding.cl.setTextColor(Color.parseColor("#000000"))

            }
        }

    }

    private fun setupViewModel() {
        leaveApproveViewModel =
            ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
                .get(LeaveViewModel::class.java)
    }

    private fun getLeaveApplicationList(
        empId: String
    ) {
        leaveApproveViewModel.leaveApprovalList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            approvalList = ArrayList()
                            approvalList = res.data
                                successApprovalList(
                                    res,
                                    binding.rvLeaveAuth,
                                    binding.txtLeaveAuthNoData
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

    private fun successApprovalList(
        res: LeaveApproveResponse,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            binding.layoutSearchType.visibility = View.VISIBLE
            rv.visibility = View.VISIBLE
            prepareAuthorizationList(rv, res.data)

        }
        else {
            Utility.getBaseMessage(requireActivity(),"Sorry","No application for approve",R.drawable.error_white,2)
            noData.visibility = View.VISIBLE
            binding.layoutSearchType.visibility = View.GONE
            rv.visibility = View.GONE
        }


        binding.shimmerApproveContainer.visibility = View.GONE
        binding.shimmerApproveContainer.stopShimmer()
    }

    private fun prepareAuthorizationList(rv: RecyclerView, items: ArrayList<LeaveApproveChild>) {

        if(items.size>0){
            rv.visibility = View.VISIBLE
            itemAdapter = LeaveApplicationApproveAdapter(items)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            rv.layoutManager = rLayoutmanager

            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :
                LeaveApplicationApproveAdapter.OnAdapterItemClickListener {
                override fun OnClickCall(v: View?, position: Int) {

                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${items[position].lvPhone}")
                    startActivity(intent)
                }

                override fun OnAccept(v: View?, position: Int) {

                    approveBottomSheet(items[position])

                }

                override fun OnReject(v: View?, position: Int) {

                    rejectBottomSheet(items[position])

                }

            })
        }
        else{
            rv.visibility = View.GONE
            binding.txtLeaveAuthNoData.visibility = View.VISIBLE
            binding.layoutSearchType.visibility = View.GONE

        }

    }

    private fun approveBottomSheet(data: LeaveApproveChild) {
        val v: View = layoutInflater.inflate(R.layout.application_confirm, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnApprove: MaterialButton = v.findViewById(R.id.btnApplicationApprove)
        val btnReject: MaterialButton = v.findViewById(R.id.btnApplicationReject)
        val txtCancel: TextView = v.findViewById(R.id.txtCancel)
        val txtMessage: TextView = v.findViewById(R.id.txtMessage)
        val imgConfirm: CircleImageView = v.findViewById(R.id.imgConfirm)

        var message = ""
        message = if (data.lvDays!!.toInt() > 1) {
            "You are going to approve " + data.lvEmpName + " " + data.lvDays + " days leave application"
        } else {
            "You are going to approve " + data.lvEmpName + " " + data.lvDays + " day leave application"
        }
        txtMessage.text = message

        Glide
            .with(v.context)
            .load(data.lvEmpPic.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(imgConfirm)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtCancel.setOnClickListener {
            dialog.hide()
        }

        btnReject.setOnClickListener {

            dialog.hide()
        }
        btnApprove.setOnClickListener {
            dialog.dismiss()

            message = if (data.lvDays!!.toInt() > 1) {
                "You  approved " + data.lvEmpName + " " + data.lvDays + " days leave application"
            } else {
                "You approved " + data.lvEmpName + " " + data.lvDays + " day leave application"
            }

            approveLeaveApplication(
                data.lvEmp.toString(),
                data.lvTrno.toString(),
                data.lvAppRow.toString(),
                data.lvFdate.toString(),
                data.lvTdate.toString(),
                message
            )
        }
    }

    private fun rejectBottomSheet(data: LeaveApproveChild) {
        val v: View = layoutInflater.inflate(R.layout.leave_application_reject, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnAppYes: MaterialButton = v.findViewById(R.id.btnAppYes)
        val btnAppNo: MaterialButton = v.findViewById(R.id.btnAppNo)
        val rejMessage: TextView = v.findViewById(R.id.rejMessage)
        val imgAppReject: CircleImageView = v.findViewById(R.id.imgAppReject)
        val reason: TextInputEditText = v.findViewById(R.id.txtRejectReason)
        val layoutRejectReason: TextInputLayout = v.findViewById(R.id.layoutRejectReason)
        var message = ""

        message = if (data.lvDays!!.toInt() > 1) {
            "You are going to reject" + data.lvEmpName + " " + data.lvDays + " days leave application"
        } else {
            "You are going to reject" + data.lvEmpName + " " + data.lvDays + " day leave application"
        }

        rejMessage.text = message


        Glide
            .with(v.context)
            .load(data.lvEmpPic.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(imgAppReject)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        btnAppNo.setOnClickListener {
            dialog.hide()
        }

        btnAppYes.setOnClickListener {
            layoutRejectReason.error = null
            layoutRejectReason.isErrorEnabled = false

            if (!reason.text.isNullOrEmpty()) {
                dialog.dismiss()

                message = if (data.lvDays!!.toInt() > 1) {
                    "You  rejected" + data.lvEmpName + " " + data.lvDays + " days leave application."
                } else {
                    "You rejected" + data.lvEmpName + " " + data.lvDays + " day leave application."
                }

                rejectLeaveApplication(
                    data.lvEmp!!,
                    data.lvTrno!!,
                    data.lvAppRow.toString(),
                    reason.text.toString(),
                    message
                )
            } else {
                layoutRejectReason.error =
                    "Please provide a reason for rejecting this leave application "
                layoutRejectReason.isErrorEnabled = true
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        inflater.inflate(R.menu.search_menu, menu)


        // below line is to get our menu item.
        //    val searchItem = menu.findItem(R.id.actionSearch)
//        val title: String = searchItem.title.toString()
//        val s = SpannableString(title)
//        s.setSpan(
//            ForegroundColorSpan(Color.parseColor("#FFFFFF")),
//            0,
//            s.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//        searchItem.title = s

        // val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.actionSearch).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(query: String): Boolean {
                filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                setLeaveTypeBg()
                filter(newText)
                return false
            }


        })
    }

    private fun typeWiseSorting(text: String) {

        if (approvalList.size > 0) {
            val filteredlist = ArrayList<LeaveApproveChild>()
            for (item in approvalList) {
                if (item.lvType!!.toLowerCase(Locale.getDefault())==text) {
                    filteredlist.add(item)
                }
            }
            if (filteredlist.size==0) {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Sorry",
                    "No data found for $text ",
                    R.drawable.error_white,
                    0
                )
            } else {
                prepareAuthorizationList(binding.rvLeaveAuth, filteredlist)
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



    private fun filter(text: String) {

        if (approvalList.size > 0) {
            val filteredlist = ArrayList<LeaveApproveChild>()
            for (item in approvalList) {
                if (item.lvEmpName!!.toLowerCase().contains(text.lowercase(Locale.getDefault())) ||
                    item.lvType!!.toLowerCase().contains(text.lowercase(Locale.getDefault()))
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
            } else {
                prepareAuthorizationList(binding.rvLeaveAuth, filteredlist)
            }

            itemAdapter.notifyDataSetChanged()
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

    private fun approveLeaveApplication(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_fdate: String,
        lv_tdate: String, message: String
    ) {
        leaveApproveViewModel.leaveApproveConfirm(user_id, lv_trn_no, lv_ap_row, lv_fdate, lv_tdate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if ((res.code == "200") && (res.message == "Leave Applied Successfully")) {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    message,
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                binding.shimmerApproveContainer.visibility = View.VISIBLE
                                binding.shimmerApproveContainer.startShimmer()
                                getLeaveApplicationList(user)

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "Sorry failed to approve this leave application",
                                    R.drawable.error_white,
                                    0
                                )
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


    private fun rejectLeaveApplication(
        user_id: String,
        lv_trn_no: String,
        lv_ap_row: String,
        lv_deny_res: String,
        message: String

    ) {
        leaveApproveViewModel.leaveRejection(user_id, lv_trn_no, lv_ap_row, lv_deny_res)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    message,
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                binding.shimmerApproveContainer.visibility = View.VISIBLE
                                binding.shimmerApproveContainer.startShimmer()
                                getLeaveApplicationList(user)

                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    0
                                )
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


    //toolbar home
    /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

     */

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