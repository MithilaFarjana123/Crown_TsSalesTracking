package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.MealViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MealViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.common.Utility.getBaseMessage
import com.crowncement.TsSalesTracking.data.Model.MealAllocationResponse
import com.crowncement.TsSalesTracking.databinding.FragApplyMealBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class Frag_Apply_Meal : Fragment() {

    private var _binding: FragApplyMealBinding? = null
    private val binding get() = _binding!!

    lateinit var user: String
    private lateinit var mealViewModel: MealViewModel
    lateinit var imageList: ArrayList<String>
    lateinit var mealInfo: MealAllocationResponse
  //  private lateinit var rootView: View
    lateinit var convertedMenuText: String




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragApplyMealBinding.inflate(inflater, container, false)

        /*

      //  rootView =inflater.inflate(R.layout.frag_apply_meal, container, false)
        // Inflate the layout for this fragment


        initiateUI()

        binding.btnCancelMeal.setOnClickListener {
            binding.btnCancelMeal.isEnabled = false

            cancelMealDialog()
        }

        binding.btnApplyMeal.setOnClickListener {
            binding.btnApplyMeal.isEnabled = false
            confirmMeal()
        }

        */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        //  rootView =inflater.inflate(R.layout.frag_apply_meal, container, false)
        // Inflate the layout for this fragment


        initiateUI()

        binding.btnCancelMeal.setOnClickListener {
            binding.btnCancelMeal.isEnabled = false

            cancelMealDialog()
        }

        binding.btnApplyMeal.setOnClickListener {
            binding.btnApplyMeal.isEnabled = false
            confirmMeal()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun initiateUI() {
        MealViewModelFactory()
        setupViewModel()
        user = Utility.getValueByKey(requireActivity(), "username")!!
        getMealInfo(user, Utility.getCurrentDate("yyyy-MM-dd").toString())
        imageList = ArrayList()
    }

    private fun setupViewModel() {
        mealViewModel = ViewModelProviders.of(this.requireActivity(), MealViewModelFactory())
            .get(MealViewModel::class.java)
    }


    private fun getMealInfo(empId: String, mealDate: String) {
        mealViewModel.getMealInfo(empId, mealDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                mealInfo = res
                                prepareInfo(res)
                            }
                            else{
                                getBaseMessage(requireActivity(),"No data found","Sorry, You don't have any meal info ",R.drawable.error_white,0)
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


    private fun prepareInfo(info: MealAllocationResponse) {

        val mealDate = changeDateFormat(info.date, "yyyy-MM-dd", " EEE, dd.MMM.yyyy").toString()
/*
        //set images to list
        loopImages.add(ConstantValue.rice_Img)
        loopImages.add(ConstantValue.khichuri_Img)
        loopImages.add(ConstantValue.daal_Img)
        loopImages.add(ConstantValue.salad_Img)
        loopImages.add(ConstantValue.beef_Img)
        loopImages.add(ConstantValue.chicken_Img)
        loopImages.add(ConstantValue.egg_Img)
        loopImages.add(ConstantValue.fish_Img)
        loopImages.add(ConstantValue.vorta_Img)



        startSlider(rootView.imgChicken)
        startSlider(rootView.imgEgg)
        startSlider(rootView.imgBeef)

 */

      //  binding.txtMealDate.text = mealDate

        _binding?.let { binding ->
            binding.txtMealDate.text = mealDate

            binding.txtItems.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(" মেনু : " + info.menu, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(info.menu)
            }

            convertedMenuText = binding.txtItems.text.toString()
            Log.e("Frag_Meal", convertedMenuText)

            when {
                !info.status.equals("Active") -> {
                    binding.btnCancelMeal.visibility = View.GONE
                }

                info.status != "" -> {
                    binding.btnApplyMeal.visibility = View.GONE
                }

                else -> {
                    binding.btnApplyMeal.visibility = View.GONE
                    binding.btnCancelMeal.visibility = View.GONE
                }
            }
        }

    }


    private fun cancelMealDialog() {
        val v: View = layoutInflater.inflate(R.layout.cancel_meal_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnCanMeal = v.findViewById<Button>(R.id.btnCanMeal)
        val imgClose = v.findViewById<ImageView>(R.id.imgClose)
        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgClose.setOnClickListener { view1: View? -> dialog.hide() }

        btnCanMeal.setOnClickListener { view1: View? ->
            imgClose.isEnabled = false
            cancelMealEntry(user, mealInfo.date.toString(), dialog)
            dialog.hide()
        }
    }


    private fun cancelMealEntry(empId: String, mealDate: String, dialog: Dialog) {
        mealViewModel.cancelMeal(empId, mealDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Your meal order has bean canceled",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            } else {
                                getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "sorry, please try again after some times.",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            }
                            dialog.dismiss()
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


    private fun confirmMeal() {
        val v: View = layoutInflater.inflate(R.layout.confirm_meal_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnConMeal = v.findViewById<Button>(R.id.btnConMeal)
        val imgClose = v.findViewById<ImageView>(R.id.imgClose)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        imgClose.setOnClickListener { view1: View? -> dialog.hide() }

        btnConMeal.setOnClickListener { view1: View? ->
            btnConMeal.isEnabled = false

            saveMealEntry(user, mealInfo.date.toString(), dialog)
            dialog.hide()
        }
    }

    private fun saveMealEntry(empId: String, mealDate: String, dialog: Dialog) {
        mealViewModel.confirmMeal(empId, mealDate)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200") {
                                getBaseMessage(
                                    requireActivity(),
                                    "Successful",
                                    "Your meal order has bean confirmed",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            } else {
                                getBaseMessage(
                                    requireActivity(),
                                    "Failed",
                                    "sorry, please try again after some times.",
                                    R.drawable.ic_checked_green,
                                    1
                                )
                            }
                            dialog.dismiss()
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