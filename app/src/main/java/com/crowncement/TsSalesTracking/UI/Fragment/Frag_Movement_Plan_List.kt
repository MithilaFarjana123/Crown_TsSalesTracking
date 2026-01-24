package com.crowncement.TsSalesTracking.UI.Fragment

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
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.MovementViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MovementViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.MovementAppChildOneAdapter
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild1
import com.crowncement.TsSalesTracking.databinding.FragMovementPlanListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

import java.util.*
import kotlin.collections.ArrayList


class Frag_Movement_Plan_List : Fragment() {

   // lateinit var rootView: View


    private var _binding: FragMovementPlanListBinding? = null
    private val binding get() = _binding!!

    lateinit var movementViewModel: MovementViewModel
    lateinit var empList: ArrayList<EmpInfo>
    lateinit var planList: ArrayList<MovementPlanChild1>

    lateinit var loadingAnim: Dialog

    var user = ""
    var appStatus = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_movement_plan_list, container, false)

        _binding = FragMovementPlanListBinding.inflate(inflater, container, false)

        /*

        MovementViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        loadingAnim.show()
        var planMonth: Int = Utility.getCurrentDate("MM")!!.toInt()
        planMonth += 1

        movementPlanList(user, Utility.getCurrentDate("yyyy").toString(), planMonth.toString())

        onEventClickListener()

         */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely


        MovementViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        loadingAnim.show()
        var planMonth: Int = Utility.getCurrentDate("MM")!!.toInt()
        planMonth += 1

        movementPlanList(user, Utility.getCurrentDate("yyyy").toString(), planMonth.toString())

        onEventClickListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun onEventClickListener() {

        binding.fabSubmit.setOnClickListener {

            confirmDialog()
        }
    }

    private fun setupViewModel() {
        movementViewModel = ViewModelProviders.of(
            this.requireActivity(),
            MovementViewModelFactory()
        )[MovementViewModel::class.java]
    }

    private fun movementPlanList(empID: String, year: String, month: String) {

        movementViewModel.movementPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            planList = arrayListOf()
                            planList.addAll(res.data)
                            appStatus = res.plnStatus.toString()
                            if (res.plnStatus == "Approved") {
                                binding.fabSubmit.visibility = View.GONE
                            }

                            loadMovementDateInfo(res.data,res.plnStatus.toString())

                        } else {
                            binding.fabSubmit.visibility = View.GONE
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Sorry,\nyou don't have any movement plan",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }

                        loadingAnim.dismiss()
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    //todo was not commented out
                 //   binding.fabApprove.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadMovementDateInfo(dataList: ArrayList<MovementPlanChild1>,planStatus: String) {

        if (dataList.size > 0) {

            binding.rvEmpMovementL.visibility = View.VISIBLE
            val itemAdapter = MovementAppChildOneAdapter(dataList,planStatus)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvEmpMovementL.layoutManager = manager
            binding.rvEmpMovementL.adapter = itemAdapter


            binding.rvEmpMovementL.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.fabSubmit.isShown) binding.fabSubmit.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        binding.fabSubmit.show()
                    if (appStatus != "") {
                        binding.fabSubmit.visibility = View.GONE
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })


            itemAdapter.setOnItemClickListener(object :
                MovementAppChildOneAdapter.OnAdapterItemClickListener {

                override fun OnClick(v: View?, position: Int) {
                }
            })
        }

        else {
            binding.rvEmpMovementL.visibility = View.GONE
            binding.fabSubmit.visibility = View.GONE
        }

    }

    private fun confirmDialog() {
        val v: View = layoutInflater.inflate(R.layout.confirm_movement_plan_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnMoveConfirm = v.findViewById<Button>(R.id.btnMoveConfirm)
        val imgMClose = v.findViewById<ImageView>(R.id.imgMClose)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgMClose.setOnClickListener { view1: View? -> dialog.hide() }

        btnMoveConfirm.setOnClickListener { view1: View? ->

            dialog.hide()
            var submitedMonth: Int = Utility.getCurrentDate("MM")!!.toInt()
            submitedMonth += 1

            submitMovementPlan(
                user,
                Utility.getCurrentDate("yyyy").toString(),
                submitedMonth.toString()
            )
        }
    }

    private fun submitMovementPlan(empID: String, year: String, month: String) {

        loadingAnim.show()
        movementViewModel.submitMovementMonthlyPlan(empID, year, month)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code.toString() == "200") {

                                binding.fabSubmit.visibility = View.GONE

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Submitted",
                                    res.message.toString(),
                                    R.drawable.ic_checked_green,
                                    1
                                )
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

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        //todo was not commented out
                       // binding.fabApprove.visibility = View.GONE
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