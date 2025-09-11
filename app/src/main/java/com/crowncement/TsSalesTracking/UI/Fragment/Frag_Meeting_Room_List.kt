package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.UI.viewmodel.MeetingViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.MeetingViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.MeetingRoomBKLogAdapter
import com.crowncement.TsSalesTracking.data.Model.MeetRoomBKListChild
import com.crowncement.TsSalesTracking.data.Model.MeetRoomBKListResponce
import com.crowncement.TsSalesTracking.databinding.FragMeetingRoomListBinding
import java.util.ArrayList


class Frag_Meeting_Room_List : Fragment() {

    //lateinit var rootView: View
    private var _binding: FragMeetingRoomListBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingAnim: Dialog

    lateinit var logViewModel: MeetingViewModel
    lateinit var meetingRoomlist : ArrayList<MeetRoomBKListChild>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // rootView = inflater.inflate(R.layout.frag_meeting_room_list, container, false)
        _binding = FragMeetingRoomListBinding.inflate(inflater, container, false)

/*
        MeetingViewModelFactory()
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        getMeetingRoomBkList("E11-001795","2024-01-01","2024-08-07")


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
        getMeetingRoomBkList("E11-001795","2024-01-01","2024-08-07")


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


    private fun getMeetingRoomBkList(
        user_id: String,
        from_date: String,
        to_date: String
    ) {
        logViewModel.getMeetRoomBKList(user_id,from_date, to_date)
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

    private fun successLogList(res: MeetRoomBKListResponce) {

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


    private fun prepareLogRV(items: ArrayList<MeetRoomBKListChild>) {
        items.sortByDescending {
            it.reqDate
        }

        meetingRoomlist = ArrayList()
        var emp = Utility.getValueByKey(requireActivity(),"username").toString()

        // alllist = items.sortedWith(compareBy({ it.visitorId.equals("") })) as ArrayList<VisitorLog>
        for (values in items) {
            //  if(values.visitCard.toString().isNotEmpty()&& values.visitOutTime.equals(blank)){

                meetingRoomlist.add(values)

        }


        if (meetingRoomlist.size > 0) {
            val meetingRoomAdapter = MeetingRoomBKLogAdapter(meetingRoomlist)
            val rLayoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())

            binding.MeetingBKRecyclerView.layoutManager = rLayoutmanager
            val manager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.MeetingBKRecyclerView.layoutManager = manager
            binding.MeetingBKRecyclerView.adapter = meetingRoomAdapter
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

}