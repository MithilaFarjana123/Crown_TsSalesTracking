package com.crowncement.TsSalesTracking.UI.Activity

import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.crowncement.TsSalesTracking.UI.viewmodel.LoginViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LoginViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.baseLoadingAnimation
import com.crowncement.TsSalesTracking.databinding.ActForgetPasswordBinding

class Act_Forget_Password : AppCompatActivity() {

    private lateinit var binding: ActForgetPasswordBinding

    private lateinit var loadingAnim: Dialog
    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        loadingAnim = baseLoadingAnimation(this, Dialog(this), "P l e a s e    w a i t")

        binding.passResBack.setOnClickListener {
            Utility.sendActivity(this, "Act_Login")

        }

        if (binding.txtEMPID.text.isNullOrEmpty()) {
            binding.txtEMPID.setText("E")
            binding.txtEMPID.setSelection(binding.txtEMPID.length())
        }

        //TODO EMP ID AUTO TEXT
        binding.txtEMPID.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty()) {
                    binding.txtEMPID.setText("E")
                    binding.txtEMPID.setSelection(binding.txtEMPID.length())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().length == 3) {
                    val givenText = binding.txtEMPID.text.toString() + "-"
                    binding.txtEMPID.setText(givenText)
                    binding.txtEMPID.setSelection(binding.txtEMPID.length())
                }

            }
        })


        binding.btnChangePass.setOnClickListener {
            if (passResUIValidation()) {
                loadingAnim.show()

                getUserPassChange(
                    binding.txtEMPID.text.toString(),
                    getAndroidDeviceId(),
                    binding.txtoldPass.text.toString(),
                    binding.txtNewPass.text.toString()

                )
            }
        }






    }


    private fun getUserPassChange(userName: String,  deviceId: String, oldpass: String, newpass: String) {

        viewModel.UserPassChange(userName, deviceId, oldpass, newpass).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.responseData?.let { res ->

                        loadingAnim.dismiss()
                        //if (res.message.equals("Success Message")) {
                        if (res.code == "200") {

                                //val userData: Data? = res.data
                           // saveUserInfo(userData, this)
                            Utility.sendActivity(this, "MainActivity")
                        } else {
                            Utility.commonToast(this, "Password reset failed", res.message.toString(), 2)
                        }
                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    loadingAnim.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun setupViewModel() {
        viewModel =
            ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
        //  viewModel =  ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private fun getAndroidDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }




    private fun passResUIValidation(): Boolean {

        var result = false
        binding.layoutEmpId.error = null
        binding.layOldpass.error = null
        binding.layNewpass.error = null
        binding.txtConPass.error = null




        if (binding.txtEMPID.text.toString().isEmpty()||binding.txtEMPID.text.toString().equals("E")) {
            result = false
            binding.layoutEmpId.isErrorEnabled = true
            binding.layoutEmpId.error = "Please, provide your employee id"
        }else if (binding.txtoldPass.text.toString().isEmpty()) {
            result = false
            binding.layOldpass.isErrorEnabled = true
            binding.layOldpass.error = "Old password is required"
        } else if (binding.txtNewPass.text.toString().isEmpty()) {
            result = false
            binding.layNewpass.isErrorEnabled = true
            binding.layNewpass.error = "New Password is required"
        }

        else if (
            binding.txtConPass.text!!.isNullOrBlank()&&
            (binding.txtConPass.text.toString().equals(binding.txtNewPass.text.toString()))

            ) {
            result = false
            binding.layConpass.isErrorEnabled = true
            binding.layConpass.error = "Confirm Password must be same as New Password"
        }

         else {
            result = true
        }

        return result
    }






}




