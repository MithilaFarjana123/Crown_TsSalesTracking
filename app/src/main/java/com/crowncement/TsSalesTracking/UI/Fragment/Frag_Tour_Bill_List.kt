package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.crowncement.TsSalesTracking.R


import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.TourBillAdapter
import com.crowncement.TsSalesTracking.data.Model.TourExpenseResponse
import com.crowncement.TsSalesTracking.data.Model.TourExpenseResponseChild
import com.crowncement.TsSalesTracking.databinding.FragTourBillBinding

import java.util.*


class Frag_Tour_Bill_List : Fragment() {

  //  lateinit var rootView: View

    private var _binding: FragTourBillBinding? = null
    private val binding get() = _binding!!

    lateinit var picker: DatePickerDialog
    var fromDate = ""
    var toDate = ""
    lateinit var paymentList: ArrayList<TourExpenseResponseChild>
    lateinit var itemAdapter: TourBillAdapter
    var user = ""


    lateinit var tourViewModel: TourViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      //  rootView = inflater.inflate(R.layout.frag_tour_bill, container, false)

        _binding = FragTourBillBinding.inflate(inflater, container, false)

/*

        binding.shimmerBill.visibility = View.VISIBLE
        binding.shimmerBill.startShimmer()

        TourViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        val timePeriod = Utility.getAttendancePeriod().split(",")

        getTourApprovalList(user, timePeriod[0], timePeriod[1])


        binding.txtBillFrom.setText(
            Utility.changeDateFormat(
                timePeriod[0],
                "yyyy-MM-dd",
                "dd.MMM.yyyy"
            ).toString()
        )
        binding.txtBillTo.setText(
            Utility.changeDateFormat(
                timePeriod[1],
                "yyyy-MM-dd",
                "dd.MMM.yyyy"
            ).toString()
        )



        binding.txtBillFrom.setOnClickListener {

            fromDatePicker()
        }
*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        binding.shimmerBill.visibility = View.VISIBLE
        binding.shimmerBill.startShimmer()

        TourViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        val timePeriod = Utility.getAttendancePeriod().split(",")

        getTourApprovalList(user, timePeriod[0], timePeriod[1])


        binding.txtBillFrom.setText(
            Utility.changeDateFormat(
                timePeriod[0],
                "yyyy-MM-dd",
                "dd.MMM.yyyy"
            ).toString()
        )
        binding.txtBillTo.setText(
            Utility.changeDateFormat(
                timePeriod[1],
                "yyyy-MM-dd",
                "dd.MMM.yyyy"
            ).toString()
        )



        binding.txtBillFrom.setOnClickListener {

            fromDatePicker()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun fromDatePicker() {
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
                binding.txtBillFrom.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    )
                )
                toDatePicker()
            }, year, month, day
        )
        picker.show()
    }

    private fun toDatePicker() {
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
                binding.txtBillTo.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    )
                )
                binding.shimmerBill.visibility = View.VISIBLE
                binding.shimmerBill.startShimmer()
                getTourApprovalList(user, fromDate, toDate)

            }, year, month, day
        )
        picker.show()
    }

    private fun setupViewModel() {
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)
    }

    private fun getTourApprovalList(empId: String, from: String, to: String) {
        tourViewModel.getTourBillList(empId, from, to)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            paymentList = ArrayList()

                            paymentList = res.data
                            preparePaymentListData(
                                res,
                                binding.rvBill,
                                binding.BillNoData
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

    private fun preparePaymentListData(
        res: TourExpenseResponse,
        rv: RecyclerView,
        noData: LottieAnimationView
    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            rv.visibility = View.VISIBLE
            prepareList(rv, res.data)
            calculateSum()

        } else {
            binding.txtTotalBillAmount.text="Total Amount: ৳ 0 /-"
            noData.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        binding.shimmerBill.visibility = View.GONE
        binding.shimmerBill.stopShimmer()
    }

    private fun prepareList(rv: RecyclerView, items: ArrayList<TourExpenseResponseChild>) {

        itemAdapter = TourBillAdapter(items)
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object : TourBillAdapter.OnAdapterItemClickListener {
            override fun OnClick(v: View?, position: Int) {

            }

        })

    }

    private fun calculateSum() {
        var total = 0.0
        if (paymentList.size > 0) {
            for (payment in paymentList) {
                total += payment.totAmt!!
            }
            val res=Utility.formatAmount(total)
            binding.txtTotalBillAmount.text = "Total Amount: ৳ $res /-"
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