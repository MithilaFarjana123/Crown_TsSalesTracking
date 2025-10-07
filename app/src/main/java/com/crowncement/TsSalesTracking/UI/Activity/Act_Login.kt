package com.crowncement.TsSalesTracking.UI.Activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.crowncement.TsSalesTracking.UI.viewmodel.LoginViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.LoginViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.baseLoadingAnimation
import com.crowncement.TsSalesTracking.common.Utility.saveUserInfo
import com.crowncement.TsSalesTracking.data.Model.Data
import com.crowncement.TsSalesTracking.databinding.ActivityLoginBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Act_Login : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loadingAnim: Dialog

    private val REQUEST_BACKGROUND_LOCATION = 66

    private lateinit var binding: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestBackgroundLocationPermission()
    }

    private fun regularTask() {
        loadingAnim = baseLoadingAnimation(this, Dialog(this), "P l e a s e    w a i t")


       // LoginViewModelFactory()

        setupViewModel()

        checkAutoLogin()

        binding.txtRegister.setOnClickListener {
          // Utility.sendActivity(this, "Act_Forget_Password")
        }

        binding.btnLogin.setOnClickListener {

            //Demo user login for google play store

            if (binding.txtEMPID.text.toString() == "E00-000000" && binding.txtPass.text.toString() == "demo1234") {
                Utility.sendActivity(this, "Act_Demo_Dashboard")
            }

            // Crown cement user

            else {
                val fcmToken = Utility.getValueByKey(this, "fcm_token").toString()
                Utility.storeKeyValue(this, "username", binding.txtEMPID.text.toString())
                if (binding.cbRemember.isChecked) {
                    val pass = binding.txtPass.text.toString()
                  //  val device = getAndroidDeviceId() + " "+ getDeviceName() + " " + getCurrentDateTime()

                    Utility.storeKeyValue(this, "pass", pass)
                   // Utility.storeKeyValue(this, "deviceId", device)

                }
                if (loginUIValidation()) {
                    loadingAnim.show()
                    var deviceID = Utility.getValueByKey(this,"deviceId")
                    if (deviceID != null) {
                        getUserLogin(
                            binding.txtEMPID.text.toString(),
                            binding.txtPass.text.toString(),
                            deviceID,
                            //getAndroidDeviceId()+" "+ getDeviceName() +" "+ getCurrentDateTime(),
                            fcmToken
                        )
                    }
                }
            }
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
    }


    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_LOCATION
            )
        } else {
            regularTask()
        }
    }

    private fun checkAutoLogin() {

        val username: String = Utility.getValueByKey(this, "username")!!
        val pass: String = Utility.getValueByKey(this, "pass")!!
        val deviceId: String = Utility.getValueByKey(this, "deviceId")!!
        val fcm = Utility.getValueByKey(this, "fcm_token").toString()

        if (username.isNotBlank() && pass.isNotBlank() && deviceId.isNotBlank() && fcm.isNotBlank()) {
            binding.txtEMPID.setText(username)
            binding.txtPass.setText(pass)
            binding.cbRemember.isChecked = true
            loadingAnim.show()
            getUserLogin(username, pass, deviceId, fcm)
        }
    }

    private fun getAndroidDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }


    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.capitalize()
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }



    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun main() {
        val currentTime = getCurrentDateTime()
        println(currentTime)  // Example: 2025-10-06 09:32:27.000
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
        //  viewModel =  ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private fun getUserLogin(userName: String, pass: String, deviceId: String, fcm: String) {

        viewModel.getUserInfo(userName, pass, deviceId, fcm).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.responseData?.let { res ->

                        loadingAnim.dismiss()
                        if (res.message.equals("Success Message")) {
                            val userData: Data? = res.data
                            saveUserInfo(userData, this)
                            Utility.sendActivity(this, "MainActivity")
                        } else {
                            Utility.commonToast(this, "Login failed", res.message.toString(), 2)
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

    private fun loginUIValidation(): Boolean {

        //layoutEmpId
        binding.layoutEmpId.error = null
        binding.laypass.error = null

        return when {
            binding.txtEMPID.text.isNullOrBlank() -> {
                binding.layoutEmpId.error = "Please, provide your employee id"
                false
            }
            binding.txtPass.text!!.isNullOrBlank() -> {
                binding.laypass.error = "Password is required"
                false
            }
            else -> true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            REQUEST_BACKGROUND_LOCATION -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    regularTask()
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                    requestBackgroundLocationPermission()
                }
                return
            }
        }
    }

}
