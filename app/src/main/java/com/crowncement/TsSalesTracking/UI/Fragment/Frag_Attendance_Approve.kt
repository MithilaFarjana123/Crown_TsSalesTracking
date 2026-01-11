package com.crowncement.TsSalesTracking.UI.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.AttendanceViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.AttendanceViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Adapter.AttendanceApproveListAdapter
import com.crowncement.TsSalesTracking.data.Adapter.BreadCramAdapter
import com.crowncement.TsSalesTracking.data.Model.AttApproveResponse
import com.crowncement.TsSalesTracking.data.Model.AttApproveResponseChild
import com.crowncement.TsSalesTracking.databinding.FragAttendanceApproveBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.logicbeanzs.uberpolylineanimation.MapAnimator
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class Frag_Attendance_Approve : Fragment() {

 //   lateinit var rootView: View
    private var _binding: FragAttendanceApproveBinding? = null
    private val binding get() = _binding!!

    lateinit var itemAdapter: AttendanceApproveListAdapter

    lateinit var atteViewModel: AttendanceViewModel

    lateinit var loadingAnim: Dialog
    lateinit var user: String
    lateinit var attApproveDate: String

    var approveList: ArrayList<AttApproveResponseChild> = ArrayList()

    val breadCramList: ArrayList<String> = ArrayList()

    lateinit var picker: DatePickerDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  rootView = inflater.inflate(R.layout.frag_attendance_approve, container, false)
        _binding = FragAttendanceApproveBinding.inflate(inflater, container, false)

/*

        binding.shimmerAttApproveContainer.visibility = View.VISIBLE
        binding.shimmerAttApproveContainer.startShimmer()
        user = Utility.getValueByKey(requireActivity(), "username").toString()

        breadCramList.add(user)
        loadBreadCram()

        binding.txtAppDate.setText(Utility.getCurrentDate("EEE, dd MMM yyyy").toString())
        attApproveDate = Utility.getCurrentDate("yyyy-MM-dd").toString()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        AttendanceViewModelFactory()
        setupViewModel()

        //attApproveList("E11-000592", "2023-02-13")
        loadingAnim.show()
        attApproveList(user, Utility.getCurrentDate("yyyy-MM-dd").toString())

        onClickEventListener()
*/


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        binding.shimmerAttApproveContainer.visibility = View.VISIBLE
        binding.shimmerAttApproveContainer.startShimmer()
        user = Utility.getValueByKey(requireActivity(), "username").toString()

        breadCramList.add(user)
        loadBreadCram()

        binding.txtAppDate.setText(Utility.getCurrentDate("EEE, dd MMM yyyy").toString())
        attApproveDate = Utility.getCurrentDate("yyyy-MM-dd").toString()

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        AttendanceViewModelFactory()
        setupViewModel()

        //attApproveList("E11-000592", "2023-02-13")
        loadingAnim.show()
        attApproveList(user, Utility.getCurrentDate("yyyy-MM-dd").toString())

        onClickEventListener()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun onClickEventListener() {

        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.isNotEmpty()) {
                    filter(query)
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    filter(newText)
                }
                return false
            }
        })

        binding.txtAppDate.setOnClickListener {

            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val monthNumber = monthOfYear + 1
                    val selectedDate = "$year1-$monthNumber-$dayOfMonth"
                    attApproveDate = selectedDate
                    binding.txtAppDate.setText(
                        Utility.changeDateFormat(
                            selectedDate,
                            "yyyy-MM-dd",
                            "EEE, dd MMM yyyy"
                        )
                    )
                    loadingAnim.show()
                    attApproveList(user, attApproveDate)
                }, year, month, day
            )
            picker.show()
        }

    }



    private fun filter(text: String) {

        if (text.isNotEmpty() && approveList.size > 0) {

            val filteredlist = java.util.ArrayList<AttApproveResponseChild>()
            for (item in approveList) {
                if (item.empName!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) ||
                    item.empId!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredlist.add(item)
                }
            }
            if (filteredlist.isEmpty()) {
                Utility.getBaseMessage(
                    requireActivity(),
                    "Sorry",
                    "No data found",
                    R.drawable.error_white,
                    0
                )
                binding.rvAttendanceAuth.adapter = null
            } else {
                prepareAttAuthList(binding.rvAttendanceAuth, filteredlist)
            }

        } else {
            Utility.getBaseMessage(
                requireActivity(),
                "Sorry",
                "No attendance request for approve",
                R.drawable.error_white,
                0
            )
        }
    }

    private fun attApproveList(
        id: String,
        date: String,

        ) {
        atteViewModel.attApproveList(id, date)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200" && res.data.size > 0) {

                                approveList = ArrayList()
                                approveList = res.data

                                successApprovalList(
                                    res,
                                    binding.rvAttendanceAuth,
                                    binding.txtAttAuthNoData
                                )
                            } else {
                                successApprovalList(
                                    res,
                                    binding.rvAttendanceAuth,
                                    binding.txtAttAuthNoData
                                )
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Sorry, No data for approve",
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
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun setupViewModel() {
        atteViewModel = ViewModelProviders.of(this.requireActivity(), AttendanceViewModelFactory())
            .get(AttendanceViewModel::class.java)
    }


    private fun successApprovalList(
        res: AttApproveResponse,
        rv: RecyclerView,
        noData: LottieAnimationView

    ) {

        if (res.code.equals("200") && (res.data.size > 0)) {
            noData.visibility = View.GONE
            rv.visibility = View.VISIBLE
            prepareAttAuthList(rv, res.data)

        } else {
            noData.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        binding.shimmerAttApproveContainer.visibility = View.GONE
        binding.shimmerAttApproveContainer.stopShimmer()
    }

    private fun prepareAttAuthList(rv: RecyclerView, items: ArrayList<AttApproveResponseChild>) {

        itemAdapter = AttendanceApproveListAdapter(items)
        val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        rv.layoutManager = rLayoutmanager

        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = manager
        rv.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object :
            AttendanceApproveListAdapter.OnAdapterItemClickListener {

            override fun OnAccept(v: View?, position: Int) {

                confirmBottomSheet(items[position])

            }
            override fun OnAbsent(v: View?, position: Int) {

                absentBottomSheet(items[position])

            }

            override fun OnClickLocation(v: View?, position: Int) {

                liveLocationBottomSheet(items[position])
            }

            override fun onLocationTrack(v: View?, position: Int) {

                shoLocationTracking(items[position])
            }

            override fun onClickItem(v: View?, position: Int) {

                if (!checkExistence(items[position].empId.toString())) {
                    breadCramList.add(items[position].empId.toString())
                }

                loadBreadCram()
                loadingAnim.show()
                attApproveList(items[position].empId.toString(), attApproveDate)
            }

        })

    }

    private fun checkExistence(value: String): Boolean {
        var res = false
        for (item in breadCramList) {
            if (value == item) {
                res = true
                break
            } else {
                res = false
            }
        }
        return res
    }

    private fun loadBreadCram() {
        if (breadCramList.size > 0) {
            val itemAdapter = BreadCramAdapter(breadCramList)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
            binding.rbBreadcram.layoutManager = rLayoutmanager

            val manager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rbBreadcram.layoutManager = manager
            binding.rbBreadcram.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object :

                BreadCramAdapter.OnAdapterItemClickListener {

                override fun OnClick(v: View?, position: Int) {

                    loadingAnim.show()
                    attApproveList(breadCramList[position], attApproveDate)

                    val itemsToRemove =
                        listOf(breadCramList[position], breadCramList[(breadCramList.size - 1)])

                    // if ((itemsToRemove.size >= 0) && (itemsToRemove.size < breadCramList.size)) {
                    for (item in itemsToRemove) {
                        val index = breadCramList.indexOf(item)
                        if (index != -1) {
                            breadCramList.removeAt(index)
                        }
                    }

                    if (breadCramList.size == 0) {
                        breadCramList.add(user)
                    }
                    // }
                    loadBreadCram()

//                    val positionToRemove = 2 // Index of the element to be removed
//                    if (positionToRemove >= 0 && positionToRemove < breadCramList.size) {
//                        breadCramList.removeAt(positionToRemove)
//                        println("Element removed successfully.")
//                    } else {
//                        println("Invalid position.")
//                    }

//                    val arrayList = ArrayList<String>()
//                    arrayList.addAll(listOf("1", "2", "3", "4", "5"))
//                    val itemsToRemove = listOf("2", "4")
//                    for (item in itemsToRemove) {
//                        val index = arrayList.indexOf(item)
//                        if (index != -1) {
//                            arrayList.removeAt(index)
//                        }
//                    }

                }
            })
        }

    }

    private fun liveLocationBottomSheet(data: AttApproveResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.emp_live_att, null)
        // val dialog = BottomSheetDialog(requireContext())
        // Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val txtLocationCancel: TextView = v.findViewById(R.id.txtLocationCancel)
        val txtLocationMessage: TextView = v.findViewById(R.id.txtLocationMessage)
        val map: MapView = v.findViewById(R.id.map_view)

        if (data.loc_data.size > 0) {
            val conVoTime = Utility.changeDateFormat(
                data.loc_data[data.loc_data.size - 1]
                    .locIntime, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a"
            )

            txtLocationMessage.text = "Location updated on ${conVoTime}"

        } else {
            txtLocationMessage.text = "Location updated on : Sorry no data found"
        }


        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        var currentLocation: LatLng? = null

        if (data.loc_data.size > 0) {
            currentLocation = LatLng(
                data.loc_data[data.loc_data.size - 1].locLat!!,
                data.loc_data[data.loc_data.size - 1].locLon!!
            )

            val googleMap: GoogleMap
            MapsInitializer.initialize(requireActivity())

            map.onCreate(dialog.onSaveInstanceState())
            map.onResume() // needed to get the map to display immediately


            map.getMapAsync { googleMap ->

                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentLocation!!)
                        .title(data.empName)
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15.0f)
                )

                googleMap.setOnMarkerClickListener { // Marker source is clicked

                    true
                }

                googleMap.addCircle(
                    CircleOptions()
                        .center(currentLocation)
                        // .radius(200.0).strokeWidth(3f).strokeColor(Color.YELLOW)
                        //.fillColor(Color.argb(70, 255, 236, 104))
                        .radius(60.0).strokeWidth(3f).strokeColor(Color.GREEN)
                        .fillColor(Color.argb(70, 50, 150, 50))
                )

                googleMap.isTrafficEnabled = true
            }
        }

        txtLocationCancel.setOnClickListener {
            dialog.hide()
        }

    }



    private fun confirmBottomSheet(data: AttApproveResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.confirm_att_approve, null)
        val dialog = BottomSheetDialog(requireContext())
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnAttApprove: MaterialButton = v.findViewById(R.id.btnAttApprove)
        val btnAttReject: MaterialButton = v.findViewById(R.id.btnAttReject)
        val txtAttCancel: TextView = v.findViewById(R.id.txtAttCancel)
        val txtConAttMessage: TextView = v.findViewById(R.id.txtConAttMessage)
        val imgAttConfirm: CircleImageView = v.findViewById(R.id.imgAttConfirm)

        val message = "You are going to approve " + data.empName + " attendance"

        txtConAttMessage.text = message

        Glide
            .with(v.context)
            .load(EndPoint.IMAGE_BASE_URL + data.empImg.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(imgAttConfirm)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtAttCancel.setOnClickListener {
            dialog.hide()
        }

        btnAttReject.setOnClickListener {

            dialog.hide()
        }
        btnAttApprove.setOnClickListener {

            dialog.dismiss()
            salesPersonAttApprove(
                user,
                data.empId.toString(),
                //Utility.getCurrentDate("yyyy-MM-dd").toString()
                attApproveDate
            )
        }
    }

    //todo for absent
    private fun absentBottomSheet(data: AttApproveResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.absent_attendence_dialouge, null)
        
        val dialog = BottomSheetDialog(requireContext())

        
        
        Objects.requireNonNull(dialog.window)!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val btnAttAbsent: MaterialButton = v.findViewById(R.id.btnAttAbsent)
     //   val btnAttReject: MaterialButton = v.findViewById(R.id.btnAttReject)
        val txtAttCancel: TextView = v.findViewById(R.id.txtAttCancel)
        val txtConAttMessage: TextView = v.findViewById(R.id.txtConAttMessage)
        val imgAttAbsent: CircleImageView = v.findViewById(R.id.imgAttAbsent)
        val txtAttAbsentReason: TextInputEditText = v.findViewById(R.id.txtAttAbsentReason)
        val layoutAttAbsentReason : TextInputLayout = v.findViewById(R.id.layoutAttAbsentReason)


        val message = "You are going to absent " + data.empName + " attendance"

        txtConAttMessage.text = message

        Glide
            .with(v.context)
            .load(EndPoint.IMAGE_BASE_URL + data.empImg.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(imgAttAbsent)

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()
        txtAttCancel.setOnClickListener {
            dialog.hide()
        }

        txtAttCancel.setOnClickListener {

            dialog.hide()
        }
        btnAttAbsent.setOnClickListener {


          //  dialog.dismiss()
            if(validationForAbsentReason(txtAttAbsentReason,layoutAttAbsentReason)){
                salesPersonAttAbsdent(
                    user,
                    data.empId.toString(),
                    data.attDate.toString(),
                    data.att_shftno.toString(),
                    data.att_shift.toString(),
                    txtAttAbsentReason.text.toString())
                dialog.dismiss()
            }


//            salesPersonAttApprove(
//                user,
//                data.empId.toString(),
//                //Utility.getCurrentDate("yyyy-MM-dd").toString()
//                attApproveDate
//            )
        }
    }

    fun validationForAbsentReason(
        txtAttAbsentReason:TextInputEditText,
        layoutAttAbsentReason:TextInputLayout): Boolean{
        var result = false

        layoutAttAbsentReason.isErrorEnabled = false
        layoutAttAbsentReason.error = null
        if (txtAttAbsentReason.text.toString().isEmpty()) {
            result = false
            layoutAttAbsentReason.isErrorEnabled = true
            layoutAttAbsentReason.error = "Absent reason is required"
        }else{
            result = true
        }
        return result
    }



    @SuppressLint("ResourceType")
    private fun shoLocationTracking(data: AttApproveResponseChild) {
        val v: View = layoutInflater.inflate(R.layout.emp_tracking, null)
//        val dialog = BottomSheetDialog(requireContext())

        val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Objects.requireNonNull(dialog.window)?.setSoftInputMode(WindowManager.LayoutParams.FILL_PARENT)

        dialog.setContentView(v)

        val txtLocationTrackingCancel: TextView = v.findViewById(R.id.txtLocationTrackingCancel)
        val txtTrackingLocationMessage: TextView = v.findViewById(R.id.txtTrackingLocationMessage)
        val tracking_map_view: MapView = v.findViewById(R.id.tracking_map_view)


        if (data.loc_data.size > 0 && data.loc_data.size > 1) {
            val conVoTime = Utility.changeDateFormat(
                data.loc_data[data.loc_data.size - 1]
                    .locIntime, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a"
            )

            txtTrackingLocationMessage.text = "Location updated on ${conVoTime}"

        } else {

            txtTrackingLocationMessage.text = "Location updated on: Sorry, no data found"
        }

        dialog.setContentView(v)
        dialog.setCancelable(true)
        dialog.show()

        // val currentLocation = LatLng(data.empLat!!, data.empLon!!)

        // val googleMap: GoogleMap
        MapsInitializer.initialize(requireActivity())

        tracking_map_view.onCreate(dialog.onSaveInstanceState())
        tracking_map_view.onResume() // needed to get the map to display immediately


        tracking_map_view.getMapAsync { googleMap ->

            googleMap.clear()

            val pointList: ArrayList<LatLng> = ArrayList()

            for (z in 0 until data.loc_data.size) {

                val  point = LatLng(data.loc_data[z].locLat!!, data.loc_data[z].locLon!!)

                pointList.add(point)
                val t=Utility.changeDateFormat(data.loc_data[z].locIntime.toString(),"yyyy-MM-dd HH:mm:ss","hh:mm:ss a")
                //    Log.d("Tracking", "Time : $t")
                //   Log.d("Tracking", "Address :"+getAddress(point))
                googleMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapFromVector(requireContext(), R.drawable.man_walking))
                        .position(point)
                        // .title(t.toString()+"("+getAddress(point)!!.split(",")[1]+")")
                        .title(t.toString())
                )?.showInfoWindow()

            }

            if (pointList.size > 0 && pointList.size > 1) {


                //  googleMap.addPolyline(options)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList[0], 13f))


                val start: LatLng = pointList[0]
                googleMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapFromVector(requireContext(), R.drawable.flag_square))
                        .position(start)
                        .title("START")
                )

                val end: LatLng = pointList[pointList.size - 1]
                googleMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapFromVector(requireContext(), R.drawable.flag_orange))
                        .position(end)
                        .title("END")
                )

                MapAnimator.getInstance().animateRoute(googleMap, pointList)
                MapAnimator.getInstance().setPrimaryLineColor(Color.BLACK)
                //  MapAnimator.getInstance().setSecondaryLineColor(Color.parseColor("#FFCB30"))
                MapAnimator.getInstance().setSecondaryLineColor(Color.MAGENTA)
                // MapAnimator.getInstance().setPercentCompletion(@IntegerRes time: Int)
                MapAnimator.getInstance().setColorFillCompletion(1000)
                MapAnimator.getInstance().setDelayTime(2000)
                // MapAnimator.getInstance().setPrimaryLineCompletion(@IntegerRes time: Int)
                // MapAnimator.getInstance().setPolylineWidth(width: Float)

            }

            googleMap.setOnMarkerClickListener { true }

            googleMap.isTrafficEnabled = true


            googleMap.setOnMarkerClickListener { clickedMarker ->

                //  Toast.makeText(requireContext(), clickedMarker.title, Toast.LENGTH_LONG).show()

                false
            }


        }

        txtLocationTrackingCancel.setOnClickListener {
            dialog.hide()
        }
    }



    private fun getAddress(lat: LatLng): String? {
        val geocoder = Geocoder(requireContext())
        val list = geocoder.getFromLocation(lat.latitude, lat.longitude,1)
        return list!![0].getAddressLine(0)
    }


    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun salesPersonAttApprove(
        id: String,
        empID: String,
        date: String

    ) {
        atteViewModel.attApprove(id, empID, date)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Approved",
                                    "Attendance approved successfully",
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                loadingAnim = Utility.baseLoadingAnimation(
                                    requireActivity(),
                                    Dialog(requireActivity()),
                                    "P l e a s e    w a i t"
                                )
                                loadingAnim.show()
                                attApproveList(
                                    user,
                                    //Utility.getCurrentDate("yyyy-MM-dd").toString()
                                    date
                                )
                                // attApproveList("E11-000592", "2023-02-13")

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
                        loadingAnim.show()

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    private fun salesPersonAttAbsdent(
        userid: String,
        empID: String,
        attDate : String,
        att_shftno : String,
        att_shift : String,
        txtAttAbsentReason  :String

    ) {
        atteViewModel.attAbsent(userid, empID, attDate,att_shftno,att_shift,txtAttAbsentReason)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {

                        it.responseData?.let { res ->

                            if (res.code == "200") {

                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Done",
                                    "You have made this employee absent .",
                                    R.drawable.ic_checked_green,
                                    1
                                )

                                loadingAnim = Utility.baseLoadingAnimation(
                                    requireActivity(),
                                    Dialog(requireActivity()),
                                    "P l e a s e    w a i t"
                                )
                                loadingAnim.show()
                                attApproveList(
                                    user,
                                    //Utility.getCurrentDate("yyyy-MM-dd").toString()
                                    attDate
                                )
                                // attApproveList("E11-000592", "2023-02-13")

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
                        loadingAnim.show()

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


}