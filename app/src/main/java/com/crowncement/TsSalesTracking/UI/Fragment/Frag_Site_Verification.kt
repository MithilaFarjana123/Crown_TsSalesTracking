package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.databinding.FragDemoSiteHomeBinding
import com.crowncement.TsSalesTracking.databinding.FragSiteVerificationBinding
import com.google.firebase.inappmessaging.MessagesProto


class Frag_Site_Verification : Fragment() {

    private var _binding: FragSiteVerificationBinding? = null
    private val binding get() = _binding!!
    private val TAG = "Frag_Demo_Site_Home"

    private lateinit var spinnerStatus: Spinner
    private lateinit var layoutMismatchFields: LinearLayout
    private lateinit var btnSubmit: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragSiteVerificationBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        layoutMismatchFields = view.findViewById(R.id.layoutMismatchFields)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        // Handle Spinner Selection
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedValue = parent?.getItemAtPosition(position).toString()

                // Show hidden fields only if "Information Mismatch" is selected
                if (selectedValue == "Information Mismatch") {
                    layoutMismatchFields.visibility = View.VISIBLE
                } else {
                    layoutMismatchFields.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Handle Submit
        btnSubmit.setOnClickListener {
            submitData()
        }

    }





    private fun submitData() {
        val status = spinnerStatus.selectedItem.toString()
        if (status == "Information Mismatch") {
            // Validate your EditTexts here before sending to API
            Toast.makeText(context, "Mismatch data submitted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Site Verified: OK", Toast.LENGTH_SHORT).show()
        }
    }
}