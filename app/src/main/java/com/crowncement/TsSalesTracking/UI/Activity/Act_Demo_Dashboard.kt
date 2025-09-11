package com.crowncement.TsSalesTracking.UI.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crowncement.TsSalesTracking.R

class Act_Demo_Dashboard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_demo_dashboard)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}