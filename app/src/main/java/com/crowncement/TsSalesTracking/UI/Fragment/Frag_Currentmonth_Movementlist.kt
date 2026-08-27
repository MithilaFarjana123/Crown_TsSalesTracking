package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.graphics.Color
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
import com.crowncement.TsSalesTracking.data.Adapter.MovementAppChildOneAdapter
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild1
import com.crowncement.TsSalesTracking.databinding.FragCurrentmonthMovementlistBinding
import com.crowncement.TsSalesTracking.databinding.FragMovementPlanListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Objects


class Frag_Currentmonth_Movementlist : Fragment() {

    private var _binding: FragCurrentmonthMovementlistBinding? = null
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

        _binding = FragCurrentmonthMovementlistBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
        planMonth += 0

        movementPlanList(user, Utility.getCurrentDate("yyyy").toString(), planMonth.toString())

      //  onEventClickListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
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
                            var plnStatus = res.plnStatus.toString()


                            if (res.plnStatus == "Approved") {
                                binding.listStatus.setText("Your movement plan has been approved.")
                                binding.listStatus.setTextColor(Color.parseColor("#007D4C"))

                              //  binding.fabSubmit.visibility = View.GONE
                            }else if (res.plnStatus == "" ){
                                binding.listStatus.setText("Please submit your movement plan.")

                            }else if(res.plnStatus == "Submitted"){
                                binding.listStatus.setText("Your movement plan is not approved yet.")
                             //   binding.fabSubmit.visibility = View.GONE

                                binding.listStatus.setTextColor(Color.parseColor("#FC9E14"))


                            }

                            loadMovementDateInfo(res.data,res.plnStatus.toString())

                        } else {
                         //   binding.fabSubmit.visibility = View.GONE
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
                   // if (dy > 0 || dy < 0 && binding.fabSubmit.isShown) binding.fabSubmit.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                      //  binding.fabSubmit.show()
                    if (appStatus != "") {
                     //   binding.fabSubmit.visibility = View.GONE
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
         //   binding.fabSubmit.visibility = View.GONE
        }

    }





}