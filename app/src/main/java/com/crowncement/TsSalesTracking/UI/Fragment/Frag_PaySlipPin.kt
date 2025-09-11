package com.crowncement.TsSalesTracking.UI.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.databinding.FragPaySlipPinBinding


class Frag_PaySlipPin : Fragment() {

    private var _binding: FragPaySlipPinBinding? = null
    private val binding get() = _binding!!


    var pin = ""
    var timecount = 0
    var click = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragPaySlipPinBinding.inflate(inflater, container, false)

        /*
        binding.submitPin.isEnabled = true

      //  animation()
        if(click<5) {
            binding.submitPin.setOnClickListener {
                click = click + 1
                if (validation()) {
                    var storedPin = Utility.getValueByKey(requireActivity(), "pinforpayslip")
                    if (storedPin != "") {
                        timecount = 1
                        givenpin()
                    } else {
                        timecount = 0
                        givenpin()
                        // storedPin = Utility.getValueByKey(requireActivity(),"pinforpayslip")
                    }


                    storedPin = Utility.getValueByKey(requireActivity(), "pinforpayslip")
                    if (storedPin == pin) {
                        binding.pin1.setText("")
                        binding.pin2.setText("")
                        binding.pin3.setText("")
                        binding.pin4.setText("")

                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_Pay_slip)

                    } else {
                        var haveClick = 5-click
                        if(haveClick>0){
                        Utility.getBaseMessage(
                            requireActivity(),
                            "Incorrect Pin ",
                            "Please enter correct pin and you have "+haveClick+"attempt left",
                            R.drawable.error_white,
                            0
                        )}else{
                            binding.submitPin.isEnabled = false
                            
                        }
                    }

                }

            }
        }else{

        }
        */

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onclick()
        // Use binding safely
        binding.submitPin.isEnabled = true


        //  animation()
        if(click<5) {
            binding.submitPin.setOnClickListener {
                click = click + 1
                if (validation()) {
                    var storedPin = Utility.getValueByKey(requireActivity(), "pinforpayslip")
                    if (storedPin != "") {
                        timecount = 1
                        givenpin()
                    } else {
                        timecount = 0
                        givenpin()
                        // storedPin = Utility.getValueByKey(requireActivity(),"pinforpayslip")
                    }


                    storedPin = Utility.getValueByKey(requireActivity(), "pinforpayslip")
                    if (storedPin == pin) {
                        binding.pin1.setText("")
                        binding.pin2.setText("")
                        binding.pin3.setText("")
                        binding.pin4.setText("")

                        val navController =
                            requireActivity().findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_Pay_slip)

                    } else {
                        var haveClick = 5-click
                        if(haveClick>0){
                            Utility.getBaseMessage(
                                requireActivity(),
                                "Incorrect Pin ",
                                "Please enter correct pin and you have "+haveClick+"attempt left",
                                R.drawable.error_white,
                                0
                            )}else{
                            if(haveClick<=0) {
                                Utility.getBaseMessage(
                                    requireActivity(),
                                    "Incorrect Pin ",
                                    "Entered wrong pin multiple times,now you don't have any attempt left",
                                    R.drawable.error_white,
                                    0
                                )
                            }
                            binding.submitPin.isEnabled = false

                        }
                    }

                }

            }
        }else{

        }
    }


    fun onclick(){

        binding.pin1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the input is not empty
                if (!s.isNullOrEmpty()) {
                    // Move focus to the next EditText
                    binding.pin2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.pin2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the input is not empty
                if (!s.isNullOrEmpty()) {
                    // Move focus to the next EditText
                    binding.pin3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.pin3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the input is not empty
                if (!s.isNullOrEmpty()) {
                    // Move focus to the next EditText
                    binding.pin4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }

/*
    fun animation(){

        val animationUrl = "https://lottiefiles.com/free-animation/password-lock-security-IH3koW1xBD.json"
        binding.lottieAnimationView.setAnimationFromUrl(animationUrl)
        binding.lottieAnimationView.playAnimation()
        binding.lottieAnimationView.repeatCount = LottieDrawable.INFINITE
    }

 */


    fun validation(): Boolean {
        var result = false
        if (binding.pin1.text.toString().isEmpty()
            || binding.pin2.text.toString().isEmpty()
            || binding.pin2.text.toString().isEmpty()
            || binding.pin2.text.toString().isEmpty()
        ) {
            result = false
            Utility.getBaseMessage(
                requireActivity(),
                "Pin Required",
                "Please enter four digit pin properly",
                R.drawable.error_white,
                0
            )

        }else {
            result = true
        }

        return result

    }


    fun givenpin(){
        val p1 = binding.pin1.text.toString()
        val p2 = binding.pin2.text.toString()
        val p3 = binding.pin3.text.toString()
        val p4 = binding.pin4.text.toString()

        pin = p1+p2+p3+p4
        if(timecount==0) {
            Utility.storeKeyValue(requireActivity(), "pinforpayslip", pin)
            timecount = 1
        }else{
            timecount = 1

        }

    }





}