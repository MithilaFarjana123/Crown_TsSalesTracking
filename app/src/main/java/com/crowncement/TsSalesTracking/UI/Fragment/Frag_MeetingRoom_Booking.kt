package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.DatePickerDialog
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
import com.crowncement.TsSalesTracking.data.Model.DepartmentData
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomBookInfo
import com.crowncement.TsSalesTracking.data.Model.MeetingRoomListResponceChild
import com.crowncement.TsSalesTracking.databinding.FragMeetingRoomBookingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson

import java.util.Calendar
import java.util.Objects
import kotlin.collections.ArrayList

class Frag_MeetingRoom_Booking : Fragment() {

   // private lateinit var rootView: View

    private var _binding: FragMeetingRoomBookingBinding? = null
    private val binding get() = _binding!!

    lateinit var departmentList: ArrayList<DepartmentData>
    lateinit var roomList: ArrayList<MeetingRoomListResponceChild>
    lateinit var loadingAnim: Dialog

    lateinit var logViewModel: MeetingViewModel
    var user =   ""
    private lateinit var picker: DatePickerDialog
    lateinit var bookingDate: String
    lateinit var currentDate: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // rootView = inflater.inflate(R.layout.frag_meeting_room_booking, container, false)


        _binding = FragMeetingRoomBookingBinding.inflate(inflater, container, false)

/*
        MeetingViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username").toString()

        binding.MEMPCode.setText("EMP Code : "+user)

        binding.MLocation.visibility = View.GONE
        binding.MApproval.visibility = View.GONE


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        onclickListener()
*/
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely

        MeetingViewModelFactory()
        setupViewModel()

        user = Utility.getValueByKey(requireActivity(), "username").toString()

        binding.MEMPCode.setText("EMP Code : "+user)

        binding.MLocation.visibility = View.GONE
        binding.MApproval.visibility = View.GONE


        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        onclickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    fun onclickListener() {
        getSavedDepartmentList()
        getRoomTypeList()
        loadStartTime(binding.acMStartTime)
        loadEndTime(binding.acMEndTime)
        //loadRoomType(rootView.acRoomType)
        DateofRequest()

        binding.acRoomType.setOnItemClickListener { parent, arg1, position, arg3 ->

            roomList = ArrayList()
            if(roomList.size>0){
                binding.MLocation.setText("Room Location : "+roomList.get(position).roomLoc.toString())
                binding.MApproval.setText("Approval : "+roomList.get(position).permLevel.toString())
            }



            if(!binding.acRoomType.text.toString().equals("Select Room Type")) {
                binding.MLocation.visibility = View.VISIBLE
                binding.MApproval.visibility = View.VISIBLE
            }else{
                binding.MLocation.visibility = View.GONE
                binding.MApproval.visibility = View.GONE
            }

        }

        binding.btnSaveMeeting.setOnClickListener {

            if (updateUIValidation()) {
                confirmStart()
            } else {


            }


        }

    }



    //confirm
    fun confirmStart() {
        val v: View = layoutInflater.inflate(R.layout.meetingroom_booking_confermation, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val btnMCancel: MaterialButton = v.findViewById(R.id.btnMCancel)
        val btnMConfirm: MaterialButton = v.findViewById(R.id.btnMConfirm)

        val Ecancel: TextView = v.findViewById(R.id.Mcancel)


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        btnMCancel.setOnClickListener {
            dialog.hide()
        }

        Ecancel.setOnClickListener {
            dialog.hide()
        }

        btnMConfirm.setOnClickListener {
            dialog.hide()

            val data = prepareMeetingRomBokingData()
            saveNewRoomBooking(user, data)
        }

    }

    private fun saveNewRoomBooking(user: String, value: String) {

        loadingAnim.show()

        logViewModel.SaveMeetingData(user, value)?.observe(requireActivity()) {
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
                                "Meeting room booking completed successfully",
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



//todo

    private fun prepareMeetingRomBokingData(): String {

        val MeetingRoomBookInfo = MeetingRoomBookInfo(
            currentDate,
            bookingDate,
            binding.acRoomType.text.toString(),
            binding.acMStartTime.text.toString(),
            binding.acMStartTime.text.toString(),
            binding.txtMeetingPurpose.text.toString(),
            binding.acMDepartment.text.toString(),
            user,
            binding.txtMeetingRemarks.text.toString()

        )

        val gson = Gson()
        return gson.toJson(MeetingRoomBookInfo)
    }



    fun updateUIValidation(): Boolean {

        var result = false

        //department
        removeUIErrorSign(binding.layoutMDepartment)
        // booking date
        removeUIErrorSign(binding.layoutMBookingDate)
        //MStartTime
        removeUIErrorSign(binding.layoutMStartTime)
        //MEndTime
        removeUIErrorSign(binding.layoutMEndTime)
        //layoutMRoomType
        removeUIErrorSign(binding.layoutMRoomType)
        //layoutMeetingPurpose
        removeUIErrorSign(binding.layoutMeetingPurpose)
        if (binding.acMDepartment.text.toString().isEmpty()||
            binding.acMDepartment.text.toString().equals("Select Department")
        ) {
            result = false
            setError(binding.layoutMDepartment, "Please select the Department ")

        } else if (binding.txtBookingDate.text.toString().isEmpty()) {
            result = false
            setError(binding.layoutMBookingDate, "Please provide Booking Date")

        } else if (binding.acMStartTime.text.toString().isEmpty()||
            binding.acMStartTime.text.toString().equals("Select Start Time")
        ) {
            result = false
            setError(binding.layoutMStartTime, "Please select Start Time")

        } else if (binding.acMEndTime.text.toString().isEmpty()||
            binding.acMEndTime.text.toString().equals("Select End Time")) {
            result = false
            setError(binding.layoutMEndTime, "Please select End Time")

        }else if (binding.acRoomType.text.toString().isEmpty()||
            binding.acRoomType.text.toString().equals("Select Room Type"))
        {
            result = false
            setError(binding.layoutMRoomType, "Please select room Type")

        }else if (binding.txtMeetingPurpose.text.toString().isEmpty())
        {
            result = false
            setError(binding.layoutMeetingPurpose, "Please select room Type")

        }else {
            result = true
        }

        return result

    }

    private fun removeUIErrorSign(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = null
    }

    private fun setError(field: TextInputLayout, message: String) {

        field.isErrorEnabled = true
        field.error = message
    }


    private fun setupViewModel() {
        logViewModel =
            ViewModelProviders.of(requireActivity(), MeetingViewModelFactory())
                .get(
                    MeetingViewModel::class.java
                )


    }


    //todo get room type
    private fun getRoomTypeList() {
        logViewModel.getMeetingList()?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        roomList = ArrayList()
                        roomList = res.data
                        loadRoomType(binding.acRoomType, roomList)
                        loadingAnim.dismiss()
                    }

                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    loadingAnim.dismiss()

                }
            }
        }
    }


    //todo DepartmentData
    private fun getSavedDepartmentList(
    ) {
        logViewModel.getDepartmentData()?.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        departmentList = ArrayList()
                        departmentList = res.data
                        loadDepartment(binding.acMDepartment, res.data)
                        loadingAnim.dismiss()
                    }

                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    loadingAnim.dismiss()

                }
            }
        }
    }


    private fun loadDepartment(ac: AutoCompleteTextView, lists: ArrayList<DepartmentData>) {
        //  private fun loadDepartment1(ac: AutoCompleteTextView) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Department")


        for (item in lists) {
            items.add(item.Department.toString())
        }



        ac.setText(items[0])
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }


    //todo
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

        //  var UserDepartment = Utility.getValueByKey(requireActivity(),"user_dept").toString()
        //   items.add(UserDepartment)
        //  selectedDepartment = UserDepartment


        ac.setText(items[0])
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        ac.setAdapter(adapter)
        adapter.notifyDataSetChanged()

    }



    private fun loadRoomType(ac: AutoCompleteTextView,
                             lists: ArrayList<MeetingRoomListResponceChild>) {

        val items: ArrayList<String> = ArrayList()
        items.add("Select Room Type")

        for (item in lists) {
            items.add(item.roomNo.toString()+
                    " ( Capacity : "+item.roomCap.toString()+" person )"
            )
        }


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

        binding.txtDateOfRequest.setText(

            Utility.changeDateFormat(
                "$year-$month-$day",
                "yyyy-MM-dd",
                "dd MMM yyyy"
            )
        )

        binding.txtBookingDate.setOnClickListener(){
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
                    binding.txtBookingDate.setText(
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