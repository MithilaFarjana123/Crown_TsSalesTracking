package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.TourViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.TourViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.getDatesBetween
import com.crowncement.TsSalesTracking.data.Adapter.ExpenseDateListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.ExpenseEntryListAdapter
import com.crowncement.TsSalesTracking.data.Model.ExpenseClaimChild
import com.crowncement.TsSalesTracking.data.Model.TourResponseChild
import com.crowncement.TsSalesTracking.databinding.FragTourExpenseClaimListBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class Frag_Tour_Expense_Claim_List : Fragment() {

 //   lateinit var rootView: View
 private var _binding: FragTourExpenseClaimListBinding? = null
    private val binding get() = _binding!!

    lateinit var adapterChildView: View
    lateinit var travelInfo: TourResponseChild

    lateinit var dateList: ArrayList<String>

    lateinit var tourViewModel: TourViewModel

    var selectedDate = ""
    lateinit var loadingAnim: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {



        _binding = FragTourExpenseClaimListBinding.inflate(inflater, container, false)

/*
 //       rootView = inflater.inflate(R.layout.frag_tour_expense_claim_list, container, false)


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireContext()), "P l e a s e    w a i t"
        )

        getPreviousFragmentValue()
        onClickEventListener()

        TourViewModelFactory()
        setupViewModel()
*/
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(), Dialog(requireContext()), "P l e a s e    w a i t"
        )

        getPreviousFragmentValue()
        onClickEventListener()

        TourViewModelFactory()
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun onClickEventListener() {
        binding.claimExpense.setOnClickListener {

            if (selectedDate.isNotEmpty()) {
                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_tour_expense)
            } else {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Warning",
                    "Please select any date from the above",
                    R.drawable.error_white,
                    0
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPreviousFragmentValue() {
        val selectedExpenseInfo = Utility.getValueByKey(requireActivity(), "selectedExpenseInfo")
        val gson = Gson()

        if (selectedExpenseInfo.toString().isNotEmpty()) {
            travelInfo = gson.fromJson(selectedExpenseInfo, TourResponseChild::class.java)
            binding.txtToutNumber.text = "Tour No: ${travelInfo.trTrno}"
            dateList = ArrayList()
            dateList = getDatesBetween(travelInfo.trFdate.toString(), travelInfo.trTdate.toString())
            loadLeaveTypeInfo(dateList)
        }
    }

    private fun loadLeaveTypeInfo(dataList: ArrayList<String>) {

        if (dataList.size > 0) {
            val itemAdapter = ExpenseDateListAdapter(dataList)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            binding.rvDate.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvDate.layoutManager = manager
            binding.rvDate.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                ExpenseDateListAdapter.OnAdapterItemClickListener {

                override fun OnClick(v: View?, position: Int) {

                    selectedDate = ""
                    selectedDate = dataList[position]
                    Utility.storeKeyValue(requireActivity(), "selectedDate", selectedDate)
                    val user = Utility.getValueByKey(requireActivity(), "username").toString()

                    //  row_index = position
                    //  adapterChildView = v!!
                    //  itemAdapter.notifyDataSetChanged()
                    //   changeBG(adapterChildView, row_index)
                    //  Toast.makeText(requireContext(), dataList[position], Toast.LENGTH_LONG).show()
                    loadingAnim.show()
                    getExpenseList(user, travelInfo.trTrno.toString(), selectedDate)

//                    val gson = Gson()
//                    val json = gson.toJson(dataList.get(position))
//                    Utility.storeKeyValue(requireActivity(), "selectedLvInfo", json)
//                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
//                    navController.navigate(R.id.nav_Leave_List)
                }
            })
        }
    }

    private fun setupViewModel() {
        tourViewModel = ViewModelProviders.of(this.requireActivity(), TourViewModelFactory())
            .get(TourViewModel::class.java)
    }

    private fun getExpenseList(userName: String, trNumber: String, dateTime: String) {
        tourViewModel.getExpenseClaimList(userName, trNumber, dateTime).observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        if (res.code == "200") {
                            loadExpenseList(res.data)
                        } else {
                            binding.rvClaimList.visibility = View.GONE
                            binding.txtExpenseNoData.visibility = View.VISIBLE
                        }
                        loadingAnim.dismiss()

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

    private fun loadExpenseList(dataList: ArrayList<ExpenseClaimChild>) {

        if (dataList.size > 0) {
            binding.rvClaimList.visibility = View.VISIBLE
            binding.txtExpenseNoData.visibility = View.GONE

            val itemAdapter = ExpenseEntryListAdapter(dataList)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            binding.rvClaimList.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvClaimList.layoutManager = manager
            binding.rvClaimList.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                ExpenseEntryListAdapter.OnAdapterItemClickListener {

                override fun showDocument(v: View?, position: Int) {
                    if (dataList[position].trImg.toString()
                            .isNotEmpty()
                    ) showAttachedDoc(dataList[position].trImg.toString())
                }
            })

        } else {
            binding.rvClaimList.visibility = View.GONE
            binding.txtExpenseNoData.visibility = View.VISIBLE
        }
    }

    private fun showAttachedDoc(docUrl: String) {
        val v: View = layoutInflater.inflate(R.layout.expense_claim_doc_preview_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val imgShowDoc: ImageView = v.findViewById(R.id.imgEShowDoc)
        val txtEDocCancel: TextView = v.findViewById(R.id.txtEDocCancel)
        val btnEOK: MaterialButton = v.findViewById(R.id.btnEOK)
        val noEDoc: LottieAnimationView = v.findViewById(R.id.noEDoc)

        if (docUrl.isNotEmpty()) {

            imgShowDoc.visibility = View.VISIBLE
            noEDoc.visibility = View.GONE
            Glide.with(v.context).load(docUrl).error(R.drawable.ic_baseline_broken_image)
                .centerCrop().placeholder(R.drawable.ic_baseline_broken_image).into(imgShowDoc)
        } else {
            imgShowDoc.visibility = View.GONE
            noEDoc.visibility = View.VISIBLE
        }



        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        txtEDocCancel.setOnClickListener {
            dialog.hide()
        }
        btnEOK.setOnClickListener {
            dialog.hide()
        }

    }


}