package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.PaySlipViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.PaySlipViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.formatAmount
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.data.Model.PaySlipChild
import com.crowncement.TsSalesTracking.data.Model.SalaryMonthResponseChild
import com.crowncement.TsSalesTracking.databinding.FragPayslipBinding


class Frag_Payslip : Fragment() {
  //  lateinit var rootView: View

    private var _binding: FragPayslipBinding? = null
    private val binding get() = _binding!!

    private lateinit var paySlipViewModel: PaySlipViewModel

    private var monthInfoList: ArrayList<SalaryMonthResponseChild> = ArrayList()

    private lateinit var user: String
    private lateinit var selectedMonthCode: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

      //  rootView = inflater.inflate(R.layout.frag_payslip, container, false)

        _binding = FragPayslipBinding.inflate(inflater, container, false)

/*

        PaySlipViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        setProfileInformation()

        getMonthInfo(user)
       // getMonthInfo("E00-005445")

        //On event click listener
        binding.acMonth.setOnItemClickListener { adapterView: AdapterView<*>?, _: View?, i: Int, _: Long ->
                var a = monthInfoList
            if (monthInfoList != null && monthInfoList.size > 0) {
                val monthInfo: SalaryMonthResponseChild = monthInfoList[i]
                if (monthInfo != null) {
                    selectedMonthCode = monthInfo.salNo.toString()
                    Log.e("Frag pay slip", "code: $selectedMonthCode")
                 //   getEmpSalaryInfo("E00-005445", selectedMonthCode)
                    getEmpSalaryInfo(user, selectedMonthCode)
                } else {
                    selectedMonthCode = ""
                }
            }
        }



 */


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely



        PaySlipViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        setProfileInformation()

        getMonthInfo(user)
        // getMonthInfo("E00-005445")

        //On event click listener
        binding.acMonth.setOnItemClickListener { adapterView: AdapterView<*>?, _: View?, i: Int, _: Long ->
            var a = monthInfoList
            if (monthInfoList != null && monthInfoList.size > 0) {
                val monthInfo: SalaryMonthResponseChild = monthInfoList[i]
                if (monthInfo != null) {
                    selectedMonthCode = monthInfo.salNo.toString()
                    Log.e("Frag pay slip", "code: $selectedMonthCode")
                    //   getEmpSalaryInfo("E00-005445", selectedMonthCode)
                    getEmpSalaryInfo(user, selectedMonthCode)
                } else {
                    selectedMonthCode = ""
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }








    private fun setupViewModel() {
        paySlipViewModel = ViewModelProviders.of(this.requireActivity(), PaySlipViewModelFactory())
            .get(PaySlipViewModel::class.java)
    }


    private fun setProfileInformation() {
        try {
            val profile: Data = Utility.getUserInfo(requireActivity())!!

            binding.txtPayName.text = "Name : "+ profile.userName
            binding.txtPayDesig.text =  profile.userDesig
            binding.txtPayDep.text =  profile.userDept
            binding.txtPayEmpType.text = "Employee Type : " + profile.att_status
            binding.txtPayLoc.text = "Job location : "+profile.userLocation
            binding.txtPayJoinDate.text = Utility.changeDateFormat(profile.userJoin,"yyyy-MM-dd","EEE, dd MMM yyyy")

            binding.txtPayEmp.text = Utility.getValueByKey(requireActivity(),"username")

            //SET IMAGE
            if (!(profile.userProfileImagePath.toString() == "")) {
                Glide
                    .with(this)
                    .load(EndPoint.IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    // .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(binding.imgSalaryPerson)
            }

        } catch (e: Exception) {
            Utility.commonToast(requireActivity(), "Error", e.message.toString(), 0)
        }
    }

    private fun getMonthInfo(empId: String) {
        paySlipViewModel.getSalaryMonthInfo(empId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                monthInfoList.addAll(res.data)
                                loadMonthSpinner(res.data)
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

    private fun loadMonthSpinner(monthList: ArrayList<SalaryMonthResponseChild>) {

        val items: ArrayList<String> = ArrayList()
        for (item in monthList) {
            val value = item.payTyp + " : " + item.salMon
            items += value
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        binding.acMonth.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }


    private fun getEmpSalaryInfo(empId: String, salaryCode: String) {
        paySlipViewModel.getSalaryInfo(empId, salaryCode)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                prepareData(res.data!!)
                            } else {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Error",
                                    "Some error has occured",
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

    private fun prepareData(value: PaySlipChild) {

        binding.txtBankInfo.text = "Bank: " + value.bankNam +"\n"+ "A/C: " + value.accNo
        binding.txtConcern.text = "Company: " + value.empSbu

        //INCOME
        binding.txtBasic.text = value.basic.toString()
        binding.txtConsi.text = value.cons.toString()
        binding.txtHouse.text = value.hr.toString()
        binding.txtConve.text = value.conv.toString()
        binding.txtAllowance.text = value.medic.toString()
        binding.txtCPF.text = value.cpfEarn.toString()
        binding.txtBonus.text = value.bonus.toString()
        binding.txtArrier.text = value.arrear.toString()
        binding.txtAbsent.text = value.absent.toString()
        binding.txtIncomeOthers.text = value.othEarn.toString()

        binding.txtTotalIncome.text = "৳  " + formatAmount(value.totEarn!!.toDouble())
        binding.txtEarning.text = "৳  " + formatAmount(value.totEarn!!.toDouble())

        //Deduction
        binding.txtAIT.text = value.aitDed.toString()
        binding.txtSalAdv.text = value.advDed.toString()
        binding.txtCpFDe.text = value.cpfDed.toString()
        binding.txtPF.text = value.pfCom.toString()
        binding.deMotor.text = value.motor.toString()
        binding.deLvPanel.text = value.lvpnDed.toString()
        binding.txtXMotor.text = value.exMob.toString()
        binding.txtLunch.text = value.lunch.toString()
        binding.txtExpenseOther.text = formatAmount(value.othDed!!.toDouble())
        binding.txtTotalDeduction.text = "৳  " + formatAmount(value.totDed!!.toDouble())
        binding.txtDeduction.text = "৳  " + formatAmount(value.totDed!!.toDouble())
        val payable: Float = value.totEarn!!.toFloat() - value.totDed!!.toFloat()
        binding.txtNetPayable.text = "৳  " + formatAmount(payable.toDouble())

    }

/*
    private fun setProfileInformation() {
        try {
            val profile: Data = Utility.getUserInfo(requireActivity())!!
            rootView.txtEmp.text = profile.userName
            rootView.txtEmpDesig.text = "Emp Code: " + user + " Designation: " + profile.userDesig
            rootView.txtDeptJobLo.text =
                getString(R.string.department) + profile.userDept + getString(R.string.job_location) + profile.userLocation
            rootView.txtJoiningDate.text = "Joining Date : " + Utility.changeDateFormat(
                profile.userJoin,
                "yyyy-MM-dd",
                "EEE, dd.MMM.yyyy"
            )

            //SET IMAGE
            if (profile.userProfileImagePath.toString() != "") {
                Glide
                    .with(this)
                    .load(Endpoint.IMAGE_BASE_URL + profile.userProfileImagePath)
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(rootView.imgSalaryPerson)
            }

        } catch (e: Exception) {
            Utility.commonToast(requireActivity(), "Error", e.message.toString(), 0)
        }
    }

 */


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