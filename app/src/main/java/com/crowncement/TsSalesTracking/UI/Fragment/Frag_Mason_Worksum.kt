package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crowncement.TsSalesTracking.databinding.FragMasonWorksumBinding


class Frag_Mason_Worksum : Fragment() {

    private var _binding: FragMasonWorksumBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragMasonWorksumBinding.inflate(inflater, container, false)

        return binding.root
    }

}