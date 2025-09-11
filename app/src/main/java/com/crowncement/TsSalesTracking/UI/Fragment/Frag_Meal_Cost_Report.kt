package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.UI.viewmodel.MealViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MealViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.formatAmount
import com.crowncement.TsSalesTracking.data.Adapter.MealCostAdapter
import com.crowncement.TsSalesTracking.data.Model.MealCost
import com.crowncement.TsSalesTracking.databinding.FragMealCostReportBinding
import java.util.*
import kotlin.collections.ArrayList


class Frag_Meal_Cost_Report : Fragment() {

   // lateinit var rootView: View
   private var _binding: FragMealCostReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var picker: DatePickerDialog
    private lateinit var mealViewModel: MealViewModel
    lateinit var user: String
    lateinit var fromDate: String
    lateinit var toDate: String

    lateinit var mealCostList: ArrayList<MealCost>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  rootView = inflater.inflate(R.layout.frag_meal_cost_report, container, false)
        _binding = FragMealCostReportBinding.inflate(inflater, container, false)

/*

        binding.shimmerMealLayout.visibility = View.VISIBLE
        binding.shimmerMealLayout.startShimmer()


        MealViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        val timePeriod = Utility.getAttendancePeriod().split(",")

        fromDatePicker()

        onEventClickListener()
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely


        binding.shimmerMealLayout.visibility = View.VISIBLE
        binding.shimmerMealLayout.startShimmer()


        MealViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username")!!

        val timePeriod = Utility.getAttendancePeriod().split(",")

        fromDatePicker()

        onEventClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        mealViewModel = ViewModelProviders.of(this.requireActivity(), MealViewModelFactory())
            .get(MealViewModel::class.java)
    }

    private fun onEventClickListener() {

        binding.txtFrDate.setOnClickListener {

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
                    binding.txtFrDate.setText(
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

//        rootView.txtTDate.setOnClickListener {
//
//            val cldr = Calendar.getInstance()
//            val day = cldr[Calendar.DAY_OF_MONTH]
//            val month = cldr[Calendar.MONTH]
//            val year = cldr[Calendar.YEAR]
//            picker = DatePickerDialog(
//                requireContext(),
//                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
//                    val monthNumber = monthOfYear + 1
//                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
//                    toDate = selectedDate
//                    rootView.txtTDate.setText(
//                        Utility.changeDateFormat(
//                            selectedDate,
//                            "yyyy-MM-dd",
//                            "dd MMM yyyy"
//                        )
//                    )
//                    rootView.shimmer_meal_layout.visibility = View.VISIBLE
//                    rootView.shimmer_meal_layout.startShimmer()
//                    getMealCost(user, fromDate, toDate)
//
//                }, year, month, day
//            )
//            picker.show()
//        }
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
                binding.txtFrDate.setText(
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
                binding.txtTDate.setText(
                    Utility.changeDateFormat(
                        selectedDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    )
                )
                binding.shimmerMealLayout.visibility = View.VISIBLE
                binding.shimmerMealLayout.startShimmer()
                getMealCost(user, fromDate, toDate)

            }, year, month, day
        )
        picker.show()
    }


    private fun getMealCost(empId: String, fDate: String, tDate: String) {
        mealViewModel.mealCost(empId, fDate, tDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->

                            if (res.code == "200") {
                                binding.noMealData.visibility = View.GONE
                                binding.rvMealList.visibility = View.VISIBLE
                                mealCostList = res.data
                                prepareMealCostRV(res.data)
                            } else {
                                binding.noMealData.visibility = View.VISIBLE
                                binding.rvMealList.visibility = View.GONE
                            }

                            binding.shimmerMealLayout.visibility = View.GONE
                            binding.shimmerMealLayout.stopShimmer()

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

    private fun prepareMealCostRV(items: ArrayList<MealCost>) {
        val itemAdapter = MealCostAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

        binding.rvMealList.layoutManager = rLayoutmanager
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMealList.layoutManager = manager
        binding.rvMealList.adapter = itemAdapter

        binding.rvMealList.addItemDecoration(
            DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        itemAdapter.setOnItemClickListener(object :
            MealCostAdapter.OnAdapterItemClickListener {
            override fun OnSelectSubMenu(v: View?, position: Int) {

            }
        })

        calculateTotal(items)
    }

    private fun calculateTotal(items: ArrayList<MealCost>) {

        var totalMealQty = 0.0
        var totalMealCost = 0.0
        var totalMealOverhead = 0.0

        for (meal in items) {
            totalMealQty += meal.lnQty!!.toFloat()
            totalMealCost += meal.lnRate!!.toFloat()
            totalMealOverhead += meal.lnOvh!!.toFloat()
        }
        binding.txtTotalMealQty.text = " ${totalMealQty.toInt()}"
        binding.txtTotalMealrate.text = "৳  " + formatAmount(totalMealCost)
        binding.txtTotalOverHead.text = "৳ " + formatAmount(totalMealOverhead)

        val totalBill = totalMealCost + totalMealOverhead
        //Total bill calculation
        binding.txtTotalPayable.text = "৳ " + formatAmount(totalBill)
    }


}