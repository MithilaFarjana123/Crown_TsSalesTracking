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
import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.TravelApprovalListAdapter
import com.crowncement.TsSalesTracking.data.Model.TourApproveResponse
import com.crowncement.TsSalesTracking.data.Model.TourApproveResponseChild
import com.crowncement.TsSalesTracking.databinding.FragTourApproveListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class Frag_Tour_Approve_List : Fragment() {


//    lateinit var rootView: View
    private var _binding: FragTourApproveListBinding? = null
    private val binding get() = _binding!!

    lateinit var tourViewModel: TourViewModel
    lateinit var approvalList: ArrayList<TourApproveResponseChild>
    lateinit var user: String
    lateinit var itemAdapter: TravelApprovalListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // rootView = inflater.inflate(R.layout.frag_tour_approve_list, container, false)

        _binding = FragTourApproveListBinding.inflate(inflater, container, false)


/*

        binding.shimmerTourApprove.visibility = View.VISIBLE
        binding.shimmerTourApprove.startShimmer()

        TourViewModelFactory()
        setupViewModel()
        user = Utility.getValueByKey(requireActivity(), "username")!!

        getTourApprovalList(user)
        //  getTourApprovalList("E11-001795")
        requireActivity().intent.putExtra("send_to","")
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely



        binding.shimmerTourApprove.visibility = View.VISIBLE
        binding.shimmerTourApprove.startShimmer()

        TourViewModelFactory()
        setupViewModel()
        user = Utility.getValueByKey(requireActivity(), "username")!!

        getTourApprovalList(user)
        //  getTourApprovalList("E11-001795")
        requireActivity().intent.putExtra("send_to","")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)
    }

    private fun getTourApprovalList(empId: String) {
        tourViewModel.getTourApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            approvalList = res.data
                            successApprovalList(
                                res,
                                binding.rvTourAuth,
                                binding.txtTourAuthNoData
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
        res: TourApproveResponse,
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

        binding.shimmerTourApprove.visibility = View.GONE
        binding.shimmerTourApprove.stopShimmer()
    }

    private fun prepareAuthorizationList(
        rv: RecyclerView,
        items: ArrayList<TourApproveResponseChild>
    ) {

        itemAdapter = TravelApprovalListAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object :
            TravelApprovalListAdapter.OnAdapterItemClickListener {

            override fun clickAccept(v: View?, position: Int) {

                approveBottomSheet(approvalList[position])

            }

            override fun clickReject(v: View?, position: Int) {

                rejectBottomSheet(approvalList[position])

            }

        })

    }


    private fun approveBottomSheet(data: TourApproveResponseChild) {
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
            "You are going to approve " + data.trEmpName + " " + Utility.changeDateFormat(
                data.trFdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + "to " + Utility.changeDateFormat(
                data.trTdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + " travel application."

        Glide
            .with(v.context)
            .load(data.trEmpPic.toString())
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

            val message = "You have approved  " + data.trEmpName + "  " + Utility.changeDateFormat(
                data.trFdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + "to " + Utility.changeDateFormat(
                data.trTdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + "  travel application."


            tourApprove(
                data.trEmp.toString(),
                data.trTrno.toString(),
                data.trAppRow.toString(),
                data.trFdt.toString(),
                data.trTdt.toString(),
                message
            )
        }
    }

    private fun tourApprove(
        user_id: String,
        tr_trn_no: String,
        tr_ap_row: String,
        tr_fdate: String,
        tr_tdate: String,
        message: String
    ) {
        tourViewModel.tourApprove(user_id, tr_trn_no, tr_ap_row, tr_fdate, tr_tdate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Approved",
                                    message,
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                binding.shimmerTourApprove.visibility = View.VISIBLE
                                binding.shimmerTourApprove.startShimmer()
                                getTourApprovalList(user)
                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    2
                                )
                            }

                            binding.shimmerTourApprove.visibility = View.VISIBLE
                            binding.shimmerTourApprove.startShimmer()
                            user = Utility.getValueByKey(requireActivity(), "username")!!
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

    private fun rejectBottomSheet(data: TourApproveResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.leave_application_reject, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val btnAppYes: MaterialButton = v.findViewById(R.id.btnAppYes)
        val btnAppNo: MaterialButton = v.findViewById(R.id.btnAppNo)
        val rejMessage: TextView = v.findViewById(R.id.rejMessage)
        val txtHeadTitle: TextView = v.findViewById(R.id.txtHeadTitle)
        txtHeadTitle.text = "Reject Travel Application"
        val imgAppReject: CircleImageView = v.findViewById(R.id.imgAppReject)
        val reason: TextInputEditText = v.findViewById(R.id.txtRejectReason)
        val layoutRejectReason: TextInputLayout = v.findViewById(R.id.layoutRejectReason)

        rejMessage.text =
            "You are going to reject " + data.trEmpName + " " + Utility.changeDateFormat(
                data.trFdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + "to " + Utility.changeDateFormat(
                data.trTdt,
                "yyyy-MM-dd",
                "MMM, dd yyyy"
            ) + " travel application."

        Glide
            .with(v.context)
            .load(data.trEmpPic.toString())
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

                val message =
                    "You have rejected " + data.trEmpName + " " + Utility.changeDateFormat(
                        data.trFdt,
                        "yyyy-MM-dd",
                        "MMM, dd yyyy"
                    ) + "to" + Utility.changeDateFormat(
                        data.trTdt,
                        "yyyy-MM-dd",
                        "MMM, dd yyyy"
                    ) + " travel application."

                tourReject(
                    data.trEmp.toString(),
                    data.trTrno.toString(),
                    data.trAppRow.toString(),
                    reason.text.toString(),
                    message
                )

            } else {
                layoutRejectReason.error =
                    "Please provide a reason for rejecting this travel application "
                layoutRejectReason.isErrorEnabled = true
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.tour_approve_list_search, menu)
        val searchView = menu.findItem(R.id.tourEmpSearch).actionView as SearchView
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
            val filteredlist = java.util.ArrayList<TourApproveResponseChild>()
            for (item in approvalList) {
                if (item.trEmpName!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) ||
                    item.trEmp!!.lowercase(Locale.getDefault())
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
                prepareAuthorizationList(binding.rvTourAuth, filteredlist)
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

    private fun tourReject(
        user_id: String,
        tr_trn_no: String,
        tr_ap_row: String,
        tr_deny_res: String,
        message: String
    ) {
        tourViewModel.tourReject(user_id, tr_trn_no, tr_ap_row, tr_deny_res)
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
                                binding.shimmerTourApprove.visibility = View.VISIBLE
                                binding.shimmerTourApprove.startShimmer()
                                getTourApprovalList(user)
                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    res.message.toString(),
                                    R.drawable.error_white,
                                    2
                                )
                            }
                            binding.shimmerTourApprove.visibility = View.VISIBLE
                            binding.shimmerTourApprove.startShimmer()
                            user = Utility.getValueByKey(requireActivity(), "username")!!
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