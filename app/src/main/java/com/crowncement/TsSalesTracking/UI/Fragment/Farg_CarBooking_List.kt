package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.viewmodel.MeetingViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MeetingViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.CarBookingListAdapter
import com.crowncement.TsSalesTracking.data.Model.CarReqEntListResponce
import com.crowncement.TsSalesTracking.data.Model.CarReqEntListResponceChild
import com.crowncement.TsSalesTracking.databinding.FargCarBookingListBinding
import java.util.ArrayList


class Farg_CarBooking_List : Fragment() {

//    lateinit var rootView: View

    private var _binding: FargCarBookingListBinding? = null
    private val binding get() = _binding!!


    lateinit var loadingAnim: Dialog

    lateinit var logViewModel: MeetingViewModel
    lateinit var carBookinglist : ArrayList<CarReqEntListResponceChild>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = inflater.inflate(R.layout.farg_car_booking_list, container, false)

        _binding = FargCarBookingListBinding.inflate(inflater, container, false)
/*
        MeetingViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        getCarReqEntList("E11-001795","2024-01-01","2024-08-07")



 */
        return binding.root

       // return rootView
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Use binding safely
        MeetingViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        getCarReqEntList("E11-001795","2024-01-01","2024-08-07")


    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

    private fun setupViewModel() {
        logViewModel =
            ViewModelProviders.of(requireActivity(), MeetingViewModelFactory())
                .get(
                    MeetingViewModel::class.java
                )


    }

    private fun getCarReqEntList(
        user_id: String,
        from_date: String,
        to_date: String
    ) {
        logViewModel.getCarReqEntList(user_id,from_date, to_date)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingAnim.dismiss()
                        it.responseData?.let { res ->
                            successLogList(res)

                        }

                    }

                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        loadingAnim.dismiss()

                        // rootView.shimmer_att_container.visibility = View.GONE
                        // rootView.shimmer_att_container.stopShimmer()

                        Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }
    }


    private fun successLogList(res: CarReqEntListResponce) {

        if (res.code == "200") {
            if (res.data.isNotEmpty()) {
                prepareLogRV(res.data)
            } else{

                /*
                loadingNoDataAnim = Utility.baseLoadingAnimation(
                    this,
                    Dialog(this),
                    "P l e a s e    w a i t"
                )

                 */

            }
        }
    }


/*
    private fun prepareLogRV(items: ArrayList<CarReqEntListResponceChild>) {
//        items.sortByDescending {
//            it.reqDate
//        }

        carBookinglist = ArrayList()
        var emp = Utility.getValueByKey(requireActivity(),"username").toString()

        // alllist = items.sortedWith(compareBy({ it.visitorId.equals("") })) as ArrayList<VisitorLog>
        for (values in items) {
            //  if(values.visitCard.toString().isNotEmpty()&& values.visitOutTime.equals(blank)){

            carBookinglist.add(values)

        }


        if (carBookinglist.size > 0) {
            val carBookAdapter = CarBookingListAdapter(carBookinglist)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())

            rootView.carBook_recyclerView.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            rootView.carBook_recyclerView.layoutManager = manager
            rootView.carBook_recyclerView.adapter = carBookAdapter
            /*
                        visitlogAdapter.setOnItemClickListener(object :
                            VisitorLogAdapter.OnAdapterItemClickListener {

                            override fun OnAccept(v: View?, position: Int) {
                                getCheckoutdone(allsavedlist[position].visitLogId.toString())

                            }
                        })

             */

        }else{

        }
    }
*/
    private fun prepareLogRV(items: ArrayList<CarReqEntListResponceChild>) {
        carBookinglist = ArrayList()

        for (values in items) {
            carBookinglist.add(values)
        }

        if (carBookinglist.isNotEmpty()) {
            val carBookAdapter = CarBookingListAdapter(carBookinglist)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())

            // Using ViewBinding to access the RecyclerView
            binding.carBookRecyclerView.layoutManager = rLayoutmanager
            binding.carBookRecyclerView.adapter = carBookAdapter
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