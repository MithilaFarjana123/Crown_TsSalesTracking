package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LateViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LateViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.common.Utility.getBaseMessage
import com.crowncement.TsSalesTracking.data.Adapter.LateApprovalListAdapter
import com.crowncement.TsSalesTracking.data.Model.LateApproveChild
import com.crowncement.TsSalesTracking.data.Model.LateApproveResponse
import com.crowncement.TsSalesTracking.databinding.FragLateApprovalBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class Frag_Late_Approval : Fragment() {

    lateinit var lateApproveViewModel: LateViewModel
    lateinit var approvalList: ArrayList<LateApproveChild>

   // lateinit var rootView: View
   private var _binding: FragLateApprovalBinding? = null
    private val binding get() = _binding!!

    lateinit var user: String

    lateinit var itemAdapter: LateApprovalListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // rootView = inflater.inflate(R.layout.frag_late_approval, container, false)

        _binding = FragLateApprovalBinding.inflate(inflater, container, false)

/*
        setHasOptionsMenu(true)

        binding.shimmerLateApprove.visibility = View.VISIBLE
        binding.shimmerLateApprove.startShimmer()

        LateViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        //getLateApprovalList("E11-001795")
        getLateApprovalList(user)


        requireActivity().intent.putExtra("send_to","")


 */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

        setHasOptionsMenu(true)

        binding.shimmerLateApprove.visibility = View.VISIBLE
        binding.shimmerLateApprove.startShimmer()

        LateViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        //getLateApprovalList("E11-001795")
        getLateApprovalList(user)


        requireActivity().intent.putExtra("send_to","")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        lateApproveViewModel = ViewModelProviders.of(this.requireActivity(), LateViewModelFactory())
            .get(LateViewModel::class.java)
    }

    private fun getLateApprovalList(empId: String) {
        lateApproveViewModel.lateApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            approvalList = res.data
                            successApprovalList(
                                res,
                                binding.rvLateAuth,
                                binding.txtLateAuthNoData
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
        res: LateApproveResponse,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            rv.visibility = View.VISIBLE
            prepareAuthorizationList(rv, res.data)

        } else {
            noData.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        binding.shimmerLateApprove.visibility = View.GONE
        binding.shimmerLateApprove.stopShimmer()
    }

    private fun prepareAuthorizationList(rv: RecyclerView, items: ArrayList<LateApproveChild>) {

        itemAdapter = LateApprovalListAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object :
            LateApprovalListAdapter.OnAdapterItemClickListener {

            override fun clickAccept(v: View?, position: Int) {

                approveBottomSheet(approvalList[position])

            }

            override fun clickReject(v: View?, position: Int) {

                rejectBottomSheet(approvalList[position])

            }

        })

    }

    private fun approveBottomSheet(data: LateApproveChild) {
        val v: View = layoutInflater.inflate(R.layout.application_confirm, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnApprove: MaterialButton = v.findViewById(R.id.btnApplicationApprove)
        val btnReject: MaterialButton = v.findViewById(R.id.btnApplicationReject)
        val txtCancel: TextView = v.findViewById(R.id.txtCancel)
        val txtMessage: TextView = v.findViewById(R.id.txtMessage)
        val imgConfirm: CircleImageView = v.findViewById(R.id.imgConfirm)


        txtMessage.text =
            "You are going to approve " + data.ltEmpName + " " + changeDateFormat(
                data.ltDt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + " late application."

        Glide
            .with(v.context)
            .load(data.ltEmpPic.toString())
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
            val message = "You have approved  " + data.ltEmpName + "  " + changeDateFormat(
                data.ltDt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + "  late application."

            lateApprove(
                data.ltEmp.toString(),
                data.ltTrno.toString(),
                data.ltAppRow.toString(),
                data.ltDt.toString(),
                message
            )
        }
    }

    private fun rejectBottomSheet(data: LateApproveChild) {
        val v: View = layoutInflater.inflate(R.layout.leave_application_reject, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnAppYes: MaterialButton = v.findViewById(R.id.btnAppYes)
        val btnAppNo: MaterialButton = v.findViewById(R.id.btnAppNo)
        val rejMessage: TextView = v.findViewById(R.id.rejMessage)
        val txtHeadTitle: TextView = v.findViewById(R.id.txtHeadTitle)
        txtHeadTitle.text = "Reject Late Application"
        val imgAppReject: CircleImageView = v.findViewById(R.id.imgAppReject)
        val reason: TextInputEditText = v.findViewById(R.id.txtRejectReason)
        val layoutRejectReason: TextInputLayout = v.findViewById(R.id.layoutRejectReason)

        rejMessage.text =
            "You are going to reject " + data.ltEmpName + " " + Utility.changeDateFormat(
                data.ltDt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + " late application."

        Glide
            .with(v.context)
            .load(data.ltEmpPic.toString())
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

                val message = "You have rejected " + data.ltEmpName + " " + changeDateFormat(
                    data.ltDt,
                    "yyyy-MM-dd",
                    "MMM, dd yyyy"
                ) + " late application."

                lateReject(
                    data.ltEmp.toString(),
                    data.ltTrno.toString(),
                    data.ltAppRow.toString(),
                    reason.text.toString(),
                    message
                )

            } else {
                layoutRejectReason.error =
                    "Please provide a reason for rejecting this late application "
                layoutRejectReason.isErrorEnabled = true
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.late_approve_list_search, menu)
        val searchView = menu.findItem(R.id.lateEmpSearch).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
    }

    private fun filter(text: String) {

        if (approvalList != null && approvalList.size > 0) {
            val filteredlist = java.util.ArrayList<LateApproveChild>()
            for (item in approvalList) {
                if (item.ltEmpName!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) ||
                    item.ltEmp!!.lowercase(Locale.getDefault())
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
            } else {
                prepareAuthorizationList(binding.rvLateAuth, filteredlist)
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

    private fun lateApprove(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_date: String, message: String
    ) {
        lateApproveViewModel.lateApprove(user_id, lt_trn_no, lt_ap_row, lt_date)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                getBaseMessage(
                                    requireActivity(),
                                    "Approved",
                                    message,
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                binding.shimmerLateApprove.visibility = View.VISIBLE
                                binding.shimmerLateApprove.startShimmer()
                                getLateApprovalList(user)

                            } else {
                                getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    2
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

    private fun lateReject(
        user_id: String,
        lt_trn_no: String,
        lt_ap_row: String,
        lt_deny_res: String, message: String
    ) {
        lateApproveViewModel.lateReject(user_id, lt_trn_no, lt_ap_row, lt_deny_res)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                getBaseMessage(
                                    requireActivity(),
                                    "Approved",
                                    message,
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                binding.shimmerLateApprove.visibility = View.VISIBLE
                                binding.shimmerLateApprove.startShimmer()
                                getLateApprovalList(user)

                            } else {
                                getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    2
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

}