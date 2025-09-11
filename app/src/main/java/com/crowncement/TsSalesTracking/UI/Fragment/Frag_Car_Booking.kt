package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.MeetingViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MeetingViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.CarReqSaveData
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.FragCarBookingBinding
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson


import java.util.Calendar
import java.util.Objects


class Frag_Car_Booking : Fragment() {

  //  lateinit var rootView: View
    private var _binding: FragCarBookingBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog

    lateinit var logViewModel: MeetingViewModel
    var user =   ""
    private lateinit var picker: DatePickerDialog

    lateinit var bookingDate: String

    lateinit var currentDate: String
    private lateinit var userInfo: Data
    var carType = "Private Car"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_car_booking, container, false)
        _binding = FragCarBookingBinding.inflate(inflater, container, false)
/*

        MeetingViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        userInfo = Utility.getUserInfo(requireActivity())!!

        onclickListener()
        imgslider()

 */



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        MeetingViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        userInfo = Utility.getUserInfo(requireActivity())!!

        onclickListener()
        imgslider()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    fun imgslider(){
        val imageList = ArrayList<SlideModel>() // Create a list to hold the images

        imageList.add(SlideModel(R.drawable.car_one, "Image 1")) // Replace with your actual drawable resources
        imageList.add(SlideModel(R.drawable.car_two, "Image 2"))
        imageList.add(SlideModel(R.drawable.car_three, "Image 3"))


        val imageSlider = binding.root.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)
    }

    private fun setupViewModel() {
        logViewModel =
            ViewModelProviders.of(requireActivity(), MeetingViewModelFactory())
                .get(
                    MeetingViewModel::class.java
                )


    }

    private fun removeUIErrorSign(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = null
    }

    private fun setError(field: TextInputLayout, message: String) {

        field.isErrorEnabled = true
        field.error = message
    }


    fun updateUIValidation(): Boolean {

        var result = false

        //department
        removeUIErrorSign(binding.layoutCarfdate)
        removeUIErrorSign(binding.layoutCarTodate)

        //time
        removeUIErrorSign(binding.layoutCarSTime)
        removeUIErrorSign(binding.layoutCarETime)

        //location
        removeUIErrorSign(binding.layoutCarPickupLoction)
        removeUIErrorSign(binding.layoutCardestination)

        //
        removeUIErrorSign(binding.layoutCarNumOfUser)
        removeUIErrorSign(binding.layoutCarUserMobile)

        //
        removeUIErrorSign(binding.layoutCarPurpose)




        if(binding.txtCarfdate.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarfdate, "Please select the start date")
        }
        else if(binding.txtCarTodate.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarTodate, "Please select the end date ")
        }else if (binding.acCarSTime.text.toString().isEmpty()||
                  binding.acCarSTime.text.toString().equals("Select Start Time")) {
            result = false
            setError(binding.layoutCarSTime, "Please Select Start Time")
        }else if (binding.acCarETime.text.toString().isEmpty()||
            binding.acCarETime.text.toString().equals("Select End Time")) {
            result = false
            setError(binding.layoutCarETime, "Please Select End Time")
        }
        else if (binding.txtCarPickupLoction.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarPickupLoction, "Please enter your pick up location ")
        }else if (binding.txtCardestination.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCardestination, "Please enter your destination ")
        }

        else if (binding.txtCarNumOfUser.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarNumOfUser, "Please Select total number of person")
        }else if (binding.txtCarUserMobile.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarUserMobile, "Please enter your mobile number")
        }else if (binding.txtCarPurpose.text.toString().isEmpty()){
            result = false
            setError(binding.layoutCarPurpose, "Please enter your purpose")
        }


        else{
            result = true
        }
        return result

        }




    fun onclickListener() {
        loadStartTime(binding.acCarSTime)
        loadEndTime(binding.acCarETime)
        //loadRoomType(rootView.acRoomType)
        DateofRequest()

        //radio button
        binding.CarTradioGroup.setOnCheckedChangeListener { group, checkedId ->
           // val radioButton: RadioButton = rootView.findViewById(checkedId)

            // Do something with the selected radio button
        }

        binding.CarTradioButton1.isChecked = true


// To handle click events
        binding.CarTradioButton1.setOnClickListener {
            carType = "Private Car"

        }

        binding.CarTradioButton2.setOnClickListener {
            carType ="Micro bus"

        }


        binding.btnSaveCar.setOnClickListener {

            if (updateUIValidation()) {
               confirmStart()
            } else {



            }


        }

    }





    private fun loadStartTime(ac: AutoCompleteTextView) {
        //  private fun loadDepartment1(ac: AutoCompleteTextView) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Start Time")
        items.add("12:00AM")
        items.add("12:00PM")
        items.add("11:00AM")
        items.add("11:00PM")
        items.add("10:00AM")
        items.add("10:00PM")
        items.add("09:00AM")
        items.add("09:00PM")
        items.add("08:00AM")
        items.add("08:00PM")
        items.add("07:00AM")
        items.add("07:00PM")
        items.add("06:00AM")
        items.add("06:00PM")
        items.add("05:00AM")
        items.add("05:00PM")
        items.add("04:00AM")
        items.add("04:00PM")
        items.add("03:00AM")
        items.add("03:00PM")
        items.add("02:00AM")
        items.add("02:00PM")
        items.add("01:00AM")
        items.add("01:00PM")



        ac.setText(items[0])
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    private fun loadEndTime(ac: AutoCompleteTextView) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select End Time")
        items.add("12:00AM")
        items.add("12:00PM")
        items.add("11:00AM")
        items.add("11:00PM")
        items.add("10:00AM")
        items.add("10:00PM")
        items.add("09:00AM")
        items.add("09:00PM")
        items.add("08:00AM")
        items.add("08:00PM")
        items.add("07:00AM")
        items.add("07:00PM")
        items.add("06:00AM")
        items.add("06:00PM")
        items.add("05:00AM")
        items.add("05:00PM")
        items.add("04:00AM")
        items.add("04:00PM")
        items.add("03:00AM")
        items.add("03:00PM")
        items.add("02:00AM")
        items.add("02:00PM")
        items.add("01:00AM")
        items.add("01:00PM")

        ac.setText(items[0])
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }

    fun DateofRequest(){

        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]

        val selectedDate = "$year-$month-$day"
        currentDate = selectedDate
/*
        rootView.txtCarfdate.setText(

            Utility.changeDateFormat(
                "$year-$month-$day",
                "yyyy-MM-dd",
                "dd MMM yyyy"
            )
        )

 */

        binding.txtCarfdate.setOnClickListener(){
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    bookingDate = selectedDate
                    binding.txtCarfdate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }




        binding.txtCarTodate.setOnClickListener(){
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    bookingDate = selectedDate
                    binding.txtCarTodate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    )
                }, year, month, day
            )
            picker.show()
        }


    }




    fun confirmStart() {
        val v: View = layoutInflater.inflate(R.layout.car_booking_confirmation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val btnCCancel: MaterialButton = v.findViewById(R.id.btnCCancel)
        val btnCConfirm: MaterialButton = v.findViewById(R.id.btnCConfirm)

        val Ccancel: TextView = v.findViewById(R.id.Ccancel)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnCCancel.setOnClickListener {
            dialog.hide()
        }

        Ccancel.setOnClickListener {
            dialog.hide()
        }

        btnCConfirm.setOnClickListener {
            dialog.hide()

            val data = prepareCarBokingData()
            saveNewCarBooking(user, data)
        }

    }


    private fun prepareCarBokingData(): String {


        val CarReqSaveData = CarReqSaveData(
            currentDate,
            binding.txtCarPickupLoction.text.toString(),
            binding.txtCardestination.text.toString(),
            binding.txtCarfdate.text.toString(),
            binding.txtCarTodate.text.toString(),
            binding.acCarSTime.text.toString(),
            binding.acCarETime.text.toString(),
            binding.txtCarPurpose.text.toString(),
            userInfo.userDept.toString(),
            user,
            binding.txtCarUserMobile.text.toString(),
            binding.txtCarNumOfUser.text.toString(),
            carType,
            binding.txtCarRemarks.text.toString()


        )

        val gson = Gson()
        return gson.toJson(CarReqSaveData)
    }


    private fun saveNewCarBooking(user: String, value: String) {

        loadingAnim.show()

        logViewModel.saveCarReqSave(user, value)?.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->

                        if (res.code == "200") {

                            loadingAnim.dismiss()
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Successful",
                                "Car booking completed successfully",
                                R.drawable.ic_checked_green,
                                1
                            )
                            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                            navController.navigate(R.id.nav_dashboard)


                        } else {
                            loadingAnim.dismiss()

                            Utility.getBaseMessage(
                                requireActivity(),
                                "Error",
                                res.message.toString(),
                                R.drawable.error_white,
                                0
                            )
                        }
                    }
                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }



}