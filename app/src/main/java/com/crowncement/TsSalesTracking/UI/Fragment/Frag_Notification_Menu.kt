package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.LateViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.LeaveViewModel
import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LateViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LeaveViewModelFactory
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.databinding.FragNotificationMenuBinding


class Frag_Notification_Menu : Fragment() {

  //  lateinit var rootView: View

    private var _binding: FragNotificationMenuBinding? = null
    private val binding get() = _binding!!
    lateinit var leaveApproveViewModel: LeaveViewModel
    lateinit var lateViewModel: LateViewModel
    lateinit var tourViewModel: TourViewModel
    lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //TODO------------------------> Initiate UI

        _binding = FragNotificationMenuBinding.inflate(inflater, container, false)

        
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        //  rootView = inflater.inflate(R.layout.frag_notification_menu, container, false)
        LeaveViewModelFactory()
        LateViewModelFactory()
        TourViewModelFactory()
        setupViewModel()

        //TODO---------------->Leave Approval List

        user = Utility.getValueByKey(requireActivity(), "username")!!
        getLeaveApplicationList(user)

        getAppliedLateList(user)

        getTourApprovalList(user)

        //getLeaveApplicationList("E00-001312")

        //TODO ------------> Click event

        binding.cardLeaveApproval.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_leave_app_auth)
        }

        binding.layoutTourApprove.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_tour_approve)
        }

        binding.cardLateApprove.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_late_approve)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        leaveApproveViewModel =
            ViewModelProviders.of(this.requireActivity(), LeaveViewModelFactory())
                .get(LeaveViewModel::class.java)
        lateViewModel = ViewModelProviders.of(this.requireActivity(), LateViewModelFactory())
            .get(LateViewModel::class.java)
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)
    }

    private fun getLeaveApplicationList(
        empId: String
    ) {
        leaveApproveViewModel.leaveApprovalList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.data.size > 0) {
                                binding.countLeaveApplication.text = res.data.size.toString()
                            } else {
                                binding.countLeaveApplication.text = "0"
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


    private fun getAppliedLateList(empId: String) {
        lateViewModel.lateApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code.equals("200") && res.data != null) {

                                binding.txtLateAppCount.text = res.data.size.toString()
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


    private fun getTourApprovalList(empId: String) {
        tourViewModel.getTourApproveList(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code.equals("200") && res.data != null) {
                                binding.txtTourApprove.text = res.data.size.toString()
                            }
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                        Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
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