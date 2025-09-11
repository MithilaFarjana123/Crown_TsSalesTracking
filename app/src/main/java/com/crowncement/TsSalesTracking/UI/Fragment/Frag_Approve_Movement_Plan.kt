package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.MovementViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MovementViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getCurrentDate
import com.crowncement.TsSalesTracking.data.Adapter.EmpSelectionAdapter
import com.crowncement.TsSalesTracking.data.Adapter.MovementAppChildOneAdapter
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild1
import com.crowncement.TsSalesTracking.databinding.FragApproveMovementPlanBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList


class Frag_Approve_Movement_Plan : Fragment() {
 //   lateinit var rootView: View
 private var _binding: FragApproveMovementPlanBinding? = null
    private val binding get() = _binding!!

    lateinit var movementViewModel: MovementViewModel
    lateinit var empList: ArrayList<EmpInfo>
    lateinit var planList: ArrayList<MovementPlanChild1>

    var user = ""
    var moveNo = ""
    var appStatus = ""

    lateinit var loadingAnim: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_approve_movement_plan, container, false)
        _binding = FragApproveMovementPlanBinding.inflate(inflater, container, false)

/*
        MovementViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        //zonalJuniorList("E11-001982", Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())
        zonalJuniorList(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())


        binding.fabApprove.setOnClickListener {

            confirmDialog()
        }

 */

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        MovementViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        //zonalJuniorList("E11-001982", Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())
        zonalJuniorList(user, getCurrentDate("yyyy-MM-dd HH:mm:ss").toString())


        binding.fabApprove.setOnClickListener {

            confirmDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        movementViewModel = ViewModelProviders.of(
            this.requireActivity(), MovementViewModelFactory()
        )[MovementViewModel::class.java]
    }

    private fun zonalJuniorList(empID: String, dateTime: String) {
        movementViewModel.getZonalJunior(empID, dateTime)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200" && res.data.size > 0) {
                            empList = ArrayList()
                            empList.addAll(res.data)
                            loadEmpInfo(res.data)

                        } else {
                            Utility.getBaseMessage(
                                requireActivity(),
                                "No subordinate set yet",
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

    private fun loadEmpInfo(dataList: ArrayList<EmpInfo>) {

        if (dataList.size > 0) {
            val itemAdapter = EmpSelectionAdapter(dataList)
            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvEmp.layoutManager = manager
            binding.rvEmp.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                EmpSelectionAdapter.OnAdapterItemClickListener {

                override fun OnClick(v: View?, position: Int) {

                    val empId = empList[position].empId

                    val appMonth: Int = getCurrentDate("MM")!!.toInt() + 1

                    binding.txtSelectedUser.text =Utility.changeDateFormat(appMonth.toString(),"MM","MMM") +" Movement Plan of: \n" + empList[position].empName

                    binding.fabApprove.visibility = View.VISIBLE

                    loadingAnim.show()

                    movementPlanList(empId.toString(),getCurrentDate("yyyy").toString(),appMonth.toString())

                }
            })
        }
    }

    private fun movementPlanList(empID: String, year: String, month: String) {

        movementViewModel.movementPlanList(empID, year, month)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            planList = arrayListOf()
                            planList.addAll(res.data)
                            moveNo = res.trnNo.toString()
                            appStatus=res.plnStatus.toString()
                            if (appStatus== "Approved") {
                                binding.fabApprove.visibility = View.GONE
                            }
                            loadMovementDateInfo(res.data,res.plnStatus.toString())


                        } else {
                            binding.fabApprove.visibility = View.GONE
                            Utility.getBaseMessage(
                                requireActivity(),
                                "No plan found",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                            val noDataList: ArrayList<MovementPlanChild1> = ArrayList()
                            loadMovementDateInfo(noDataList,"")
                        }

                        loadingAnim.dismiss()
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    loadingAnim.dismiss()
                    binding.fabApprove.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadMovementDateInfo(dataList: ArrayList<MovementPlanChild1>, planStatus:String) {

        if (dataList.size > 0) {

            binding.rvEmpMovement.visibility = View.VISIBLE
            val itemAdapter = MovementAppChildOneAdapter(dataList,planStatus)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvEmpMovement.layoutManager = manager
            binding.rvEmpMovement.adapter = itemAdapter


            binding.rvEmpMovement.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.fabApprove.isShown) binding.fabApprove.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.fabApprove.show()
                        if (appStatus== "Approved") {
                            binding.fabApprove.visibility = View.GONE
                        }
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                }
            })


            itemAdapter.setOnItemClickListener(object :
                MovementAppChildOneAdapter.OnAdapterItemClickListener {

                override fun OnClick(v: View?, position: Int) {
                }
            })
        }

        else {
            binding.rvEmpMovement.visibility = View.GONE
            binding.fabApprove.visibility = View.GONE
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

            approveMovementPlan(user, moveNo)
            dialog.hide()

        }
    }

    private fun approveMovementPlan(empID: String, movNo: String) {

        loadingAnim.show()
        movementViewModel.approveMovementMonthlyPlan(empID, movNo)?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code.toString() == "200") {

                            binding.fabApprove.visibility = View.GONE

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Approved",
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
                    binding.fabApprove.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}