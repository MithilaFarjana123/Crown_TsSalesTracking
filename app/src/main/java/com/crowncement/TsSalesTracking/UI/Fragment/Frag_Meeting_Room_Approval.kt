package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crowncement.TsSalesTracking.databinding.FragMeetingRoomApprovalBinding


class Frag_Meeting_Room_Approval : Fragment() {

    //lateinit var rootView: View

    private var _binding: FragMeetingRoomApprovalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    //    rootView = inflater.inflate(R.layout.frag_meeting_room_approval, container, false)

        _binding = FragMeetingRoomApprovalBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }

}